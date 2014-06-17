package com.foxitsample.view_rotation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.foxitsample.service.FoxitDoc;
import com.foxitsample.service.WrapPDFFunc;
import com.foxitsample.view_rotation.R;

public class mainActivity extends Activity {

	private static String TAG = "demo_view_rotation";
	public WrapPDFFunc func = null;
	public ImageView imageView = null;

	private FoxitDoc myDoc;
	private int currentPage = 0;
	private float xScaleFactor = 1f;
	private float yScaleFactor = 1f;
	private int rotateFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.main);   

		imageView = (ImageView)findViewById(R.id.display);
		
		try{
			//code start
			String fileName = "/mnt/sdcard/FoxitBigPreview.pdf";
			String password = "";
			String strFontFilePath = "/mnt/sdcard/DroidSansFallback.ttf";
			int initialMemory = 5 * 1024 * 1024;
			rotateFlag = 0;

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
			imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, display.getWidth(), display.getHeight(), xScaleFactor, yScaleFactor,rotateFlag));
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

	public boolean onCreateOptionsMenu(Menu menu) 
	{	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{ 
		try{
			int width = getWindowManager().getDefaultDisplay().getWidth();
			int height = getWindowManager().getDefaultDisplay().getHeight();

			switch (item.getItemId()) 
			{

			case R.id.RotateLeft:
				rotateFlag+=3;
				rotateFlag = rotateFlag%4;
				break;
			case R.id.RotateRight:
				rotateFlag++;
				rotateFlag = rotateFlag%4;
				break;
			}
			if(rotateFlag==0||rotateFlag==2)
				imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, width, height, xScaleFactor, yScaleFactor,rotateFlag));
			else
				imageView.setImageBitmap(myDoc.getPageBitmap(currentPage, height, width, xScaleFactor, yScaleFactor,rotateFlag));
			imageView.invalidate();	        	
		}catch (Exception e){
			postToLog(e.getMessage());
		}
		return true;
	}

	private void postToLog(String msg){
		Log.v(TAG,msg);
	}
}