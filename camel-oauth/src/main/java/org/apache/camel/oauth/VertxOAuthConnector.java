package org.apache.camel.oauth;

import java.util.Optional;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2AuthorizationURL;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.Oauth2Credentials;
import io.vertx.ext.auth.oauth2.providers.OpenIDConnectAuth;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxOAuthConnector implements OAuthConnector {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Vertx vertx;
    private OAuthConfig config;
    private OAuth2Auth oauth2;

    public VertxOAuthConnector(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void discover(OAuthConfig config) throws Exception {

        OAuth2Options aux = new OAuth2Options()
                .setSite(config.getBaseUrl())
                .setClientId(config.getClientId())
                .setClientSecret(config.getClientSecret());

        oauth2 = OpenIDConnectAuth.discover(vertx, aux)
                .onFailure(th -> log.error("Error in OAuth config discover", th))
                .toCompletionStage()
                .toCompletableFuture()
                .get();

        config.setAuthorizationPath(aux.getAuthorizationPath())
                .setTokenPath(aux.getTokenPath())
                .setRevocationPath(aux.getRevocationPath())
                .setLogoutPath(aux.getLogoutPath())
                .setUserInfoPath(aux.getUserInfoPath())
                .setIntrospectionPath(aux.getIntrospectionPath());
    }

    @Override
    public String authRequestUrl(AuthRequestParams params) {
        if (params.getScopes() == null) {
            params.setScope("openid");
        }
        return oauth2.authorizeURL(new OAuth2AuthorizationURL()
                .setRedirectUri(params.getRedirectUri())
                .setScopes(params.getScopes()));
    }

    @Override
    public String logoutRequestUrl(LogoutRequestParams params) {
        var postLogoutUrl = params.getRedirectUri();
        var user = ((VertxUserProfile) params.getUser()).getDelegate();
        return oauth2.endSessionURL(user) + "&post_logout_redirect_uri=" + postLogoutUrl;
    }

    @Override
    public Optional<UserProfile> tokenRequest(TokenRequestParams params) {

        var creds = new Oauth2Credentials()
                .setFlow(OAuth2FlowType.valueOf(params.getFlowType().name()))
                .setRedirectUri(params.getRedirectUri())
                .setCode(params.getCode());

        User user = oauth2.authenticate(creds)
                .toCompletionStage()
                .toCompletableFuture()
                .join();

        return Optional.of(new VertxUserProfile(user));
    }

    @Override
    public Optional<UserProfile> getUserProfile(Exchange exchange) {
        var registry = exchange.getContext().getRegistry();
        var userProfile = registry.lookupByNameAndType("OAuthUserProfile", UserProfile.class);
        return Optional.ofNullable(userProfile);
    }

    @Override
    public void putUserProfile(Exchange exchange, UserProfile userProfile) {
        // [TODO] GHI-4 Authenticated UserProfile binds to camel context rather than http session
        var registry = exchange.getContext().getRegistry();
        registry.bind("OAuthUserProfile", userProfile);
    }

    @Override
    public void removeUserProfile(Exchange exchange) {
        var registry = exchange.getContext().getRegistry();
        registry.unbind("OAuthUserProfile");
    }
}
