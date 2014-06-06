package com.example.pdfviewer;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.qoppa.android.pdfProcess.PDFPage;
import com.qoppa.viewer.QPDFViewerView;
import com.qoppa.viewer.views.PDFPageView;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.os.Build;

public class MainActivity extends Activity implements SensorEventListener {
	
	private QPDFViewerView viewer;
	private PDFPage page;
	private GestureDetector mGestureDetector;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mGyroscope;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		viewer = new QPDFViewerView (this);
	    viewer.setActivity(this);
	    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File file = new File(path, "augmate.pdf");
	    viewer.loadDocument(file.getPath());
	    viewer.showHideToolbar();
	    viewer.setMaximumScale(30);
	    
	    mGestureDetector = createGestureDetector(this);
	    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Log.d("Darien", "Start");
		//mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
	    setContentView(viewer);
		
		/*
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		*/
	}
	
	 private GestureDetector createGestureDetector(Context context) {
		    GestureDetector gestureDetector = new GestureDetector(context);
		        //Create a base listener for generic gestures
		        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
		            @Override
		            public boolean onGesture(Gesture gesture) {
		                if (gesture == Gesture.TAP) {
		                	viewer.nextPage();
		                	//page = viewer.getPageView(viewer.getCurrentPageNumber()).getPage();
		                	Log.d("Darien", "Tap to turn page");
		                    return true;
		                } else if (gesture == Gesture.TWO_TAP) {
		                    // do something on two finger tap
		                    return true;
		                } else if (gesture == Gesture.SWIPE_RIGHT) {
		                	Log.d("Darien", "Zoom In");
		                	viewer.zoomIn(null);
		                    return true;
		                } else if (gesture == Gesture.SWIPE_LEFT) {
		                	Log.d("Darien", "Zoom Out");
		                	viewer.zoomOut(null);
		                    return true;
		                } else if (gesture == Gesture.SWIPE_DOWN) {
		                	android.os.Process.killProcess(android.os.Process.myPid());
		                    System.exit(1);
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
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
		{
			
			//mAccVals.z = (float) (event.values[2] * FILTERING_FACTOR + mAccVals.z * (1.0 - FILTERING_FACTOR) );
			page.convPoint(event.values[1]*.1f, event.values[0]*.1f);
			Log.d("Darien", "Gyro test");
			//scene.camera().target.x = scene.camera().target.x - event.values[1]*.1f;
	        //scene.camera().target.y = scene.camera().target.y + event.values[0]*.1f;
	        //scene.camera().target.z = scene.camera().target.z + -event.values[2]*.1f;
		}
		else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
		{
			//mAccVals.x = (float) (event.values[1] * FILTERING_FACTOR + mAccVals.x * (1.0 - FILTERING_FACTOR) );
			//mAccVals.y = (float) (event.values[0] * FILTERING_FACTOR + mAccVals.y * (1.0 - FILTERING_FACTOR) );
			
			
			
			//scene.camera().position.y = scene.camera().position.y + event.values[1]*.1f;
			//scene.camera().position.x = scene.camera().position.x + event.values[0]*((float)0.8);
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//if (id == R.id.action_settings) {
		//	return true;
		//}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
