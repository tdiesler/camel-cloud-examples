package org.apache.camel.cloud.oauth;

import java.util.List;

public class AuthRequestParams {

    private String clientId;
    private String redirectUri;
    private AuthRequestResponseType responseType;
    private List<String> scopes;
    private String state;
    private AuthRequestDisplayOpts display;
    private List<AuthRequestPromptOpts> prompt;
    private Integer maxAge;
    private String uiLocales;
    private String idTokenHint;
    private String loginHint;
    private String acrValues;

    public String getClientId() {
        return clientId;
    }

    public AuthRequestParams setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public AuthRequestParams setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public AuthRequestResponseType getResponseType() {
        return responseType;
    }

    public AuthRequestParams setResponseType(AuthRequestResponseType responseType) {
        this.responseType = responseType;
        return this;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public AuthRequestParams setScope(String scope) {
        this.scopes = List.of(scope);
        return this;
    }

    public AuthRequestParams setScopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    public String getState() {
        return state;
    }

    public AuthRequestParams setState(String state) {
        this.state = state;
        return this;
    }

    public AuthRequestDisplayOpts getDisplay() {
        return display;
    }

    public AuthRequestParams setDisplay(AuthRequestDisplayOpts display) {
        this.display = display;
        return this;
    }

    public List<AuthRequestPromptOpts> getPrompt() {
        return prompt;
    }

    public AuthRequestParams setPrompt(List<AuthRequestPromptOpts> prompt) {
        this.prompt = prompt;
        return this;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public AuthRequestParams setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public String getUiLocales() {
        return uiLocales;
    }

    public AuthRequestParams setUiLocales(String uiLocales) {
        this.uiLocales = uiLocales;
        return this;
    }

    public String getIdTokenHint() {
        return idTokenHint;
    }

    public AuthRequestParams setIdTokenHint(String idTokenHint) {
        this.idTokenHint = idTokenHint;
        return this;
    }

    public String getLoginHint() {
        return loginHint;
    }

    public AuthRequestParams setLoginHint(String loginHint) {
        this.loginHint = loginHint;
        return this;
    }

    public String getAcrValues() {
        return acrValues;
    }

    public AuthRequestParams setAcrValues(String acrValues) {
        this.acrValues = acrValues;
        return this;
    }
}
