package controllers.google;

import com.google.api.services.people.v1.model.*;
import models.DataSource;
import models.FileMeta;
import models.FileManager;
import models.Person;
import models.PersonAccount;
import models.PersonFactory;
import models.PersonInfo;
import models.PropertyType;
import org.apache.commons.io.FileUtils;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GooglePersonProcessor {
    private static final Pattern GOOGLE_PLUS_REGEX = Pattern.compile("^https?://plus\\.google\\.com/(\\d+)");

    private EntityManager em;

    public GooglePersonProcessor(EntityManager entityManager) {
        this.em = entityManager;
    }

    public Person processGooglePerson(com.google.api.services.people.v1.model.Person googlePerson) {
        PersonFactory personFactory = new PersonFactory(em);
        processNames(personFactory, googlePerson);
        Person person = personFactory.build();
        addGoogleAccount(person, googlePerson);
        processUrls(person, googlePerson);
        processPhoneNumbers(person, googlePerson);
        processAddresses(person, googlePerson);
        processEmailAddresses(person, googlePerson);
        processBirthdays(person, googlePerson);
        processPhotos(person, googlePerson);
        return person;
    }

    private void addGoogleAccount(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        PersonAccount personAccount = new PersonAccount();
        personAccount.setPerson(person);
        personAccount.setSource(DataSource.DataSourceList.google);
        personAccount.setAccount(googlePerson.getResourceName());
        em.persist(personAccount);
        person.getAccounts().add(personAccount);
    }

    private void processUrls(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        List<Url> urls = googlePerson.getUrls();
        if (urls == null) {
            return;
        }

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
                personAccount.setPerson(person);
                em.persist(personAccount);
                person.getAccounts().add(personAccount);
            }
        }
    }

    private void processNames(PersonFactory personFactory, com.google.api.services.people.v1.model.Person googlePerson) {
        List<Name> names = googlePerson.getNames();
        if (names != null && !names.isEmpty()) {
            Name name = names.get(0);
            personFactory.setDisplayName(name.getDisplayName());
            personFactory.setFirstName(name.getGivenName());
            personFactory.setMiddleName(name.getMiddleName());
            personFactory.setLastName(name.getFamilyName());
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

            PersonInfo info = new PersonInfo(person, type, phoneNumber.getCanonicalForm());
            person.getInfo().add(info);
            em.persist(info);
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

            PersonInfo info = new PersonInfo(person, type, address.getFormattedValue());
            person.getInfo().add(info);
            em.persist(info);
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

            PersonInfo info = new PersonInfo(person, type, emailAddress.getValue());
            person.getInfo().add(info);
            em.persist(info);
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

            PersonInfo info = new PersonInfo(person, PropertyType.PropertyTypeList.birthdate, dateText);
            person.getInfo().add(info);
            em.persist(info);
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
