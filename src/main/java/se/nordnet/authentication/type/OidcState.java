package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

import java.util.Base64;
import java.util.Set;

public record OidcState(@NotBlank String country, @Nullable String targetIosSimulatorId) {
    private static final Set<String> COUNTRY_CODES = Set.of("SE", "NO", "FI", "DK");
    public OidcState {
        country = country.toUpperCase();
        if (!COUNTRY_CODES.contains(country)) {
            throw new IllegalArgumentException("CountryCode must be one of " + COUNTRY_CODES);
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
}
