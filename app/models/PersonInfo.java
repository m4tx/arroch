package models;

import javax.persistence.*;

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
}
