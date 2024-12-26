package se.nordnet.authentication.domain;

import java.util.Base64;
import java.util.Set;

public record Customer(String name, String id, String country) {
    private static final Set<String> COUNTRY_CODES = Set.of("SE", "NO", "FI", "DK");
    public Customer {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("CustomerId must not be null or blank");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("CountryCode must not be null or blank");
        }
        if (COUNTRY_CODES.stream().noneMatch(country::equalsIgnoreCase)) {
            throw new IllegalArgumentException("CountryCode must be one of " + COUNTRY_CODES);
        }
        country = country.toLowerCase();
    }
    public String getBase64Id() {
        return Base64.getEncoder().encodeToString(id.getBytes());
    }
}
