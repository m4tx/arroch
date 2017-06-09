package crawlers;

import models.Person;
import models.PersonAccount;
import models.PersonFactory;
import models.PersonInfo;
import utils.SimpleQuery;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Base class for any processors of retrieved people data. Provides a unified API
 * for invoking processing and helper methods that take care of merging the information
 * with existing data.
 *
 * @param <T> class that contains the person data, i.e. class that the processor will
 *            get the data from
 */
public abstract class PersonProcessor<T> {
    protected EntityManager em;

    public PersonProcessor(EntityManager entityManager) {
        this.em = entityManager;
    }

    /**
     * Process given {@code sourceObject} and add the data to
     * a newly created or existing {@link Person} object
     *
     * @param sourceObject an object to get the data from
     * @return created or modified Person object
     */
    public Person process(T sourceObject) {
        List<PersonAccount> accounts = getAccounts(sourceObject);
        Person person = null;
        for (PersonAccount account : accounts) {
            PersonAccount personAccount = em.find(PersonAccount.class, account);
            if (personAccount != null) {
                if (person != null && person != personAccount.getPerson()) {
                    throw new PersonAccountConflictException(person, personAccount.getPerson());
                }
                person = personAccount.getPerson();
            }
        }
        if (person == null) {
            person = new PersonFactory().build(em);
        }

        for (PersonAccount account : accounts) {
            if (!person.getAccounts().contains(account)) {
                account.setPerson(person);
                em.persist(account);
                person.getAccounts().add(account);
            }
        }

        processWithPerson(person, sourceObject);
        return person;
    }

    /**
     * Return list of PersonAccount objects that can be used to uniquely identify a person
     * <p>
     * This is used to avoid creating multiple Person objects with the same data
     *
     * @param sourceObject an object that the data should be retrieved from
     * @return list of PersonAccounts (not persisted)
     */
    protected abstract List<PersonAccount> getAccounts(T sourceObject);

    /**
     * Add data from given {@code sourceObject} to the provided {@code person}
     *
     * @param person       Person object to save the data into
     * @param sourceObject an object that the data should be retrieved from
     */
    protected abstract void processWithPerson(Person person, T sourceObject);

    /**
     * Add a {@link PersonInfo} object to {@link Person} if not exists yet
     *
     * @param person     Person instance to add the PersonInfo to
     * @param personInfo PersonInfo instance to add
     */
    protected void addPersonInfo(Person person, PersonInfo personInfo) {
        // Check if person info already exists
        if (new SimpleQuery<>(em, PersonInfo.class)
                .where("person", person)
                .where("type", personInfo.getType())
                .where("value", personInfo.getValue())
                .getResultList()
                .isEmpty()) {
            em.persist(personInfo);
            person.getInfo().add(personInfo);
        }
    }
}
