package se.nordnet.authentication.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import se.nordnet.authentication.IosSimulatorHelper;
import se.nordnet.authentication.property.CustomerProperties;
import se.nordnet.authentication.type.Customer;
import se.nordnet.authentication.type.IosSimulator;
import se.nordnet.authentication.type.TargetEnvironment;
import se.nordnet.authentication.type.OidcState;
import se.nordnet.authentication.type.WebAppNextStaging;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class StartScreen {
    // Custom colors
    private static final Color NORDNET_BLUE = new Color(0, 91, 169);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color BUTTON_HOVER = new Color(0, 71, 149);

    private final CustomerProperties customerProperties;
    private final JCheckBox loginOnAllSimulatorsCheckBox = new JCheckBox("Login on all iOS simulators");

    private final JFrame frame = new JFrame("Nordnet Dev Authenticator");
    private final Desktop desktop = Desktop.getDesktop();
    private final JPanel mainPanel = new JPanel();
    private final JPanel customerCardsPanel = new JPanel();

    public StartScreen(CustomerProperties customerProperties) {
        this.customerProperties = customerProperties;
        setupMainFrame();
    }

    private void setupMainFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1200, 800));

        // Set custom look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            customizeUIComponents();
        } catch (Exception e) {
            log.error("Failed to set look and feel", e);
        }

        // Setup main panel
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Setup cards panel
        customerCardsPanel.setLayout(new BoxLayout(customerCardsPanel, BoxLayout.Y_AXIS));
        customerCardsPanel.setBackground(BACKGROUND_COLOR);

        // Create and add components
        JPanel headerPanel = createHeader();
        JScrollPane scrollPane = new JScrollPane(customerCardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add main panel to frame
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Initial render
        renderCustomerLoginCards(customerProperties.customers());
    }

    private void customizeUIComponents() {
        // Customize button appearance
        UIManager.put("Button.background", NORDNET_BLUE);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));

        // Customize text fields
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 20));
        header.setBackground(CARD_BACKGROUND);
        header.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        NordnetLogoPanel logoPanel = new NordnetLogoPanel();
        logoPanel.setBackground(CARD_BACKGROUND);

        IosSimulatorStatusPanel iosStatusPanel = new IosSimulatorStatusPanel();
        iosStatusPanel.setBackground(CARD_BACKGROUND);

        JPanel topSection = new JPanel(new BorderLayout(20, 0));
        topSection.setBackground(CARD_BACKGROUND);
        topSection.add(logoPanel, BorderLayout.WEST);
        topSection.add(iosStatusPanel, BorderLayout.EAST);

        header.add(topSection, BorderLayout.NORTH);
        header.add(createSearchPanel(), BorderLayout.SOUTH);
        header.add(createSettingsPanel(), BorderLayout.EAST);

        return header;
    }

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        settingsPanel.setBackground(CARD_BACKGROUND);
        loginOnAllSimulatorsCheckBox.setBackground(CARD_BACKGROUND);
        loginOnAllSimulatorsCheckBox.setSelected(false);
        settingsPanel.add(loginOnAllSimulatorsCheckBox);
        return settingsPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(CARD_BACKGROUND);

        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.putClientProperty("JTextField.placeholderText", "Search customers...");

        // Use a Unicode search icon instead of an image resource
        JLabel searchIcon = new JLabel("\uD83D\uDD0D");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchIcon.setForeground(new Color(128, 128, 128));

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);

        // Add search functionality
        searchField.getDocument().addDocumentListener(new SearchDocumentListener(this::filterCustomers));

        return searchPanel;
    }

    private JPanel createCustomerCard(Customer customer) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        card.add(createCustomerInfoPanel(customer), BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(CARD_BACKGROUND);
        buttonsPanel.add(loginButton( "iOS Simulator", e1 -> openBrowserForLoginOnIosSimulator(customer)));
        buttonsPanel.add(loginButton( "Webapp next test", e -> openBrowserForLoginOnWebAppNextTestEnv(customer)));
        buttonsPanel.add(loginButton( "Webapp next local", e -> openBrowserForLoginOnWebAppNextLocal(customer)));
        buttonsPanel.add(webAppNextStagingLoginSection(customer));
//        buttonsPanel.add(loginButton( "Android emulator", e -> {}));
        card.add(buttonsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel webAppNextStagingLoginSection(Customer customer) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JTextField prIdentifierField = new JTextField(10);
        prIdentifierField.setPreferredSize(new Dimension(100, 35));
        prIdentifierField.putClientProperty("JTextField.placeholderText", "Web app next PR Identifier or Alias (oidc, ace-oidc, webauthn)");
        prIdentifierField.setText("pr-6061");

        JLabel prLabel = new JLabel("PR identifier");
        prLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prLabel.setForeground(new Color(128, 128, 128));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(CARD_BACKGROUND);
        inputPanel.add(prLabel, BorderLayout.WEST);
        inputPanel.add(prIdentifierField, BorderLayout.CENTER);

        JButton webappNextStagingLoginButton = loginButton("Webapp next staging", e -> {
            String prIdentifier = prIdentifierField.getText();
            if (!prIdentifier.isEmpty()) {
                String state = OidcState.forWebAppNextStaging(customer.country(), new WebAppNextStaging(prIdentifier)).getBase64Json();
                URI authorizationUri = getAuthorizationUrl(customer.getBase64Id(), "http://localhost:9070", state);
                openBrowser(authorizationUri);
            } else {
                log.error("PR Number is required for Webapp next staging login");
                //todo warn user using a pop up
            }
        });

        panel.add(webappNextStagingLoginButton, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerInfoPanel(Customer customer) {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);

        JLabel nameLabel = new JLabel(customer.name());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel countryLabel = new JLabel(customer.country());
        countryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        countryLabel.setForeground(new Color(128, 128, 128));

        JLabel customerIdLabel = new JLabel(customer.id());
        customerIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerIdLabel.setForeground(new Color(128, 128, 128));

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(countryLabel, BorderLayout.CENTER);
        infoPanel.add(customerIdLabel, BorderLayout.SOUTH);
        return infoPanel;
    }

    private JButton loginButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(170, 35));
        button.setBorderPainted(true);
        button.setBorder(new LineBorder(NORDNET_BLUE, 5));
        button.setBackground(NORDNET_BLUE);
        button.setForeground(Color.BLACK);
        button.addActionListener(listener);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(NORDNET_BLUE);
            }
        });
        return button;
    }

    private void filterCustomers(String query) {
        List<Customer> filteredCustomers = customerProperties.customers().stream()
                .filter(customer -> customer.name().toLowerCase().contains(query.toLowerCase()) ||
                        customer.id().toLowerCase().contains(query.toLowerCase()))
                .toList();
        filteredCustomers = filteredCustomers.isEmpty() ? customerProperties.customers().subList(0, 3) : filteredCustomers;
        renderCustomerLoginCards(filteredCustomers);
    }

    private void renderCustomerLoginCards(List<Customer> customers) {
        customerCardsPanel.removeAll();
        customers.forEach(customer -> {
            customerCardsPanel.add(createCustomerCard(customer));
            customerCardsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between cards
        });
        customerCardsPanel.revalidate();
        customerCardsPanel.repaint();
    }

    private void openBrowserForLoginOnIosSimulator(Customer customer) {
        List<IosSimulator> targetIosSimulators = loginOnAllSimulatorsCheckBox.isSelected() ?
                IosSimulatorHelper.runningSimulatorsWithNordnetApp() :
                List.of(IosSimulatorHelper.runningSimulatorsWithNordnetApp().get(0));
        targetIosSimulators.forEach(iosSimulator -> {
            String state = OidcState.forIosSimulator(customer.country(), iosSimulator).getBase64Json();
            URI authorizationUri = getAuthorizationUrl(customer.getBase64Id(), "http://localhost:9070", state);
            openBrowser(authorizationUri);
        });
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
                .build()
                .toUri();
    }

    class SearchDocumentListener implements javax.swing.event.DocumentListener {
        private final java.util.function.Consumer<String> searchCallback;

        public SearchDocumentListener(java.util.function.Consumer<String> searchCallback) {
            this.searchCallback = searchCallback;
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            triggerSearch(e);
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            triggerSearch(e);
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            triggerSearch(e);
        }

        private void triggerSearch(javax.swing.event.DocumentEvent e) {
            try {
                String text = e.getDocument().getText(0, e.getDocument().getLength());
                searchCallback.accept(text);
            } catch (javax.swing.text.BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
}
