
package com.example.admin.prematixchatbot.Activity.Bean;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("date")
    private String mDate;
    @SerializedName("Description")
    private String mDescription;
    @SerializedName("fromtime")
    private String mFromtime;
    @SerializedName("Participants")
    private String mParticipants;
    @SerializedName("Place")
    private String mPlace;
    @SerializedName("senderName")
    private Object mSenderName;
    @SerializedName("sendingTime")
    private String mSendingTime;
    @SerializedName("subject")
    private String mSubject;
    @SerializedName("totime")
    private String mTotime;
    @SerializedName("userid")
    private String mUserid;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getFromtime() {
        return mFromtime;
    }

    public void setFromtime(String fromtime) {
        mFromtime = fromtime;
    }

    public String getParticipants() {
        return mParticipants;
    }

    public void setParticipants(String participants) {
        mParticipants = participants;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public Object getSenderName() {
        return mSenderName;
    }

    public void setSenderName(Object senderName) {
        mSenderName = senderName;
    }

    public String getSendingTime() {
        return mSendingTime;
    }

    public void setSendingTime(String sendingTime) {
        mSendingTime = sendingTime;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getTotime() {
        return mTotime;
    }

    public void setTotime(String totime) {
        mTotime = totime;
    }

    public String getUserid() {
        return mUserid;
    }

    public void setUserid(String userid) {
        mUserid = userid;
    }

}
