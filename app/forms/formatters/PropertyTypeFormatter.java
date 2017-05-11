package forms.formatters;

import models.PropertyType;
import play.data.format.Formatters;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.util.Locale;

public class PropertyTypeFormatter extends Formatters.SimpleFormatter<PropertyType> {
    @Override
    public PropertyType parse(String text, Locale locale) throws ParseException {
        EntityManager em = JPA.em();
        PropertyType propertyType = em.find(PropertyType.class, text);
        if (propertyType == null) {
            throw new ParseException("There is no such property type", 0);
        }
        return propertyType;
    }

    @Override
    public String print(PropertyType propertyType, Locale locale) {
        return propertyType.getPropertyId();
    }
}
