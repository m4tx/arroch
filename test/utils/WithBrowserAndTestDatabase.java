package utils;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;

import java.util.concurrent.TimeUnit;

import static utils.WithTestDatabase.configureTestDatabase;


public class WithBrowserAndTestDatabase extends WithBrowser {
    protected void waitForPageLoad() {
        browser.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
    }

    @Override
    protected final Application provideApplication() {
        return configureTestDatabase(new GuiceApplicationBuilder()).build();
    }
}
