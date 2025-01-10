package se.nordnet.authentication.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.nordnet.authentication.IosSimulatorHelper;
import se.nordnet.authentication.type.OidcState;

import java.util.List;
import java.util.Optional;

import static se.nordnet.authentication.IosSimulatorHelper.executeCommand;
import static se.nordnet.authentication.IosSimulatorHelper.nordnetAppContainingIosSimulatorIds;

@Slf4j
@RestController
public class ProxyResource {

    public static final String CLOSE_TAB_HTML = """
            <html>
            <head>
                <title>IOS Simulator</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                    .message { font-size: 20px; margin-top: 20px; }
                </style>
                <script>
                    let countdown = 5;
                    function updateTimer() {
                        document.getElementById('timer').innerText = countdown;
                        if (countdown > 0) {
                            countdown--;
                            setTimeout(updateTimer, 1000);
                        } else {
                            window.close();
                        }
                    }
                    window.onload = function() {
                        updateTimer();
                    };
                </script>
            </head>
            <body>
                <h1>Check your running/booted iOS simulator</h1>
                <p class="message">This window/tab will be closed automatically in <span id="timer">5</span> seconds.</p>
            </body>
            </html>
            """;

    // TODO support Android
    @GetMapping(produces = "text/html")
    public String openSimulatorWithAuthzCode(@RequestParam String code, @RequestParam String state) {
        OidcState oidcState = OidcState.fromBase64Json(state);

        List<String> iosSimulators_WithNordetApp_id = nordnetAppContainingIosSimulatorIds();
        if (iosSimulators_WithNordetApp_id.isEmpty()) {
            throw new IllegalStateException("No iOS simulator with Nordnet app installed found");
        }
        String targetIosSimulatorId = Optional.ofNullable(oidcState)
                .map(OidcState::targetIosSimulatorId)
                .filter(iosSimulators_WithNordetApp_id::contains)
                .orElseGet(() -> iosSimulators_WithNordetApp_id.get(0));
        try {
            terminateNordnetAppOnIosSimulator(targetIosSimulatorId);
        } catch (Exception e) {
            log.error("Error terminating Nordnet app on iOS simulator", e);
        }
        waitForNordnetAppTermination(targetIosSimulatorId);
        lunchNordnetApp(code, oidcState.country(), targetIosSimulatorId);

        return CLOSE_TAB_HTML;
    }

    private static void waitForNordnetAppTermination(String targetIosSimulatorId) {
        while (!IosSimulatorHelper.isNordeAppRunning(targetIosSimulatorId)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Error waiting for Nordnet app to terminate", e);
            }
        }
    }

    private static void lunchNordnetApp(String code, String country, String udid) {
        executeCommand("""
                xcrun simctl launch %s com.nordnet.Nordnet -entraIdAuthzCode "%s" -countryCode "%s"
                """.formatted(udid, code, country).stripIndent());
    }

    private static void terminateNordnetAppOnIosSimulator(String iosSimulatorId) {
        executeCommand("xcrun simctl terminate %s com.nordnet.Nordnet".formatted(iosSimulatorId));
    }
}
