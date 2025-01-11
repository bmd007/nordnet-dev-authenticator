package se.nordnet.authentication.type;


import jakarta.validation.constraints.NotBlank;

public record AndroidEmulator(@NotBlank String id, @NotBlank String name) {
}
