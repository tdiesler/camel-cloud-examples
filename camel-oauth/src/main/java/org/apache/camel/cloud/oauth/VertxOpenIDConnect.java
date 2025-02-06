package org.apache.camel.cloud.oauth;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2AuthorizationURL;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.Oauth2Credentials;
import io.vertx.ext.auth.oauth2.providers.OpenIDConnectAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxOpenIDConnect implements OpenIDConnect {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Vertx vertx;
    private OpenIDConfig config;
    private OAuth2Auth oauth2;

    public VertxOpenIDConnect(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public OpenIDConfig discover(OpenIDConfig config) throws Exception {

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

        return config;
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
        var user = ((VertxOpenIDUser) params.getUser()).getDelegate();
        return oauth2.endSessionURL(user) + "&post_logout_redirect_uri=" + postLogoutUrl;
    }

    @Override
    public OpenIDUser tokenRequest(TokenRequestParams params) {

        var creds = new Oauth2Credentials()
                .setFlow(OAuth2FlowType.valueOf(params.getFlowType().name()))
                .setRedirectUri(params.getRedirectUri())
                .setCode(params.getCode());

        User user = oauth2.authenticate(creds)
                .toCompletionStage()
                .toCompletableFuture()
                .join();

        return new VertxOpenIDUser(user);
    }
}
