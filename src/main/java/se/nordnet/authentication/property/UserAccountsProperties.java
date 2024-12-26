package se.nordnet.authentication.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import se.nordnet.authentication.domain.UserAccount;

import java.util.List;


@ConfigurationProperties(prefix = "test.env.customer-ids")
public record UserAccountsProperties(List<UserAccount> userAccounts) {

}
