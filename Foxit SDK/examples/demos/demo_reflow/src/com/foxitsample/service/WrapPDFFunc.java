package com.foxitsample.service;


import java.nio.ByteBuffer;

import com.foxitsample.exception.errorException;
import com.foxitsample.exception.fileAccessException;
import com.foxitsample.exception.formatException;
import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.memoryException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.exception.passwordException;
import com.foxitsample.exception.statusException;
import com.foxitsample.exception.toBeContinuedException;

import FoxitEMBSDK.EMBJavaSupport;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * defined for a wrap for All PDF implements
 * @author Foxit
 *
 */

public class WrapPDFFunc
{
	/** state variables*/
	private static final String TAG = "WrapPDFFunc";
	private static final String fileName = "/mnt/sdcard/FoxitText.pdf";
	private static final String password = "";
	private int nFileRead = 0;
	private int nPDFDocHandler = 0;
	private int nPDFCurPageHandler = 0;
	private int nPageCurReflowPageHandler = 0;
	private int nReflowHeight = 0;
	private int nReflowWidth = 0;
	
	/** Init EMB SDK
	 * @throws invalidLicenseException 
	 * @throws parameterException */
	public int InitFoxitFixedMemory()
	{
		try {
			EMBJavaSupport.FSMemInitFixedMemory(5*1024*1024);
			EMBJavaSupport.FSInitLibrary(0);
			EMBJavaSupport.FSUnlock("XXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");	
			
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (invalidLicenseException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		}		
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Destroy EMB SDK*/
	public int DestroyFoxitFixedMemory(){
		
		EMBJavaSupport.FSDestroyLibrary();	
		EMBJavaSupport.FSMemDestroyMemory();
		
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
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
	
	protected void postToLog(String msg){
		Log.v(TAG,msg);
	}
	/** Load PDF Document*/
	public int InitPDFDoc(){
			
		/** Init a FS_FileRead structure*/
		try {
			nFileRead = EMBJavaSupport.FSFileReadAlloc(fileName);
			
				nPDFDocHandler = EMBJavaSupport.FPDFDocLoad(nFileRead, password);
		} catch (memoryException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (invalidLicenseException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (fileAccessException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (formatException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (passwordException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (errorException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		}
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
				
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Close PDF Document*/
	public int ClosePDFDoc(){
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		try {
			EMBJavaSupport.FPDFDocClose(nPDFDocHandler);
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		}
		nPDFDocHandler = 0;		
		EMBJavaSupport.FSFileReadRelease(nFileRead);
		nFileRead = 0;
		
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Load and parser a PDF page*/
	public int InitPDFPage(int nPageIndex){
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		try {
			nPDFCurPageHandler = EMBJavaSupport.FPDFPageLoad(nPDFDocHandler, nPageIndex);
			EMBJavaSupport.FPDFPageStartParse(nPDFCurPageHandler, 0, 0);
		
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (toBeContinuedException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (statusException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (memoryException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		}
		if (nPDFCurPageHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Close a PDF Page*/
	public int ClosePDFPage(){
		
		if (nPDFCurPageHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		try {
			EMBJavaSupport.FPDFPageClose(nPDFCurPageHandler);
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nPDFCurPageHandler = 0;
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Count PDF page*/
	public int GetPageCounts(){
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		int nPageCount = 0;
		try {
			nPageCount = EMBJavaSupport.FPDFDocGetPageCount(nPDFDocHandler);
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (memoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nPageCount;
	}
	
	public Bitmap getPageBitmap(int displayWidth, int displayHeight){
		if(nPDFCurPageHandler == 0) {
			return null;
		} 
								
		Bitmap bm;
		bm = Bitmap.createBitmap(displayWidth,displayHeight,Bitmap.Config.ARGB_8888);
		
		int dib;
		try {
			dib = EMBJavaSupport.FSBitmapCreate(displayWidth, displayHeight, 7, null, 0);
			EMBJavaSupport.FSBitmapFillColor(dib,0xff);
			EMBJavaSupport.FPDFRenderPageStart(dib, nPDFCurPageHandler, 0, 0, displayWidth, displayHeight, 0, 0, null, 0);
			byte[] bmpbuf;
			bmpbuf = EMBJavaSupport.FSBitmapGetBuffer(dib);
			ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
			bm.copyPixelsFromBuffer(bmBuffer);
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (memoryException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (errorException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (toBeContinuedException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (statusException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		}		
		return bm;
	}
	
	public Bitmap getPageBitmap(int displayWidth, int displayHeight,boolean breflow){
		if(nPDFCurPageHandler == 0) {
			return null;
		} 					
		Bitmap bm;
		bm = Bitmap.createBitmap(displayWidth,displayHeight,Bitmap.Config.ARGB_8888);
		int dib;
		try {
			dib = EMBJavaSupport.FSBitmapCreate(displayWidth, displayHeight, 7, null, 0);
			EMBJavaSupport.FSBitmapFillColor(dib,0xff);
			int nRet = 0;
			if (true == breflow)
				nRet = EMBJavaSupport.FPDFReflowStartRender(dib, nPageCurReflowPageHandler, 0, 0, displayWidth, displayHeight, 0,0);
			else
				nRet = EMBJavaSupport.FPDFRenderPageStart(dib, nPDFCurPageHandler, 0, 0, displayWidth, displayHeight, 0, 0, null, 0);
			if (nRet != 0){
				return null;
			} 
			byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
			ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
			bm.copyPixelsFromBuffer(bmBuffer);
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (memoryException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (errorException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (toBeContinuedException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		} catch (statusException e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		}		
		return bm;
	}
	
	public int ReflowPage()
	{
		if (nPDFCurPageHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		int nPageHeight = 0;
		int nPageWidth = 0;
		try {
			nPageHeight = (int)EMBJavaSupport.FPDFPageGetSizeY(nPDFCurPageHandler);
			nPageWidth = (int)EMBJavaSupport.FPDFPageGetSizeX(nPDFCurPageHandler);
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (nPageCurReflowPageHandler == 0)
		{
			nPageCurReflowPageHandler = EMBJavaSupport.FPDFReflowAllocPage();
			if(nPageCurReflowPageHandler == 0) return 0;
			EMBJavaSupport.FPDFReflowStartParse(nPageCurReflowPageHandler, nPDFCurPageHandler, nPageWidth, nPageHeight, 0, 0);
			nReflowHeight = EMBJavaSupport.FPDFReflowGetPageHight(nPageCurReflowPageHandler);
			nReflowWidth = EMBJavaSupport.FPDFReflowGetPageWidth(nPageCurReflowPageHandler);
		}
		return 1;
	}
	
	public int GetReflowHight()
	{
		return nReflowHeight;
	}
	
	public int GetReflowWidth()
	{
		return nReflowWidth;
	}
			
}