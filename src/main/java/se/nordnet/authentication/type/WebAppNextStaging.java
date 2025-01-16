package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.util.UriComponentsBuilder;

public record WebAppNextStaging(String pullRequestIdentifier) {
    @JsonIgnore
    public String url(String countryCode, String authorizationCode) {
        return UriComponentsBuilder.fromUriString("https://%s.webapp-next.staging.nordnet-test.%s".formatted(pullRequestIdentifier, countryCode))
                .path("/login")
                .queryParam("code", authorizationCode)
                .queryParam("state", "MICROSOFT_ENTRAID_CARISMA")
                .queryParam("loginFeatures", "nordet-dev-authenticator-webapp-next-staging-login")
                .build()
                .toUriString();
    }
}
