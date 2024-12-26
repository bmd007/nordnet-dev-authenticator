package se.nordnet.authentication.domain;

public record UserAccount(String name, String customerId, CountryCode countryCode) {
}
