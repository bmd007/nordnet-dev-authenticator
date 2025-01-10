package se.nordnet.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public class IosSimulatorHelper {

    public static final String GET_BOOTED_SIMULATORS_COMMAND = "xcrun simctl list devices booted --json | jq '.devices | to_entries | map(select(.value | length > 0) | .value[] | {udid, name})'".strip();

    public record IosSimulator(String udid, String name) {
        public IosSimulator {
            if (udid == null || udid.isBlank()) {
                throw new IllegalArgumentException("udid cannot be null or blank");
            }
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name cannot be null or blank");
            }
        }
    }

    private static final ObjectMapper objectsMapper = new ObjectMapper();

    public static List<IosSimulator> getRunningIosSimulators() {
        try {
            return objectsMapper.readValue(executeCommand(GET_BOOTED_SIMULATORS_COMMAND), new TypeReference<List<IosSimulator>>() {
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

    public static IosSimulator simulatorWithNordnetAppInstalled() {
        return getRunningIosSimulators()
                .stream()
                .filter(simulator -> isNordnetAppInstalled(simulator.udid()))
                .findFirst()
                .orElseThrow();
    }

    public static List<IosSimulator> simulatorsWithNordnetAppInstalled() {
        return getRunningIosSimulators()
                .stream()
                .filter(simulator -> isNordnetAppInstalled(simulator.udid()))
                .toList();
    }

    public static String executeCommand(String userCommand) {
        List<String> command = List.of("sh", "-c", userCommand);
        StringBuilder output = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
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
                throw new RuntimeException("Command %s execution failed with exit code: %s".formatted(command, exitCode));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing command %s".formatted(command), e);
        }
        return output.toString();
    }
}
