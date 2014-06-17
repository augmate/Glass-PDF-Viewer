package com.foxitsample.formfiled;

import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.PointF;
import FoxitEMBSDK.EMBJavaSupport.RectangleF;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.service.WrapPDFFunc;

public class mainActivity extends Activity implements OnTouchListener{
    /** Called when the activity is first created. */
	/** instance variables*/
	public WrapPDFFunc func = null;
	public int nDisplayWidth = 0;
	public int nDisplayHeight = 0;
	public PDFView pdfView;
	
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);      
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	        pdfView = new PDFView(this);  
	        setContentView(pdfView);   
	        pdfView.setOnTouchListener(this);	    
	        
	        //code start
      		func = new WrapPDFFunc(this);
      		boolean nRet=false;
			try {
				nRet = func.InitFoxitFixedMemory();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
      		if (nRet != true){
      			return;
      		}
      		
      		func.LoadJbig2Decoder();
      		func.LoadJpeg2000Decoder();
      		func.LoadCNSFontCMap();
      		func.LoadKoreaFontCMap();
      		func.LoadJapanFontCMap();
      		
      		nRet = func.InitPDFDoc();
      		if (nRet != true){
      			return;
      		}
      		
     		int nPageCount = func.GetPageCounts();
      		
      		nRet = func.InitPDFPage(0);
      		if (nRet != true){
      			return;
      		}
			displayPDFView();
	    }

		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
						
				boolean nRet = func.ClosePDFPage();
				if (nRet != false){
					System.exit(0);
				}
				
				nRet = func.ClosePDFDoc();
				if (nRet != false){
					System.exit(0);
				}
				
				nRet = func.DestroyFoxitFixedMemory();
				if (nRet != false){
					System.exit(0);
				}
				
				super.onDestroy();
				
		}   

		public void displayPDFView(){
			Display display = getWindowManager().getDefaultDisplay();
      		nDisplayWidth = display.getWidth();
      		nDisplayHeight = display.getHeight();
      		
      		pdfView.setPDFBitmap(func.getPageBitmap(nDisplayWidth, nDisplayHeight), nDisplayWidth, nDisplayHeight);
      		pdfView.OnDraw();
		}
		
		public void invalidate(float left, float top, float right, float bottom){
			int l, t, r, b;
			RectangleF rect = new EMBJavaSupport().new RectangleF();
			rect.left = left;
			rect.top = top;
			rect.right = right;
			rect.bottom = bottom;
			EMBJavaSupport.FPDFPagePageToDeviceRectF(func.getCurPDFPageHandler(), 0, 0, nDisplayWidth, nDisplayHeight, 0, rect);
			l = (int)rect.left;
			t = (int)rect.top;
			r = (int)rect.right;
			b = (int)rect.bottom;
			Rect rc = new Rect(l,t,r,b);			
			pdfView.setDirtyRect(l, t, r, b);
			pdfView.setDirtyBitmap(func.getDirtyBitmap(rc, nDisplayWidth, nDisplayHeight));
			pdfView.OnDraw();
			
		}


		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int actionType=event.getAction()&MotionEvent.ACTION_MASK;
			int actionId=event.getAction()&MotionEvent.ACTION_POINTER_ID_MASK;
			actionId=actionId>>8;  
			
			PointF point = new EMBJavaSupport().new PointF();
			point.x = event.getX();
			point.y = event.getY();			
			EMBJavaSupport.FPDFPageDeviceToPagePointF(func.getCurPDFPageHandler(), 0, 0, nDisplayWidth, nDisplayHeight, 0, point);
			
			switch(actionType){
			case MotionEvent.ACTION_MOVE://
				EMBJavaSupport.FPDFFormFillOnMouseMove(func.getPDFFormHandler(), func.getCurPDFPageHandler(), 0, point.x, point.y);
				break;
			case MotionEvent.ACTION_DOWN:	//	
				EMBJavaSupport.FPDFFormFillOnMouseMove(func.getPDFFormHandler(), func.getCurPDFPageHandler(), 0, point.x, point.y);
				EMBJavaSupport.FPDFFormFillOnLButtonDown(func.getPDFFormHandler(), func.getCurPDFPageHandler(), 0, point.x, point.y);
				break;
			case MotionEvent.ACTION_UP:	//
				EMBJavaSupport.FPDFFormFillOnLButtonUp(func.getPDFFormHandler(), func.getCurPDFPageHandler(), 0, point.x, point.y);
				break;
			}
			return true;
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			menu.add(0,Menu.FIRST,0,"ExportToXML");    
		 	menu.add(0,Menu.FIRST+1,1,"ImportFromXML"); 
			return super.onCreateOptionsMenu(menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch(item.getItemId()){
			case Menu.FIRST:{
				func.exportToXML();
				break;
			}
			case Menu.FIRST+1:{
				func.importFromXML();
				EMBJavaSupport.FPDFFormFillUpdatForm(func.getPDFFormHandler());
				pdfView.setDirtyRect(null);
				pdfView.setDirtyBitmap(null);
				displayPDFView();						
				break;
			}
			default:
				break;
			}
			return super.onOptionsItemSelected(item);
		}
		
		public void createAndroidTextField(String text){
			
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("textValue", text);
			intent.setClass(this, textfieldActivity.class);
			intent.putExtra("key", bundle);
			this.startActivityForResult(intent, 0);			
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			if (resultCode == RESULT_OK && requestCode == 0){
				Bundle bundle = data.getBundleExtra("Result");
				String text = bundle.getString("ResultValue");
				EMBJavaSupport.FPDFFormFillOnSetText(func.getPDFFormHandler(), func.getCurPDFPageHandler(), text, 0);
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		
		
}