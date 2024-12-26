package se.nordnet.authentication;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties(prefix = "test.env.customer-ids")
public record UserAccountsProperties(List<UserAccount> userAccounts) {
    record UserAccount(String name, String customerId) {
    }
}
