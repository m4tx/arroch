package org.arroch.person;

public class Person {
    private String firstName;
    private String middleName;
    private String lastName;
    private String displayName;

    public Person(String firstName, String middleName, String lastName, String displayName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
