package com.foxitsample.service;


import java.nio.ByteBuffer;

import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.CPDFFormFillerInfo;
import FoxitEMBSDK.EMBJavaSupport.CPDFJsPlatform;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.foxitsample.exception.*;
import com.foxitsample.formfiled.mainActivity;

/**
 * defined for a wrap for All PDF implements¡£
 * @author Foxit
 *
 */

public class WrapPDFFunc
{
	/** state variables*/
	private static final String TAG = "WrapPDFFunc";
	private static final String fileName = "/mnt/sdcard/FoxitForm.pdf";
	private static final String password = "";
	private int nFileRead = 0;
	private int nPDFDocHandler = 0;
	private int nPDFCurPageHandler = 0;
	
	/** form*/
	private mainActivity mainView = null;
	private CPDFFormFillerInfo formFillerInfo = null;
	private int nPDFFormFillerInfo = 0;
	private CPDFJsPlatform jsPlatform = null;
	private int nPDFJsPlatform = 0;
	private int nPDFFormHandler = 0;
		
	/** */
	public WrapPDFFunc(mainActivity context){
		mainView = context;
	}
	
	/** Init EMB SDK*/
	public boolean InitFoxitFixedMemory() throws parameterException, invalidLicenseException{
		EMBJavaSupport.FSMemInitFixedMemory(5*1024*1024);		
		EMBJavaSupport.FSInitLibrary(0);
		EMBJavaSupport.FSUnlock("XXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

		
		/////////formfiller implemention
		if (mainView == null) 
			return false;
		formFillerInfo = new EMBJavaSupport().new CPDFFormFillerInfo(mainView);
		if (formFillerInfo == null)
			return false;
		nPDFFormFillerInfo = EMBJavaSupport.FPDFFormFillerInfoAlloc(formFillerInfo);
		if (nPDFFormFillerInfo == 0)
			return false;
		
		jsPlatform = new EMBJavaSupport().new CPDFJsPlatform();
		if (jsPlatform == null)
			return false;
		nPDFJsPlatform = EMBJavaSupport.FPDFJsPlatformAlloc(jsPlatform);
		if (nPDFJsPlatform == 0)
			return false;
		
		EMBJavaSupport.FPDFFormFillerInfoSetJsPlatform(nPDFFormFillerInfo, nPDFJsPlatform);
		EMBJavaSupport.FPDFJsPlatformSetFormFillerInfo(nPDFJsPlatform, nPDFFormFillerInfo);
		
		///////////////////
				
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
	
	/** Load PDF Document*/
	public boolean InitPDFDoc(){
		
		/** Init a FS_FileRead structure*/
		try {
			nFileRead = EMBJavaSupport.FSFileReadAlloc(fileName);

	
			nPDFDocHandler = EMBJavaSupport.FPDFDocLoad(nFileRead, password);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		///formfiller implemention
		nPDFFormHandler = EMBJavaSupport.FPDFDocInitFormFillEnviroument(nPDFDocHandler, nPDFFormFillerInfo);
		if (nPDFFormHandler == 0)
			return false;
				
		return true;
	}
	
	/** Close PDF Document*/
	public boolean ClosePDFDoc(){
		
		if (nPDFDocHandler == 0){
			return false;
		}
		
		///formfiller implemention
		EMBJavaSupport.FPDFDocExitFormFillEnviroument(nPDFFormHandler);
		nPDFFormHandler = 0;
		
		EMBJavaSupport.FPDFFormFillerInfoRelease(nPDFFormFillerInfo);
		nPDFFormFillerInfo = 0;
		
		EMBJavaSupport.FPDFJsPlatformRelease(nPDFJsPlatform);
		nPDFJsPlatform = 0;
		/////
		
		try {
			EMBJavaSupport.FPDFDocClose(nPDFDocHandler);
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nPDFDocHandler = 0;

		
		EMBJavaSupport.FSFileReadRelease(nFileRead);
		nFileRead = 0;
		
		return true;
	}
	
	/** Load and parser a PDF page*/
	public boolean InitPDFPage(int nPageIndex){
		
		if (nPDFDocHandler == 0){
			return false;
		}
		
		try {
			nPDFCurPageHandler = EMBJavaSupport.FPDFPageLoad(nPDFDocHandler, nPageIndex);
		} catch (parameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		try {
			EMBJavaSupport.FPDFPageStartParse(nPDFCurPageHandler, 0, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
		
		///formfiller implemention
		EMBJavaSupport.FPDFFormFillOnAfterLoadPage(nPDFFormHandler, nPDFCurPageHandler);
		///
		
		return true;
	}
	
	public int getPageHandler(int nPageIndex){
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		if (nPageIndex == 0 && nPDFCurPageHandler != 0)
			return nPDFCurPageHandler;
		
		int nPageHandler=0;
		try {
			nPageHandler = EMBJavaSupport.FPDFPageLoad(nPDFDocHandler, nPageIndex);

			EMBJavaSupport.FPDFPageStartParse(nPageHandler, 0, 0);

		
		///formfiller implemention
		//EMBJavaSupport.FPDF_FormFill_OnAfterLoadPage(nPageHandler, nPDFFormHandler);
		EMBJavaSupport.FPDFFormFillOnAfterLoadPage(nPDFFormHandler, nPageHandler);
		///
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return nPageHandler;
	}
	
	/** Close a PDF Page*/
	public boolean ClosePDFPage(){
		
		if (nPDFCurPageHandler == 0){
			return false;
		}
		
		try {
			EMBJavaSupport.FPDFPageClose(nPDFCurPageHandler);
		} catch (parameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nPDFCurPageHandler = 0;
		
		return true;
	}
	
	/** Count PDF page*/
	public int GetPageCounts(){
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		int nPageCount=-1;
		try {
			nPageCount = EMBJavaSupport.FPDFDocGetPageCount(nPDFDocHandler);
		} catch (Exception e) {
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

		
		///formfiller implemention
		if (nPDFFormHandler == 0)
			return null;
		EMBJavaSupport.FPDFFormFillDraw(nPDFFormHandler, dib, nPDFCurPageHandler, 0, 0, displayWidth, displayHeight, 0, 0);
		///
		
		byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
		
		ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
		bm.copyPixelsFromBuffer(bmBuffer);
		
		EMBJavaSupport.FSBitmapDestroy(dib);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bm;
	}
	
	public Bitmap getDirtyBitmap(Rect rect, int nSizex, int nSizey){
		Bitmap bm = null;
		if(nPDFCurPageHandler == 0) {
			return null;
		} 
		
		bm = Bitmap.createBitmap(rect.width(),rect.height(),Bitmap.Config.ARGB_8888);
		int dib;
		try {
			dib = EMBJavaSupport.FSBitmapCreate(rect.width(), rect.height(), 7, null, 0);
	
			EMBJavaSupport.FSBitmapFillColor(dib,0xff);
			EMBJavaSupport.FPDFRenderPageStart(dib, nPDFCurPageHandler, -rect.left, -rect.top, nSizex, nSizey, 0, 0, null, 0);
		
			
			///formfiller implemention
			if (nPDFFormHandler == 0)
				return null;
			EMBJavaSupport.FPDFFormFillDraw(nPDFFormHandler, dib, nPDFCurPageHandler, -rect.left, -rect.top, nSizex, nSizey, 0, 0);
			///
			
			byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
			
			ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
			bm.copyPixelsFromBuffer(bmBuffer);
			
			EMBJavaSupport.FSBitmapDestroy(dib);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return bm;
	}
	
	public int getCurPDFPageHandler(){
		return nPDFCurPageHandler;
	}
	
	public int getPDFFormHandler(){
		return nPDFFormHandler;
	}
	
	public boolean exportToXML(){
		EMBJavaSupport.FPDFFormFillOnKillFocus(nPDFFormHandler);
		int nInterForm = EMBJavaSupport.FPDFFormCreateInterForm(nPDFDocHandler, false);
		boolean bRet = EMBJavaSupport.FPDFFormExportToXML(nInterForm, 0);
		EMBJavaSupport.FPDFFormReleaseInterForm(nInterForm);
		return bRet;
	}
	
	public boolean importFromXML(){
		EMBJavaSupport.FPDFFormFillOnKillFocus(nPDFFormHandler);
		int nInterForm = EMBJavaSupport.FPDFFormCreateInterForm(nPDFDocHandler, false);
		boolean bRet = EMBJavaSupport.FPDFFormImportFromXML(nInterForm, 0);
		EMBJavaSupport.FPDFFormReleaseInterForm(nInterForm);
		return bRet;
	}
			
}