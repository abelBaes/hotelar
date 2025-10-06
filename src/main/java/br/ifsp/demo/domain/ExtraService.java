package br.ifsp.demo.domain;

public class ExtraService {
    private final String description;
    private final double value;

    public ExtraService(String description, double value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }
}
