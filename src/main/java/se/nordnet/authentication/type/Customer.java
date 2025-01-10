package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

import java.util.Base64;
import java.util.Set;

public record Customer(@NotBlank String name, @NotBlank String id, @NotBlank String country) {
    private static final Set<String> COUNTRY_CODES = Set.of("SE", "NO", "FI", "DK");
    public Customer {
        if (COUNTRY_CODES.stream().noneMatch(country::equalsIgnoreCase)) {
            throw new IllegalArgumentException("CountryCode must be one of " + COUNTRY_CODES);
        }
        country = country.toUpperCase();
    }

    @JsonIgnore
    public String getBase64Id() {
        return Base64.getEncoder().encodeToString(id.getBytes());
    }
}
