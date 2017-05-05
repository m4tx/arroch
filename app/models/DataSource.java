package models;

import javax.persistence.*;

@Entity
@Table(name = "sources")
public class DataSource {
    @Id
    @GeneratedValue
    @Column(name = "source_id")
    private int id;

    @Column
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
