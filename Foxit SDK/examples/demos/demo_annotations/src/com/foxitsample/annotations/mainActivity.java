package com.foxitsample.annotations;

import FoxitEMBSDK.EMBJavaSupport;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.foxitsample.exception.memoryException;
import com.foxitsample.service.WrapPDFFunc;

public class mainActivity extends Activity {
	 /** Called when the activity is first created. */
	
		/** instance variables*/
		public WrapPDFFunc func = null;
		public ImageView imageView = null;
		
		public int nAddNoteFlag = 0;
		public int nAddPencilFlag = 0;
		public int nAddHighlightFlag = 0;
		public int nAddStampFlag = 0;
		public int nAddFileAttachmentFlag = 0;
		public int nDeleteFileAttachmentFlag = 0;
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);        
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        setContentView(R.layout.main);   
	        
	        imageView = (ImageView)findViewById(R.id.display);
	        Display display = getWindowManager().getDefaultDisplay();
      		int nWidth = display.getWidth();
      		int nHeight = display.getHeight();
      		//code start     		
	      		func = new WrapPDFFunc(nWidth, nHeight);
	      		boolean bRet;
				try {
					bRet = func.InitFoxitFixedMemory();
				} catch (Exception e) {
					e.printStackTrace();
				} 
	      		
	      		func.LoadJbig2Decoder();
	      		func.LoadJpeg2000Decoder();
	      		func.LoadCNSFontCMap();
	      		func.LoadKoreaFontCMap();
	      		func.LoadJapanFontCMap();
	      		
	      		int nRet = func.InitPDFDoc();
	      		if (nRet != 0){
	      			return;
	      		}
	      		
	     		int nPageCount = func.GetPageCounts();
	      		
	      		nRet = func.InitPDFPage(0);
	      		if (nRet != 0){
	      			return;
	      		}
	      		
	      		dispalyPage();
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
		
		/** for display.*/
		public void dispalyPage(){
			Display display = getWindowManager().getDefaultDisplay();
      		int nWidth = display.getWidth();
      		int nHeight = display.getHeight();
      		
      		imageView.setImageBitmap(func.getPageBitmap(nWidth, nHeight));
      		imageView.invalidate();
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			 	menu.add(0,Menu.FIRST,0,"Note");    
			 	menu.add(0,Menu.FIRST+1,1,"Pencil");    
			 	menu.add(0,Menu.FIRST+2,2,"Highlight");
			 	menu.add(1,Menu.FIRST+3,3,"Stamp");     
			 	menu.add(1,Menu.FIRST+4,4,"FileAttachment");
			 	menu.add(1,Menu.FIRST+5,5,"Delete").setIcon(android.R.drawable.ic_menu_delete); 
			return super.onCreateOptionsMenu(menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch(item.getItemId()){
			case Menu.FIRST:{
				if (nAddNoteFlag == 0){
					try {
						func.addAnnot(EMBJavaSupport.EMBJavaSupport_ANNOTTYPE_NOTE);
					} catch (memoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nAddNoteFlag = 1;
				}else{
					Toast.makeText(this, "You have already added the note annot!", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case Menu.FIRST+1:{
				if (nAddPencilFlag == 0){
					try {
						func.addAnnot(EMBJavaSupport.EMBJavaSupport_ANNOTTYPE_PENCIL);
					} catch (memoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nAddPencilFlag = 1;
				}else{
					Toast.makeText(this, "You have already added the pencil annot!", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case Menu.FIRST+2:{
				if (nAddHighlightFlag == 0){
					try {
						func.addAnnot(EMBJavaSupport.EMBJavaSupport_ANNOTTYPE_HIGHLIGHT);
					} catch (memoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nAddHighlightFlag = 1;
				}else{
					Toast.makeText(this, "You have already added the highlight annot!", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case Menu.FIRST+3:{
				if (nAddStampFlag == 0){
					try {
						func.addAnnot(EMBJavaSupport.EMBJavaSupport_ANNOTTYPE_STAMP);
					} catch (memoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nAddStampFlag = 1;
				}else{
					Toast.makeText(this, "You have already added the stamp annot!", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case Menu.FIRST+4:{
				if (nAddFileAttachmentFlag == 0){
					try {
						func.addAnnot(EMBJavaSupport.EMBJavaSupport_ANNOTTYPE_FILEATTACHMENT);
					} catch (memoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nAddFileAttachmentFlag = 1;
				}else{
					Toast.makeText(this, "You have already added the fileattachment annot!", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case Menu.FIRST+5:{
				func.deleteAnnot();
				break;
			}
			default:
				break;
			}
			
			dispalyPage();
			return super.onOptionsItemSelected(item);
		}
		
		
}