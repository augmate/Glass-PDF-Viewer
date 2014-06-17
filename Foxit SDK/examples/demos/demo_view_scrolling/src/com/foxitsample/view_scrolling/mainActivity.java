package com.foxitsample.view_scrolling;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.foxitsample.main.R;
import com.foxitsample.service.FoxitDoc;
import com.foxitsample.service.WrapPDFFunc;

public class mainActivity extends Activity {
	
	private static String TAG = "demo_view_scrolling";
	
	public WrapPDFFunc func = null;
	public PDFView imageView = null;
	private FoxitDoc myDoc;
	private int currentPage;
	private float xScaleFactor = 2f;
	private float yScaleFactor = 2f;
	private int rotateFlag;
	
	private float PreoffsetX = 0;
	private	float PreoffsetY = 0;
	private float CuroffsetX = 0;
	private float CuroffsetY = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        
		  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		imageView = new PDFView(getApplicationContext());
		setContentView(imageView);
		//code start
		try {
			String fileName = "/mtp://[usb:003,015]/Phone/Pictures/augmate.pdf";
			String password = "";
			//String strFontFilePath = "/mnt/sdcard/DroidSansFallback.ttf";
			rotateFlag = 0;
			currentPage = 0;
			int initialMemory = 8 * 1024 * 1024;

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
			imageView.setDisplay(display.getWidth(),display.getHeight());
			imageView.setBitmap(myDoc.getPageBitmap(currentPage, display.getWidth()*2, display.getHeight()*2, xScaleFactor, yScaleFactor,rotateFlag));
			imageView.invalidate();
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
			func.DestroyFoxitFixedMemory();
		} catch (Exception e){
			System.exit(0);
		}

		super.onDestroy();
	}   
	public boolean onTouchEvent(MotionEvent event){ 

		int action = event.getAction();  
		switch(action){  
		case MotionEvent.ACTION_DOWN:  
			PreoffsetX = event.getX();  
			PreoffsetY = event.getY();  

			break;  
		case MotionEvent.ACTION_MOVE: 
			CuroffsetX = event.getX();  
			CuroffsetY = event.getY(); 
			imageView.SetMartix(CuroffsetX - PreoffsetX,CuroffsetY - PreoffsetY);        	
			imageView.invalidate();
			PreoffsetX = CuroffsetX;
			PreoffsetY = CuroffsetY;
			break;
		case MotionEvent.ACTION_UP:  

			break;
		case MotionEvent.ACTION_CANCEL:  
			break;  
		}  
		//event.recycle();  
		return(true);  
	}  

	private void postToLog(String msg){
		Log.v(TAG,msg);
	}
}