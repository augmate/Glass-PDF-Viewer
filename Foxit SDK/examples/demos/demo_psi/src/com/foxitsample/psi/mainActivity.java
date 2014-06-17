package com.foxitsample.psi;

import FoxitEMBSDK.EMBJavaSupport;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

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
        	Display display = getWindowManager().getDefaultDisplay();
        	nDisplayWidth = display.getWidth();
  			nDisplayHeight = display.getHeight();
      		func = new WrapPDFFunc(this, nDisplayWidth, nDisplayHeight);
      		pdfView.InitView(func);
      		boolean bRet = false;
			try {
				bRet = func.InitFoxitFixedMemory();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      		if (bRet != true){
      			return;
      		}
      		
      		func.LoadJbig2Decoder();
      		func.LoadJpeg2000Decoder();
      		func.LoadCNSFontCMap();
      		func.LoadKoreaFontCMap();
      		func.LoadJapanFontCMap();
      		func.SetFontFileMap();
      		int nRet = func.InitPDFDoc();
      		if (nRet != 0){
      			return;
      		}
      		
      		nRet = func.InitPDFPage(0);
      		if (nRet != 0){
      			return;
      		}
      		
      		displayPDFView();
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
					
			int nRet = func.ClosePDFPage();
			if (nRet != 0){
				System.exit(0);
			}
			
			nRet = func.ClosePDFDoc();
			if (nRet != 0){
				System.exit(0);
			}
			
			pdfView.finalize();
			
			super.onDestroy();
			
	}
	
	public void displayPDFView(){
  		pdfView.setPDFBitmap(func.getPageBitmap(nDisplayWidth, nDisplayHeight), nDisplayWidth, nDisplayHeight);
  		pdfView.OnDraw();
	}

	public void invalidate(int left, int top, int right, int bottom){	
		if (right - left == 0 || bottom - top == 0)
			return;
		
		Rect rc = new Rect(left, top, right, bottom);
		
		pdfView.setDirtyRect(left, top, right, bottom);
		pdfView.setDirtyBitmap(func.getDirtyBitmap(rc, nDisplayWidth, nDisplayHeight));
		pdfView.OnDraw();
		
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		int actionType=event.getAction()&MotionEvent.ACTION_MASK;
		int actionId=event.getAction()&MotionEvent.ACTION_POINTER_ID_MASK;
		actionId=actionId>>8;  
		
		float x = event.getX();
		float y = event.getY();		
		
		switch(actionType){
		case MotionEvent.ACTION_MOVE://
			
			AddPoint(EMBJavaSupport.PSI_ACTION_MOVE, x, y, 1f, EMBJavaSupport.FXG_PT_LINETO);
			break;
		case MotionEvent.ACTION_DOWN:	//	
			
			AddPoint(EMBJavaSupport.PSI_ACTION_DOWN, x, y, 1f, EMBJavaSupport.FXG_PT_MOVETO);
			break;
		case MotionEvent.ACTION_UP:	//
			
			AddPoint(EMBJavaSupport.PSI_ACTION_UP, x, y, 1f, EMBJavaSupport.FXG_PT_LINETO | EMBJavaSupport.FXG_PT_ENDPATH);
			break;
		}
		
		return true;
	}   
    
    public void AddPoint(int nActionType, float x, float y, float nPressures, int flag){
    	pdfView.addAction(nActionType, x, y, nPressures, flag);
    }
	
}