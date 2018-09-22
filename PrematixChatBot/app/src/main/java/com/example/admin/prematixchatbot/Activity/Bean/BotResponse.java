
package com.example.admin.prematixchatbot.Activity.Bean;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BotResponse {

    @SerializedName("Message")
    private List<Message> mMessage;
    @SerializedName("StatusCode")
    private Long mStatusCode;

    public List<Message> getMessage() {
        return mMessage;
    }

    public void setMessage(List<Message> message) {
        mMessage = message;
    }

    public Long getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(Long statusCode) {
        mStatusCode = statusCode;
    }

}
