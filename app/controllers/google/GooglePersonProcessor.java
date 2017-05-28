package controllers.google;

import com.google.api.services.people.v1.model.Name;
import models.Person;
import models.PersonFactory;

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
        return personFactory.build();
    }

    private void processNames(PersonFactory personFactory, com.google.api.services.people.v1.model.Person googlePerson) {
        List<Name> names = googlePerson.getNames();
        if (names != null && !names.isEmpty()) {
            Name name = names.get(0);
            personFactory.setDisplayName(name.getDisplayName());
            personFactory.setFirstName(name.getGivenName());
            personFactory.setMiddleName(name.getMiddleName());
            personFactory.setLastName(name.getFamilyName());
        } else {
            System.out.println(googlePerson);
        }
    }
}
