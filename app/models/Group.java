package models;

import modules.preloader.DatabasePreloader;
import utils.SimpleQuery;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private long id;

    @Column(length = 70)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private GroupType type;

    @Column(length = 120)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    private FileMeta photo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "members",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private List<Person> members = new ArrayList<>();

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileMeta getPhoto() {
        return photo;
    }

    public void setPhoto(FileMeta photo) {
        this.photo = photo;
    }

    public List<Person> getMembers() {
        return members;
    }

    public List<Post> getPosts() {
        return posts;
    }

    static {
        DatabasePreloader.addTest((em -> {
            for (int i = 0; i < 100; i++) {
                Group group = new Group();
                Random randomLength = new Random();
                int length = randomLength.nextInt(10) + 4;
                String groupName = capitalizeFully(randomAlphabetic(length));
                group.setName(groupName);
                GroupType groupType;
                if(i > 50)  groupType = em.find(GroupType.class, GroupType.DefaultTypes.social);
                else  groupType = em.find(GroupType.class, GroupType.DefaultTypes.conversation);
                group.setType(groupType);
                length = randomLength.nextInt(15) + 5;
                String description = capitalizeFully(randomAlphabetic(length));
                group.setDescription(description);
                List<Person> people = (new SimpleQuery(em, Person.class)).getResultList();
                int random = new Random().nextInt(people.size());
                ThreadLocalRandom.current().ints(0, people.size()).distinct().limit(random).forEach(index -> group.members.add(people.get(index)));
                em.persist(group);
            }
        }), 30);

    }
}
