package models;

import modules.preloader.DatabasePreloader;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "group_types")
public class GroupType {
    @Id
    @Column(name = "group_type_id")
    @Size(max = 30)
    private String id;

    @Column
    @Size(max = 50)
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

    public static class DefaultTypes {
        public static final String selfGroup = "selfGroup";
        public static final String conversation = "conversation";
        public static final String social = "social";
    }

    static {
        DatabasePreloader.addDefault((em -> {
            em.persist(new GroupType(DefaultTypes.selfGroup, "Person Group"));
            em.persist(new GroupType(DefaultTypes.conversation, "Conversation"));
            em.persist(new GroupType(DefaultTypes.social, "Social group"));
        }), 0);
    }
}
