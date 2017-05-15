package models;

import modules.preloader.DatabasePreloader;
import utils.SimpleQuery;

import javax.persistence.*;
import javax.validation.constraints.Past;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue
    @Column(name = "message_id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Message parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Person author;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    @Past
    private Date timestamp;

    @Column(length = 10000)
    private String body;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tags",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private List<Person> tags = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "upvotes",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private List<Person> upvotes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "message_attachments",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private List<Person> messageAttachment = new ArrayList<>();

    public long getId() {
        return id;
    }

    public Message getParent() {
        return parent;
    }

    public void setParent(Message parent) {
        this.parent = parent;
    }

    public List<Message> getComments() {
        return comments;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Person> getTags() {
        return tags;
    }

    static {
        DatabasePreloader.addTest((em -> {
            List<Message> parentList = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Message message = new Message();
                List<Person> people = (new SimpleQuery(em, Person.class)).getResultList();
                int random = new Random().nextInt(people.size());
                Person author = people.get(random);
                message.setAuthor(author);
                random = new Random().nextInt(6);
                if(i == 0 || random == 1) {
                    parentList.add(message);
                }
                else{
                    random = new Random().nextInt(parentList.size());
                    message.setParent(parentList.get(random));
                }

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
                long end = timestamp.getTime();
                long diff = end - offset + 1;
                Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
                message.setTimestamp(rand);
                int blanks = new Random().nextInt(10) + 1;
                StringBuilder body = new StringBuilder();
                for(int j = 0; j <= blanks; j++){
                    Random randomLength = new Random();
                    int length = randomLength.nextInt(15) + 1;
                    body.append(capitalizeFully(randomAlphabetic(length)));
                    body.append(" ");
                }
                message.setBody(body.toString());
                em.persist(message);
            }
        }), 50);

    }
}
