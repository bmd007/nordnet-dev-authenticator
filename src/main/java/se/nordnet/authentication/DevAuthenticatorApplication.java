package se.nordnet.authentication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class DevAuthenticatorApplication {
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DevAuthenticatorApplication.class);
        builder.headless(false);
        builder.bannerMode(org.springframework.boot.Banner.Mode.OFF);
        builder.run(args);
    }
}
