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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


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
    java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        JPanel mainPanel = new JPanel(new java.awt.GridLayout(1, 2, 10, 10)); // 1 row, 2 columns, 10px gaps

        JPanel userAccountsPanel = new JPanel(new java.awt.GridLayout(0, 2, 10, 10)); // 2 columns, variable rows, 10px gaps
        for (UserAccount userAccount : userAccountsProperties.userAccounts()) {
            userAccountsPanel.add(createCard(userAccount));
        }
        mainPanel.add(userAccountsPanel);

        mainPanel.add(createEmulatorListPanel());

        frame.getContentPane().add(mainPanel);
        frame.setSize(1024, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createEmulatorListPanel() {
        JPanel emulatorPanel = new JPanel();
        emulatorPanel.setLayout(new BoxLayout(emulatorPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Running iOS Emulators:");
        emulatorPanel.add(titleLabel);

        List<String> emulators = getRunningIOSEmulators();
        for (String emulator : emulators) {
            JLabel emulatorLabel = new JLabel(emulator);
            emulatorPanel.add(emulatorLabel);
        }

        return emulatorPanel;
    }

    public List<String> getRunningIOSEmulators() {
        List<String> emulators = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("xcrun simctl list devices booted");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("(Booted)")) {
                    emulators.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emulators;
    }

    private JPanel createCard(UserAccount userAccount) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(userAccount.name() + " in " + userAccount.countryCode());
        card.add(nameLabel);

        JButton button = new JButton("IOS Emulator");
        button.addActionListener(e -> openBrowser(getAuthorizationUrlForLoginOnEmulator(userAccount)));
        card.add(button);
        card.add(button);

        JButton button2 = new JButton("Webapp next test");
        button2.addActionListener(e -> openBrowser(getAuthorizationUrlForLoginOnWebAppNextTestEnv(userAccount)));
        card.add(button2);

        JButton button3 = new JButton("Webapp next local");
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
