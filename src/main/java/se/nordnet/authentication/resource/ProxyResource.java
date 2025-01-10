package se.nordnet.authentication.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static se.nordnet.authentication.IosSimulatorHelper.simulatorWithNordnetAppInstalled;
import static se.nordnet.authentication.IosSimulatorHelper.executeCommand;

@RestController
public class ProxyResource {

    private static final String LUNCH_IOS_APP_COMMAND_TEMPLATE = """
            xcrun simctl launch %s com.nordnet.Nordnet -entraIdAuthzCode "%s" -countryCode "%s"
            """.stripIndent();
    public static final String TERMINATE_NORDNET_IOS_APP_COMMAND_TEMPLATE = "xcrun simctl terminate %s com.nordnet.Nordnet";

    //TODO support Android: multiplex on path or state!
    @GetMapping(produces = "text/html")
    public String openSimulatorWithAuthzCode(@RequestParam String code, @RequestParam String state) {
        String udid = simulatorWithNordnetAppInstalled().udid();
        executeCommand(TERMINATE_NORDNET_IOS_APP_COMMAND_TEMPLATE.formatted(udid));
        executeCommand(LUNCH_IOS_APP_COMMAND_TEMPLATE.formatted(udid, code, state));
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
}
