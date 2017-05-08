package models;

import modules.preloader.DatabasePreloader;

import javax.persistence.*;
import java.lang.reflect.Field;

@Entity
@Table(name = "property_types")
public class PropertyType {
    @Id
    @Column(name = "property_id", nullable = false)
    private String propertyId;

    @Column(nullable = false)
    private String name;

    @Column
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
            for (Field field : PropertyTypeData.class.getFields()) {
                if (field.getType() == PropertyType.class) {
                    try {
                        field.set(null, em.merge(field.get(null)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }), 0);
    }
}
