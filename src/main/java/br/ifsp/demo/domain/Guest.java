package br.ifsp.demo.domain;

public class Guest {

    private final String name;
    private final Integer age;
    private final String cpf;
    private final boolean isVip;

    public Guest(String name, Integer age,  String cpf) {
        this.name = name;
        this.age = age;
        this.cpf = cpf;
        this.isVip = false;
    }

    public Guest(String name, Integer age, String cpf, boolean isVip) {
        this.name = name;
        this.age = age;
        this.cpf = cpf;
        this.isVip = isVip;
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

    public boolean isVip() {
        return isVip;
    }
}
