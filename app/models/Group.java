package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Cacheable
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", length = 16)
@Table(name = "groups")
abstract public class Group {
    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private long id;

    @Column(length = 70)
    private String name;

    @Column(length = 120)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    private FileMeta photo;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "group_files",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "file_id")}
    )
    private List<FileMeta> files = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileMeta getPhoto() {
        return photo;
    }

    public void setPhoto(FileMeta photo) {
        this.photo = photo;
    }

    public abstract List<Person> getMembers();

    public List<Post> getPosts() {
        return posts;
    }

    public List<FileMeta> getFiles() {
        return files;
    }
}
