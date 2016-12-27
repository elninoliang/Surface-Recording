package com.example.user.recordtest;

import android.annotation.SuppressLint;
import android.app.Activity;
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
//
//    @Override
//    protected void onResume() {
//
//        super.onResume();
//        mDrawView.onResume();
//    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_start:
                GLRecorder.startRecording();
                break;
            case R.id.button_end:
                GLRecorder.stopRecording();
                break;
            default:
                break;
        }
    }
}
