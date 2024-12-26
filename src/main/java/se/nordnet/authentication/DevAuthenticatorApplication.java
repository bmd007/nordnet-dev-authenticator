package se.nordnet.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;
import org.springframework.web.util.UriComponentsBuilder;
import se.nordnet.authentication.domain.UserAccount;
import se.nordnet.authentication.property.UserAccountsProperties;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
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

    private static final String authorizationBaseUrl = "https://login.microsoftonline.com/eae4202d-7ff0-44f1-b130-3afd5da7b347/oauth2/v2.0/authorize";

    @Autowired
    private UserAccountsProperties userAccountsProperties;

    JFrame frame = new JFrame("Dev Authenticator");
    Desktop desktop = Desktop.getDesktop();

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columns, variable rows, 10px gaps
        for (UserAccount userAccount : userAccountsProperties.userAccounts()) {
            panel.add(createCard(userAccount));
        }
        frame.getContentPane().add(panel);
        frame.setSize(1024, 800);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createCard(UserAccount userAccount) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(userAccount.name());
        card.add(nameLabel);
        JButton button = new JButton("IOS Emulator: "+ userAccount.countryCode());
        button.addActionListener(e -> openBrowser(getAuthorizationUrlForLoginOnEmulator(userAccount)));
        card.add(button);
        card.add(button);

        JButton button2 = new JButton("Webapp next test: " + userAccount.countryCode());
        button2.addActionListener(e -> openBrowser(getAuthorizationUrlForLoginOnWebAppNextTestEnv(userAccount)));
        card.add(button2);

        JButton button3 = new JButton("Webapp next local: " + userAccount.countryCode());
        button3.addActionListener(e -> openBrowser(getAuthorizationUrlForLoginOnWebAppNextLocal(userAccount)));
        card.add(button3);

        return card;
    }

    private void openBrowser(URI url) {
        try {
            desktop.browse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private URI getAuthorizationUrlForLoginOnEmulator(UserAccount userAccount) {
        String base64CustomerId = Base64.getEncoder().encodeToString(userAccount.customerId().getBytes());
        String lowerCodeCountryCode = userAccount.countryCode().toString().toLowerCase();
        return getAuthorizationUrl(base64CustomerId, "http://localhost:9070", lowerCodeCountryCode);
    }

    private URI getAuthorizationUrlForLoginOnWebAppNextTestEnv(UserAccount userAccount) {
        String base64CustomerId = Base64.getEncoder().encodeToString(userAccount.customerId().getBytes());
        String lowerCodeCountryCode = userAccount.countryCode().toString().toLowerCase();
        return getAuthorizationUrl(base64CustomerId, "https://www.nordnet-test.%s/login".formatted(lowerCodeCountryCode), "MICROSOFT_ENTRAID_CARISMA");
    }

    private URI getAuthorizationUrlForLoginOnWebAppNextLocal(UserAccount userAccount) {
        String base64CustomerId = Base64.getEncoder().encodeToString(userAccount.customerId().getBytes());
        String lowerCodeCountryCode = userAccount.countryCode().toString().toLowerCase();
        return getAuthorizationUrl(base64CustomerId, "https://www.nordnet-local.%s:8081/login".formatted(lowerCodeCountryCode), "MICROSOFT_ENTRAID_CARISMA");
    }

    private URI getAuthorizationUrl(String nonce, String redirectUri, String state) {
        return UriComponentsBuilder.fromUriString(authorizationBaseUrl)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", "fe88cb91-1d7f-4d8e-a4e7-b2287bce567b")
                .queryParam("response_type", "code")
                .queryParam("nonce", nonce)
                .queryParam("scope", "api://fe88cb91-1d7f-4d8e-a4e7-b2287bce567b/Read openid")
                .queryParam("state", state)
                .build().toUri();
    }
}
