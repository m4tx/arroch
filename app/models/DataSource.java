package models;

import modules.preloader.DatabasePreloader;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "sources")
public class DataSource {
    @Id
    @GeneratedValue
    @Column(name = "source_id")
    private long id;

    @Column(length = 30)
    private String name;

    @Column(length = 15)
    private String icon;

    @Column(length = 50)
    private String url;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    static {
        DatabasePreloader.addDefault(em -> {
            DataSource d = new DataSource();
            d.setName("Arroch");
            d.setIcon("database");
            em.persist(d);
            d = new DataSource();
            d.setName("Google");
            d.setIcon("google");
            d.setUrl("https://plus.google.com/");
            em.persist(d);
            d = new DataSource();
            d.setName("Facebook");
            d.setIcon("facebook");
            d.setUrl("https://www.facebook.com/");
            em.persist(d);
        }, 0);
    }
}
