package com.example.uibestpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<Msg>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private static int add3 = 0;

    private static boolean isEnglish = false;

    private static boolean isSpace = false;

    private static boolean isAgain = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMsgs(); // 初始化消息数据
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = inputText.getText().toString();
//                boolean isEnglish = false;
                for (int i = 0; i < content.length();i++)
                {
                    int num = content.charAt(i);
                    if((num>='a'&&num<='z')||(num>='A'&&num<='Z'))
                    {
                        isEnglish = true;
                        break;
                    }
                }
                if (!content.isEmpty()){
//                    if (content.charAt(0) == ' ') {
                        Msg msg = new Msg(content, Msg.TYPE_SENT);
                        msgList.add(msg);
                        sendRequestWithOkHttp(content);
                        Log.d("something", "==================00000=========");
                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                        msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                        inputText.setText(""); // 清空输入框中的内容
//                    }
                }
//                int k = content.charAt(0);
//                if (k == ' ' && !content.isEmpty()){
//                    isSpace = true;
//                    isEnglish = false;
//                }
//                if (isSpace && !content.isEmpty()) {
//                    Msg msg = new Msg(content, Msg.TYPE_SENT);
//                    msgList.add(msg);
//                    sendRequestWithOkHttp(content);
//                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
//                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
//                    inputText.setText(""); // 清空输入框中的内容
//                    isSpace = false;
//                } else if (!content.isEmpty()) {
//                    if ( isEnglish) {
//                        Msg msg = new Msg(content, Msg.TYPE_SENT);
//                        msgList.add(msg);
//                        sendRequestWithOkHttp(content);
//                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
//                        msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
//                        inputText.setText(""); // 清空输入框中的内容
//                    } else  {
//                        Msg msg = new Msg(content, Msg.TYPE_SENT);
//                        msgList.add(msg);
//                        sendRequestWithOkHttp1(content);
//                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
//                        msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
//                        inputText.setText(""); // 清空输入框中的内容
//                    }
//
//                }

            }
        });

    }

    private void sendRequestWithOkHttp(final String msgsend){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=earthquakelearn&key=82577513&type=data&doctype=json&version=1.1&q=";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(baseUrl + msgsend)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);
                    Log.d("something", "==================00001=========");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void sendRequestWithOkHttp1(final String msgsend){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = "http://www.tuling123.com/openapi/api?key=6a94a09f577648ab98f221e7e7ae686f&info=";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(baseUrl + msgsend)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJSONObject1(responseData);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String text = jsonObject.getString("translation");
            String text1 = text.substring(2,text.length()-2);
            showResponse(text1);
            Log.d("something", "==================00002=========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSONWithJSONObject1(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String text = jsonObject.getString("text");
            showResponse1(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"This is english",Toast.LENGTH_SHORT).show();
                Msg msg = new Msg(response, Msg.TYPE_RECEIVED);
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
            }
        });
    }

    private void showResponse1(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"这是中文",Toast.LENGTH_SHORT).show();
                Msg msg = new Msg(response, Msg.TYPE_RECEIVED);
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
            }
        });
    }

    private void initMsgs() {
        Msg msg1 = new Msg("hello，无聊的话我可以陪你聊天哦", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("发英文的话我可以给你翻译", Msg.TYPE_RECEIVED);
        msgList.add(msg2);
        Msg msg3 = new Msg("翻译中文的话在第一个字前面加个空格就好了", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }

}
