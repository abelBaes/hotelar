package br.ifsp.demo.domain;

public class Guest {

    private final String name;
    private final Integer age;

    public Guest(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}
