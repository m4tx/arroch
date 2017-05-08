package controllers;

import controllers.utils.SimpleQuery;
import models.Person;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.editPerson;
import views.html.person;
import views.html.personList;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static play.mvc.Controller.flash;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class People {
    private FormFactory formFactory;

    @Inject
    public People(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    @Transactional
    public Result people() {
        List<Person> people = (new SimpleQuery<>(JPA.em(), Person.class)
                .orderByAsc("lastName")
                .orderByAsc("firstName"))
                .getResultList();
        return ok(personList.render(people));
    }

    @Transactional
    public Result person(Integer id) {
        EntityManager em = JPA.em();
        Person p = em.find(Person.class, id);
        return ok(person.render(p));
    }

    @Transactional
    public Result editPerson(Integer id) {
        EntityManager em = JPA.em();
        Person person = em.find(Person.class, id);
        Form<Person> form = formFactory.form(Person.class);
        form = form.fill(person);
        return ok(editPerson.render(person, form));
    }

    @Transactional
    public Result editPersonPost(Integer id) {
        EntityManager em = JPA.em();
        Form<Person> form = formFactory.form(Person.class);
        Person person = em.find(Person.class, id);
        Person newPerson = form.bindFromRequest().get();
        person.setFirstName(newPerson.getFirstName());
        person.setMiddleName(newPerson.getMiddleName());
        person.setLastName(newPerson.getLastName());
        person.setDisplayName(newPerson.getDisplayName());
        flash("success", "The person has been edited");
        return redirect(routes.People.person(id));
    }
}
