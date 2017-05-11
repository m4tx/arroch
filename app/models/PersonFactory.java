package models;

import javax.persistence.EntityManager;

/**
 * Utility class to simplify creating {@link Person} instances.
 *
 * This delegates set* methods from Person class and provides initial values for the fields and
 * has {@link #build()} method that makes the instance persistent. Note that you shouldn't
 * edit any fields after an instance is built.
 */
public class PersonFactory {
    private Person person;
    private Group selfGroup;
    private EntityManager em;
    private boolean built = false;

    public PersonFactory(EntityManager em) {
        this.em = em;

        person = new Person();
        person.setFirstName("");
        person.setLastName("");
        person.setDisplayName("");

        GroupType selfGroup = em.find(GroupType.class, GroupType.DefaultTypes.selfGroup);
        this.selfGroup = new Group();
        this.selfGroup.setType(selfGroup);
        person.setSelfGroup(this.selfGroup);
    }

    public PersonFactory setFirstName(String firstName) {
        assert !built;
        person.setFirstName(firstName);
        return this;
    }

    public PersonFactory setMiddleName(String middleName) {
        assert !built;
        person.setMiddleName(middleName);
        return this;
    }

    public PersonFactory setLastName(String lastName) {
        assert !built;
        person.setLastName(lastName);
        return this;
    }

    public PersonFactory setDisplayName(String displayName) {
        assert !built;
        person.setDisplayName(displayName);
        return this;
    }

    public PersonFactory setSelfGroup(Group selfGroup) {
        assert !built;
        this.selfGroup = selfGroup;
        person.setSelfGroup(selfGroup);
        return this;
    }

    public Person build() {
        if (!built) {
            person.getSelfGroup().getMembers().add(person);
            em.persist(person);
            built = true;
        }
        return person;
    }
}
