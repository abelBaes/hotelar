package br.ifsp.demo.dto.request;

import jakarta.validation.constraints.*;

public record ExtraServiceDTO(
        @NotBlank String description,
        @DecimalMin("0.0") double value
) {}
