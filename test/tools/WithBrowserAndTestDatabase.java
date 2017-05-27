package tools;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;

import java.util.concurrent.TimeUnit;


public class WithBrowserAndTestDatabase extends WithBrowser {
    protected void waitForPageLoad() {
        browser.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
    }

    @Override
    protected final Application provideApplication() {
        return WithTestDatabase.configureTestDatabase(new GuiceApplicationBuilder()).build();
    }
}
