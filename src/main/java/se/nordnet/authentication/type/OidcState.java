package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public record OidcState(String country, TargetEnvironment targetEnvironment) {

    public OidcState {
        if (country.isBlank()) {
            throw new IllegalArgumentException("Country must not be blank");
        }
    }

    @JsonIgnore
    public String getBase64Json() {
        try {
            return Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static OidcState fromBase64Json(String base64Json) {
        try {
            return new ObjectMapper().readValue(Base64.getDecoder().decode(base64Json), OidcState.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public boolean isIosTargeted() {
        return targetEnvironment.iosSimulator() != null && targetEnvironment.androidEmulator() == null;
    }

    @JsonIgnore
    public boolean isAndroidTargeted() {
        return targetEnvironment.androidEmulator() != null && targetEnvironment.iosSimulator() == null;
    }

    @JsonIgnore
    public boolean isWebAppNextStagingTargeted() {
        return targetEnvironment.webAppNextStaging() != null && targetEnvironment.iosSimulator() == null && targetEnvironment.androidEmulator() == null;
    }

    public static OidcState forIosSimulator(String country, IosSimulator iosSimulator) {
        return new OidcState(country, new TargetEnvironment(iosSimulator));
    }

    public static OidcState forAndroidEmulator(String country, AndroidEmulator androidEmulator) {
        return new OidcState(country, new TargetEnvironment(androidEmulator));
    }

    public static OidcState forWebAppNextStaging(String country, WebAppNextStaging webAppNextStaging) {
        return new OidcState(country, new TargetEnvironment(webAppNextStaging));
    }
}
