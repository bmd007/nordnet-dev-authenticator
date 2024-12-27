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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class DevAuthenticatorUserInterface {

    private final CustomerProperties customerProperties;
    private final JFrame frame = new JFrame("Nordnet Dev Authenticator");
    private final Desktop desktop = Desktop.getDesktop();
    private final JPanel loginButtonsPanel = new JPanel(new GridLayout(0, 1, 15, 25));

    public DevAuthenticatorUserInterface(CustomerProperties customerProperties) {
        this.customerProperties = customerProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        frame.getContentPane().add(createHeader(), BorderLayout.NORTH);
        frame.getContentPane().add(loginButtonsPanel, BorderLayout.CENTER);

        renderCustomerCards(customerProperties.customers().subList(0, 3));

        frame.setSize(1024, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new GridLayout(2, 2, 1, 1));
        header.setAlignmentX(0.5f);
        header.setBorder(BorderFactory.createEtchedBorder());
        header.add(new NordnetLogoPanel());
        header.add(new IosEmulatorCheckPanel());
        header.add(createSearchBar());

        return header;
    }

    private JPanel createSearchBar() {
        JPanel searchBar = new JPanel();
        searchBar.setLayout(new BoxLayout(searchBar, BoxLayout.X_AXIS));
        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(200, 30));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterCustomers(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterCustomers(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterCustomers(searchField.getText());
            }
        });
        searchBar.add(new JLabel("Search: "));
        searchBar.add(searchField);
        return searchBar;
    }

    private void filterCustomers(String query) {
        List<Customer> filteredCustomers = customerProperties.customers().stream()
                .filter(customer -> customer.name().toLowerCase().contains(query.toLowerCase()) ||
                        customer.id().toLowerCase().contains(query.toLowerCase()))
                .toList();
        filteredCustomers = filteredCustomers.isEmpty() ? customerProperties.customers().subList(0, 3) : filteredCustomers;
        renderCustomerCards(filteredCustomers);
    }

    private void renderCustomerCards(List<Customer> customers) {
        loginButtonsPanel.removeAll();
        customers.stream().map(this::createLoginCard).forEach(loginButtonsPanel::add);
        loginButtonsPanel.revalidate();
        loginButtonsPanel.repaint();
    }

    private JPanel createLoginCard(Customer customer) {
        JPanel row = new JPanel(null); // Set layout to null
        row.setPreferredSize(new Dimension(1024, 60)); // Set preferred size for the row

        JLabel label = createStyledLabel(customer.name() + " in " + customer.country());
        label.setBounds(10, 10, 200, 40); // Set bounds for the label
        row.add(label);

        JButton iosButton = createStyledButton("IOS Emulator");
        iosButton.setBounds(220, 10, 200, 40);
        iosButton.addActionListener(e -> openBrowserForLoginOnIosEmulator(customer));
        row.add(iosButton);

        JButton webAppNextTestButton = createStyledButton("Webapp next test");
        webAppNextTestButton.setBounds(440, 10, 200, 40);
        webAppNextTestButton.addActionListener(e -> openBrowserForLoginOnWebAppNextTestEnv(customer));
        row.add(webAppNextTestButton);

        JButton webappNextLocalButton = createStyledButton("Webapp next local");
        webappNextLocalButton.setBounds(650, 10, 200, 40);
        webappNextLocalButton.addActionListener(e -> openBrowserForLoginOnWebAppNextLocal(customer));
        row.add(webappNextLocalButton);

        return row;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        Font arial = new Font("Arial", Font.BOLD, 13);
        label.setFont(arial);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        Dimension buttonSize = new Dimension(200, 50); // Set your desired fixed size
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
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
