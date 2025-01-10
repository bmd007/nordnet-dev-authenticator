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
import static se.nordnet.authentication.IosSimulatorHelper.iosSimulatorsWithNordnetAppInstalled;

@Slf4j
@RestController
public class ProxyResource {

    // TODO support Android
    @GetMapping(produces = "text/html")
    public String openSimulatorWithAuthzCode(@RequestParam String code, @RequestParam String state) {
        OidcState oidcState = OidcState.fromBase64Json(state);

        List<String> iosSimulatorWithNordetApp = iosSimulatorsWithNordnetAppInstalled()
                .stream()
                .map(IosSimulatorHelper.IosSimulator::udid)
                .toList();
        if (iosSimulatorWithNordetApp.isEmpty()) {
            throw new IllegalStateException("No iOS simulator with Nordnet app installed found");
        }
        String targetIosSimulatorId = Optional.ofNullable(oidcState)
                .map(OidcState::targetIosSimulatorId)
                .filter(iosSimulatorWithNordetApp::contains)
                .orElseGet(() -> iosSimulatorWithNordetApp.get(0));
        try {
            terminateNordnetAppOnIosSimulator(targetIosSimulatorId);
        } catch (Exception e) {
            log.error("Error terminating Nordnet app on iOS simulator", e);
        }
        //todo wait until nordnet app is terminated
        while (IosSimulatorHelper.isNordeAppRunning(targetIosSimulatorId)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Error waiting for Nordnet app to terminate", e);
            }
        }
        lunchNordnetApp(code, oidcState.country(), targetIosSimulatorId);

        return """
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
    }

    private static void lunchNordnetApp(String code, String country, String udid) {
        executeCommand("""
                xcrun simctl launch %s com.nordnet.Nordnet -entraIdAuthzCode "%s" -countryCode "%s"
                """.stripIndent().formatted(udid, code, country));
    }

    private static void terminateNordnetAppOnIosSimulator(String iosSimulatorId) {
        executeCommand("xcrun simctl terminate %s com.nordnet.Nordnet".formatted(iosSimulatorId));
    }
}
