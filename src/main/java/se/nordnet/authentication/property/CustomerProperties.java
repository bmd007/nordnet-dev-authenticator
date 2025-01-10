package se.nordnet.authentication.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import se.nordnet.authentication.type.Customer;

import java.util.List;

@ConfigurationProperties(prefix = "test.env")
public record CustomerProperties(List<Customer> customers) {
}
