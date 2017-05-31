package controllers.people;

import models.Person;
import models.PersonFactory;
import org.junit.Test;
import play.db.jpa.JPAApi;
import tools.WithBrowserAndTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

public class EditPersonWebTests extends WithBrowserAndTestDatabase {
    private static final String TEST_FIRST_NAME = "Dwight";
    private static final String TEST_MIDDLE_NAME = "Kurt";
    private static final String TEST_LAST_NAME = "Schrute";
    private static final String TEST_DISPLAY_NAME = "Dwight K. Schrute III";

    public static final String FIRST_NAME_SELECTOR = "#firstName";
    public static final String MIDDLE_NAME_SELECTOR = "#middleName";
    public static final String LAST_NAME_SELECTOR = "#lastName";
    public static final String DISPLAY_NAME_SELECTOR = "#displayName";

    private Person createTestPerson(JPAApi jpaApi) {
        final Person[] person = new Person[1];
        jpaApi.withTransaction(() -> {
            person[0] = new PersonFactory()
                    .setFirstName(TEST_FIRST_NAME)
                    .setMiddleName(TEST_MIDDLE_NAME)
                    .setLastName(TEST_LAST_NAME)
                    .setDisplayName(TEST_DISPLAY_NAME)
                    .build(jpaApi.em());
        });

        return person[0];
    }

    @Test
    public void testFormIsFilledOut() {
        JPAApi jpaApi = app.injector().instanceOf(JPAApi.class);
        browser.goTo(controllers.people.routes.EditPerson.get(createTestPerson(jpaApi).getId()).url());
        assertThat(browser.$(FIRST_NAME_SELECTOR).value()).isEqualTo(TEST_FIRST_NAME);
        assertThat(browser.$(MIDDLE_NAME_SELECTOR).value()).isEqualTo(TEST_MIDDLE_NAME);
        assertThat(browser.$(LAST_NAME_SELECTOR).value()).isEqualTo(TEST_LAST_NAME);
        assertThat(browser.$(DISPLAY_NAME_SELECTOR).value()).isEqualTo(TEST_DISPLAY_NAME);
    }

    @Test
    public void testEditPerson() {
        JPAApi jpaApi = app.injector().instanceOf(JPAApi.class);
        Person person = createTestPerson(jpaApi);
        browser.goTo(controllers.people.routes.EditPerson.get(person.getId()).url());
        browser.$(FIRST_NAME_SELECTOR).first().fill().with("A");
        browser.$(MIDDLE_NAME_SELECTOR).first().fill().with("B");
        browser.$(LAST_NAME_SELECTOR).first().fill().with("C");
        browser.$(DISPLAY_NAME_SELECTOR).first().fill().with("D");
        browser.$("button[type=submit]").click();
        waitForPageLoad();
        jpaApi.withTransaction(() -> {
            Person refreshedPerson = jpaApi.em().find(Person.class, person.getId());
            assertThat(refreshedPerson.getFirstName()).isEqualTo("A");
            assertThat(refreshedPerson.getMiddleName()).isEqualTo("B");
            assertThat(refreshedPerson.getLastName()).isEqualTo("C");
            assertThat(refreshedPerson.getDisplayName()).isEqualTo("D");
        });
    }
}
