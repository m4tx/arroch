package models;

import javax.persistence.*;

@Entity
@Table(name = "conversations")
public class Conversation {
    @Id @GeneratedValue
    @Column(name = "conversation_id", nullable = false)
    private int conversationId;

    @Column(name = "name")
    private int name;

    @Column(name = "source")
    private String source;

    public int getConversationId() {
        return conversationId;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
