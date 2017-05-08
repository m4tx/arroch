package controllers.people;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.person;

import javax.persistence.EntityManager;

import static play.mvc.Results.ok;

public class Person {
    @Transactional
    public Result get(Long id) {
        EntityManager em = JPA.em();
        models.Person p = em.find(models.Person.class, id);
        return ok(person.render(p));
    }
}
