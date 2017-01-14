package org.sss.dquiz.Constants;

/**
 * Created by iam on 1/14/17.
 */

public enum DquizRestURL {

    CHAPTERS("fetchChapters/","GET");

    private String url;

    private String method;

    DquizRestURL(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
