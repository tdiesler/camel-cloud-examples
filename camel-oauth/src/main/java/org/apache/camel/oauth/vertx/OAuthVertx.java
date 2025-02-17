package org.apache.camel.oauth.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2AuthorizationURL;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.Oauth2Credentials;
import io.vertx.ext.auth.oauth2.providers.OpenIDConnectAuth;
import org.apache.camel.oauth.AuthRequestParams;
import org.apache.camel.oauth.InMemorySessionStore;
import org.apache.camel.oauth.LogoutRequestParams;
import org.apache.camel.oauth.OAuth;
import org.apache.camel.oauth.OAuthConfig;
import org.apache.camel.oauth.OAuthException;
import org.apache.camel.oauth.OAuthSessionStore;
import org.apache.camel.oauth.TokenRequestParams;
import org.apache.camel.oauth.UserProfile;

public class OAuthVertx implements OAuth {

    private final Vertx vertx;
    private final OAuthSessionStore sessionStore;

    private OAuth2Auth oauth2;

    public OAuthVertx(Vertx vertx) {
        this.vertx = vertx;
        this.sessionStore = new InMemorySessionStore();
    }

    @Override
    public OAuthSessionStore getSessionStore() {
        return sessionStore;
    }

    @Override
    public void discover(OAuthConfig config) throws OAuthException {

        var baseUrl = config.getBaseUrl();
        OAuth2Options aux = new OAuth2Options()
                .setSite(baseUrl)
                .setClientId(config.getClientId())
                .setClientSecret(config.getClientSecret());

        try {
            oauth2 = OpenIDConnectAuth.discover(vertx, aux)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get();
        } catch (Exception ex) {
            throw new OAuthException("Cannot discover OAuth config from: " + baseUrl, ex);
        }

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
    public UserProfile authenticate(UserProfile user) throws OAuthException {

        var scope = (String) user.principal().get("scope");
        var creds = new TokenCredentials()
                .setToken(user.accessToken().orElseThrow())
                .addScope(scope);

        try {
            User vtxUser = oauth2.authenticate(creds)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get();
            user.merge(new UserProfileVertx(vtxUser));
        } catch (Exception ex) {
            throw new OAuthException("Cannot authenticate user", ex);
        }

        return user;
    }

    @Override
    public UserProfile tokenRequest(TokenRequestParams params) throws OAuthException {

        var creds = new Oauth2Credentials()
                .setFlow(OAuth2FlowType.valueOf(params.getFlowType().name()))
                .setRedirectUri(params.getRedirectUri())
                .setCode(params.getCode());

        try {
            User vtxUser = oauth2.authenticate(creds)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get();
            return new UserProfileVertx(vtxUser);
        } catch (Exception ex) {
            throw new OAuthException("Cannot obtain access token", ex);
        }
    }

    @Override
    public UserProfile refresh(UserProfile user) throws OAuthException {

        try {
            User vtxUser = ((UserProfileVertx) user).getVertxUser();
            vtxUser = oauth2.refresh(vtxUser)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get();
            return new UserProfileVertx(vtxUser);
        } catch (Exception ex) {
            throw new OAuthException("Cannot refresh user", ex);
        }
    }

    @Override
    public String logoutRequestUrl(LogoutRequestParams params) {

        var user = ((UserProfileVertx) params.getUser()).getVertxUser();
        String endSessionURL = oauth2.endSessionURL(user);

        var postLogoutUrl = params.getRedirectUri();
        if (postLogoutUrl != null) {
            endSessionURL += "&post_logout_redirect_uri=" + postLogoutUrl;
        }

        return endSessionURL;
    }
}
