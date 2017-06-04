package models;

import modules.preloader.DatabasePreloader;
import utils.SimpleQuery;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group groupId) {
        this.group = groupId;
    }

    static {
        DatabasePreloader.addTest((em -> {
            Random generator = new Random();

            List<Group> groups = (new SimpleQuery<>(em, Group.class)).getResultList();
            for (Group group : groups) {
                List<Person> members = group.getMembers();

                int randPosts = generator.nextInt(50);
                for (; randPosts > 0; randPosts--) {
                    Post post = new Post();
                    post.setGroup(group);

                    Message thread = new Message();
                    post.setThread(thread);

                    thread.setAuthor(members.get(generator.nextInt(members.size())));
                    thread.setBody(capitalizeFully(randomAlphabetic(generator.nextInt(500))));
                    thread.setTimestamp(Timestamp.valueOf("2012-01-01 00:00:00"));
                    em.persist(post);
                    int random = new Random().nextInt(members.size());
                    ThreadLocalRandom.current().ints(0, members.size())
                            .distinct().limit(random).forEach(index -> thread.getTags().add(members.get(index)));
                    ThreadLocalRandom.current().ints(0, members.size())
                            .distinct().limit(random).forEach(index -> thread.getUpvotes().add(members.get(index)));

                    int randComments = generator.nextInt(20);
                    for (; randComments > 0; randComments--) {
                        Message comment = new Message();
                        comment.setAuthor(members.get(generator.nextInt(members.size())));
                        comment.setBody(capitalizeFully(randomAlphabetic(generator.nextInt(500))));
                        comment.setTimestamp(Timestamp.valueOf("2012-01-01 00:00:00"));
                        comment.setParent(thread);
                        em.persist(comment);
                    }
                }
            }
        }), 50);
    }
}
