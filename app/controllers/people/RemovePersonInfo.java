package controllers.people;

import models.PersonInfo;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.persistence.EntityManager;

import static play.mvc.Controller.flash;
import static play.mvc.Results.redirect;

public class RemovePersonInfo {
    @Transactional
    public Result post(Long id, Long infoId) {
        EntityManager em = JPA.em();
        PersonInfo personInfo = em.find(PersonInfo.class, infoId);
        em.remove(personInfo);
        flash("success", "Information removed");
        return redirect(controllers.people.routes.Person.get(id));
    }
}
