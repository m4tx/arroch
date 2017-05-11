package controllers.people;

import models.Person;
import models.PersonFactory;
import org.junit.Test;
import play.db.jpa.JPAApi;
import utils.WithBrowserAndTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

public class EditPersonWebTests extends WithBrowserAndTestDatabase {
    private static final String TEST_FIRST_NAME = "Dwight";
    private static final String TEST_MIDDLE_NAME = "Kurt";
    private static final String TEST_LAST_NAME = "Schrute";
    private static final String TEST_DISPLAY_NAME = "Dwight K. Schrute III";

    private Person createTestPerson(JPAApi jpaApi) {
        final Person[] person = new Person[1];
        jpaApi.withTransaction(() -> {
            person[0] = new PersonFactory(jpaApi.em())
                    .setFirstName(TEST_FIRST_NAME)
                    .setMiddleName(TEST_MIDDLE_NAME)
                    .setLastName(TEST_LAST_NAME)
                    .setDisplayName(TEST_DISPLAY_NAME)
                    .build();
        });

        return person[0];
    }

    @Test
    public void testFormIsFilledOut() {
        JPAApi jpaApi = app.injector().instanceOf(JPAApi.class);
        browser.goTo(controllers.people.routes.EditPerson.get(createTestPerson(jpaApi).getId()).url());
        assertThat(browser.$("#firstName").getAttribute("value")).isEqualTo(TEST_FIRST_NAME);
        assertThat(browser.$("#middleName").getAttribute("value")).isEqualTo(TEST_MIDDLE_NAME);
        assertThat(browser.$("#lastName").getAttribute("value")).isEqualTo(TEST_LAST_NAME);
        assertThat(browser.$("#displayName").getAttribute("value")).isEqualTo(TEST_DISPLAY_NAME);
    }
}
