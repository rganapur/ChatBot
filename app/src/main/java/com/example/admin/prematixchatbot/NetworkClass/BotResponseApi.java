package com.example.admin.prematixchatbot.NetworkClass;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.admin.prematixchatbot.Activity.Bean.BotResponse;
import com.example.admin.prematixchatbot.Activity.NetworkCallbackResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class BotResponseApi {


    NetworkCallbackResponse networkCallbackResponse;
    Context context;

    public BotResponseApi(NetworkCallbackResponse networkCallbackResponse, Context context) {
        this.networkCallbackResponse = networkCallbackResponse;
        this.context = context;

    }


    public void BotResponseFromServer(Context context, String input) {


        RequestQueue mRequestQueue;

// Instantiate the cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();
        String url = "http://paypre.info/s1/Fetch_meetings?date=" + input + "";
        Log.e(TAG, "url" + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        networkCallbackResponse.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        // networkCallbackResponse.onSuccess("Error");
                        networkCallbackResponse.onError(error);


                        Log.e(TAG, error.toString());
                    }
                }) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("access_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MzY5ODk4MzN9.ApRkN7zg-AQgflS28jOzwWF3VonyeTkTdnK1UtsdhvY");
                return headers;
            }
        };


// Add the request to the RequestQueue.
        mRequestQueue.add(jsonObjectRequest);

    }
}
