package models;

import modules.preloader.DatabasePreloader;
import utils.SimpleQuery;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static utils.RandomUtils.randomDate;

@Entity
@Table(name = "people_info")
public class PersonInfo {
    @Id
    @GeneratedValue
    @Column
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyType type;

    private String value;

    public PersonInfo() {
    }

    public PersonInfo(Person person, PropertyType type, String value) {
        this.person = person;
        this.type = type;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    static {
        DatabasePreloader.addTest((em -> {
            List<Person> people = (new SimpleQuery<>(em, Person.class)).getResultList();
            List<PropertyType> prop = (new SimpleQuery<>(em, PropertyType.class)).getResultList();
            for (Person p : people) {
                for (PropertyType a : prop) {
                    String value;
                    if (a.getName().contains("Date") || a.getName().contains("date")) {
                        value = new SimpleDateFormat("yyyy-mm-dd").format(randomDate(1950, 2015));
                    } else if (a.getName().contains("number")) {
                        value = randomNumeric(9);
                    } else {
                        value = randomAlphabetic(10);
                    }
                    em.persist(new PersonInfo(p, a, value));
                }
            }
        }), 20);
    }
}
