package models;

import modules.preloader.DatabasePreloader;
import modules.preloader.DefaultValue;

import javax.persistence.*;

@Entity
@Cacheable
@Table(name = "sources")
public class DataSource {
    @Id
    @Column(name = "source_id", length = 15)
    private String id;

    @Column(length = 30)
    private String name;

    @Column(length = 15)
    private String icon;

    @Column(length = 50)
    private String url;

    public DataSource() {
    }

    public DataSource(String id, String name, String icon, String url) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }

    public static class DataSourceList {
        @DefaultValue
        public static DataSource arroch = new DataSource("arroch", "Arroch", "database", "#");

        @DefaultValue
        public static DataSource google = new DataSource("google", "Google", "google", "https://plus.google.com/");

        @DefaultValue
        public static DataSource googlePlus = new DataSource("googlePlus", "Google Plus", "google-plus", "https://plus.google.com/");

        @DefaultValue
        public static DataSource facebook = new DataSource("facebook", "Facebook", "facebook", "https://www.facebook.com/");
    }

    static {
        DatabasePreloader.addDefault(0, DataSourceList.class);
    }
}
