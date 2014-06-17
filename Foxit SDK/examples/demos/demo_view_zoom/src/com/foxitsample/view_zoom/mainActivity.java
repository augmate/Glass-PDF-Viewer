package com.foxitsample.view_zoom;

import java.io.File;

import FoxitEMBSDK.EMBJavaSupport;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

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
	private float xScaleFactor = 1f;
	private float yScaleFactor = 1f;
	
	private float PreoffsetX = 0;
	private	float PreoffsetY = 0;
	private float CuroffsetX = 0;
	private float CuroffsetY = 0;
	
	private GestureDetector mGestureDetector;
	private SensorManager mSensorManager;
	private Sensor mGyroscope;
	private boolean gyroLock = true;
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
     	    File file = new File(path, "augmate.pdf");
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
      		Display display = getWindowManager().getDefaultDisplay();
      		int nHeight = display.getHeight() ;
      		fScal = nHeight / myDoc.GetPageSizeY(currentPage);
      		imageView.setBitmap(myDoc.getPageBitmap(currentPage, display.getWidth(), display.getHeight(), xScaleFactor, yScaleFactor,0));
      		//imageView.setBitmap(myDoc.getPageBitmap(currentPage, myDoc.GetPageSizeX(currentPage)* fScal, nHeight,rotateFlag,M_FitHeight));
      		imageView.invalidate();
      		
      		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    	    mGestureDetector = createGestureDetector(this);
    	    mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    	    mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
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

	public boolean onCreateOptionsMenu(Menu menu) 
	{	
		menu.add(0, M_FitWidth, 0, "Fit Width");
		menu.add(0, M_FitHeight, 1,"Fit Height");
		menu.add(0, M_ActiualSize,2,"Actiual Size");
		menu.add(0, M_ZoomIn,3,"Zoom In");
		menu.add(0, M_ZoomOut,4,"ZoomOut");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{ 
		float nPageWidth = -1;
		float nPageHeight = -1;
		nPageWidth = myDoc.GetPageSizeX(currentPage);
   	  	nPageHeight = myDoc.GetPageSizeY(currentPage);
   	  	if(nPageWidth == 0 || nPageHeight == 0)
   		  return true;
		Display display = getWindowManager().getDefaultDisplay();
		float nWidth = display.getWidth();
		float nHeight = display.getHeight();	
		int nZoomFlag = -1;
		
		
		switch (item.getItemId() ) 
		{
			//flag = 0
	        case M_FitWidth:
	        	  nZoomFlag = myDoc.GetCurZoomFlag();
	        	  if(nZoomFlag == M_FitWidth)
	        		  return true;
	        	  fScal = nWidth / nPageWidth;
	        	  nHeight =  (nPageHeight * fScal);
	        	  imageView.setBitmap(myDoc.getPageBitmap(currentPage,nWidth, nHeight,rotateFlag,M_FitWidth));
	        	  imageView.invalidate();	        	  
	      		break;
		    case M_FitHeight:	    	
		    	 nZoomFlag = myDoc.GetCurZoomFlag();
	        	  if(nZoomFlag == M_FitHeight)
	        		  return true;
	        	  fScal = nHeight / nPageHeight;
	        	  nWidth =  (nPageWidth * fScal);
	        	  imageView.setBitmap(myDoc.getPageBitmap(currentPage,nWidth, nHeight,rotateFlag,M_FitHeight));
	        	  imageView.invalidate();	    	
	      		break;
		    case M_ActiualSize:
		    	 nZoomFlag = myDoc.GetCurZoomFlag();
	        	  if(nZoomFlag == M_ActiualSize)
	        		  return true;
	        	  fScal = 1;
	        	  imageView.setBitmap(myDoc.getPageBitmap(currentPage,nPageWidth, nPageHeight,rotateFlag,M_ActiualSize));
	        	  imageView.invalidate();	  
		    	break;		    	
		    case M_ZoomIn:
		    	  if(fScal >=1.5)
		    	  {
		    		  return true;
		    	  }
	        	  fScal += 0.25;
	        	  if(fScal >= 5)
	        		  fScal = 5;
	        	  imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
	        		imageView.invalidate();	  
		    	break;
		    case M_ZoomOut:
		   	 		if(fScal <=0.25)
		   	 		{
		   	 			return true;
		   	 		}
		   	 		fScal -= 0.25;
		   	 		if(fScal <0.25)
		   	 			fScal =(float) 0.25;
		 	        imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, 
		 	        			 nPageHeight*fScal,rotateFlag,-1));
		 	        imageView.invalidate();	
		    	break;
		}
		return true;
	}
	 @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_CAMERA) {
        	Log.d("Darien", "Gyro LOCK");
            gyroLock = !gyroLock;
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
	
	private GestureDetector createGestureDetector(Context context) {
	    GestureDetector gestureDetector = new GestureDetector(context);
	        //Create a base listener for generic gestures
	        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
	            @Override
	            public boolean onGesture(Gesture gesture) {
	            	float nPageWidth = -1;
	        		float nPageHeight = -1;
	        		nPageWidth = myDoc.GetPageSizeX(currentPage);
	           	  	nPageHeight = myDoc.GetPageSizeY(currentPage);
	           	  	if(nPageWidth == 0 || nPageHeight == 0)
	           		  return true;
	        		Display display = getWindowManager().getDefaultDisplay();
	        		float nWidth = display.getWidth();
	        		float nHeight = display.getHeight();	
	        		int nZoomFlag = -1;
	    	          if (gesture == Gesture.TAP) {
	    	        	  Log.d("Darien", "Zoom In");
	    	        	  if(fScal >=1.5)
		   		    		  return true;
		   	        	  fScal += 0.25;
		   	        	  if(fScal >= 5)
		   	        		  fScal = 5;
		   	        	imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
	    						getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));
		   	        	imageView.SetMartix(0, 0);
		   	        	//imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
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
			   	 		imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
    						getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));
			 	        //imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
			 	        imageView.invalidate();	
	                    return true;
	                } else if (gesture == Gesture.SWIPE_LEFT) {
	                	Log.d("Darien", "Page Back");
	                	if(currentPage==0)
	    					return true;
	    				
	    				currentPage--;
	    				imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
	    						getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));
	    				//imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
	    				imageView.invalidate();	     
	                    return true;
	                } else if (gesture == Gesture.SWIPE_RIGHT) {
	                	Log.d("Darien", "Page Forward");
	                	if(currentPage+1==myDoc.CountPages())
	    					return true;

	    				currentPage++;
	    				imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
	    						getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));
	    				//imageView.setBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
	    				imageView.invalidate();	  
	                    return true;
	                }
	                return false;
	            }
	        });
	        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
	            @Override
	            public void onFingerCountChanged(int previousCount, int currentCount) {
	              // do something on finger count changes
	            }
	        });
	        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
	            @Override
	            public boolean onScroll(float displacement, float delta, float velocity) {
	            	
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
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
		{
			
			//mAccVals.z = (float) (event.values[2] * FILTERING_FACTOR + mAccVals.z * (1.0 - FILTERING_FACTOR) );
			//Log.d("Darien", "Gyro test");
			//Log.d("Darien", "X:" + event.values[1] + " Y:" + event.values[0]);
			if(!gyroLock){
				/*
				CuroffsetX = event.values[1]*.1f;  
				CuroffsetY = event.values[0]*.1f; 
				imageView.SetMartix(CuroffsetX - PreoffsetX,CuroffsetY - PreoffsetY);        	
				imageView.invalidate();
				PreoffsetX = CuroffsetX;
				PreoffsetY = CuroffsetY;
				*/
				imageView.SetMartix(event.values[1],event.values[0]);        	
				imageView.invalidate();
			}
		}
		else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
		{
			//mAccVals.x = (float) (event.values[1] * FILTERING_FACTOR + mAccVals.x * (1.0 - FILTERING_FACTOR) );
			//mAccVals.y = (float) (event.values[0] * FILTERING_FACTOR + mAccVals.y * (1.0 - FILTERING_FACTOR) );
			
			
			
			//scene.camera().position.y = scene.camera().position.y + event.values[1]*.1f;
			//scene.camera().position.x = scene.camera().position.x + event.values[0]*((float)0.8);
		}
	}

	private void postToLog(String msg){
		Log.v(TAG,msg);
	}

}