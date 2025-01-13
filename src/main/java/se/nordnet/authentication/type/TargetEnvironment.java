package se.nordnet.authentication.type;


public record TargetEnvironment(IosSimulator iosSimulator,
                                AndroidEmulator androidEmulator,
                                WebAppNextStaging webAppNextStaging) {
    public TargetEnvironment {
        if (iosSimulator == null && androidEmulator == null && webAppNextStaging == null) {
            throw new IllegalArgumentException("At least one target environment must be provided");
        }
        if (iosSimulator != null && androidEmulator != null && webAppNextStaging != null) {
            throw new IllegalArgumentException("Only one target environment must be provided");
        }
    }

    public TargetEnvironment(IosSimulator iosSimulator) {
        this(iosSimulator, null, null);
    }

    public TargetEnvironment(AndroidEmulator androidEmulator) {
        this(null, androidEmulator, null);
    }

    public TargetEnvironment(WebAppNextStaging webAppNextStaging) {
        this(null, null, webAppNextStaging);
    }
}
