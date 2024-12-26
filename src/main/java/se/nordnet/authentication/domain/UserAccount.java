package se.nordnet.authentication.domain;

import java.util.Set;

public record UserAccount(String name, String customerId, String countryCode) {
    private static final Set<String> COUNTRY_CODES = Set.of("SE", "NO", "FI", "DK");
    public UserAccount {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("CustomerId must not be null or blank");
        }
        if (countryCode == null || countryCode.isBlank()) {
            throw new IllegalArgumentException("CountryCode must not be null or blank");
        }
        if (!COUNTRY_CODES.contains(countryCode)) {
            throw new IllegalArgumentException("CountryCode must be one of " + COUNTRY_CODES);
        }
        countryCode = countryCode.toUpperCase();
    }
}
