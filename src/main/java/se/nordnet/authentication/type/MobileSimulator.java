package se.nordnet.authentication.type;


public record MobileSimulator(IosSimulator ios, AndroidEmulator android) {
    public MobileSimulator {
        if (ios == null && android == null) {
            throw new IllegalArgumentException("At least one simulator must be provided");
        }
        if (ios != null && android != null) {
            throw new IllegalArgumentException("Only one simulator must be provided");
        }
    }

    public MobileSimulator(IosSimulator iosSimulator) {
        this(iosSimulator, null);
    }

    public MobileSimulator(AndroidEmulator androidEmulator) {
        this(null, androidEmulator);
    }
}
