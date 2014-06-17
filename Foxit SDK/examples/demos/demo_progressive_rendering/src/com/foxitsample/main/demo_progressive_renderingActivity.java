package com.foxitsample.main;


import java.util.Calendar;

import com.foxitsample.exception.errorException;
import com.foxitsample.exception.fileAccessException;
import com.foxitsample.exception.formatException;
import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.memoryException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.exception.passwordException;
import com.foxitsample.exception.statusException;
import com.foxitsample.exception.toBeContinuedException;
import com.foxitsample.progressive.R;
import com.foxitsample.service.WrapPDFFunc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.widget.ImageView;

public class demo_progressive_renderingActivity extends Activity {
    /** Called when the activity is first created. */
	public WrapPDFFunc func = null;
	public ImageView imageView = null;
	public Handler mHandler;

	public static final int UPDATE_UI = 0;
	public int nDisplayWidth = 0;
	public int nDisplayHeight = 0;
	private Bitmap m_bmp = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageView = (ImageView)findViewById(R.id.display);
        
        
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                case UPDATE_UI:

                	imageView.setImageBitmap(m_bmp);
            		imageView.invalidate();
                    break;
                }
            }
        };

            
        //code start
        		func = new WrapPDFFunc();
        		func.SetMainView(this);
        		boolean nRet2=false;
				try {
					nRet2 = func.InitFoxitFixedMemory();
				} catch (Exception e) {
					e.printStackTrace();
				} 
        		if (nRet2 != true){
        			return;
        		}
        		int nRet = 0;
        		func.LoadJbig2Decoder();
        		func.LoadJpeg2000Decoder();
        		func.LoadCNSFontCMap();
        		func.LoadKoreaFontCMap();
        		func.LoadJapanFontCMap();
        		try {
					func.SetFontFileMap();
				} catch (parameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		try {
					nRet = func.InitPDFDoc();
				} catch (memoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (parameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (invalidLicenseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (fileAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (formatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (passwordException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (errorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		if (nRet != 0){
        			return;
        		}
        		
       		try {
				int nPageCount = func.GetPageCounts();
			} catch (parameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (memoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        		
        		try {
					nRet = func.InitPDFPage(0);
				} catch (parameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (toBeContinuedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (statusException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (memoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		if (nRet != 0){
        			return;
        		}
        		
        		Display display = getWindowManager().getDefaultDisplay();
        		int nWidth = display.getWidth();
        		int nHeight = display.getHeight();
        		nDisplayWidth = display.getWidth();
          		nDisplayHeight = display.getHeight();
        		
        		try {
					imageView.setImageBitmap(func.getPageBitmap(nWidth, nHeight));
				} catch (parameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (memoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (errorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (toBeContinuedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (statusException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		imageView.invalidate();
      }

  	@Override
  	protected void onDestroy() {
  		// TODO Auto-generated method stub
  					
  			int nRet = 0;
			try {
				nRet = func.ClosePDFPage();
			} catch (parameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  			if (nRet != 0){
  				System.exit(0);
  			}
  			
  			try {
				nRet = func.ClosePDFDoc();
			} catch (parameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  			if (nRet != 0){
  				System.exit(0);
  			}
  			
  			//nRet = ;
  			if (!func.DestroyFoxitFixedMemory()){
  				System.exit(0);
  			}
  			
  			super.onDestroy();
  			
  	}   
      
	public void displayPDFView() throws parameterException, memoryException, errorException, toBeContinuedException, statusException{
		Display display = getWindowManager().getDefaultDisplay();
  		nDisplayWidth = display.getWidth();
  		nDisplayHeight = display.getHeight();
  		

  		imageView.setImageBitmap(func.getPageBitmap(nDisplayWidth, nDisplayWidth));
		imageView.invalidate();
	}
	
	public void setReDrawbmp(Bitmap bm){
		m_bmp = bm;
		
	}
      
  	
  }