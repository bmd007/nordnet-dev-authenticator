package se.nordnet.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import se.nordnet.authentication.type.IosSimulator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public class IosSimulatorHelper {

    public static final String GET_BOOTED_IOS_SIMULATORS_COMMAND = "xcrun simctl list devices booted --json | jq '.devices | to_entries | map(select(.value | length > 0) | .value[] | {udid, name})'".strip();

    private static final ObjectMapper objectsMapper = new ObjectMapper();

    public static List<IosSimulator> getRunningIosSimulators() {
        try {
            return objectsMapper.readValue(executeCommand(GET_BOOTED_IOS_SIMULATORS_COMMAND), new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Error getting running iOS simulators", e);
            return List.of();
        }
    }

    public static boolean isNordnetAppInstalled(String simulatorId) {
        if (simulatorId == null) {
            return false;
        }
        return executeCommand("xcrun simctl listapps " + simulatorId).contains("com.nordnet.Nordnet");
    }

    public static List<IosSimulator> iosSimulatorsWithNordnetApp() {
        return getRunningIosSimulators()
                .stream()
                .filter(simulator -> isNordnetAppInstalled(simulator.udid()))
                .toList();
    }

    public static boolean isNordeAppRunning(String simulatorId) {
        if (simulatorId == null) {
            return false;
        }
        return executeCommand("xcrun simctl spawn " + simulatorId + " launchctl list")
                .contains("com.nordnet.Nordnet");
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
                log.error("Command {} execution failed with exit code: {}, result: {}", userCommand, exitCode, output);
                throw new RuntimeException("Command %s execution failed with exit code: %s".formatted(command, exitCode));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing command %s".formatted(command), e);
        }
        return output.toString();
    }
}
