package se.nordnet.authentication.ui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Image;
import java.util.Objects;

public class NordnetLogoPanel extends JPanel {

    public NordnetLogoPanel() {
        super();
        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/nordnet-logo.png")));
        Image image = logoIcon.getImage();
        Image scaledImage = image.getScaledInstance(logoIcon.getIconWidth() / 3, logoIcon.getIconHeight() / 3, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(0f);
        logoLabel.setSize(30, 30);
        this.add(logoLabel);
    }
}
