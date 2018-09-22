package com.example.admin.prematixchatbot.Activity;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface NetworkCallbackResponse {

    void onSuccess(JSONObject message);

    void onError(VolleyError message);
}
