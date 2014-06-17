package com.foxitsample.text_search;
import com.foxitsample.exception.*;
import com.foxitsample.service.TextDoc;
import com.foxitsample.text_search.FPDFView;
import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.Rectangle;
import FoxitEMBSDK.EMBJavaSupport.RectangleF;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class mainActivity extends Activity {
    /** Called when the activity is first created. */
	/** instance variables*/
	private static String TAG = "demo_text_search";
	public FPDFView imageView = null;
	private int currentPage;
	private float xScaleFactor;
	private float yScaleFactor;
	private int rotateFlag;
	private TextDoc myDoc;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageView = new FPDFView(getApplicationContext());
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(imageView);
		
		//code start     		
		try {
			String fileName = "/mnt/sdcard/FoxitText.pdf";
			String strFontFilePath = "/mnt/sdcard/DroidSansFallback.ttf";
			String password = "";
			currentPage = 0;
			xScaleFactor = 1f;
			yScaleFactor = 1f;
			rotateFlag = 0;

			int initialMemory = 5 * 1024 * 1024;

			InitFoxitFixedMemory(initialMemory);
			SetDecodersAndMaps();
			SetFontFileMap(strFontFilePath);

			//must be done AFTER initializing engine
			myDoc = new TextDoc(fileName, password);
			myDoc.CountPages();
			myDoc.InitPage(0);
			myDoc.InitPDFTextPage();

			Display display = getWindowManager().getDefaultDisplay();
			
			imageView.setBitmap(myDoc.getPageBitmap(currentPage, display.getWidth(),display.getHeight(), xScaleFactor, yScaleFactor, rotateFlag));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.invalidate();
		} 
		catch (Exception e) {
			/* In this demo, we decide do nothing for exceptions
			 * however, you will want to handle exceptions in some way*/
			postToLog(e.getMessage());
		} 
	}

	/*
	 * OnDestroy closes all pages.
	 */
	protected void onDestroy() {
		try {
			myDoc.close();
			DestroyFoxitFixedMemory();
		} catch (Exception e){
			System.exit(0);
		}
		super.onDestroy();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{ 
		int rectnum = 0;
		switch (item.getItemId()) 
		{			
	        case R.id.Previous:
	        	rectnum = myDoc.FindPrev();
	        	if(rectnum == 0) return true;
		    	for(int i= 0;i<rectnum;i++)
		    	{
		    		RectangleF rect = (new EMBJavaSupport()).new RectangleF();
		    		rect = myDoc.GetHighLightMarkedRect(i);
		    		//init a blue color bitmap
		    		int width = (int)(rect.right - rect.left);
		    		int stride = (int)width*4;
		    		int height = (int)(rect.bottom - rect.top);
		    		Bitmap map = myDoc.GetHighLightMarkedRectBitmap(width,height,stride);
		    		imageView.setHighLightBitmap(map);	
		    		imageView.setHighLightBitmapXY((int)rect.left, (int)rect.top);
		      		imageView.invalidate(); 
		    	}		    	
	      		break;
		    case R.id.Search:	
		    	rectnum = myDoc.SearchStart();
		    	if(rectnum == 0) return true;
		    	for(int i= 0;i<rectnum;i++)
		    	{
		    		EMBJavaSupport.RectangleF rect = (new EMBJavaSupport()).new RectangleF();
		    		rect = myDoc.GetHighLightMarkedRect(i);
		    		//init a blue color bitmap
		    		int width = (int)(rect.right - rect.left);
		    		int stride = (int)width*4;
		    		int height = (int)(rect.bottom - rect.top);
		    		Bitmap map = myDoc.GetHighLightMarkedRectBitmap(width,height,stride);
		    		imageView.setHighLightBitmap(map);	
		    		imageView.setHighLightBitmapXY((int)rect.left, (int)rect.top);
		      		imageView.invalidate(); 
		    	}
	      		break;
		    case R.id.Next:
		    	rectnum = myDoc.FindNext();		    	
		    	if(rectnum == 0) return true;
		    	for(int i= 0;i<rectnum;i++)
		    	{
		    		RectangleF rect = (new EMBJavaSupport()).new RectangleF();
		    		rect = myDoc.GetHighLightMarkedRect(i);
		    		//init a blue color bitmap
		    		int width = (int)(rect.right - rect.left);
		    		int stride = (int)width*4;
		    		int height = (int)(rect.bottom - rect.top);
		    		Bitmap map = myDoc.GetHighLightMarkedRectBitmap(width,height,stride);
		    		imageView.setHighLightBitmap(map);	
		    		imageView.setHighLightBitmapXY((int)rect.left, (int)rect.top);
		      		imageView.invalidate(); 
		    	}
		    	break;
		}
		return true;
	}
	
	private void postToLog(String msg){
		Log.v(TAG,msg);
	}
	
	
	/** Init EMB SDK
	 * @throws invalidLicenseException 
	 * @throws parameterException */
	public boolean InitFoxitFixedMemory(int initMemSize) throws parameterException, invalidLicenseException{
		EMBJavaSupport.FSMemInitFixedMemory(initMemSize);		
		EMBJavaSupport.FSInitLibrary(0);
		EMBJavaSupport.FSUnlock("XXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		return true;
	}
	
	/** Destroy EMB SDK*/
	public boolean DestroyFoxitFixedMemory(){
		EMBJavaSupport.FSDestroyLibrary();	
		EMBJavaSupport.FSMemDestroyMemory();
		return true;
	}

	
	public void SetFontFileMap(String strFontFilePath) throws parameterException
	{
		EMBJavaSupport.FSSetFileFontmap(strFontFilePath);
	}
	
	private void SetDecodersAndMaps(){
			EMBJavaSupport.FSLoadJbig2Decoder();
			EMBJavaSupport.FSLoadJpeg2000Decoder();
			EMBJavaSupport.FSFontLoadJapanCMap();
			EMBJavaSupport.FSFontLoadJapanExtCMap();
			EMBJavaSupport.FSFontLoadGBCMap();
			EMBJavaSupport.FSFontLoadGBExtCMap();
			EMBJavaSupport.FSFontLoadCNSCMap();
			EMBJavaSupport.FSFontLoadKoreaCMap();

	}
	
}