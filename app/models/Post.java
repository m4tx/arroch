package models;

import modules.preloader.DatabasePreloader;
import modules.preloader.Preloadable;
import utils.RandomUtils;
import utils.SimpleQuery;

import javax.persistence.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Entity
@Cacheable
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "message_id")
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
        DatabasePreloader.addTest((new Preloadable() {
            private String genMessageBody() {
                int blanks = new Random().nextInt(40) + 1;
                StringBuilder body = new StringBuilder();
                for (int j = 0; j <= blanks; j++) {
                    Random randomLength = new Random();
                    int length = randomLength.nextInt(15) + 1;
                    body.append(capitalizeFully(randomAlphabetic(length)));
                    body.append(" ");
                }
                return body.toString();
            }

            @Override
            public void run(EntityManager em) {
                Random generator = new Random();

                List<Group> groups = (new SimpleQuery<>(em, Group.class)).getResultList();
                for (Group group : groups) {
                    List<Person> members = group.getMembers();

                    int randPosts = generator.nextInt(50);
                    for (; randPosts > 0; randPosts--) {
                        Post post = new Post();
                        post.setGroup(group);
                        em.persist(post);

                        Message thread = new Message();
                        post.setThread(thread);

                        thread.setAuthor(members.get(generator.nextInt(members.size())));
                        thread.setBody(genMessageBody());
                        thread.setTimestamp(Timestamp.valueOf("2012-01-01 00:00:00"));
                        thread.setPost(post);

                        if(Math.random() < 0.5) {
                            FileMeta attachment = FileManager.createFile("photo.jpg", "image/jpeg", em);
                            try {
                                RandomUtils.randomImage(100, 200, FileManager.getFile(attachment));
                            } catch (IOException e) {
                                assert false;
                            }
                            thread.getMessageAttachment().add(attachment);
                        }

                        em.persist(thread);

                        int random = new Random().nextInt(members.size());
                        ThreadLocalRandom.current().ints(0, members.size())
                                .distinct().limit(random).forEach(index -> thread.getTags().add(members.get(index)));
                        random = new Random().nextInt(members.size());
                        ThreadLocalRandom.current().ints(0, members.size())
                                .distinct().limit(random).forEach(index -> thread.getUpvotes().add(members.get(index)));

                        int randComments = generator.nextInt(20);
                        for (; randComments > 0; randComments--) {
                            Message comment = new Message();
                            comment.setAuthor(members.get(generator.nextInt(members.size())));
                            comment.setBody(genMessageBody());
                            comment.setTimestamp(Timestamp.valueOf("2012-01-01 00:00:00"));
                            comment.setParent(thread);
                            comment.setPost(post);
                            random = new Random().nextInt(members.size() / 10 + 1);

                            if(Math.random() < 0.1) {
                                FileMeta attachment = FileManager.createFile("photo.jpg", "image/jpeg", em);
                                try {
                                    RandomUtils.randomImage(50, 100, FileManager.getFile(attachment));
                                } catch (IOException e) {
                                    assert false;
                                }
                                comment.getMessageAttachment().add(attachment);
                            }

                            if (random > 0) {
                                ThreadLocalRandom.current().ints(0, members.size())
                                        .distinct().limit(random).forEach(index -> comment.getTags().add(members.get(index)));
                            }
                            random = new Random().nextInt(members.size() / 10 + 1);
                            if (random > 0) {
                                ThreadLocalRandom.current().ints(0, members.size())
                                        .distinct().limit(random).forEach(index -> comment.getUpvotes().add(members.get(index)));
                            }
                            em.persist(comment);
                        }
                    }
                }
            }
        }), 50);
    }
}
