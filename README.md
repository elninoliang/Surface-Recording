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

## The module code you can use

**1.permission problem after API 21**   
After Android 5.0, you need to allocate android dynamic permission    
Add this java to your project    
*app\src\main\java\Permission\PermissionsUtil.java*     
Then add this code in your main.java          
`PermissionsUtil.checkAndRequestPermissions(this);`

**2.Get max video saving size for different android device**

This is module code:

  int flag = 0;

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
                          Log.e(TAG, "maxWidth: " + vidCaps.getSupportedWidths().getUpper());
                          Log.e(TAG, "maxHeight: " + vidCaps.getSupportedHeights().getUpper());

                 if(flag == 0)
                 {
                     this.max_width = vidCaps.getSupportedWidths().getUpper();
                     this.max_height = vidCaps.getSupportedHeights().getUpper();
                 }
                 flag++;
             }
         }
     }

         

## Problem may be upset you

**1. If API>19, got error "EGL_BAD_SURFACE"**

This is the UI thread and OPENGL thread getting confict, so you need change UI thread like:     
``case R.id.button_end:
  mDrawView.queueEvent(new Runnable() {
   @Override
   public void run() {
       GLRecorder.stopRecording();
   }});
break;``

**2.If the frame is wrong, like always flash**

You could changed this code in GLRecorder.java    

``if (mTick % 2 == 0) {
    ++mTick;
    return;
}
++mTick;``

**3.If the matrix you want to change, like you want to change video size or you want to make different between display and saving**

You could modify RecordMatrix and DislayMatrix,like add view chaned api.    

`RecordMatrix = new float[16];
Matrix.setIdentityM(RecordMatrix, 0);
Matrix.scaleM(RecordMatrix,0,width_ratio,height_ratio,1.0f);``
