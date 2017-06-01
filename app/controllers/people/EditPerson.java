package controllers.people;

import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.pages.person.editPerson;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static play.mvc.Controller.flash;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class EditPerson {
    private FormFactory formFactory;

    @Inject
    public EditPerson(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    @Transactional
    public Result get(Long id) {
        EntityManager em = JPA.em();
        models.Person person = em.find(models.Person.class, id);
        Form<models.Person> form = formFactory.form(models.Person.class);
        form = form.fill(person);
        return ok(editPerson.render(person, form));
    }

    @Transactional
    public Result post(Long id) {
        EntityManager em = JPA.em();
        Form<models.Person> form = formFactory.form(models.Person.class);
        models.Person person = em.find(models.Person.class, id);
        models.Person newPerson = form.bindFromRequest().get();
        person.setFirstName(newPerson.getFirstName());
        person.setMiddleName(newPerson.getMiddleName());
        person.setLastName(newPerson.getLastName());
        person.setDisplayName(newPerson.getDisplayName());
        flash("success", "The person has been edited");
        return redirect(controllers.people.routes.Person.get(id));
    }
}
