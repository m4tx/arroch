package utils;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;

import static utils.WithTestDatabase.configureTestDatabase;


public class WithBrowserAndTestDatabase extends WithBrowser {
    @Override
    protected final Application provideApplication() {
        return configureTestDatabase(new GuiceApplicationBuilder()).build();
    }
}
