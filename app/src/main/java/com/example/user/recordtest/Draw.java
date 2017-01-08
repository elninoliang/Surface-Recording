package com.example.user.recordtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.ConditionVariable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Range;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.research.GLRecorder.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;

import static android.content.ContentValues.TAG;


public class Draw extends GLSurfaceView{

    private EGLConfig mEGLConfig;

    private final ConditionVariable syncObj = new ConditionVariable();

    private int phone_height;
    private int phone_width;
    private int max_width;
    private int max_height;

    private int times = 0;

    private void drawcolor()//draw picture in screen
    {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);
        GLES20.glClearColor(0.6f, 1.0f, 0.2f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        GLES20.glFinish();
    }

    private  void chooseViewport()//According to mRecordingEnabled to choose viewport
    {
        if(GLRecorder.mRecordingEnabled)
        {
            GLES20.glViewport(0,0,max_width, max_height);
        }
        else
        {
            GLES20.glViewport(0,0,phone_width, phone_height);
        }
    }


    private void getSize()
    {
        int flag = 0;

//            todo capabilities code
        for(MediaCodecInfo codecInfo : new MediaCodecList(MediaCodecList.ALL_CODECS).getCodecInfos()){
            if(!codecInfo.isEncoder())
                continue;
            String[] types = codecInfo.getSupportedTypes();
            for(int j=0;j<types.length;j++){
                if("video/avc".equalsIgnoreCase(types[j])){
                    MediaCodecInfo.CodecCapabilities codecCaps = codecInfo.getCapabilitiesForType("video/avc");
                    MediaCodecInfo.VideoCapabilities vidCaps = codecCaps.getVideoCapabilities();
                    Range<Integer> framerates = vidCaps.getSupportedFrameRates();
                    Range<Integer> widths = vidCaps.getSupportedWidths();
                    Range<Integer> heights = vidCaps.getSupportedHeights();
                    Log.d("H.264Encoder", "Found encoder with\n" + widths.toString()
                            + " x " + heights.toString() + " @ " + framerates.toString() + " fps aligned to " + vidCaps.getWidthAlignment());
//                        Log.e(TAG, "maxWidth: " + vidCaps.getSupportedWidths().getUpper());
//                        Log.e(TAG, "maxHeight: " + vidCaps.getSupportedHeights().getUpper());

                    if(flag == 0)
                    {
                        this.max_width = vidCaps.getSupportedWidths().getUpper();
                        this.max_height = vidCaps.getSupportedHeights().getUpper();
                    }
                    flag++;
                }
            }
        }


    }

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


    private class Renderer implements GLSurfaceView.Renderer{
        public void onDrawFrame(GL10 gl){

            GLRecorder.beginDraw();

            chooseViewport();

            drawcolor();

            GLRecorder.endDraw();

        }

        public void onSurfaceChanged(GL10 gl, int width, int height){


            getSize();//get maxwidth and maxheight

            Log.e(TAG, "maxWidth: " + max_width);
            Log.e(TAG, "maxHeight: " + max_height);

            phone_width = width;
            phone_height = height;

            float width_ratio = (float) max_width/(float)width;
            float height_ratio = (float)max_height/(float)height;

            Log.e(TAG, "width_ratio: " + width_ratio);
            Log.e(TAG, "height_ratio: " + height_ratio);

            GLRecorder.init(max_width, max_height, width_ratio,height_ratio, mEGLConfig);//todo recorder
            GLRecorder.setRecordOutputFile("/sdcard/Record.mp4");


        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config){

            mEGLConfig = config;

        }

    }

    public void touchEvent() {


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
