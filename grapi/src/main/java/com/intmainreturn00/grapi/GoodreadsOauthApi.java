package com.intmainreturn00.grapi;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;

public class GoodreadsOauthApi extends DefaultApi10a {
    private static final String AUTHORIZE_URL = "https://www.goodreads.com/oauth/authorize?mobile=1&oauth_token=%s";

    private GoodreadsOauthApi() {
    }

    private static class InstanceHolder {
        private static final GoodreadsOauthApi INSTANCE = new GoodreadsOauthApi();
    }

    public static GoodreadsOauthApi instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://www.goodreads.com/oauth/access_token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return AUTHORIZE_URL;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return "https://www.goodreads.com/oauth/request_token";
    }

    @Override
    public String getAuthorizationUrl(OAuth1RequestToken requestToken) {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }
}
