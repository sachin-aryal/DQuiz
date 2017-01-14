package org.sss.dquiz.service;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.Constants.DquizRestURL;

import cz.msebera.android.httpclient.Header;

/**
 * Created by iam on 1/14/17.
 */

public class RestService {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static JSONObject buildRestCall(RequestParams params,String actionName){
        DquizRestURL dquizRestURL = DquizRestURL.valueOf(actionName);
        RestResult restResult = new RestResult();
        switch (dquizRestURL.getMethod()){
            case "GET":
                get(dquizRestURL.getUrl(),params,restResult);
                break;
            case "POST":
                post(dquizRestURL.getUrl(),params,new RestResult());
                break;
            default:
                break;
        }
        return restResult.getResult();

    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return DquizConstants.REST_BASE_URL + relativeUrl;
    }


}
