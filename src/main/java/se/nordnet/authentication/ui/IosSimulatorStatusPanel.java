package se.nordnet.authentication.ui;

import se.nordnet.authentication.type.IosSimulator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.Optional;

import static se.nordnet.authentication.IosSimulatorHelper.runningIosSimulators;
import static se.nordnet.authentication.IosSimulatorHelper.isNordnetAppInstalled;

public class IosSimulatorStatusPanel extends JPanel {

    private static final String GREEN_TICK = "✅";
    private static final String RED_CROSS = "❌";

    public IosSimulatorStatusPanel() {
        super();
        setLayout(new GridLayout(4, 1, 1, 1));
        updatePanel();
    }

    private void updatePanel() {
        removeAll();

        Optional<IosSimulator> anyRunningIosSimulator = runningIosSimulators().stream().findFirst();
        JLabel simulatorStatusLabel = new JLabel("iOS Simulator running: %s".formatted(anyRunningIosSimulator.isPresent() ? GREEN_TICK : RED_CROSS));
        add(simulatorStatusLabel);

        JLabel nordnetAppInstalledLabel = new JLabel("Nordnet app installed: %s".formatted(anyRunningIosSimulator.isPresent() && isNordnetAppInstalled(anyRunningIosSimulator.get()) ? GREEN_TICK : RED_CROSS));
        add(nordnetAppInstalledLabel);

        JButton checkAgainButton = new JButton("Check Again");
        checkAgainButton.setForeground(java.awt.Color.BLUE);
        checkAgainButton.addActionListener(e -> updatePanel());
        add(checkAgainButton);

        revalidate();
        repaint();
    }
}
