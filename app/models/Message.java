package models;

import javax.persistence.*;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
}
