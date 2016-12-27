package com.example.user.recordtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.research.GLRecorder.GLRecorder;

import Permission.PermissionsUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static Activity instance;
    private Draw mDrawView;
    private static final int START_RECORDING = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PermissionsUtil.checkAndRequestPermissions(this);
        super.onCreate(savedInstanceState);
        instance = this;
//        mDrawView = new Draw(getApplication());
//        setContentView(mDrawView);

        ///////////////////////////////////////////////////
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_start).setOnClickListener(this);
        findViewById(R.id.button_end).setOnClickListener(this);
        mDrawView = (Draw)findViewById(R.id.glContent);

        ////////////////////////////////////////////////////

    }

//    @Override
//    protected void onPause() {
//
//        super.onPause();
//        mDrawView.onPause();
//
//    }
//leave screen for pause and stop recording



    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_start:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() { //　新建一个线程，并新建一个Message的对象，是用Handler的对象发送这个Message
//
//                    }
//                }).start();
//                Message msg = new Message();
//                msg.what = START_RECORDING; // 用户自定义的一个值，用于标识不同类型的消息
//                mDrawView.myHandler.sendMessage(msg); // 发送消息
                mDrawView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        GLRecorder.startRecording();
                    }});
                //
                break;
            case R.id.button_end:
                mDrawView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        GLRecorder.stopRecording();
                    }});

                break;
            default:
                break;
        }
    }
}
