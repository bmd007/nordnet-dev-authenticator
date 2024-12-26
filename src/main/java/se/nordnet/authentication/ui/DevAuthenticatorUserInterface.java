package se.nordnet.authentication.ui;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import se.nordnet.authentication.domain.UserAccount;
import se.nordnet.authentication.property.UserAccountsProperties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.net.URI;
import java.util.Base64;

@Component
public class DevAuthenticatorUserInterface {

    private static final String AUTHORIZATION_BASE_URL = "https://login.microsoftonline.com/eae4202d-7ff0-44f1-b130-3afd5da7b347/oauth2/v2.0/authorize";

    private final UserAccountsProperties userAccountsProperties;

    private final JFrame frame = new JFrame("Dev Authenticator");
    private final Desktop desktop = Desktop.getDesktop();

    public DevAuthenticatorUserInterface(UserAccountsProperties userAccountsProperties) {
        this.userAccountsProperties = userAccountsProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 3)); // 1 row, 2 columns, 10px gaps

        JPanel userAccountsPanel = new JPanel(new GridLayout(0, 2, 10, 3)); // 2 columns, variable rows, 10px gaps
        for (UserAccount userAccount : userAccountsProperties.userAccounts()) {
            userAccountsPanel.add(createUserLoginCard(userAccount));
        }
        mainPanel.add(userAccountsPanel);

        frame.getContentPane().add(mainPanel);
        frame.setSize(1024, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createUserLoginCard(UserAccount userAccount) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(userAccount.name() + " in " + userAccount.countryCode());
        card.add(nameLabel);

        JButton iosButton = new JButton("IOS Emulator");
        iosButton.addActionListener(e -> openBrowserForLoginOnIosEmulator(userAccount));
        card.add(iosButton);

        JButton androidButton = new JButton("Android Simulator: TODO");
        card.add(androidButton);

        JButton webAppNextTestButton = new JButton("Webapp next test");
        webAppNextTestButton.addActionListener(e -> openBrowserForLoginOnWebAppNextTestEnv(userAccount));
        card.add(webAppNextTestButton);

        JButton webappNextLocalButton = new JButton("Webapp next local");
        webappNextLocalButton.addActionListener(e -> openBrowserForLoginOnWebAppNextLocal(userAccount));
        card.add(webappNextLocalButton);

        return card;
    }

    private void openBrowserForLoginOnIosEmulator(UserAccount userAccount) {
        String base64CustomerId = Base64.getEncoder().encodeToString(userAccount.customerId().getBytes());
        URI authorizationUri = getAuthorizationUrl(base64CustomerId, "http://localhost:9070", userAccount.countryCode());
        openBrowser(authorizationUri);
    }

    private void openBrowserForLoginOnWebAppNextTestEnv(UserAccount userAccount) {
        String base64CustomerId = Base64.getEncoder().encodeToString(userAccount.customerId().getBytes());
        URI authorizationUri =  getAuthorizationUrl(base64CustomerId, "https://www.nordnet-test.%s/login".formatted(userAccount.countryCode()), "MICROSOFT_ENTRAID_CARISMA");
        openBrowser(authorizationUri);
    }

    private void openBrowserForLoginOnWebAppNextLocal(UserAccount userAccount) {
        String base64CustomerId = Base64.getEncoder().encodeToString(userAccount.customerId().getBytes());
        URI authorizationUri =  getAuthorizationUrl(base64CustomerId, "https://www.nordnet-local.%s:8081/login".formatted(userAccount.countryCode()), "MICROSOFT_ENTRAID_CARISMA");
        openBrowser(authorizationUri);
    }

    private void openBrowser(URI url) {
        try {
            desktop.browse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private URI getAuthorizationUrl(String nonce, String redirectUri, String state) {
        return UriComponentsBuilder.fromUriString(AUTHORIZATION_BASE_URL)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", "fe88cb91-1d7f-4d8e-a4e7-b2287bce567b")
                .queryParam("response_type", "code")
                .queryParam("nonce", nonce)
                .queryParam("scope", "api://fe88cb91-1d7f-4d8e-a4e7-b2287bce567b/Read openid")
                .queryParam("state", state)
                .build().toUri();
    }
}
