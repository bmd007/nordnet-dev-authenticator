package se.nordnet.authentication.type;


public record AndroidEmulator(String id, String name) {
    public AndroidEmulator {
        if (id.isBlank()) {
            throw new IllegalArgumentException("AndroidEmulator id must not be blank");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("AndroidEmulator name must not be blank");
        }
    }
}
