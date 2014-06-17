package com.foxitsample.service;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;


import com.foxitsample.exception.errorException;
import com.foxitsample.exception.fileAccessException;
import com.foxitsample.exception.formatException;
import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.memoryException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.exception.passwordException;
import com.foxitsample.exception.statusException;
import com.foxitsample.exception.toBeContinuedException;
import com.foxitsample.main.demo_progressive_renderingActivity;

import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.CEMBPause;
import android.graphics.Bitmap;
import android.graphics.EmbossMaskFilter;
import android.util.Log;

/**
 * defined for a wrap for All PDF implements¡£
 * @author Foxit
 *
 */

public class WrapPDFFunc
{
	class MyTimerTask extends TimerTask 
	{ 
	    @Override 
	    public void run() 
	    { 
	    	int nprogress = EMBJavaSupport.FPDFRenderPageGetRenderProgress(nPDFCurPageHandler);
	    	if (nprogress  == 0) {
	    		return;
	    	}
	    	if(nprogress == 100)
	    		return;

			try {
				ContinueRender();
			} catch (toBeContinuedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (memoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (statusException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (errorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (parameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	     } 
	 }  
	
	protected void postToLog(String msg){
		Log.v(TAG,msg);
	}
	
	public void ContinueRender() throws toBeContinuedException, memoryException, statusException, errorException, parameterException
	{
		try{
			EMBJavaSupport.FPDFRenderPageContinue(nPDFCurPageHandler,m_pause);
		}catch(parameterException e)
		{
			postToLog(e.getMessage());
		}
		Bitmap bm;
		bm = Bitmap.createBitmap(mainView.nDisplayWidth,mainView.nDisplayHeight,Bitmap.Config.ARGB_8888);
		byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
		
		ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
		bm.copyPixelsFromBuffer(bmBuffer);

		mainView.setReDrawbmp(bm);
		mainView.mHandler.sendEmptyMessage(mainView.UPDATE_UI); 

	}
	
	public void SetMainView(demo_progressive_renderingActivity view){
		mainView = view;
	}
	
	
	private static int dib = 0;
	private static demo_progressive_renderingActivity mainView = null;
	/** state variables*/
	private static final String TAG = "WrapPDFFunc";
	private static final String fileName = "/mnt/sdcard/FoxitBigPreview.pdf";
	private static final String strFontFilePath = "/mnt/sdcard/DroidSansFallback.ttf";
	private static final String password = "";
	private int nInitMemHandler = 0;
	private int nFixedMemmgrHandler = 0;
	private int nOOMHandler = 0;
	private int nFileRead = 0;
	private int nPDFDocHandler = 0;
	private int nPDFCurPageHandler = 0;
	private CEMBPause CPause = null;
	private int m_pause=0;
	private boolean m_bInProgressiveRender = false;
	
	private Timer mTimer = new Timer(); 
	private MyTimerTask mTimerTask = new MyTimerTask();

	/** Init EMB SDK*/
	public boolean InitFoxitFixedMemory() throws parameterException, invalidLicenseException{
		EMBJavaSupport.FSMemInitFixedMemory(5*1024*1024);		
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
	
	/** Load jbig2 decoder.*/
	public void LoadJbig2Decoder(){
		EMBJavaSupport.FSLoadJbig2Decoder();
	}
	
	/** Load jpeg2000 decoder. */
	public void LoadJpeg2000Decoder(){
		EMBJavaSupport.FSLoadJpeg2000Decoder();
	}
	
	/** */
	public void LoadJapanFontCMap(){
		EMBJavaSupport.FSFontLoadJapanCMap();
		EMBJavaSupport.FSFontLoadJapanExtCMap();
	}
	
	/** */
	public void LoadCNSFontCMap(){
		EMBJavaSupport.FSFontLoadGBCMap();
		EMBJavaSupport.FSFontLoadGBExtCMap();
		EMBJavaSupport.FSFontLoadCNSCMap();
	}
	
	public void LoadKoreaFontCMap(){
		EMBJavaSupport.FSFontLoadKoreaCMap();
	}
	public void SetFontFileMap() throws parameterException
	{
		EMBJavaSupport.FSSetFileFontmap(strFontFilePath);
	}
	/** Load PDF Document
	 * @throws memoryException 
	 * @throws errorException 
	 * @throws passwordException 
	 * @throws formatException 
	 * @throws fileAccessException 
	 * @throws invalidLicenseException 
	 * @throws parameterException */
	public int InitPDFDoc() throws memoryException, parameterException, invalidLicenseException, fileAccessException, formatException, passwordException, errorException{
			

		/** Init a FS_FileRead structure*/
		nFileRead = EMBJavaSupport.FSFileReadAlloc(fileName);
		
		nPDFDocHandler = EMBJavaSupport.FPDFDocLoad(nFileRead, password);
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Close PDF Document
	 * @throws parameterException */
	public int ClosePDFDoc() throws parameterException{
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		EMBJavaSupport.FPDFDocClose(nPDFDocHandler);
		nPDFDocHandler = 0;
		
		EMBJavaSupport.FSPauseHandlerRelease(m_pause);
		EMBJavaSupport.FSFileReadRelease(nFileRead);
		nFileRead = 0;
		
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Load and parser a PDF page
	 * @throws parameterException 
	 * @throws memoryException 
	 * @throws statusException 
	 * @throws toBeContinuedException */
	public int InitPDFPage(int nPageIndex) throws parameterException, toBeContinuedException, statusException, memoryException{
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		nPDFCurPageHandler = EMBJavaSupport.FPDFPageLoad(nPDFDocHandler, nPageIndex);
		if (nPDFCurPageHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
	
		
		EMBJavaSupport.FPDFPageStartParse(nPDFCurPageHandler, 0, 0);
		
		CPause = new CEMBPause(); 
		CPause.clientData = nPDFCurPageHandler;
		m_pause = EMBJavaSupport.FSEMBPauseHandlerAlloc(CPause);	
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Close a PDF Page
	 * @throws parameterException */
	public int ClosePDFPage() throws parameterException{
		
		if (nPDFCurPageHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		EMBJavaSupport.FPDFPageClose(nPDFCurPageHandler);
		nPDFCurPageHandler = 0;
		
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Count PDF page
	 * @throws memoryException 
	 * @throws parameterException */
	public int GetPageCounts() throws parameterException, memoryException{
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		int nPageCount = EMBJavaSupport.FPDFDocGetPageCount(nPDFDocHandler);
		return nPageCount;
	}
	
	
	public Bitmap getPageBitmap(int displayWidth, int displayHeight) throws parameterException, memoryException, errorException, toBeContinuedException, statusException{
		if(nPDFCurPageHandler == 0) {
			return null;
		} 
								
		Bitmap bm;
		bm = Bitmap.createBitmap(displayWidth,displayHeight,Bitmap.Config.ARGB_8888);
		
		dib = EMBJavaSupport.FSBitmapCreate(displayWidth, displayHeight, 7, null, 0);
		EMBJavaSupport.FSBitmapFillColor(dib,0xff);
		
		EMBJavaSupport.FPDFRenderPageStart(dib, nPDFCurPageHandler, 0, 0, displayWidth, displayHeight, 0, 0, null, m_pause);
		 
		int nProgress = EMBJavaSupport.FPDFRenderPageGetRenderProgress(nPDFCurPageHandler);

		if(0 != nProgress)
		{
			m_bInProgressiveRender = true;
			mTimer.schedule(mTimerTask, 0, 200);
		}
		
		byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
		
		ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
		bm.copyPixelsFromBuffer(bmBuffer);
		
		return bm;
	}
			
}