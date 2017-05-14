package models;

import modules.preloader.DatabasePreloader;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.lang.reflect.Field;

@Entity
@Table(name = "property_types")
public class PropertyType {
    @Id
    @Column(name = "property_id", nullable = false, length = 50)
    private String propertyId;

    @Column(nullable = false, length = 70)
    private String name;

    @Column(length = 50)
    private String icon;

    public PropertyType() {
    }

    public PropertyType(String propertyId, String name, String icon) {
        this.propertyId = propertyId;
        this.name = name;
        this.icon = icon;
    }

    public String getPropertyId() {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyType && ((PropertyType) obj).getPropertyId().equals(getPropertyId())) {
            return true;
        }
        return super.equals(obj);
    }

    static {
        DatabasePreloader.addDefault((em -> {
            em.persist(new PropertyType("phoneNumber", "Phone number", "phone"));
            em.persist(new PropertyType("workPhoneNumber", "Work phone number", "phone"));
            em.persist(new PropertyType("birthdate", "Date of birth", "calendar"));
            em.persist(new PropertyType("company", "Company", ""));
            em.persist(new PropertyType("education", "Education", ""));
            em.persist(new PropertyType("homeAddress", "Home Address", "home"));
        }), 0);
    }
}
