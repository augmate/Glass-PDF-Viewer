package com.yangyang.reader.view;

import java.io.File;

import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.PointF;
import FoxitEMBSDK.EMBJavaSupport.RectangleF;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;

import com.foxitsdk.exception.memoryException;
import com.foxitsdk.service.FoxitDoc;
import com.foxitsdk.service.WrapPDFFunc;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.yangyang.reader.R;
import com.yangyang.reader.util.Constant;
import com.yangyang.reader.util.ZoomStatus;
import com.yangyang.reader.view.OpenFileDialog.CallbackBundle;


public class MainActivity extends Activity implements SensorEventListener{

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
	float nPageWidth;
	float nPageHeight;
	private GestureDetector mGestureDetector;
	private SensorManager mSensorManager;
	private boolean gyroActive = false;
	private int gyroSense = 120;
	private int gyroCount = 0;
	private int gyroThreshold = 10;
	private float gyroX;
	private float gyroY;
	
    private ZoomStatus zoomStatus;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		
	    imageView = new PDFView(getApplicationContext());
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(imageView);
	    		
		/*code start
		 * The following code pulls a PDF named 'augmate2.pdf' from the devices Pictures library and the initializes a FoxitDoc object from the PDF. Using the dimensions of the 
		 * PDF, a bitmap is generated. After the gyroscope is initialized 
	    */
		try {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
     	    File file = new File(path, "augmate2.pdf");
     	    String fileName = file.getPath();
        	String password = "";
        	int initialMemory = 5 * 1024 * 1024;
  
        	
      		func = new WrapPDFFunc(this);
      		func.InitFoxitFixedMemory(initialMemory);      		
      		func.LoadJbig2Decoder();
      		func.LoadJpeg2000Decoder();
      		func.LoadCNSFontCMap();
      		func.LoadKoreaFontCMap();
      		func.LoadJapanFontCMap();
      		
      		myDoc = func.createFoxitDoc(fileName, password);
      		myDoc.CountPages();
      		nPageWidth = myDoc.GetPageSizeX(currentPage);
       	  	nPageHeight = myDoc.GetPageSizeY(currentPage);
      		Display display = getWindowManager().getDefaultDisplay();
      		@SuppressWarnings("deprecation")
			int nHeight = display.getHeight() ;
      		fScal = nHeight / myDoc.GetPageSizeY(currentPage);
      		//imageView.setDisplay(display.getWidth()-nPageWidth, 0);
      		//imageView.setBitmap(myDoc.getPageBitmap(currentPage, display.getWidth(), display.getHeight(), xScaleFactor, yScaleFactor,0));
      		imageView.setDirtyBitmap(myDoc.getPageBitmap(currentPage, myDoc.GetPageSizeX(currentPage)* fScal, nHeight,rotateFlag,M_FitHeight));
      		//getWindow().setLayout((int) (myDoc.GetPageSizeX(currentPage)*fScal),nHeight);
      		imageView.invalidate();      		
      		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    	    mGestureDetector = createGestureDetector(this);
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

	
	/*
	 * When the camera button is pressed, the gyroscope is activated. The gyroscope allows the user to scan the PDF using head movements. When the button is pressed again, the
	 * gyroscope is deactivated. 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_CAMERA) {
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
	    				  //Bitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
		    					//	getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));	
		   	        	  imageView.setDirtyBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
		   	        	  imageView.SetMartix(0, 0);
		   	        	  imageView.invalidate();	 
		   	      		  //getWindow().setLayout((int) (myDoc.GetPageSizeX(currentPage)* fScal), getWindowManager().getDefaultDisplay().getHeight());

	                    return true;
	                } else if (gesture == Gesture.TWO_TAP) {
	                	Log.d("Darien", "Zoom Out");
	                	if(fScal <=0.25)
			   	 			return true;
			   	 		fScal -= 0.25;
			   	 		if(fScal <0.25)
			   	 			fScal =(float) 0.25;
			   	 		//imageView.setBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
    						//getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));
			 	        imageView.setDirtyBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
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
	    				imageView.setDirtyBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
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
	    				imageView.setDirtyBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
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
		gyroCount++;
		if (gyroCount > 10 && event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
		{
			gyroX = event.values[1] * gyroSense * fScal;
			gyroY = event.values[0] * gyroSense * fScal;
			//if(2 < Math.abs(gyroX) || 2 < Math.abs(gyroY)){
				imageView.SetMartix(gyroX, gyroY);
				//imageView.SetMartix(-20,-20);
				imageView.invalidate();
				//gyroLock = !gyroLock;
			//}
			gyroCount = 0;
		}
	}

	private void postToLog(String msg){
		Log.v(TAG,msg);
	}
	public void invalidate(float left, float top, float right, float bottom) {
        if (right - left == 0 || bottom - top == 0)
                return;
        int l, t, r, b;
        RectangleF rect = new EMBJavaSupport().new RectangleF();
        rect.left = left;
        rect.top = top;
        rect.right = right;
        rect.bottom = bottom;
        EMBJavaSupport.FPDFPagePageToDeviceRectF(func.getCurPDFPageHandler(),
                        0, 0, this.zoomStatus.getDisplayWidth(),
                        this.zoomStatus.getDisplayHeight(), 0, rect);
        l = (int) rect.left;
        t = (int) rect.top;
        r = (int) rect.right;
        b = (int) rect.bottom;
        Rect rc = new Rect(l, t, r, b);
        imageView.setDirtyRect(l, t, r, b);
        imageView.setDirtyBitmap(func.getDirtyBitmap(rc,
                        this.zoomStatus.getDisplayWidth(),
                        this.zoomStatus.getDisplayHeight()));
        imageView.OnDraw();

	}
	 public void createAndroidTextField(String text) {
         Intent intent = new Intent();
         Bundle bundle = new Bundle();
         bundle.putString("textValue", text);
         intent.setClass(this, textfieldActivity.class);
         intent.putExtra("key", bundle);
         this.startActivityForResult(intent, 0);
	 }

}
