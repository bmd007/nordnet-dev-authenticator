package se.nordnet.authentication.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import se.nordnet.authentication.domain.Customer;
import se.nordnet.authentication.property.CustomerProperties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URI;

@Slf4j
@Component
public class DevAuthenticatorUserInterface {

    private final CustomerProperties customerProperties;

    private final JFrame frame = new JFrame("Nordnet Dev Authenticator");
    private final Desktop desktop = Desktop.getDesktop();

    public DevAuthenticatorUserInterface(CustomerProperties customerProperties) {
        this.customerProperties = customerProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        JPanel userAccountsPanel = new JPanel(new GridLayout(0, 2, 15, 25));
        customerProperties.customers()
                .stream().map(this::createLoginCard)
                .forEach(userAccountsPanel::add);
        frame.getContentPane().add(userAccountsPanel);

        frame.setSize(1024, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createLoginCard(Customer customer) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(0.5f);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        card.add(createStyledLabel(customer.name() + " in " + customer.country()));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton iosButton = createStyledButton("IOS Emulator");
        iosButton.addActionListener(e -> openBrowserForLoginOnIosEmulator(customer));
        buttonPanel.add(iosButton);

        JButton androidButton = createStyledButton("Android Simulator: TODO");
        buttonPanel.add(androidButton);

        JButton webAppNextTestButton = createStyledButton("Webapp next test");
        webAppNextTestButton.addActionListener(e -> openBrowserForLoginOnWebAppNextTestEnv(customer));
        buttonPanel.add(webAppNextTestButton);

        JButton webappNextLocalButton = createStyledButton("Webapp next local");
        webappNextLocalButton.addActionListener(e -> openBrowserForLoginOnWebAppNextLocal(customer));
        buttonPanel.add(webappNextLocalButton);

        card.add(buttonPanel);

        return card;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(0.5f);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(50, 100, 150));
        label.setBorder(BorderFactory.createRaisedBevelBorder());
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(0.5f);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        return button;
    }

    private void openBrowserForLoginOnIosEmulator(Customer customer) {
        URI authorizationUri = getAuthorizationUrl(customer.getBase64Id(), "http://localhost:9070", customer.country());
        openBrowser(authorizationUri);
    }

    private void openBrowserForLoginOnWebAppNextTestEnv(Customer customer) {
        URI authorizationUri = getAuthorizationUrl(customer.getBase64Id(), "https://www.nordnet-test.%s/login".formatted(customer.country()), "MICROSOFT_ENTRAID_CARISMA");
        openBrowser(authorizationUri);
    }

    private void openBrowserForLoginOnWebAppNextLocal(Customer customer) {
        URI authorizationUri = getAuthorizationUrl(customer.getBase64Id(), "https://www.nordnet-local.%s:8081/login".formatted(customer.country()), "MICROSOFT_ENTRAID_CARISMA");
        openBrowser(authorizationUri);
    }

    private void openBrowser(URI url) {
        try {
            desktop.browse(url);
        } catch (Exception e) {
            log.error("Failed to open browser", e);
        }
    }

    private URI getAuthorizationUrl(String nonce, String redirectUri, String state) {
        return UriComponentsBuilder.fromUriString("https://login.microsoftonline.com/eae4202d-7ff0-44f1-b130-3afd5da7b347/oauth2/v2.0/authorize")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", "fe88cb91-1d7f-4d8e-a4e7-b2287bce567b")
                .queryParam("response_type", "code")
                .queryParam("nonce", nonce)
                .queryParam("scope", "api://fe88cb91-1d7f-4d8e-a4e7-b2287bce567b/Read openid")
                .queryParam("state", state)
                .build().toUri();
    }
}
