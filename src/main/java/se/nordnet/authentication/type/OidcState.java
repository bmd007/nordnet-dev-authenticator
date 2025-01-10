package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

import java.util.Base64;

public record OidcState(@NotBlank String country, @Nullable IosSimulator targetIosSimulatorId) {

    public OidcState {
        country = country.toUpperCase();
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
