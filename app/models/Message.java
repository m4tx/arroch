package models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {
    @Id @GeneratedValue
    @Column(name = "message_id", nullable = false)
    private int messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Person parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Person author;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp")
    private Date date;

    @Column(name = "body")
    private String body;
    


    public int getMessage() {
        return messageId;
    }

    public int getConversation_id() {
        return messageId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setParent(Person parent) {
        this.parent = parent;
    }

    public Person getAuthor() {
        return author;
    }
}
