package controllers;

import models.Person;

public class PersonAccountConflictException extends RuntimeException {
    private Person firstPerson;
    private Person secondPerson;

    PersonAccountConflictException(Person firstPerson, Person secondPerson) {
        super(String.format("PersonAccount conflict: %s and %s",
                firstPerson.getDisplayName(), secondPerson.getDisplayName()));
        this.firstPerson = firstPerson;
        this.secondPerson = secondPerson;
    }

    public Person getFirstPerson() {
        return firstPerson;
    }

    public Person getSecondPerson() {
        return secondPerson;
    }
}
