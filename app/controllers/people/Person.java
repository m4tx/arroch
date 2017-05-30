package controllers.people;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.personInfo;
import views.html.personAccounts;
import views.html.personFriends;
import views.html.personFriendOf;

import javax.persistence.EntityManager;

import static play.mvc.Results.ok;

public class Person {
    @Transactional
    public Result get(Long id) {
        return getInfo(id);
    }

    @Transactional
    public Result getInfo(Long id) {
        return ok(personInfo.render(JPA.em().find(models.Person.class, id)));
    }

    @Transactional
    public Result getAccounts(Long id) {
        return ok(personAccounts.render(JPA.em().find(models.Person.class, id)));
    }

    @Transactional
    public Result getFriends(Long id) {
        return ok(personFriends.render(JPA.em().find(models.Person.class, id)));
    }

    @Transactional
    public Result getFriendOf(Long id) {
        return ok(personFriendOf.render(JPA.em().find(models.Person.class, id)));
    }
}
