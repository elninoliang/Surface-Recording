package com.example.user.recordtest;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.research.GLRecorder.*;

public class Draw extends GLSurfaceView{

    private EGLConfig mEGLConfig;

    private final ConditionVariable syncObj = new ConditionVariable();

    public Draw(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(GLRecorder.getEGLConfigChooser());
        setRenderer(new Renderer());
    }

    public Draw(Context context ) {
        super(context);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(GLRecorder.getEGLConfigChooser());
        setRenderer(new Renderer());
    }

//    public Draw(Context context)
//    {
//        super(context);
//        setEGLContextClientVersion(2);
//        setEGLConfigChooser(GLRecorder.getEGLConfigChooser());
//        setRenderer(new Renderer());
//    }


    private class Renderer implements GLSurfaceView.Renderer{
        public void onDrawFrame(GL10 gl){

            GLRecorder.beginDraw();

            GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glFinish();

            GLRecorder.endDraw();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height){

            GLRecorder.init(width, height, mEGLConfig);//todo recorder
            GLRecorder.setRecordOutputFile("/sdcard/TestRecord.mp4");
            GLES20.glViewport(0,0,width,height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config){

            mEGLConfig = config;

        }

    }

    public void touchEvent() {

        GLRecorder.startRecording();

    }

    public void onViewPause(ConditionVariable syncObj) {

        GLRecorder.stopRecording();

        syncObj.open();
    }

    @Override
    public void onPause() {

        super.onPause();

        //Log.d(TAG, "asking renderer to pause");
        syncObj.close();
        queueEvent(new Runnable() {
            @Override public void run() {
                onViewPause(syncObj);
            }});
        syncObj.block();

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                queueEvent(new Runnable() {
                    @Override public void run() {
                        touchEvent();
                    }});
                break;
            default:
                break;
        }

        return true;
    }

}
