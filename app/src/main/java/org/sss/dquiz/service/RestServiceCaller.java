package org.sss.dquiz.service;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sss.dquiz.Constants.DquizConstants;
import org.sss.dquiz.Constants.DquizRestURL;

/**
 * Created by iam on 1/14/17.
 */

public class RestServiceCaller {

    private static SyncHttpClient client = new SyncHttpClient();

    public static JSONObject buildRestCall(RequestParams params,String actionName){
        DquizRestURL dquizRestURL = DquizRestURL.valueOf(actionName);
        params.put("actionName",dquizRestURL.getUrl());
        RestResult restResult = new RestResult();
        switch (dquizRestURL.getMethod()){
            case "GET":
                get(params,restResult);
                break;
            case "POST":
                post(params,restResult);
                break;
            default:
                break;
        }
        return restResult.getResult();

    }

    public static void get(RequestParams params, RestResult restResult) {
        client.get(getAbsoluteUrl(), params, restResult);
    }

    public static void post(RequestParams params, RestResult restResult) {
        //System.out.println("here ----------------- "+getAbsoluteUrl()   );
        client.post(getAbsoluteUrl(), params, restResult);
        //System.out.println(restResult);
    }

    private static String getAbsoluteUrl() {
        return DquizConstants.REST_BASE_URL;
    }


}
