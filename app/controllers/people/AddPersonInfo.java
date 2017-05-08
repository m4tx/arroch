package controllers.people;

import models.PersonInfo;
import models.PropertyType;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.SimpleQuery;
import views.html.addPersonInfo;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static play.mvc.Controller.flash;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class AddPersonInfo {
    private FormFactory formFactory;

    @Inject
    public AddPersonInfo(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    @Transactional
    public Result get(Long id) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        models.Person person = em.find(models.Person.class, id);
        List<PropertyType> options = (new SimpleQuery<>(JPA.em(), PropertyType.class)).getResultList();
        return ok(addPersonInfo.render(person, form, options));
    }

    @Transactional
    public Result post(Long id) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        PersonInfo newPersonInfo = form.bindFromRequest().get();
        models.Person person = em.find(models.Person.class, id);
        newPersonInfo.setPerson(person);
        em.persist(newPersonInfo);
        flash("success", "New information added");
        return redirect(controllers.people.routes.Person.get(id));
    }
}
