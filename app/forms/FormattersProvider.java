package forms;

import models.PropertyType;
import play.data.format.Formatters;
import play.data.format.Formatters.SimpleFormatter;
import play.db.jpa.JPA;
import play.i18n.MessagesApi;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.text.ParseException;
import java.util.Locale;


/**
 * Formatters provider for any of our custom models.
 */
@Singleton
public class FormattersProvider implements Provider<Formatters> {
    private final MessagesApi messagesApi;

    @Inject
    public FormattersProvider(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
    }

    @Override
    public Formatters get() {
        Formatters formatters = new Formatters(messagesApi);

        formatters.register(PropertyType.class, new SimpleFormatter<PropertyType>() {
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
        });

        return formatters;
    }
}
