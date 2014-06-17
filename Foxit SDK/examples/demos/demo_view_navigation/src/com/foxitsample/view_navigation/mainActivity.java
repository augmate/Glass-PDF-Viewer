package com.foxitsample.view_navigation;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.foxitsample.service.*;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class mainActivity extends Activity {

	private static String TAG = "demo_view";
	public WrapPDFFunc func = null;
	public ImageView imageView = null;

	private FoxitDoc myDoc;
	private int currentPage = 0;
	private float xScaleFactor = 1f;
	private float yScaleFactor = 1;
	private int rotateFlag = 0;
	public float fScal = (float) 1;
	
	private GestureDetector mGestureDetector;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);   
        
        imageView = (ImageView)findViewById(R.id.display);
        
      //code start
        try{
        	File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
     	    File file = new File(path, "augmate.pdf");
     	    String fileName = file.getPath();
        	//String fileName = "/mnt/sdcard/readme.pdf";
        	String password = "";
        	int initialMemory = 5 * 1024 * 1024;
      		
      		func = new WrapPDFFunc();
      		func.InitFoxitFixedMemory(initialMemory);
      		func.LoadJbig2Decoder();
      		func.LoadJpeg2000Decoder();
      		func.LoadCNSFontCMap();
      		func.LoadKoreaFontCMap();
      		func.LoadJapanFontCMap();
      		     		
      		myDoc = func.createFoxitDoc(fileName, password);
      		myDoc.CountPages();
      		Display display = getWindowManager().getDefaultDisplay();
      		imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, display.getWidth(), display.getHeight(), xScaleFactor, yScaleFactor,0));
      		imageView.invalidate();
      		
      		mGestureDetector = createGestureDetector(this);
        } catch (Exception e){
        	/* In this demo, we decide do nothing for exceptions
        	 * however, you will want to handle exceptions in some way*/
        	postToLog(e.getMessage());	
      	}
    }

	@Override
	protected void onDestroy() {
		try {
			myDoc.close();
			func.DestroyFoxitFixedMemory();
		} catch (Exception e){
			System.exit(0);
		}
		
		super.onDestroy();
	}   
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
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
	                	Log.d("Darien", "ZoomIN");
	                	if(fScal >=1.5)
		  		 		  return true;
	                	fScal += 0.25;
	                	if(fScal >= 5)
	                		fScal = 5;
	                	imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, nPageWidth*fScal, nPageHeight*fScal,rotateFlag,-1));
	                	imageView.invalidate();	  
	                	return true;
	                } else if (gesture == Gesture.TWO_TAP) {
	                    // do something on two finger tap
	                    return true;
	                } else if (gesture == Gesture.SWIPE_LEFT) {
	                	Log.d("Darien", "Page Back");
	                	if(currentPage==0)
	    					return true;
	    				
	    				currentPage--;
	    				imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
	    						getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));				
	    				imageView.invalidate();	     
	                    return true;
	                } else if (gesture == Gesture.SWIPE_RIGHT) {
	                	Log.d("Darien", "Page Forward");
	                	if(currentPage+1==myDoc.CountPages())
	    					return true;

	    				currentPage++;
	    				imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
	    						getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));				
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
	            	/*
	            	if(displacement > 0){
	            		Log.d("Darien", Float.toString(displacement));
	            		viewer.zoomIn(null);
	            	}
	            	else{
	            		viewer.zoomOut(null);
	            	}
	            	*/
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

	
	public boolean onOptionsItemSelected(MenuItem item)
	{ 
		try{
			switch (item.getItemId()) 
			{

			case R.id.PreviousPage:
				if(currentPage==0)
					return true;
				
				currentPage--;
				imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
						getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));				
				imageView.invalidate();	        	
				break;
			case R.id.NextPage:
				if(currentPage+1==myDoc.CountPages())
					return true;

				currentPage++;
				imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, getWindowManager().getDefaultDisplay().getWidth(),
						getWindowManager().getDefaultDisplay().getHeight(), xScaleFactor, yScaleFactor,0));				
				imageView.invalidate();	        	
				break;
			}
		}catch (Exception e){
			postToLog(e.getMessage());
		}
		return true;
	}
    
	private void postToLog(String msg){
		Log.v(TAG,msg);
	}
}