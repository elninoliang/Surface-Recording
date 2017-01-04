# Reocord Demo

This is a record video sample.

Make sure your APP project compile with Android API level 18 or above.

## Usage

1. Add this project as a library for your project.
2. Add below permissions in your AndroidManifest.xml
3. Import import com.research.GLRecorder.GLRecorder in your GLSurface Render.
4. Set EGLConfigChooser Provider by GLRecorder before setRender of GLSurfaceView
 `  setEGLConfigChooser(GLRecorder.getEGLConfigChooser());  
    setRenderer(YourRender);`
5. Initialize GLRecorder at Surface size determine like onSurfaceCreated

`` public void onSurfaceCreated(GL10 unused, EGLConfig config) {
         mEGLConfig = config;
         // Some other code.
   }``
6. Insert GLRecord.beginDraw() before your game begins drawing its frame, and GLRecord.endDraw() when your game has finished drawing its frame

 `GLRecorder.beginDraw();
 draw();     // Draw game frame
 GLRecorder.endDraw();``
7. Start Recording and Stop it at appropriate time


## Problem may be upset you

**1. If API>19, got error "EGL_BAD_SURFACE"**

This is the UI thread and OPENGL thread getting confict, so you need change UI thread like:

 '
 case R.id.button_end:
        mDrawView.queueEvent(new Runnable() {
           @Override
           public void run() {
               GLRecorder.stopRecording();
           }});
       break;
 '

**2.If the frame is wrong, like always flash**

You could changed this code in GLRecorder.java

'
    if (mTick % 2 == 0) {
        ++mTick;
        return;
    }
    ++mTick;
'

**3.If the matrix you want to change, like you want to change video size or you want to make different between display and saving**

You could modify RecordMatrix and DislayMatrix,like add view chaned api.

'
RecordMatrix = new float[16];
Matrix.setIdentityM(RecordMatrix, 0);
Matrix.scaleM(RecordMatrix,0,width_ratio,height_ratio,1.0f);
'
