package com.foxitsample.view_zoom;

import java.io.File;

import FoxitEMBSDK.EMBJavaSupport;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.foxitsample.service.FoxitDoc;
import com.foxitsample.service.WrapPDFFunc;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class mainActivity extends Activity implements SensorEventListener{

	private static String TAG = "demo_view";
	public WrapPDFFunc func = null;
	private FoxitDoc myDoc;
	private int currentPage =0;
	private int rotateFlag = 0;
	public PDFView imageView = null;
	public float fScal = (float) 1;
	static final int M_FitWidth = 0;
	static final int M_FitHeight = 1;
	static final int M_ActiualSize =2;
	static final int M_ZoomIn = 3;
	static final int M_ZoomOut = 4;
	static final int M_ZoomInMax = 5;
	static final int M_ZoomOutMax = 6;
	
	float nPageWidth = -1;
	float nPageHeight = -1;
	
	private GestureDetector mGestureDetector;
	private SensorManager mSensorManager;
	private boolean gyroActive = false;
	private int gyroSense = 20;
	private float gyroX;
	private float gyroY;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		
	    imageView = new PDFView(getApplicationContext());
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(imageView);
	    		
		//code start
		try {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
     	    File file = new File(path, "augmate2.pdf");
     	    String fileName = file.getPath();
        	String password = "";
        	int initialMemory = 5 * 1024 * 1024;
  
        	
      		func = new WrapPDFFunc();
      		func.InitFoxitFixedMemory(initialMemory);      		
      		func.LoadJbig2Decoder();
      		func.LoadJpeg2000Decoder();
      		func.LoadCNSFontCMap();
      		func.LoadKoreaFontCMap();
      		func.LoadJapanFontCMap();
      		//func.SetFontFileMap(strFontFilePath);
      		
      		myDoc = func.createFoxitDoc(fileName, password);
      		myDoc.CountPages();
      		nPageWidth = myDoc.GetPageSizeX(currentPage);
       	  	nPageHeight = myDoc.GetPageSizeY(currentPage);
      		Display display = getWindowManager().getDefaultDisplay();
      		@SuppressWarnings("deprecation")
			int nHeight = display.getHeight() ;
      		fScal = nHeight / myDoc.GetPageSizeY(currentPage);
      		//imageView.setBitmap(myDoc.getPageBitmap(currentPage, display.getWidth(), display.getHeight(), xScaleFactor, yScaleFactor,0));
      		imageView.setBitmap(myDoc.getPageBitmap(currentPage, myDoc.GetPageSizeX(currentPage)* fScal, nHeight,rotateFlag,M_FitHeight));
      		imageView.invalidate();
      		
      		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    	    mGestureDetector = createGestureDetector(this);
    	    //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
		} catch (Exception e){
			/* In this demo, we decide do nothing for exceptions
			 * however, you will want to handle exceptions in some way*/
			postToLog(e.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		try{
			myDoc.close();
			EMBJavaSupport.FSMemDestroyMemory();
		} catch (Exception e){
			System.exit(0);
		}

		super.onDestroy();
	}

	
	 @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_CAMERA) {
        	Log.d("Darien", "Gyro LOCK " + mSensorManager.getSensors());
        	gyroActive = !gyroActive;
        	if(gyroActive) 
        		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
        	else
        		mSensorManager.unregisterListener(this);
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
	
	private GestureDetector createGestureDetector(Context context) {
	    GestureDetector gestureDetector = new GestureDetector(context);
	        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
	            @Override
	            public boolean onGesture(Gesture gesture) {
	    	          if (gesture == Gesture.TAP) {
	    	        	  Log.d("Darien", "Zoom In");
	    	        	  if(fScal >=1.5)
		   		    		  return true;
		   	        	  fScal += 0.25;
		   	        	  if(fScal >= 5)
		   	        		  fScal = 5;
			   	          //imageView.se//imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
	    				  //tBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
		    					//	getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));	
		   	        	  imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
		   	        	  imageView.SetMartix(0, 0);
		   	        	  imageView.invalidate();	 
	                    return true;
	                } else if (gesture == Gesture.TWO_TAP) {
	                	Log.d("Darien", "Zoom Out");
	                	if(fScal <=0.25)
			   	 		{
			   	 			return true;
			   	 		}
			   	 		fScal -= 0.25;
			   	 		if(fScal <0.25)
			   	 			fScal =(float) 0.25;
			   	 		//imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
    						//getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));
			 	        imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
			 	        imageView.SetMartix(0, 0);
			 	        imageView.invalidate();	
	                    return true;
	                } else if (gesture == Gesture.SWIPE_LEFT) {
	                	Log.d("Darien", "Page Back");
	                	if(currentPage==0)
	    					return true;
	    				
	    				currentPage--;
	    				//imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
	    						//getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));
	    				imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
	    				imageView.SetMartix(0, 0);
	    				imageView.invalidate();	     
	                    return true;
	                } else if (gesture == Gesture.SWIPE_RIGHT) {
	                	Log.d("Darien", "Page Forward");
	                	if(currentPage+1==myDoc.CountPages())
	    					return true;

	    				currentPage++;
	    				//imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
	    						//getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));
	    				imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
	    				imageView.SetMartix(0, 0);
	    				imageView.invalidate();	  
	                    return true;
	                }
	                return false;
	            }
	        });
	       return gestureDetector;
    }
    
	
	/*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
		{
			//Log.d("Darien", "Gyro test");
			//Log.d("Darien", "X:" + event.values[1] + " Y:" + event.values[0]);
			gyroX = event.values[1] * gyroSense;
			gyroY = event.values[0] * gyroSense;
			//imageView.SetMartix(-20,-20);
			//imageView.SetMartix(20,20);
			if(2 < Math.abs(gyroX) || 2 < Math.abs(gyroY)){
				imageView.SetMartix(gyroX, gyroY);
				//imageView.SetMartix(-20,-20);
				imageView.invalidate();
				//gyroLock = !gyroLock;
			}
		}
	}

	private void postToLog(String msg){
		Log.v(TAG,msg);
	}

}