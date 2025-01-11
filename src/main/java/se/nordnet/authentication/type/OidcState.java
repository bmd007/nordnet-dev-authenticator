package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;

import java.util.Base64;
import java.util.List;

public record OidcState(@NotBlank String country, List<IosSimulator> targetIosSimulators) {

    public OidcState(String country) {
        this(country, List.of());
    }

    public OidcState {
        if (targetIosSimulators == null) {
            targetIosSimulators = List.of();
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
