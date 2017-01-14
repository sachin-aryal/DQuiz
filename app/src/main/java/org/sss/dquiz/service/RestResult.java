package org.sss.dquiz.service;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by iam on 1/14/17.
 */

public class RestResult extends JsonHttpResponseHandler {

    private JSONObject result = null;

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        result = response;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

    }

    public JSONObject getResult(){
        return result;
    }
}