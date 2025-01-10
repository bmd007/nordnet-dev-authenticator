package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

import java.util.Base64;
import java.util.Set;

public record Customer(@NotBlank String name, @NotBlank String id, @NotBlank String country) {
    private static final Set<String> COUNTRY_CODES = Set.of("se", "no", "fi", "dk");
    public Customer {
        country = country.toLowerCase();
        if (COUNTRY_CODES.contains(country)) {
            throw new IllegalArgumentException("CountryCode must be one of " + COUNTRY_CODES);
        }
    }

    @JsonIgnore
    public String getBase64Id() {
        return Base64.getEncoder().encodeToString(id.getBytes());
    }
}
