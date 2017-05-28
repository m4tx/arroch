package controllers.google;

import com.google.api.services.people.v1.model.Address;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.PhoneNumber;
import models.Person;
import models.PersonFactory;
import models.PersonInfo;
import models.PropertyType;

import javax.persistence.EntityManager;
import java.util.List;

public class GooglePersonProcessor {
    private EntityManager em;

    public GooglePersonProcessor(EntityManager entityManager) {
        this.em = entityManager;
    }

    public Person processGooglePerson(com.google.api.services.people.v1.model.Person googlePerson) {
        PersonFactory personFactory = new PersonFactory(em);
        processNames(personFactory, googlePerson);
        Person person = personFactory.build();
        processPhoneNumbers(person, googlePerson);
        processAddresses(person, googlePerson);
        return person;
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
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
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
    }

    private void processAddresses(Person person, com.google.api.services.people.v1.model.Person googlePerson) {
        List<Address> addresses = googlePerson.getAddresses();
        if (addresses != null && !addresses.isEmpty()) {
            for (Address address : addresses) {
                PropertyType type;
                switch (address.getType()) {
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
    }
}
