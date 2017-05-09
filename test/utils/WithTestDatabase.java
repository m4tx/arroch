package utils;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

public abstract class WithTestDatabase extends WithApplication {
    @Override
    protected final Application provideApplication() {
        return configureTestDatabase(new GuiceApplicationBuilder()).build();
    }

    static GuiceApplicationBuilder configureTestDatabase(GuiceApplicationBuilder builder) {
        return builder
                .configure("db.default.driver", "org.h2.Driver")
                .configure("db.default.url", "jdbc:h2:mem:play")
                .configure("db.default.jndiName", "TestDS")
                .configure("db.default.logSql", "false")
                .configure("jpa.default", "testPersistenceUnit");
    }
}
