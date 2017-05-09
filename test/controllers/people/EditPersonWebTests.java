package controllers.people;

import models.Group;
import models.GroupType;
import models.Person;
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
        Person person = new Person();
        jpaApi.withTransaction(() -> {
            // Person
            person.setFirstName(TEST_FIRST_NAME);
            person.setMiddleName(TEST_MIDDLE_NAME);
            person.setLastName(TEST_LAST_NAME);
            person.setDisplayName(TEST_DISPLAY_NAME);

            // Group
            GroupType selfGroup = jpaApi.em().find(GroupType.class, GroupType.DefaultTypes.selfGroup);
            Group group = new Group();
            group.setType(selfGroup);
            person.setSelfGroup(group);
            person.getSelfGroup().getMembers().add(person);

            jpaApi.em().persist(person);
        });
        return person;
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
