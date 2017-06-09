package models;

import modules.preloader.DatabasePreloader;
import utils.SimpleQuery;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Entity
@Table(name = "people_map")
public class PersonAccount implements Serializable {
    @Column
    @Id
    private String account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id", nullable = false)
    private DataSource source;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PersonAccount)) {
            return false;
        }
        PersonAccount other = (PersonAccount) obj;
        return account.equals(other.account) && source.equals(other.source);
    }

    @Override
    public int hashCode() {
        return 31 * account.hashCode() + source.hashCode();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public DataSource getSource() {
        return source;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    static {
        DatabasePreloader.addTest((em -> {
            List<Person> people = (new SimpleQuery<>(em, Person.class)).getResultList();
            for (Person p : people) {
                PersonAccount a = new PersonAccount();
                a.setPerson(p);
                a.setAccount(randomAlphabetic(10));
                a.setSource(DataSource.DataSourceList.google);
                em.persist(a);
                a = new PersonAccount();
                a.setPerson(p);
                a.setAccount(randomAlphabetic(15));
                a.setSource(DataSource.DataSourceList.facebook);
                em.persist(a);
            }
        }), 20);
    }
}
