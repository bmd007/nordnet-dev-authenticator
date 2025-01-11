package se.nordnet.authentication.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.nordnet.authentication.IosSimulatorHelper;
import se.nordnet.authentication.type.IosSimulator;
import se.nordnet.authentication.type.OidcState;

import java.util.List;

@Slf4j
@RestController
public class ProxyResource {

    public static final String CLOSE_TAB_HTML = """
            <html>
            <head>
                <title>iOS Simulator</title>
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
        if (!oidcState.targetIosSimulators().isEmpty()) {
            lunchNordnetAppOnIosSimulators(code, oidcState);
        }
        return CLOSE_TAB_HTML;
    }

    private void lunchNordnetAppOnIosSimulators(String code, OidcState oidcState) {
        List<IosSimulator> iosSimulatorsWithNordnetApp = IosSimulatorHelper.runningSimulatorsWithNordnetApp();
        if (iosSimulatorsWithNordnetApp.isEmpty()) {
            throw new IllegalStateException("Running iOS simulator, with Nordnet app installed, NOT found!");
        }
        oidcState.targetIosSimulators()
                .stream()
                .filter(iosSimulatorsWithNordnetApp::contains)
                .distinct()
                .forEach(iosSimulator -> {
                    // unfortunately this doesn't result in log out! manually log out in the app required
                    IosSimulatorHelper.terminateNordnetApp(iosSimulator);
                    IosSimulatorHelper.lunchNordnetApp(code, oidcState.country(), iosSimulator);
                });
    }
}
