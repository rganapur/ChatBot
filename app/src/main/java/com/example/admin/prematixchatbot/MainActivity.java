package com.example.admin.prematixchatbot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.admin.prematixchatbot.Activity.AppData;
import com.example.admin.prematixchatbot.Activity.MessengerActivity;
import com.example.admin.prematixchatbot.Activity.SimpleChatActivity;
import com.maxwell.speechrecognition.OnSpeechRecognitionListener;
import com.maxwell.speechrecognition.OnSpeechRecognitionPermissionListener;
import com.maxwell.speechrecognition.SpeechRecognition;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity implements View.OnClickListener, OnSpeechRecognitionPermissionListener, OnSpeechRecognitionListener {

    public ListView mList;
    public Button speakButton;
    SpeechRecognition speechRecognition;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speakButton = (Button) findViewById(R.id.btn_speak);
        speakButton.setOnClickListener(this);
        speechRecognition = new SpeechRecognition(this);
        speechRecognition.setSpeechRecognitionPermissionListener(this);
        speechRecognition.setSpeechRecognitionListener(this);

        voiceinputbuttons();
        speechRecognition.startSpeechRecognition();

    }

    public void informationMenu() {
        startActivity(new Intent("android.intent.action.INFOSCREEN"));
    }

    public void voiceinputbuttons() {
        speakButton = (Button) findViewById(R.id.btn_speak);
        mList = (ListView) findViewById(R.id.list);
    }

    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        //  startVoiceRecognitionActivity();

        speechRecognition.startSpeechRecognition();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, matches));
            // matches is the result of voice input. It is a list of what the
            // user possibly said.
            // Using an if statement for the keyword you want to use allows the
            // use of any activity if keywords match
            // it is possible to set up multiple keywords to use the same
            // activity so more than one word will allow the user
            // to use the activity (makes it so the user doesn't have to
            // memorize words from a list)
            // to use an activity from the voice input information simply use
            // the following format;
            // if (matches.contains("keyword here") { startActivity(new
            // Intent("name.of.manifest.ACTIVITY")

            if (matches.contains("information")) {
                informationMenu();
            }
        }
    }

    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionDenied() {

    }

    @Override
    public void OnSpeechRecognitionStarted() {

    }

    @Override
    public void OnSpeechRecognitionStopped() {

    }

    @Override
    public void OnSpeechRecognitionFinalResult(String s) {
        Log.e(TAG, "OnSpeechRecognitionFinalResult" + s);
    }

    @Override
    public void OnSpeechRecognitionCurrentResult(String s) {
        Log.e(TAG, "cureent" + s);
    }

    @Override
    public void OnSpeechRecognitionError(int i, String s) {

    }
}

//extends Activity {
//
//    public static final int SIMPLE_CHAT = 0;
//    public static final int MESSENGER = 1;
//    public static final int RESET_DATA = 2;
//
//    private String[] mMenu = {"Simple Chat", "Messenger", "Reset Data"};
//
//    @VisibleForTesting
//    protected String[] gettMenu() {
//        return mMenu;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//
//        ListView menuList = findViewById(R.id.menu_list);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_expandable_list_item_1, mMenu);
//        menuList.setAdapter(adapter);
//        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Intent intent;
//                switch (position) {
//                    case SIMPLE_CHAT:
//                        intent = new Intent(MainActivity.this, SimpleChatActivity.class);
//                        startActivity(intent);
//                        break;
//                    case MESSENGER:
//                        intent = new Intent(MainActivity.this, MessengerActivity.class);
//                        startActivity(intent);
//                        break;
//                    case RESET_DATA:
//                        AppData.reset(MainActivity.this);
//                        break;
//                    default:
//                        return;
//                }
//
//            }
//        });
//
//
//
//    }
//}
