package models;

import javax.persistence.*;

@Entity
@Table(name = "property_types")
public class Property {
    @Id @GeneratedValue
    @Column(name = "property_id", nullable = false)
    private int propertyId;

    @Column(name = "name")
    private String name;

    @Column(name = "icon")
    private String icon;

    public int getPropertyId() {
        return propertyId;
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
}
