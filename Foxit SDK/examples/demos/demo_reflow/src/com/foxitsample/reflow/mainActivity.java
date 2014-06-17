package com.foxitsample.reflow;


import com.foxitsample.service.WrapPDFFunc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class mainActivity extends Activity {
	 /** Called when the activity is first created. */
		/** instance variables*/
		public WrapPDFFunc func = null;
		public ImageView imageView = null;
		public boolean m_bReflow =false;
		
		public static final int REFLOW_ON=Menu.FIRST;
		public static final int REFLOW_OFF=Menu.FIRST+1;
		public float fScal = (float) 1;
		
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) 
		{
			super.onCreateOptionsMenu(menu);
			 menu.add(0, REFLOW_ON, 0, R.string.menu_on);
			 menu.add(0, REFLOW_OFF, 1, R.string.menu_off);
			 return true; }
		  
		 public boolean onOptionsItemSelected(MenuItem item) 
		 {
			   float nPageWidth = -1;
				float nPageHeight = -1;
				int nRet = func.ReflowPage();
				if(nRet == 0 )return true;
				nPageWidth = func.GetReflowWidth();
		   	  	nPageHeight = func.GetReflowHight();

				Display display = getWindowManager().getDefaultDisplay();
				float nWidth = display.getWidth();
				float nHeight = display.getHeight();	
				int nZoomFlag = -1;
				
			 switch (item.getItemId()) 
			 {
			 case REFLOW_ON: 
				 Reflow_on();
				 fScal = nWidth / nPageWidth;
				 nWidth =  (nPageWidth * fScal);
		      		imageView.setImageBitmap(func.getPageBitmap((int)nWidth, (int)nHeight,true));
		      		imageView.invalidate();
				 break;
			 case REFLOW_OFF: 
				 fScal = nWidth / nPageWidth;
				 nWidth =  (nPageWidth * fScal);
		      		imageView.setImageBitmap(func.getPageBitmap((int)nWidth, (int)nHeight,false));
		      		imageView.invalidate();
				 break;
			 }
			 return super.onOptionsItemSelected(item);
		 	}
	
		 public void Reflow_on()
		 {
			 m_bReflow = true;
			 func.GetPageCounts();
			 func.ReflowPage();
			 

			 
		 }
		 
		 public void Reflow_off()
		 {
			 m_bReflow = false;
			 
		 }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);        
	        setContentView(R.layout.main);   
	        
	        imageView = (ImageView)findViewById(R.id.display);
	        
	      //code start
	      		func = new WrapPDFFunc();
	      		int nRet = func.InitFoxitFixedMemory();
	      		if (nRet != 0){
	      			return;
	      		}
	      		
	      		func.LoadJbig2Decoder();
	      		func.LoadJpeg2000Decoder();
	      		func.LoadCNSFontCMap();
	      		func.LoadKoreaFontCMap();
	      		func.LoadJapanFontCMap();
	      		
	      		nRet = func.InitPDFDoc();
	      		if (nRet != 0){
	      			return;
	      		}
	      		
	     		int nPageCount = func.GetPageCounts();
	      		
	      		nRet = func.InitPDFPage(0);
	      		if (nRet != 0){
	      			return;
	      		}
	      		
	      		Display display = getWindowManager().getDefaultDisplay();
	      		int nWidth = display.getWidth();
	      		int nHeight = display.getHeight();
	      		
	      		imageView.setImageBitmap(func.getPageBitmap(nWidth, nHeight));
	      		imageView.invalidate();
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
				
				nRet = func.DestroyFoxitFixedMemory();
				if (nRet != 0){
					System.exit(0);
				}
				
				super.onDestroy();
				
		}   
	    
	    
		
	}