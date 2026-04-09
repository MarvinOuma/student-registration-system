package com.school.studentregistration.model;

public abstract class Person {
    private String name;

    protected Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // This is overridden in child classes.
    public abstract String getDisplayRole();
}
