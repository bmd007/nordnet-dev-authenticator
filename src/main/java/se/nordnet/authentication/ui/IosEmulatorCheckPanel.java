package se.nordnet.authentication.ui;

import se.nordnet.authentication.IosEmulatorHelper;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.Optional;

import static se.nordnet.authentication.IosEmulatorHelper.getRunningIosEmulators;
import static se.nordnet.authentication.IosEmulatorHelper.isNordnetAppInstalled;

public class IosEmulatorCheckPanel extends JPanel {

    private static final String GREEN_TICK = "✅";
    private static final String RED_CROSS = "❌";

    public IosEmulatorCheckPanel() {
        super();
        setLayout(new GridLayout(4, 1, 1, 1));
        updatePanel();
    }

    private void updatePanel() {
        removeAll();

        Optional<String> emulatorIdExists = getRunningIosEmulators().stream().map(IosEmulatorHelper.IosEmulator::udid).findFirst();
        JLabel emulatorStatusLabel = new JLabel("IOS Emulator running: %s".formatted(emulatorIdExists.isPresent() ? GREEN_TICK : RED_CROSS));
        add(emulatorStatusLabel);

        JLabel nordnetAppInstalledLabel = new JLabel("Nordnet app installed: %s".formatted(isNordnetAppInstalled(emulatorIdExists.get()) ? GREEN_TICK : RED_CROSS));
        add(nordnetAppInstalledLabel);

        JButton checkAgainButton = new JButton("Check Again");
        checkAgainButton.addActionListener(e -> updatePanel());
        add(checkAgainButton);

        revalidate();
        repaint();
    }
}
