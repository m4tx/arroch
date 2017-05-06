package models;

import javax.persistence.*;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private int id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @Column(nullable = false)
    private GroupType type;

    @Column
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    @Column
    private File photo;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }
}
