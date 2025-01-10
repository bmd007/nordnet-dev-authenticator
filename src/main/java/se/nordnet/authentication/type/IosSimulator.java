package se.nordnet.authentication.type;


import jakarta.validation.constraints.NotBlank;

public record IosSimulator(@NotBlank String udid, @NotBlank String name) {
}
