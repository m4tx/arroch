package models;

import modules.preloader.DatabasePreloader;
import modules.preloader.Preloadable;

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

    static {
        DatabasePreloader.addDefault(em -> {
            DataSource d = new DataSource();
            d.setName("Arroch");
            em.persist(d);
        }, 0);
    }
}
