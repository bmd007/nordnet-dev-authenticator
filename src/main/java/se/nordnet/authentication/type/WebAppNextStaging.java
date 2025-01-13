package se.nordnet.authentication.type;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record WebAppNextStaging(String pullRequestIdentifier) {
    @JsonIgnore
    public String url(String countryCode, String authorizationCode) {
        return "https://%s.webapp-next.staging.nordnet-test.%s/login?code=%s&state=NORDNET_DEV_AUTHENTICATOR_STAGING".formatted(pullRequestIdentifier, countryCode, authorizationCode);
    }
}
