package models;

import utils.RandomUtils;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Objects;

/**
 * Utility class to simplify creating {@link Person} instances.
 * <p>
 * This delegates set* methods from Person class and provides initial values for the fields and
 * has {@link #build(EntityManager)} method that makes the instance persistent. Note that you shouldn't
 * edit any fields after an instance is built.
 */
public class PersonFactory {
    private Person person;
    private boolean built = false;

    public PersonFactory() {
        person = new Person();
        person.setFirstName("");
        person.setLastName("");
        person.setDisplayName("");
        person.setSelfGroup(new SelfGroup());
        person.getSelfGroup().setMember(person);
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
        person.setDisplayName(Objects.toString(displayName, ""));
        return this;
    }

    public PersonFactory genPhoto(int height, int width, EntityManager em) {
        assert !built;
        FileMeta pic = FileManager.createFile(person.getDisplayName() + ".jpg", "image/jpeg", em);
        try {
            RandomUtils.randomImage(height, width, FileManager.getFile(pic));
        } catch (IOException e) {
            assert false;
        }
        person.setPhoto(pic);
        person.getFiles().add(pic);
        return this;
    }

    public Person get() {
        return person;
    }

    public Person build(EntityManager em) {
        if (!built) {
            em.persist(person);
            built = true;
        }
        return person;
    }
}
