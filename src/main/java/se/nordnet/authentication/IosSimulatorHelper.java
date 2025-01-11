package se.nordnet.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import se.nordnet.authentication.type.CommandExecutionException;
import se.nordnet.authentication.type.IosSimulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public class IosSimulatorHelper {

    public static final String GET_BOOTED_IOS_SIMULATORS_COMMAND = "xcrun simctl list devices booted --json | jq '.devices | to_entries | map(select(.value | length > 0) | .value[] | {udid, name})'".strip();

    private static final ObjectMapper objectsMapper = new ObjectMapper();

    public static List<IosSimulator> runningIosSimulators() {
        try {
            return objectsMapper.readValue(executeCommand(GET_BOOTED_IOS_SIMULATORS_COMMAND), new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error getting running iOS simulators", e);
            return List.of();
        }
    }

    public static boolean isNordnetAppInstalled(IosSimulator simulator) {
        return executeCommand("xcrun simctl listapps " + simulator.udid()).contains("com.nordnet.Nordnet");
    }

    public static List<IosSimulator> runningSimulatorsWithNordnetApp() {
        return runningIosSimulators()
                .stream()
                .filter(IosSimulatorHelper::isNordnetAppInstalled)
                .toList();
    }

    public static boolean isNordetAppOpen(IosSimulator iosSimulator) {
        String simulatorId = iosSimulator.udid();
        try {
            return executeCommand("xcrun simctl spawn " + simulatorId + " launchctl list")
                    .contains("com.nordnet.Nordnet");
        } catch (Exception e) {
            log.error("Error checking if Nordnet app is running on iOS simulator {}", iosSimulator, e);
            throw new IllegalStateException("Error checking if Nordnet app is running on iOS simulator", e);
        }
    }

    public static void terminateNordnetApp(IosSimulator iosSimulator) {
        try {
            executeCommand("xcrun simctl terminate %s com.nordnet.Nordnet".formatted(iosSimulator.udid()));
            while (isNordetAppOpen(iosSimulator)) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.error("Error terminating Nordnet app on iOS simulator {}", iosSimulator, e);
        }
    }

    public static void lunchNordnetApp(String code, String country, IosSimulator iosSimulator) {
        String udid = iosSimulator.udid();
        try {
            executeCommand("""
                    xcrun simctl launch %s com.nordnet.Nordnet -entraIdAuthzCode "%s" -countryCode "%s"
                    """.formatted(udid, code, country).stripIndent());
        } catch (Exception e) {
            log.error("Error launching Nordnet app on iOS simulator {}", iosSimulator, e);
        }
    }

    public static String executeCommand(String userCommand) {
        List<String> command = List.of("sh", "-c", userCommand);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        StringBuilder output = new StringBuilder();
        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new CommandExecutionException(exitCode, output.toString(), userCommand);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output.toString();
    }
}
