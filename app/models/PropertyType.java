package models;

import modules.preloader.DatabasePreloader;
import modules.preloader.DefaultValue;

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

    public static class PropertyTypeList {
        @DefaultValue
        public static PropertyType phoneNumber = new PropertyType("phoneNumber", "Phone number", "phone");

        @DefaultValue
        public static PropertyType workPhoneNumber = new PropertyType("workPhoneNumber", "Work phone number", "phone");

        @DefaultValue
        public static PropertyType homePhoneNumber = new PropertyType("homePhoneNumber", "Home phone number", "phone");

        @DefaultValue
        public static PropertyType birthdate = new PropertyType("birthdate", "Date of birth", "calendar");

        @DefaultValue
        public static PropertyType company = new PropertyType("company", "Company", "");

        @DefaultValue
        public static PropertyType education = new PropertyType("education", "Education", "");

        @DefaultValue
        public static PropertyType address = new PropertyType("address", "Address", "home");

        @DefaultValue
        public static PropertyType homeAddress = new PropertyType("homeAddress", "Home Address", "home");

        @DefaultValue
        public static PropertyType workAddress = new PropertyType("workAddress", "Work Address", "home");
    }

    static {
        DatabasePreloader.addDefault(0, PropertyTypeList.class);
    }
}
