package models;

import modules.preloader.DatabasePreloader;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Entity
@Table(name = "people")
public class Person {
    @Id
    @GeneratedValue
    @Column(name = "person_id")
    private int id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonInfo> info = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<PersonInfo> getInfo() {
        return info;
    }

    static {
        DatabasePreloader.addTest((em -> {
            for(int i = 0; i < 100; i++) {
                Person person = new Person();
                person.setFirstName(capitalizeFully(randomAlphabetic(10)));
                person.setLastName(capitalizeFully(randomAlphabetic(15)));
                person.setDisplayName(person.getFirstName() + " " + person.getLastName());
                em.persist(person);
            }
        }), 0);
    }
}
