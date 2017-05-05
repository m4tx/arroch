package controllers;

import models.Person;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.index;
import views.html.person;
import views.html.personList;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static play.mvc.Results.ok;

public class People {
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
    public Result person(Integer id) {
        EntityManager em = JPA.em();
        Person p = em.find(Person.class, id);
        return ok(person.render(p));
    }

    /**
     * (Very) temporary (for test purposes) controller that creates a random new Person.
     */
    @Transactional
    public Result testCreate() {
        EntityManager em = JPA.em();
        Person person = new Person();
        Random random = new Random();
        person.setFirstName(randomString(random, 10));
        person.setLastName(randomString(random, 10));
        person.setDisplayName(person.getFirstName() + " " + person.getLastName());
        em.persist(person);
        return ok(index.render("ID: " + person.getId()));
    }

    private static String randomString(Random random, final int maxLength) {
        final int length = 1 + random.nextInt(maxLength);
        return random.ints(length, 'a', 'z' + 1)
                .mapToObj(i -> Character.toString((char) i))
                .collect(Collectors.joining());
    }
}
