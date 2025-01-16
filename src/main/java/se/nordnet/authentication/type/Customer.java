package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Base64;
import java.util.Set;

public record Customer(String alias, String id, String country) {
    private static final Set<String> COUNTRY_CODES = Set.of("se", "no", "fi", "dk");

    public Customer {
        if (alias.isBlank()) {
            throw new IllegalArgumentException("Alias must not be blank");
        }
        if (id.isBlank()) {
            throw new IllegalArgumentException("Id must not be blank");
        }
        if (COUNTRY_CODES.stream().noneMatch(country::equalsIgnoreCase)) {
            throw new IllegalArgumentException("CountryCode %s must be one of %s".formatted(country, COUNTRY_CODES));
        }
        country = country.toLowerCase();
    }

    @JsonIgnore
    public String getBase64Id() {
        return Base64.getEncoder().encodeToString(id.getBytes());
    }
}
