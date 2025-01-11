package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Base64;

public record OidcState(@NotBlank String country, @NotNull MobileSimulator targetIosSimulator) {

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
        return targetIosSimulator.ios() != null && targetIosSimulator.android() == null;
    }

    @JsonIgnore
    public boolean isAndroidTargeted() {
        return targetIosSimulator.android() != null && targetIosSimulator.ios() == null;
    }
}
