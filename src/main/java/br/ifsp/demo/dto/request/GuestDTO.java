package br.ifsp.demo.dto.request;

import jakarta.validation.constraints.*;

public record GuestDTO(
        @NotBlank String name,
        @Min(0) int age,
        @NotBlank String cpf,
        boolean isVip
) {}

