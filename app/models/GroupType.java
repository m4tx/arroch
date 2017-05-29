package models;

import modules.preloader.DatabasePreloader;
import modules.preloader.DefaultValue;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Cacheable
@Table(name = "group_types")
public class GroupType {
    @Id
    @Column(name = "group_type_id", length = 30)
    private String id;

    @Column(length = 50)
    private String type;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public GroupType() {
    }

    public GroupType(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public static class GroupTypeList {
        @DefaultValue
        public static GroupType selfGroup = new GroupType("selfGroup", "Person Group");

        @DefaultValue
        public static GroupType conversation = new GroupType("conversation", "Conversation");

        @DefaultValue
        public static GroupType social = new GroupType("social", "Social group");
    }

    static {
        DatabasePreloader.addDefault(0, GroupTypeList.class);
    }
}
