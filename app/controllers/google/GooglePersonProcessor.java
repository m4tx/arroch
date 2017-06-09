package controllers.google;

import com.google.api.services.people.v1.model.*;
import com.google.api.services.people.v1.model.Date;
import controllers.PersonProcessor;
import models.DataSource;
import models.FileMeta;
import models.FileManager;
import models.Person;
import models.PersonAccount;
import models.PersonInfo;
import models.PropertyType;
import org.apache.commons.io.FileUtils;
import play.Logger;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GooglePersonProcessor extends PersonProcessor<com.google.api.services.people.v1.model.Person> {
    private static final Pattern GOOGLE_PLUS_REGEX = Pattern.compile("^https?://plus\\.google\\.com/(\\d+)");

    public GooglePersonProcessor(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected void processWithPerson(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        processNames(person, googlePerson);
        processPhoneNumbers(person, googlePerson);
        processAddresses(person, googlePerson);
        processEmailAddresses(person, googlePerson);
        processBirthdays(person, googlePerson);
        processPhotos(person, googlePerson);
    }

    @Override
    protected List<PersonAccount> getAccounts(com.google.api.services.people.v1.model.Person googlePerson) {
        List<PersonAccount> accounts = new ArrayList<>();
        accounts.add(getGoogleAccount(googlePerson));
        accounts.addAll(getGooglePlusAccounts(googlePerson));
        accounts.addAll(getEmailAccounts(googlePerson));
        return accounts;
    }

    private static PersonAccount getGoogleAccount(com.google.api.services.people.v1.model.Person googlePerson) {
        PersonAccount personAccount = new PersonAccount();
        personAccount.setSource(DataSource.DataSourceList.google);
        personAccount.setAccount(googlePerson.getResourceName());
        return personAccount;
    }

    private static Collection<PersonAccount> getGooglePlusAccounts(
            com.google.api.services.people.v1.model.Person googlePerson) {
        List<Url> urls = googlePerson.getUrls();
        if (urls == null) {
            return Collections.emptyList();
        }

        List<PersonAccount> accounts = new ArrayList<>();
        for (Url url : urls) {
            String urlString = url.getValue();
            if (urlString == null) {
                continue;
            }

            Matcher matcher = GOOGLE_PLUS_REGEX.matcher(urlString);
            if (matcher.matches()) {
                PersonAccount personAccount = new PersonAccount();
                personAccount.setAccount(matcher.group(1));
                personAccount.setSource(DataSource.DataSourceList.googlePlus);
                accounts.add(personAccount);
            }
        }

        return accounts;
    }

    private static Collection<PersonAccount> getEmailAccounts(
            com.google.api.services.people.v1.model.Person googlePerson) {
        List<EmailAddress> emailAddresses = googlePerson.getEmailAddresses();
        if (emailAddresses == null) {
            return Collections.emptyList();
        }

        List<PersonAccount> accounts = new ArrayList<>();

        for (EmailAddress emailAddress : emailAddresses) {
            PersonAccount personAccount = new PersonAccount();
            personAccount.setSource(DataSource.DataSourceList.email);
            personAccount.setAccount(emailAddress.getValue());
            accounts.add(personAccount);
        }

        return accounts;
    }

    private void processNames(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        List<Name> names = googlePerson.getNames();
        if (names != null && !names.isEmpty()) {
            Name name = names.get(0);
            person.setDisplayName(name.getDisplayName());
            person.setFirstName(name.getGivenName());
            person.setMiddleName(name.getMiddleName());
            person.setLastName(name.getFamilyName());
        } else {
            List<Organization> organizations = googlePerson.getOrganizations();
            if (organizations != null && !organizations.isEmpty()) {
                for (Organization organization : organizations) {
                    person.setDisplayName(organization.getName());
                }
            }
        }
    }

    private void processPhoneNumbers(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        List<PhoneNumber> phoneNumbers = googlePerson.getPhoneNumbers();
        if (phoneNumbers == null) {
            return;
        }

        for (PhoneNumber phoneNumber : phoneNumbers) {
            PropertyType type;
            switch (phoneNumber.getType()) {
                case "home":
                    type = PropertyType.PropertyTypeList.homePhoneNumber;
                    break;
                case "work":
                    type = PropertyType.PropertyTypeList.workPhoneNumber;
                    break;
                default:
                    type = PropertyType.PropertyTypeList.phoneNumber;
            }

            addPersonInfo(person, new PersonInfo(person, type, phoneNumber.getCanonicalForm()));
        }
    }

    private void processAddresses(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        List<Address> addresses = googlePerson.getAddresses();
        if (addresses == null) {
            return;
        }

        for (Address address : addresses) {
            PropertyType type;
            String addressType = address.getType();

            // Address type may be null
            switch (addressType == null ? "other" : addressType) {
                case "home":
                    type = PropertyType.PropertyTypeList.homeAddress;
                    break;
                case "work":
                    type = PropertyType.PropertyTypeList.workAddress;
                    break;
                default:
                    type = PropertyType.PropertyTypeList.address;
            }

            addPersonInfo(person, new PersonInfo(person, type, address.getFormattedValue()));
        }
    }

    private void processEmailAddresses(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        List<EmailAddress> emailAddresses = googlePerson.getEmailAddresses();
        if (emailAddresses == null) {
            return;
        }

        for (EmailAddress emailAddress : emailAddresses) {
            PropertyType type;
            String emailType = emailAddress.getType();

            // Email type may be null
            switch (emailType == null ? "other" : emailType) {
                case "home":
                    type = PropertyType.PropertyTypeList.homeEmailAddress;
                    break;
                case "work":
                    type = PropertyType.PropertyTypeList.workEmailAddress;
                    break;
                default:
                    type = PropertyType.PropertyTypeList.emailAddress;
            }

            addPersonInfo(person, new PersonInfo(person, type, emailAddress.getValue()));
        }
    }

    private void processBirthdays(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        List<Birthday> birthdays = googlePerson.getBirthdays();
        if (birthdays == null) {
            return;
        }

        for (Birthday birthday : birthdays) {
            Date date = birthday.getDate();
            String dateText = birthday.getText();
            if (date != null && date.getYear() != null && date.getMonth() != null && date.getDay() != null) {
                dateText = new SimpleDateFormat("yyyy-MM-dd").format(
                        new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay()).getTime());
            }

            addPersonInfo(person, new PersonInfo(person, PropertyType.PropertyTypeList.birthdate, dateText));
        }
    }

    private void processPhotos(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        List<Photo> photos = googlePerson.getPhotos();
        if (photos == null) {
            return;
        }

        for (Photo photo : photos) {
            try {
                URL url = new URL(photo.getUrl());
                URLConnection urlConnection = url.openConnection();
                FileMeta fileMeta = FileManager.createFile(url.getFile(), urlConnection.getContentType(), em);
                FileUtils.copyToFile(urlConnection.getInputStream(), FileManager.getFile(fileMeta));
                person.setPhoto(fileMeta);
                person.getSelfGroup().getFiles().add(fileMeta);
            } catch (IOException e) {
                Logger.warn("Google: Could not download photo", e);
            }
        }
    }
}
