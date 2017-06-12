package models;

import modules.preloader.DatabasePreloader;
import utils.RandomUtils;
import utils.SimpleQuery;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

@Entity
@DiscriminatorValue("conversation")
public class ConversationGroup extends Group {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conversation_members",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private List<Person> members = new ArrayList<>();

    @Override
    public List<Person> getMembers() {
        return members;
    }

    static {
        DatabasePreloader.addTest((em -> {
            for (int i = 0; i < 100; i++) {
                ConversationGroup group = new ConversationGroup();
                Random randomLength = new Random();
                group.setName(capitalizeFully(randomAlphabetic(randomLength.nextInt(10) + 4)));
                group.setDescription(capitalizeFully(randomAlphabetic(randomLength.nextInt(15) + 5)));
                List<Person> people = (new SimpleQuery<>(em, Person.class)).getResultList();
                int random = new Random().nextInt(people.size() - 1);
                ThreadLocalRandom.current().ints(0, people.size())
                        .distinct().limit(random + 1).forEach(index -> group.members.add(people.get(index)));
                FileMeta pic = FileManager.createFile(group.getName() + ".jpg", "image/jpeg", em);
                try {
                    RandomUtils.randomImage(200, 800, FileManager.getFile(pic));
                } catch (IOException e) {
                    assert false;
                }
                group.setPhoto(pic);
                group.getFiles().add(pic);
                em.persist(group);
            }
        }), 30);
    }
}
