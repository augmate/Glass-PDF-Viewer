package com.foxitsample.service;


import java.nio.ByteBuffer;

import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.CPDFPSI;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.psi.mainActivity;

/**
 * defined for a wrap for All PDF implements¡£
 * @author Foxit
 *
 */

public class WrapPDFFunc
{
	/** state variables*/
	private static final String TAG = "WrapPDFFunc";
	private static final String fileName = "/mnt/sdcard/FoxitBigPreview.pdf";
	private static final String strFontFilePath = "/mnt/sdcard/DroidSansFallback.ttf";
	private static final String password = "";
	private int nFileRead = 0;
	private int nPDFDocHandler = 0;
	private int nPDFCurPageHandler = 0;
	
	private mainActivity mainView = null;
	private CPDFPSI fxPsi = null;
	private int nPSICallback = 0;
	private int nPSIHandle = 0;
	private int nDisplayWidth = 0;
	private int nDisplayHeight = 0;
	
	/** */
	public WrapPDFFunc(mainActivity context, int width, int height){
		mainView = context;
		nDisplayWidth = width;
		nDisplayHeight = height;
	}
	
	/** Init EMB SDK*/
	public boolean InitFoxitFixedMemory() throws parameterException, invalidLicenseException{
		EMBJavaSupport.FSMemInitFixedMemory(5*1024*1024);		
		EMBJavaSupport.FSInitLibrary(0);
		EMBJavaSupport.FSUnlock("XXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		return true;
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
	
	public void SetFontFileMap()
	{
		try {
			EMBJavaSupport.FSSetFileFontmap(strFontFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Load PDF Document*/
	public int InitPDFDoc(){
			
		/** Init a FS_FileRead structure*/
		try {
			nFileRead = EMBJavaSupport.FSFileReadAlloc(fileName);
	
			
			nPDFDocHandler = EMBJavaSupport.FPDFDocLoad(nFileRead, password);
			if (nPDFDocHandler == 0){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}
			//PSI
			if (mainView == null){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}
			fxPsi = new EMBJavaSupport().new CPDFPSI(mainView);
			if (fxPsi == null){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}
			nPSICallback = EMBJavaSupport.FPSIInitAppCallback(fxPsi);
			nPSIHandle = EMBJavaSupport.FPSIInitEnvironment(nPSICallback, true);
			if (nPSIHandle == 0){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}
			int nRet = EMBJavaSupport.FPSISetInkDiameter(nPSIHandle, 20);
			if (nRet != 0){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}
			nRet = EMBJavaSupport.FPSIInitCanvas(nPSIHandle, nDisplayWidth, nDisplayHeight);
			if (nRet != 0){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}			
			nRet = EMBJavaSupport.FPSISetInkColor(nPSIHandle, 255);
			if (nRet != 0){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Close PDF Document*/
	public int ClosePDFDoc(){
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		///PSI		
		if (nPSIHandle == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		EMBJavaSupport.FPSIDestroyEnvironment(nPSIHandle);
		nPSIHandle = 0;
		EMBJavaSupport.FPSIReleaseAppCallback(nPSICallback);
		nPSICallback = 0;
		//
		
		try {
			EMBJavaSupport.FPDFDocClose(nPDFDocHandler);
		} catch (parameterException e) {
			e.printStackTrace();
		}
		nPDFDocHandler = 0;
		
		EMBJavaSupport.FSFileReadRelease(nFileRead);
		nFileRead = 0;
		
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Load and parser a PDF page.*/
	public int InitPDFPage(int nPageIndex){
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		try {
			nPDFCurPageHandler = EMBJavaSupport.FPDFPageLoad(nPDFDocHandler, nPageIndex);
	
			if (nPDFCurPageHandler == 0){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}
			
			EMBJavaSupport.FPDFPageStartParse(nPDFCurPageHandler, 0, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return nPageCount;
	}
	
	public Bitmap getPageBitmap(int displayWidth, int displayHeight){
		if(nPDFCurPageHandler == 0) {
			return null;
		} 
		
		Bitmap bm = null;
		bm = Bitmap.createBitmap(displayWidth, displayHeight, Bitmap.Config.ARGB_8888);
		
		int dib;
		try {
			dib = EMBJavaSupport.FSBitmapCreate(displayWidth, displayHeight, 7, null, 0);

			EMBJavaSupport.FSBitmapFillColor(dib,0xff);
			EMBJavaSupport.FPDFRenderPageStart(dib, nPDFCurPageHandler, 0, 0, displayWidth, displayHeight, 0, 1, null, 0);
	
			
			byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
			
			ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
			bm.copyPixelsFromBuffer(bmBuffer);
			
			EMBJavaSupport.FSBitmapDestroy(dib);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return bm;
	}
	
	public Bitmap getDirtyBitmap(Rect rect, int nSizex, int nSizey){
		Bitmap bm = null;
		if(nPDFCurPageHandler == 0) {
			return null;
		} 
		
		bm = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);		
		int dib;
		try {
			dib = EMBJavaSupport.FSBitmapCreate(rect.width(), rect.height(), 7, null, 0);
	
			EMBJavaSupport.FSBitmapFillColor(dib,0xff);
			int nRet = 1;
			EMBJavaSupport.FPDFRenderPageStart(dib, nPDFCurPageHandler, -rect.left, -rect.top, nSizex, nSizey, 0, 1, null, 0);
	
			nRet = EMBJavaSupport.FPSIRender(nPSIHandle, dib, 0, 0, rect.right-rect.left, rect.bottom-rect.top, rect.left, rect.top);
			if (nRet != 0){
				return null;
			} 
			
			byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
			
			ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
			bm.copyPixelsFromBuffer(bmBuffer);
			
			EMBJavaSupport.FSBitmapDestroy(dib);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return bm;
	}
	
	public int getCurPDFPageHandler(){
		return nPDFCurPageHandler;
	}
	
	public int getCurPSIHandle(){
		return nPSIHandle;
	}
			
}