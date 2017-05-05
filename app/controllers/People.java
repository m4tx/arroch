package controllers;

import models.Person;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.index;
import views.html.person;

import javax.persistence.EntityManager;

import java.util.Random;
import java.util.stream.Collectors;

import static play.mvc.Results.ok;

public class People {
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
