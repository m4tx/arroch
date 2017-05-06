package models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {
    @Id @GeneratedValue
    @Column(name = "message_id", nullable = false)
    private int messageId;

    @Column(name = "parent_id", nullable = false)
    private int parentId;

    @Column(name = "author_id", nullable = false)
    private int authorId;

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

    public int getAuthor_id() {
        return authorId;
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
}
