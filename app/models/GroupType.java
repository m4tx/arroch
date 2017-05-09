package models;

import modules.preloader.DatabasePreloader;

import javax.persistence.*;

@Entity
@Table(name = "group_types")
public class GroupType {
    @Id
    @Column(name = "group_type_id")
    private String id;

    @Column
    private String type;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GroupType() {
    }

    public GroupType(String id, String type) {
        this.id = id;
        this.type = type;
    }

    static class DefaultTypes {
        static final String selfGroup = "selfGroup";
        static final String conversation = "conversation";
        static final String social = "social";
    }

    static {
        DatabasePreloader.addDefault((em -> {
            em.persist(new GroupType(DefaultTypes.selfGroup, "Person Group"));
            em.persist(new GroupType(DefaultTypes.conversation, "Conversation"));
            em.persist(new GroupType(DefaultTypes.social, "Social group"));
        }), 0);
    }
}
