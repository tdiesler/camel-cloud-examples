package org.apache.camel.oauth;

public class TokenRequestParams {

    private String code;
    private String redirectUri;
    private OAuthFlowType flowType;

    public String getCode() {
        return code;
    }

    public TokenRequestParams setCode(String code) {
        this.code = code;
        return this;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public TokenRequestParams setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public OAuthFlowType getFlowType() {
        return flowType;
    }

    public TokenRequestParams setFlowType(OAuthFlowType flowType) {
        this.flowType = flowType;
        return this;
    }
}
