package forms;

import forms.formatters.PropertyTypeFormatter;
import models.PropertyType;
import play.data.format.Formatters;
import play.i18n.MessagesApi;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;


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
        formatters.register(PropertyType.class, new PropertyTypeFormatter());
        return formatters;
    }
}
