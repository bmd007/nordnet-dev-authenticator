package se.nordnet.authentication.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ProxyResource {
    @GetMapping
    public String openSimulatorWithAuthzCode(@RequestParam String code, @RequestParam String state) throws IOException {
        String commandToLunchSimulator = """
                xcrun simctl launch booted com.nordnet.Nordnet -entraIdAuthzCode "%s" -countryCode "%s"
                """.formatted(code, state);
        //todo close currently open Nordnet app in emulator
        //todo support android simulator?!
        Runtime.getRuntime().exec(commandToLunchSimulator);
        //todo return java script that closes the open tab on browser
        return "Close this tab and check your emulator";
    }
}
