package com.foxitsample.text;

import com.foxitsample.text.R;
import com.foxitsample.exception.errorException;
import com.foxitsample.exception.fileAccessException;
import com.foxitsample.exception.formatException;
import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.memoryException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.exception.passwordException;
import com.foxitsample.exception.statusException;
import com.foxitsample.exception.toBeContinuedException;
import com.foxitsample.service.*;

import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.Rectangle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class Demo_textActivity extends Activity {

	private static String TAG = "demo_text";
	public WrapPDFFunc func = null;
	public PDFView imageView = null;
	private TextDoc myDoc;
	private int currentPage;
	private float xScaleFactor;
	private float yScaleFactor;
	private int rotateFlag;
	private boolean isSelect = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageView = new PDFView(getApplicationContext());
		setContentView(imageView);

		//original...
		//setContentView(R.layout.main);
		//imageView = (PDFView)findViewById(R.id.display);

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

			func = new WrapPDFFunc();
			func.InitFoxitFixedMemory(initialMemory);
			func.LoadJbig2Decoder();
			func.LoadJpeg2000Decoder();
			func.LoadCNSFontCMap();
			func.LoadKoreaFontCMap();
			func.LoadJapanFontCMap();
			func.SetFontFileMap(strFontFilePath);

			//must be done AFTER initializing engine
			myDoc = new TextDoc(fileName, password);
			myDoc.CountPages();
			Display display = getWindowManager().getDefaultDisplay();
			imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, display.getWidth(),display.getHeight(), xScaleFactor, yScaleFactor, rotateFlag));
			imageView.invalidate();
		} 
		catch (Exception e) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		myDoc.initTextPage(currentPage);
		int totalCharCount = myDoc.getCharCount(currentPage);
		switch(item.getItemId()){
		case R.id.SelectText:
			//highlight entire page
			imageView.newHighlight();
			int rectCount = myDoc.getRectCount(currentPage, 0, totalCharCount);
			imageView.setRectNum(rectCount);
			for(int i=0;i<rectCount;i++){
				RectF rt = myDoc.getRect(currentPage, i, 0, 0, getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());				
				imageView.addRect(rt);
			}
			isSelect = true;
			imageView.invalidate();
			break;
		case R.id.ExtractText:
			if(isSelect == false)
			{
				String textout = "You must select text first";
				Toast.makeText(this, textout, Toast.LENGTH_SHORT).show();
				break;
			}
			
			//extract entire page
			String text = myDoc.getText(currentPage,0,totalCharCount);
			Toast.makeText(imageView.getContext(), text, Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void postToLog(String msg){
		Log.v(TAG,msg);
	}
}