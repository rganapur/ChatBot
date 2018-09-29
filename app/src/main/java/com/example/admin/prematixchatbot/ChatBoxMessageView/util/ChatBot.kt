 package com.example.admin.prematixchatbot.ChatBoxMessageView.util

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Chat Bot for demo
 * Created by nakayama on 2016/12/03.
 */
object ChatBot {

    fun talk(username: String?, message: String): String? {
        val receive = message.toLowerCase()
        when {
            receive.contains("hello") -> {
                var user = ""
                if (username != null) {
                    user = " " + username
                }
                return "Hello$user!"
            }
            receive.contains("python") -> return "Hi  python :)"
            receive.contains("prematix") -> return "Hi prematix :)"
            receive.contains("arjun") -> return "use SpeechRecognition.handleAudioPermissions(false) to disable this behavior. If this is set but the permission was not granted, SpeechRecognition will throw a SecurityException"


            receive.contains("hi") -> return "Hi $username :)"
            receive.contains("bye") -> return "Bye $username :)"

            receive.contains("hey") -> return "Hey $username!"
            receive.startsWith("do ") -> return "Yes, I do."
            receive.contains("time") -> return "It's " + TimeUtils.calendarToString(Calendar.getInstance(), null) + "."
            receive.contains("today iniyan") -> return "It's " + TimeUtils.calendarToString(Calendar.getInstance(), "M/d(E)")
            else -> {
                var reply = "Sorry ,Iniyan No Key Word Matched"
//                if (receive.length > 7) {
//                    reply += ", consectetur adipiscing elit, " + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
//                }
                return reply
            }


        }
    }


}


