package se.nordnet.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DevAuthenticatorApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DevAuthenticatorApplication.class);
        builder.headless(false);
        builder.bannerMode(org.springframework.boot.Banner.Mode.OFF);
        builder.run(args);
    }

    @Autowired
    private UserAccountsProperties userAccountsProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Dev Authenticator");
            javax.swing.JButton button = new javax.swing.JButton("login in simulator as robson");

            button.addActionListener(e -> {
                Desktop desktop = Desktop.getDesktop()  ;
                try {
                    URI url = getAuthorizationUrl(userAccountsProperties.userAccounts().get(0).customerId());
                    desktop.browse(url);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            frame.getContentPane().add(button);
            frame.setSize(1024, 800);
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private URI getAuthorizationUrl(String customerId) {
        String base64CustomerId = Base64.getEncoder().encodeToString(customerId.getBytes());
        String urlTemplate = "https://login.microsoftonline.com/eae4202d-7ff0-44f1-b130-3afd5da7b347/oauth2/v2.0/authorize?redirect_uri=http://localhost:9070&client_id=fe88cb91-1d7f-4d8e-a4e7-b2287bce567b&response_type=code&nonce=%s&scope=api://fe88cb91-1d7f-4d8e-a4e7-b2287bce567b/Read openid&state=MICROSOFT_ENTRAID_DEV_AUTHENTICATOR";
        String urlWithNonce = String.format(urlTemplate, base64CustomerId);
        //todo use UriComponentsBuilder to add query params instead of a long string
        return UriComponentsBuilder.fromHttpUrl(urlWithNonce).build().toUri();
    }
}
