package controllers.people;

import models.Person;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.SimpleQuery;
import views.html.personList;

import java.util.List;

import static play.mvc.Results.ok;

public class People {
    @Transactional
    public Result get() {
        return getPage(1);
    }

    @Transactional
    public Result getPage(int page) {
        List<Person> people = (new SimpleQuery<>(JPA.em(), Person.class)
                .orderByAsc("lastName")
                .orderByAsc("firstName"))
                .getResultList();
        return ok(personList.render(people, "People", 25, page));
    }
}
