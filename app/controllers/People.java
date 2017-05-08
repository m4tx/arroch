package controllers;

import utils.SimpleQuery;
import models.Person;
import models.PersonInfo;
import models.PropertyType;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.*;

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
    public Result person(Long id) {
        EntityManager em = JPA.em();
        Person p = em.find(Person.class, id);
        return ok(person.render(p));
    }

    @Transactional
    public Result editPerson(Long id) {
        EntityManager em = JPA.em();
        Person person = em.find(Person.class, id);
        Form<Person> form = formFactory.form(Person.class);
        form = form.fill(person);
        return ok(editPerson.render(person, form));
    }

    @Transactional
    public Result editPersonPost(Long id) {
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

    @Transactional
    public Result editPersonInfo(Long id, Long infoId) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        Person person = em.find(Person.class, id);
        PersonInfo personInfo = em.find(PersonInfo.class, infoId);
        form = form.fill(personInfo);
        List<PropertyType> options = (new SimpleQuery<>(JPA.em(), PropertyType.class)).getResultList();
        return ok(editPersonInfo.render(person, personInfo, form, options));
    }

    @Transactional
    public Result editPersonInfoPost(Long id, Long infoId) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        PersonInfo personInfo = em.find(PersonInfo.class, infoId);

        PersonInfo newPersonInfo = form.bindFromRequest().get();
        personInfo.setType(newPersonInfo.getType());
        personInfo.setValue(newPersonInfo.getValue());
        flash("success", "This person's info has been edited");
        return redirect(routes.People.person(id));
    }

    @Transactional
    public Result removePersonInfoPost(Long id, Long infoId) {
        EntityManager em = JPA.em();
        PersonInfo personInfo = em.find(PersonInfo.class, infoId);
        em.remove(personInfo);
        flash("success", "Information removed");
        return redirect(routes.People.person(id));
    }

    @Transactional
    public Result addPersonInfo(Long id) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        Person person = em.find(Person.class, id);
        List<PropertyType> options = (new SimpleQuery<>(JPA.em(), PropertyType.class)).getResultList();
        return ok(addPersonInfo.render(person, form, options));
    }

    @Transactional
    public Result addPersonInfoPost(Long id) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        PersonInfo newPersonInfo = form.bindFromRequest().get();
        Person person = em.find(Person.class, id);
        newPersonInfo.setPerson(person);
        em.persist(newPersonInfo);
        flash("success", "New information added");
        return redirect(routes.People.person(id));
    }
}
