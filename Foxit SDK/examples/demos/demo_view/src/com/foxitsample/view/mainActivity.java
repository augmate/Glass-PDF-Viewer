package com.foxitsample.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.foxitsample.service.*;

public class mainActivity extends Activity {

	private static String TAG = "demo_view";
	public WrapPDFFunc func = null;
	public ImageView imageView = null;
	private FoxitDoc myDoc;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);   
        
        imageView = (ImageView)findViewById(R.id.display);
        
      //code start
        try{
//        	String fileName = "/data/data/com.foxitsample.service/demo.pdf";
        	String fileName = "/mnt/sdcard/FoxitBigPreview.pdf";
        	String strFontFilePath = "/mnt/sdcard/DroidSansFallback.ttf";
        	String password = "";
        	int initialMemory = 5 * 1024 * 1024;
      		float xScaleFactor = 1f;
      		float yScaleFactor = 1f;
        	
      		func = new WrapPDFFunc();
      		func.InitFoxitFixedMemory(initialMemory);      		
      		func.LoadJbig2Decoder();
      		func.LoadJpeg2000Decoder();
      		func.LoadCNSFontCMap();
      		func.LoadKoreaFontCMap();
      		func.LoadJapanFontCMap();
      		func.SetFontFileMap(strFontFilePath);
      		
      		myDoc = func.createFoxitDoc(fileName, password);
      		myDoc.CountPages();
      		Display display = getWindowManager().getDefaultDisplay();
      		imageView.setImageBitmap(myDoc.getPageBitmap(0, display.getWidth(), display.getHeight(), xScaleFactor, yScaleFactor,0));
      		imageView.invalidate();
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
	
	private void postToLog(String msg){
		Log.v(TAG,msg);
	}
}