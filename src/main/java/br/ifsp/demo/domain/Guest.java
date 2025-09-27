package br.ifsp.demo.domain;

public class Guest {

    private final String name;
    private final Integer age;
    private final String cpf;

    public Guest(String name, Integer age,  String cpf) {
        this.name = name;
        this.age = age;
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}
