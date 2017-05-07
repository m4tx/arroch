package models;

import javax.persistence.*;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message thread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Group group;

    public long getId() {
        return id;
    }

    public Message getThread() {
        return thread;
    }

    public void setThread(Message thread) {
        this.thread = thread;
    }

    public Group getGroupId() {
        return group;
    }

    public void setGroupId(Group groupId) {
        this.group = groupId;
    }
}
