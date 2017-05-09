package utils;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

/**
 * Created by Adam on 2017-05-09.
 */
public abstract class WithTestDatabase extends WithApplication {
    @Override
    protected final Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("db.default.driver", "org.h2.Driver")
                .configure("db.default.url", "jdbc:h2:mem:play")
                .configure("db.default.jndiName", "TestDS")
                .configure("db.default.logSql", "false")
                .configure("jpa.default", "testPersistenceUnit")
                .build();
    }
}
