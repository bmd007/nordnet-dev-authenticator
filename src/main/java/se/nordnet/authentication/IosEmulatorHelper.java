package se.nordnet.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public class IosEmulatorHelper {

    public static final String GET_BOOTED_EMULATORS_COMMAND = "xcrun simctl list devices booted --json | jq '.devices | to_entries | map(select(.value | length > 0) | .value[] | {udid, name})'".strip();

    public record IosEmulator(String udid, String name) {
        public IosEmulator {
            if (udid == null || udid.isBlank()) {
                throw new IllegalArgumentException("udid cannot be null or blank");
            }
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name cannot be null or blank");
            }
        }
    }

    private static final ObjectMapper objectsMapper = new ObjectMapper();

    public static List<IosEmulator> getRunningIosEmulators() {
        try {
            return objectsMapper.readValue(executeCommand(GET_BOOTED_EMULATORS_COMMAND), new TypeReference<List<IosEmulator>>() {});
        } catch (Exception e) {
            log.error("Error getting running iOS emulators", e);
            return List.of();
        }
    }


    public static boolean isNordnetAppInstalled(String emulatorId) {
        if (emulatorId == null) {
            return false;
        }
        return executeCommand("xcrun simctl listapps " + emulatorId).contains("com.nordnet.Nordnet");
    }

    public static IosEmulator emulatorWithNordnetAppInstalled(){
        return getRunningIosEmulators()
                .stream().filter(emulator -> isNordnetAppInstalled(emulator.udid()))
                .findFirst()
                .orElseThrow();
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
                throw new RuntimeException("Command execution failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing command", e);
        }

        return output.toString();
    }
}
