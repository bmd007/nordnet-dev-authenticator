package se.nordnet.authentication.type;


public record IosSimulator(String udid, String name) {
    public IosSimulator {
        if (udid.isBlank()) {
            throw new IllegalArgumentException("IosSimulator udid must not be blank");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("IosSimulator name must not be blank");
        }
    }
}
