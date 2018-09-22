package com.example.admin.prematixchatbot.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.example.admin.prematixchatbot.ChatBoxMessageView.util.ChatBot;
import com.example.admin.prematixchatbot.ChatBoxMessageView.view.ChatView;
import com.example.admin.prematixchatbot.ChatBoxMessageView.view.MessageView;
import com.example.admin.prematixchatbot.NetworkClass.BotResponseApi;
import com.example.admin.prematixchatbot.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.example.admin.prematixchatbot.ChatBoxMessageView.model.Message;
import com.example.admin.prematixchatbot.ChatBoxMessageView.model.IChatUser;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static com.android.volley.VolleyLog.TAG;

/**
 * Simple chat example activity
 * Created by nakayama on 2016/12/03.
 */
public class MessengerActivity extends AppCompatActivity implements NetworkCallbackResponse, RecognitionListener {
    String responsedata = null;

    Toolbar toolbar;
    Menu mMenu;
    private static final int REQUEST_RECORD_PERMISSION = 100;

    private String LOG_TAG = "VoiceRecognitionActivity";

    private SpeechRecognizer speechRecognizer;
    Context context;
    NetworkCallbackResponse networkCallbackResponse;

    Intent intent;
    boolean response_delivered;
    boolean isSofiaSpeakingCompleted;

    public MessengerActivity() {
    }

    @VisibleForTesting
    protected static final int RIGHT_BUBBLE_COLOR = R.color.colorPrimaryDark;
    @VisibleForTesting
    protected static final int LEFT_BUBBLE_COLOR = R.color.gray300;
    @VisibleForTesting
    protected static final int BACKGROUND_COLOR = R.color.teal100;
    @VisibleForTesting
    protected static final int SEND_BUTTON_COLOR = R.color.blueGray500;
    @VisibleForTesting
    protected static final int SEND_ICON = R.drawable.ic_action_send;
    @VisibleForTesting
    protected static final int OPTION_BUTTON_COLOR = R.color.teal500;
    @VisibleForTesting
    protected static final int RIGHT_MESSAGE_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int LEFT_MESSAGE_TEXT_COLOR = Color.BLACK;
    @VisibleForTesting
    protected static final int USERNAME_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int SEND_TIME_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int DATA_SEPARATOR_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final int MESSAGE_STATUS_TEXT_COLOR = Color.WHITE;
    @VisibleForTesting
    protected static final String INPUT_TEXT_HINT = "New message..";
    @VisibleForTesting
    protected static final int MESSAGE_MARGIN = 5;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private Gson gson;
    private ChatView mChatView;
    private MessageList mMessageList;
    private ArrayList<User> mUsers;

    private int mReplyDelay = -1;

    private static final int READ_REQUEST_CODE = 200;
    BotResponseApi botResponseApi;
    TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("ChatBot");
        context = MessengerActivity.this;
        initUsers();


        mChatView = findViewById(R.id.chat_view);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");

        gson = gsonBuilder.create();


        ActivityCompat.requestPermissions
                (MessengerActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);

        checkPermission();
        textToSpeech();
        //Load saved messages
        loadMessages();

        //Set UI parameters if you need
        mChatView.setRightBubbleColor(ContextCompat.getColor(this, RIGHT_BUBBLE_COLOR));
        mChatView.setLeftBubbleColor(ContextCompat.getColor(this, LEFT_BUBBLE_COLOR));
        mChatView.setBackgroundColor(ContextCompat.getColor(this, BACKGROUND_COLOR));
        mChatView.setSendButtonColor(ContextCompat.getColor(this, SEND_BUTTON_COLOR));
        mChatView.setSendIcon(SEND_ICON);
        mChatView.setOptionIcon(R.drawable.ic_account_circle);
        mChatView.setOptionButtonColor(OPTION_BUTTON_COLOR);
        mChatView.setRightMessageTextColor(RIGHT_MESSAGE_TEXT_COLOR);
        mChatView.setLeftMessageTextColor(LEFT_MESSAGE_TEXT_COLOR);
        mChatView.setUsernameTextColor(USERNAME_TEXT_COLOR);
        mChatView.setSendTimeTextColor(SEND_TIME_TEXT_COLOR);
        mChatView.setDateSeparatorColor(DATA_SEPARATOR_COLOR);
        mChatView.setMessageStatusTextColor(MESSAGE_STATUS_TEXT_COLOR);
        mChatView.setInputTextHint(INPUT_TEXT_HINT);
        mChatView.setMessageMarginTop(MESSAGE_MARGIN);
        mChatView.setMessageMarginBottom(MESSAGE_MARGIN);
        mChatView.setMaxInputLine(5);
        mChatView.setUsernameFontSize(getResources().getDimension(R.dimen.font_small));
        mChatView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mChatView.setInputTextColor(ContextCompat.getColor(this, R.color.black));
        mChatView.setInputTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        botResponseApi = new BotResponseApi(this, getApplicationContext());

        mChatView.setOnBubbleClickListener(new Message.OnBubbleClickListener() {
            @Override
            public void onClick(Message message) {
                mChatView.updateMessageStatus(message, MyMessageStatusFormatter.STATUS_SEEN);
                Toast.makeText(
                        MessengerActivity.this,
                        "click : " + message.getUser().getName() + " - " + message.getText(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        mChatView.setOnIconClickListener(new Message.OnIconClickListener() {
            @Override
            public void onIconClick(Message message) {
                Toast.makeText(
                        MessengerActivity.this,
                        "click : icon " + message.getUser().getName(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        mChatView.setOnIconLongClickListener(new Message.OnIconLongClickListener() {
            @Override
            public void onIconLongClick(Message message) {
                Toast.makeText(
                        MessengerActivity.this,
                        "Removed this message \n" + message.getText(),
                        Toast.LENGTH_SHORT
                ).show();
                mChatView.getMessageView().remove(message);
            }
        });


        mChatView.setMessageOnLongClick(new Message.onClickMessageListener() {
            @Override
            public void onMessageClicked(@NotNull Message message) {
                Toast.makeText(
                        MessengerActivity.this,
                        "show message \n" + message.getText(),
                        Toast.LENGTH_SHORT
                ).show();

            }
        });


        //Click Send Button
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initUsers();
                //new message


                onClickButtonSendMessage();

            }

        });

        //Click option button
        mChatView.setOnClickOptionButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        mChatView.setMicOn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                askSpeechInput();
            }
        });

    }

    private void speechRecognizerinit() {


        if (SpeechRecognizer.isRecognitionAvailable(this)) {


            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);


            speechRecognizer.setRecognitionListener(this);


//            speechRecognizer.setRecognitionListener(new RecognitionListener() {
//                @Override
//                public void onReadyForSpeech(Bundle params) {
//
//                }
//
//                @Override
//                public void onBeginningOfSpeech() {
//
//                }
//
//                @Override
//                public void onRmsChanged(float rmsdB) {
//
//                }
//
//                @Override
//                public void onBufferReceived(byte[] buffer) {
//
//                }
//
//                @Override
//                public void onEndOfSpeech() {
//
//                }
//
//                @Override
//                public void onError(int error) {
//
//                }
//
//                @Override
//                public void onResults(Bundle result) {
//
//                    List<String> results = result.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                    assert results != null;
//                    speechProccessResult(results.get(0));
//
//                    Log.e(TAG, "Result recognise" + results.get(0));
//
//                }
//
//                @Override
//                public void onPartialResults(Bundle partialResults) {
//
//                }
//
//                @Override
//                public void onEvent(int eventType, Bundle params) {
//
//                }
//            });
        }

    }

    private void speechProccessResult(String command) {

        command = command.toLowerCase();
        Log.e(TAG, "command=========" + command);


        mChatView.setInputText(command);
        onClickButtonSendMessage();


    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void onClickButtonSendMessage() {

        if (!response_delivered) {

            response_delivered = true;

            Message message = new Message.Builder()
                    .setUser(mUsers.get(0))
                    .setRight(true)
                    .setText(mChatView.getInputText().toLowerCase())
                    .hideIcon(false)
                    .setSendTimeFormatter(new MyTimeFormatter()) // here!
                    .setStatusIconFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                    .setStatusTextFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                    .setStatusStyle(Message.Companion.getSTATUS_ICON())
                    .setStatus(MyMessageStatusFormatter.STATUS_DELIVERED)
                    .build();


            //Set to chat view
            mChatView.send(message);
            //Add message list
            mMessageList.add(message);
            //Reset edit text
            mChatView.setInputText("");


            if (message.getText().equalsIgnoreCase("Stop")) {

                receiveMessage("Stop Mic ");
                mMenu.findItem(R.id.micon).setVisible(false);
                mMenu.findItem(R.id.micoff).setVisible(true);
                speechRecognizer.destroy();

                response_delivered = false;


            } else if (message.getText().equalsIgnoreCase("Clear")) {
                AppData.reset(MessengerActivity.this);
                mChatView.getMessageView().removeAll();
                // if(speechRecognizer!=null)

                response_delivered = false;

                //  speechRecognizer.startListening(intent);


            } else {

                chatResponse(message.getText());
//                if (speechRecognizer != null)
//                    speechRecognizer.startListening(intent);

            }
        } else {
            mChatView.setInputText("");
            Toast.makeText(getApplicationContext(), "please wait loading ...", Toast.LENGTH_SHORT).show();
            response_delivered = false;
            //   speechRecognizer.startListening(intent);
        }
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException ignored) {

        }
    }


    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void receiveMessage(final String sendText) {
        //Ignore hey
        if (!sendText.contains("hey")) {

            //Receive message
            final Message receivedMessage = new Message.Builder()
                    .setUser(mUsers.get(1))
                    .setRight(false)
                    .setSendTimeFormatter(new MyTimeFormatter()) // here!
                    .setText(sendText)
                    .hideIcon(false)
                    .setStatusIconFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                    .setStatusTextFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                    .setStatusStyle(Message.Companion.getSTATUS_ICON())
                    .setStatus(MyMessageStatusFormatter.STATUS_DELIVERED)
                    .build();


            // ChatBot.INSTANCE.talk(mUsers.get(0).getName(), sendText,context)
            if (sendText.equals(Message.Type.PICTURE.name())) {
                receivedMessage.setText("Nice!");
            }

            // This is a demo bot
            // Return within 3 seconds
            if (mReplyDelay < 0) {
                mReplyDelay = (new Random().nextInt(1) + 1) * 1000;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("message", receivedMessage.getText());
                    //  receivedMessage.setText("Hi");
                    mChatView.receive(receivedMessage);
                    //Add message list
                    mMessageList.add(receivedMessage);

                    response_delivered = false;
                    if (tts != null) {
                        speakResponse(sendText);

                        Log.e(TAG, "speakResponse" + "true");
                    }
                }
            }, mReplyDelay);

        }

    }

    private void speakResponse(String receivedMessage) {


        if (Build.VERSION.SDK_INT >= 21)
            tts.speak(receivedMessage, TextToSpeech.QUEUE_FLUSH, null, null);
        else tts.speak(receivedMessage, TextToSpeech.QUEUE_FLUSH, null);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode != READ_REQUEST_CODE || resultCode != RESULT_OK || data == null) {
//            return;
//        }


        Log.e(TAG, requestCode + "" + resultCode);
        if (data != null && READ_REQUEST_CODE == requestCode) {


            Uri uri = data.getData();
            try {
                Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Message message = new Message.Builder()
                        .setRight(true)
                        .setText(Message.Type.PICTURE.name())
                        .setUser(mUsers.get(0))
                        .hideIcon(true)
                        .setPicture(picture)
                        .setType(Message.Type.PICTURE)
                        .setStatusIconFormatter(new MyMessageStatusFormatter(MessengerActivity.this))
                        .setStatusStyle(Message.Companion.getSTATUS_ICON())
                        .setStatus(MyMessageStatusFormatter.STATUS_DELIVERED)
                        .build();
                mChatView.send(message);
                //Add message list
                mMessageList.add(message);
                receiveMessage(Message.Type.PICTURE.name());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        } else if (REQ_CODE_SPEECH_INPUT == requestCode) {


            assert data != null;
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (result.get(0) != null) {


                mChatView.setInputText(result.get(0));
                if (tts != null) {


                    onClickButtonSendMessage();
                }

            }


        }


    }

    private void initUsers() {
        mUsers = new ArrayList<>();
        //User id
        int myId = 0;
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_2);
        //User name
        String myName = "Iniyan";

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        String yourName = "Ashok";

        final User me = new User(myId, myName, myIcon);
        final User you = new User(yourId, yourName, yourIcon);

        mUsers.add(me);
        mUsers.add(you);
    }

    /**
     * Load saved messages
     */
    private void loadMessages() {
        List<Message> messages = new ArrayList<>();
        mMessageList = AppData.getMessageList(this);
        if (mMessageList == null) {
            mMessageList = new MessageList();
        } else {
            for (int i = 0; i < mMessageList.size(); i++) {
                Message message = mMessageList.get(i);
                //Set extra info because they were removed before save messages.
                for (IChatUser user : mUsers) {
                    if (message.getUser().getId().equals(user.getId())) {
                        message.getUser().setIcon(Objects.requireNonNull(user.getIcon()));
                    }
                }
                if (!message.isDateCell() && message.isRight()) {
                    message.hideIcon(true);

                }
                message.setStatusStyle(Message.Companion.getSTATUS_ICON_RIGHT_ONLY());
                message.setStatusIconFormatter(new MyMessageStatusFormatter(this));
                message.setStatus(MyMessageStatusFormatter.STATUS_DELIVERED);
                messages.add(message);
            }
        }
        MessageView messageView = mChatView.getMessageView();
        messageView.init(messages);
        messageView.setSelection(messageView.getCount() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        initUsers();
    }

    @Override
    public void onPause() {


        if (tts != null) {

            tts.stop();
            tts.shutdown();
        }
        super.onPause();
        //Save message
        mMessageList = new MessageList();
        mMessageList.setMessages(mChatView.getMessageView().getMessageList());
        AppData.putMessageList(this, mMessageList);

        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
            Log.i(LOG_TAG, "destroy");
        }


    }


    @VisibleForTesting
    public ArrayList<User> getUsers() {
        return mUsers;
    }


    public void setReplyDelay(int replyDelay) {
        mReplyDelay = replyDelay;
    }

    private void showDialog() {
        final String[] items = {
                getString(R.string.send_picture),
                getString(R.string.clear_messages)
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.options))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        switch (position) {
                            case 0:
                                openGallery();
                                break;
                            case 1:
                                mChatView.getMessageView().removeAll();
                                break;
                        }
                    }
                })
                .show();
    }


    @Override
    protected void onStop() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer.stopListening();

            Log.i(LOG_TAG, "destroy");
        }
        super.onStop();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //   speechRecognizer.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MessengerActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                    speechRecognizer.stopListening();
                }
        }
    }


    private void chatResponse(final String sendText) {

        String responseChatBot = ChatBot.INSTANCE.talk(mUsers.get(0).getName(), sendText);

        if (responseChatBot.equalsIgnoreCase("Sorry ,Iniyan No Key Word Matched")) {

            try {
                botResponseApi.BotResponseFromServer(getApplicationContext(), URLEncoder.encode(sendText, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

//            RequestQueue mRequestQueue;
//
//// Instantiate the cache
//            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
//
//// Set up the network to use HttpURLConnection as the HTTP client.
//            Network network = new BasicNetwork(new HurlStack());
//
//// Instantiate the RequestQueue with the cache and network.
//            mRequestQueue = new RequestQueue(cache, network);
//
//// Start the queue
//            mRequestQueue.start();
//            String url = "http://paypre.info/s1/Fetch_meetings?date=" + sendText + "";
//            Log.e(TAG, "url" + url);
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                        @Override
//                        public void onResponse(JSONObject response) {
//
//                            StringBuilder sb = new StringBuilder();
//                            System.out.println(sb.capacity());//default 16
//
//                            Log.e(TAG, response.toString());
//
//
//                            try {
//                                if (response.getString("StatusCode").equalsIgnoreCase("200")) {
//                                    BotResponse posts = gson.fromJson(response.toString(), BotResponse.class);
//
//
//                                    List<BotResponse> users = new ArrayList<>();
//                                    users.add(posts);
//
//
//                                    for (com.example.admin.prematixchatbot.Activity.Bean.Message message : users.get(0).getMessage()) {
//
//                                        Log.e("PostActivity", message.getUserid() + "--" + message.getSubject());
//                                        String date[] = message.getDate().split("T");
//
//                                        sb.append("Name: ").append(message.getSenderName()).append("\n").append("Place: ").append(message.getPlace()).append("\n").append("Subject : ").append(message.getSubject()).append("\n").append("Description: ").append(message.getDescription()).append("\n").append("Date: ").append(date[0]).append("\n").append("Participants: ").append(message.getParticipants()).append("\n");
//
//                                    }
//
//
//                                    responsedata = sb.toString();
//                                } else {
//
//                                    responsedata = "No Events Found";
//
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//
//                            Log.e(TAG, "responsedata" + sb.toString());
//                            //   networkCallbackResponse.onSuccess("success");
//                        }
//                    }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // TODO: Handle error
//                            // networkCallbackResponse.onSuccess("Error");
//
//
//                            Log.e(TAG, error.toString());
//                        }
//                    }) {
//                /** Passing some request headers* */
//                @Override
//                public Map getHeaders() throws AuthFailureError {
//                    HashMap headers = new HashMap();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("access_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MzY5ODk4MzN9.ApRkN7zg-AQgflS28jOzwWF3VonyeTkTdnK1UtsdhvY");
//                    return headers;
//                }
//            };
//
//
//// Add the request to the RequestQueue.
//            mRequestQueue.add(jsonObjectRequest);
//
//            return responsedata;
        } else {

            receiveMessage(responseChatBot);
        }


    }


    @Override
    public void onSuccess(JSONObject response) {
        StringBuilder sb = new StringBuilder();
        System.out.println(sb.capacity());//default 16

        Log.e(TAG, "callback" + response.toString());


        try {
            if (response.getString("StatusCode").equalsIgnoreCase("200")) {
                BotResponse posts = gson.fromJson(response.toString(), BotResponse.class);


                List<BotResponse> users = new ArrayList<>();
                users.add(posts);


                for (com.example.admin.prematixchatbot.Activity.Bean.Message message : users.get(0).getMessage()) {

                    Log.e("PostActivity", message.getUserid() + "--" + message.getSubject());
                    String date[] = message.getDate().split("T");

                    sb.append("Name: ").append(message.getSenderName()).append("\n").append("Place: ").append(message.getPlace()).append("\n").append("Subject : ").append(message.getSubject()).append("\n").append("Description: ").append(message.getDescription()).append("\n").append("Date: ").append(date[0]).append("\n").append("Participants: ").append(message.getParticipants()).append("\n");

                }


                responsedata = sb.toString();


                receiveMessage(responsedata);


            } else {

                responsedata = "No Events Found";
                receiveMessage(responsedata);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.e(TAG, "responsedata" + sb.toString());
        //   networkCallbackResponse.onSuccess("success");
    }

    @Override
    public void onError(VolleyError message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_menu, menu);
        mMenu = menu;

        mMenu.findItem(R.id.micon).setVisible(false);
        mMenu.findItem(R.id.speakoff).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {


            case R.id.speakon:
                Toast.makeText(getApplicationContext(), "speak off", Toast.LENGTH_LONG).show();
                mMenu.findItem(R.id.speakoff).setVisible(true);
                mMenu.findItem(R.id.speakon).setVisible(false);

                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }

                return true;
            case R.id.speakoff:
                Toast.makeText(getApplicationContext(), "Speak on", Toast.LENGTH_LONG).show();
                mMenu.findItem(R.id.speakon).setVisible(true);
                mMenu.findItem(R.id.speakoff).setVisible(false);

                textToSpeech();


                return true;


            case R.id.micon:


                Toast.makeText(getApplicationContext(), "mic off", Toast.LENGTH_LONG).show();
                mMenu.findItem(R.id.micoff).setVisible(true);
                mMenu.findItem(R.id.micon).setVisible(false);
//                if (speechRecognizer != null) {
//                    if (!tts.isSpeaking()) {
//                        speechRecognizer.stopListening();
//                        speechRecognizer.destroy();
//                    } else
//                        Toast.makeText(getApplicationContext(), "Please Wait ", Toast.LENGTH_LONG).show();
//
//
//                }

                speechRecognizer.stopListening();
                speechRecognizer.destroy();

                return true;
            case R.id.micoff:
                Toast.makeText(getApplicationContext(), "mic on", Toast.LENGTH_LONG).show();
                mMenu.findItem(R.id.micon).setVisible(true);
                mMenu.findItem(R.id.micoff).setVisible(false);
                speechRecognizerinit();


                if (!tts.isSpeaking()) {

                    intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN");
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    speechRecognizer.startListening(intent);


                } else
                    Toast.makeText(getApplicationContext(), "Please Wait ", Toast.LENGTH_LONG).show();


                return true;


            case R.id.clear:
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();
                AppData.reset(MessengerActivity.this);

                mChatView.getMessageView().removeAll();
                return true;
            case R.id.logout:
                Toast.makeText(getApplicationContext(), "Exit", Toast.LENGTH_LONG).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void textToSpeech() {
        tts = new TextToSpeech(MessengerActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {


                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS && tts.getEngines().size() != 0) {
                    IntentFilter filter = new IntentFilter(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);

                    context.registerReceiver(receiver, filter);


                    int result = tts.setLanguage(Locale.US);
                    tts.setPitch(1);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {
                        Log.e("Success", "This Language is supported");

                    }
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });


    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e("errrrrrr", "onReadyForSpeech" + params);

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e("errrrrrr", "onBeginningOfSpeech");

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

        Log.e("errrrrrr", "onEndOfSpeech");

        speechRecognizer.startListening(intent);

    }

    @Override
    public void onError(int code) {

//        if (code == SpeechRecognizer.ERROR_AUDIO || code == SpeechRecognizer.ERROR_NO_MATCH || code == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
//            Log.e("errrrrrr sucues", "" + code);
//
//            // speechRecognizer.cancel();
//            speechRecognizer.startListening(intent);

        //   }

//
//        if (ERROR_RECOGNIZER_BUSY == code && ERROR_SPEECH_TIMEOUT == code) {
//            Log.e("errrrrrr", "code  == inside " + code + "" + speechRecognizer);
//
//            speechRecognizer.startListening(intent);
//
//        } else  if(speechRecognizer!=null){
//            speechRecognizer.stopListening();
//            speechRecognizer.destroy();
//            Log.e("errrrrrr", "code == else" + code);
//
//
//        }

        if (SpeechRecognizer.ERROR_SPEECH_TIMEOUT == code) {
            Log.e(TAG, "Errorcode code  == inside " + code + "" + speechRecognizer);

            speechRecognizer.startListening(intent);

        } else if (SpeechRecognizer.ERROR_RECOGNIZER_BUSY == code) {

            Log.e(TAG, "Errorcode ====" + code);

//            speechRecognizer.stopListening();
//
//            speechRecognizer.destroy();

            speechRecognizer.startListening(intent);

        }


    }

    @Override
    public void onResults(Bundle bundle) {


        ArrayList<String> matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        StringBuilder text = new StringBuilder();
        assert matches != null;
        for (String result : matches)
            text.append(result).append("\n");

        //message.append(matches.get(0)+"\n");

        Log.e(TAG, " recognise" + text + "matches" + matches.get(0));


        Log.e("COMPLETED", "YEAAAAHH first " + isSofiaSpeakingCompleted);


        if (matches.get(0) != null) {
            isSofiaSpeakingCompleted = false;
            speechProccessResult(matches.get(0));
            Log.e(TAG, "COMPLETED tts.isSpeaking() if" + tts.isSpeaking());

        }

//        if (!tts.isSpeaking() && tts != null ) {
//
//        } else {
//            Log.e(TAG, "COMPLETED tts.isSpeaking() else" + tts.isSpeaking());
//            Toast.makeText(getApplicationContext(), "Please Wait Until Speak End ", Toast.LENGTH_SHORT).show();
//
//
//        }


    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.e("partialResponses", "---" + partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED) && tts != null) {
                Log.e("COMPLETED receive", "YEAAAAHH");
                isSofiaSpeakingCompleted = true;

            }
        }
    };
}







