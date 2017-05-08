package controllers;

import models.Person;
import models.PersonInfo;
import models.PropertyType;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.editPerson;
import views.html.editPersonInfo;
import views.html.person;
import views.html.personList;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
        EntityManager em = JPA.em();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Person> query = criteriaBuilder.createQuery(Person.class);
        Root<Person> from = query.from(Person.class);
        query.select(from).orderBy(
                criteriaBuilder.asc(from.get("lastName")),
                criteriaBuilder.asc(from.get("firstName"))
        );
        List<Person> people = em.createQuery(query).getResultList();
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

    private List<PropertyType> getAvailablePropertyTypes() {
        EntityManager em = JPA.em();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PropertyType> query = criteriaBuilder.createQuery(PropertyType.class);
        query.from(PropertyType.class);
        return em.createQuery(query).getResultList();
    }

    @Transactional
    public Result editPersonInfo(Long id, Long infoId) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        Person person = em.find(Person.class, id);
        PersonInfo personInfo = em.find(PersonInfo.class, infoId);
        form = form.fill(personInfo);

        return ok(editPersonInfo.render(person, personInfo, form, getAvailablePropertyTypes()));
    }

    @Transactional
    public Result editPersonInfoPost(Long id, Long infoId) {
        EntityManager em = JPA.em();
        Form<PersonInfo> form = formFactory.form(PersonInfo.class);
        Person person = em.find(Person.class, id);
        PersonInfo personInfo = em.find(PersonInfo.class, infoId);

        PersonInfo newPersonInfo = form.bindFromRequest().get();
        personInfo.setType(newPersonInfo.getType());
        personInfo.setValue(newPersonInfo.getValue());
        flash("success", "This person's info has been edited");
        return redirect(routes.People.person(id));
    }
}
