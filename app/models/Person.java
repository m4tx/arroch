package models;

import modules.preloader.DatabasePreloader;
import org.hibernate.annotations.OrderBy;
import utils.SimpleQuery;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Entity
@Cacheable
@Table(name = "people")
public class Person {
    @Id
    @GeneratedValue
    @Column(name = "person_id")
    private long id;

    @Column(name = "first_name", length = 30)
    private String firstName;

    @Column(name = "middle_name", length = 30)
    private String middleName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "display_name", nullable = false, length = 81)
    private String displayName;

    @OneToOne(fetch = FetchType.LAZY)
    private FileMeta photo;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(clause = "type")
    private List<PersonInfo> info = new ArrayList<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Group> memberOf = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, unique = true)
    private Group selfGroup;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> taggedIn = new ArrayList<>();

    @ManyToMany(mappedBy = "upvotes", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> upvotesIn = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "friends",
            joinColumns = {@JoinColumn(name = "person_id")},
            inverseJoinColumns = {@JoinColumn(name = "friend_id")}
    )
    private List<Person> friends = new ArrayList<>();

    @ManyToMany(mappedBy = "friends", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Person> friendOf = new ArrayList<>();

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PersonAccount> accounts = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<Group> getMemeberOf() {
        return memberOf;
    }

    public FileMeta getPhoto() {
        return photo;
    }

    public void setPhoto(FileMeta photo) {
        this.photo = photo;
    }

    public Group getSelfGroup() {
        return selfGroup;
    }

    public void setSelfGroup(Group selfGroup) {
        this.selfGroup = selfGroup;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Message> getTaggedIn() {
        return taggedIn;
    }

    public List<Person> getFriends() {
        return friends;
    }

    public List<Person> getFriendOf() {
        return friendOf;
    }

    public List<PersonAccount> getAccounts() {
        return accounts;
    }

    static {
        DatabasePreloader.addTest((em -> {
            for (int i = 0; i < 100; i++) {
                String firstName = capitalizeFully(randomAlphabetic(10));
                String lastName = capitalizeFully(randomAlphabetic(10));
                new PersonFactory(em)
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setDisplayName(firstName + " " + lastName)
                        .genPhoto(100, 100)
                        .build();
            }

            List<Person> people = (new SimpleQuery<>(em, Person.class)).getResultList();
            for (Person person : people) {
                int random = new Random().nextInt(people.size());
                ThreadLocalRandom.current().ints(0, people.size()).distinct().limit(random).forEach(index -> person.friends.add(people.get(index)));
            }
        }), 10);
    }
}
