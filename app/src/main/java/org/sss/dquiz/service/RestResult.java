package org.sss.dquiz.service;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by iam on 1/14/17.
 */

public class RestResult extends JsonHttpResponseHandler {

    private JSONObject result = null;

    public RestResult(){
        result = new JSONObject();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        System.out.println("rest result received.........................................."+response);
        this.result = response;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

    }

    public JSONObject getResult(){
        return this.result;
    }
}