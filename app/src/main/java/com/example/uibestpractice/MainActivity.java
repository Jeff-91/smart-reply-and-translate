package com.example.uibestpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<Msg>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private static int add3 = 0;

    private static boolean isEnglish = false;

    private static boolean once = false;
    private static boolean twice = false;
    private static boolean thirdtime = false;



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
                for (int i = 0; i < content.length();i++)
                {
                    int num = content.charAt(i);
                    if((num>='a'&&num<='z')||(num>='A'&&num<='Z'))
                    {
                        isEnglish = true;
                    }
                }
                if ("I love you".equals(content)) {
                    isEnglish = false;
                    ++add3;
                    if (add3 == 1) {
                        once = true;
                        twice = false;
                        thirdtime = false;
                    } else if (add3 == 2) {
                        once = false;
                        twice = true;
                        thirdtime = false;
                    } else if (add3 == 3) {
                        once = false;
                        twice = false;
                        thirdtime = true;
                        add3 = 0;
                    }
                    }

                if (once || twice || thirdtime) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容
                    showResponse1("win");

                } else {
                    add3 = 0;
                    if (!content.isEmpty()){
                        if (content.charAt(0) == ' ') {
                            isEnglish = false;
                            Msg msg = new Msg(content, Msg.TYPE_SENT);
                            msgList.add(msg);
                            sendRequestWithOkHttp(content);
                            adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                            msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                            inputText.setText(""); // 清空输入框中的内容
                        } else {
                            if (isEnglish) {
                                Msg msg = new Msg(content, Msg.TYPE_SENT);
                                msgList.add(msg);
                                sendRequestWithOkHttp(content);
                                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                                inputText.setText(""); // 清空输入框中的内容
                            } else {
                                Msg msg = new Msg(content, Msg.TYPE_SENT);
                                msgList.add(msg);
                                sendRequestWithOkHttp1(content);
                                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                                inputText.setText(""); // 清空输入框中的内容
                            }
                        }
                    }

                }
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
                if (isEnglish) {
                    sendRequestWithOkHttp1(response);
                } else {
                    Msg msg = new Msg(response, Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                }

            }
        });
    }

    private void showResponse1(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (once) {
                    String response1 = "I don't know what are you talking about";
                    Msg msg = new Msg(response1, Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    once = false;
                } else if (twice){
                    String response1 = "Are you sure?";
                    Msg msg = new Msg(response1, Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    twice = false;
                } else if (thirdtime) {
                    String response1 = "哈哈哈，恭喜获得彩蛋!I love you too,mua!";
                    Msg msg = new Msg(response1, Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    thirdtime = false;
                } else {
                    if (isEnglish) {
                        sendRequestWithOkHttp(response);
                        isEnglish = false;
                    } else {
                        Msg msg = new Msg(response, Msg.TYPE_RECEIVED);
                        msgList.add(msg);
                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                        msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    }
                }
            }
        });
    }

    private void initMsgs() {
        Msg msg1 = new Msg("hello，无聊的话我可以陪你聊天哦", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("第一个字前加空格我可以给你翻译哦", Msg.TYPE_RECEIVED);
        msgList.add(msg2);
    }

}
