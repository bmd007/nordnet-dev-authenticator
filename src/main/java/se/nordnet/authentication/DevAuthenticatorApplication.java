package se.nordnet.authentication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@SpringBootApplication
public class DevAuthenticatorApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DevAuthenticatorApplication.class);
        builder.headless(false);
        builder.bannerMode(org.springframework.boot.Banner.Mode.OFF);
        builder.run(args);
    }

    @GetMapping
    public String openSimulatorWithAuthzCode(@RequestParam String code) throws IOException {
        String commandToLunchSimulator = """
                xcrun simctl launch booted com.nordnet.Nordnet -entraIdAuthzCode "%s"
                """.formatted(code);
		Process p = Runtime.getRuntime().exec(commandToLunchSimulator);
		return p.info().toString();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        String url = "https://login.microsoftonline.com/eae4202d-7ff0-44f1-b130-3afd5da7b347/oauth2/v2.0/authorize?redirect_uri=http%3A%2F%2Flocalhost%3A9070&client_id=fe88cb91-1d7f-4d8e-a4e7-b2287bce567b&response_type=code&nonce=ZTlhN2I4OWQtMWEyYi00ZjQ0LTg3YWMtNjk4Yzg5YmM3ZTEx&scope=api://fe88cb91-1d7f-4d8e-a4e7-b2287bce567b/Read%20openid&state=MICROSOFT_ENTRAID_DEV_AUTHENTICATOR";
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
