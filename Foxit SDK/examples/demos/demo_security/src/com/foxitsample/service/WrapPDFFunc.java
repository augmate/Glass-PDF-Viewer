package com.foxitsample.service;


import java.nio.ByteBuffer;

import com.foxitsample.exception.errorException;
import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.memoryException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.exception.statusException;
import com.foxitsample.exception.toBeContinuedException;

import FoxitEMBSDK.EMBJavaSupport;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * defined for a wrap for All PDF implements¡£
 * @author Foxit
 *
 */

public class WrapPDFFunc
{
	/** state variables*/
	private static final String TAG = "WrapPDFFunc";
	private static final String strFontFilePath = "/mnt/sdcard/DroidSansFallback.ttf";

	private static final String password = "";
	private int nFileRead = 0;
	private int nCusFileRead =0;
	private int nPKIFileRead =0;
	private int nPDFDocHandler = 0;
	private int nPDFCurPageHandler = 0;
	private int nSecurityHandler = 0;
	private int nPKIHandler= 0;
	private int nPDFCusDocHandler = 0;
	private int nPDFPKIDocHandler = 0;
	private int nEnvelopesHandler = 0;
	
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
	
	public void SetFontFileMap()
	{
		try {
			EMBJavaSupport.FSSetFileFontmap(strFontFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void postToLog(String msg){
		Log.v(TAG,msg);
	}
	
	/** Load PDF Document*/
	public int InitPDFDoc(String fileName){
			
		/** Init a FS_FileRead structure*/
		try {
			nFileRead = EMBJavaSupport.FSFileReadAlloc(fileName);
		
			nPDFDocHandler = EMBJavaSupport.FPDFDocLoad(nFileRead, password);
			if (nPDFDocHandler == 0){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}
		} catch (Exception e) {
			postToLog(e.getMessage());
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
		} catch (Exception e) {
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
			if (nPDFCurPageHandler == 0){
				return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
			}
			EMBJavaSupport.FPDFPageStartParse(nPDFCurPageHandler, 0, 0);
		} catch (Exception e) {
			postToLog(e.getMessage());
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
			postToLog(e.getMessage());
		}
		
		nPDFCurPageHandler = 0;
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/** Count PDF page*/
	public int GetPageCounts(){
		
		if (nPDFDocHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		int nPageCount=0;
		try {
			nPageCount = EMBJavaSupport.FPDFDocGetPageCount(nPDFDocHandler);
		} catch (Exception e) {
			postToLog(e.getMessage());
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
 
			byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
			
			ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
			bm.copyPixelsFromBuffer(bmBuffer);
		} catch (Exception e) {
			postToLog(e.getMessage());
		}
		return bm;
	}
	public int CustomEncrypt(String strCustomEncryptFilePath)
	{
		if(nSecurityHandler != 0)
		{
			EMBJavaSupport.FPDFSecurityDestroySecurityHandler(nSecurityHandler);
		}
		nSecurityHandler = EMBJavaSupport.FPDFSecurityCreateSecurityHandler();
		int nRet = EMBJavaSupport.FPDFSecurityCustomEncrypt(nPDFDocHandler, "EMBSDK2.0", nSecurityHandler, strCustomEncryptFilePath);
		return nRet;
	}
	public int CustomDecrypt(String strCustomEncryptFilePath,String strCustomDecryptFilePath)
	{
		try {
			if(nSecurityHandler == 0) return 1;
			if(nPDFCusDocHandler != 0)
			{
				
				EMBJavaSupport.FPDFDocClose(nPDFCusDocHandler);
	
				EMBJavaSupport.FSFileReadRelease(nCusFileRead);
				nCusFileRead = 0;
				nPDFCusDocHandler = 0;
			}
			if(nCusFileRead == 0)
			{
				nCusFileRead = EMBJavaSupport.FSFileReadAlloc(strCustomEncryptFilePath);
			}
			if(nCusFileRead == 0) return 1;
			int nRet = EMBJavaSupport.FPDFSecurityRegisterHandler("EMBSDK2.0", nSecurityHandler);
			if(nRet != 0) return 1;
			nPDFCusDocHandler = EMBJavaSupport.FPDFDocLoad(nCusFileRead, "");
			if(nPDFCusDocHandler == 0) return 1;
			nRet = EMBJavaSupport.FPDFSecurityRemove(nPDFCusDocHandler,strCustomDecryptFilePath); 
			if(nRet != 0) return 1;
			EMBJavaSupport.FPDFSecurityUnRegisterHandler("EMBSDK2.0");		
			EMBJavaSupport.FPDFSecurityDestroySecurityHandler(nSecurityHandler);
			nSecurityHandler = 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		}
		return 0;
	}
	public int PKIEncrypt(String strPKIEncryptFilePath)
	{
		if(nEnvelopesHandler != 0)
			EMBJavaSupport.FPDFSecurityDestroyEnvelopes(nEnvelopesHandler);
		nEnvelopesHandler = EMBJavaSupport.FPDFSecurityCreateEnvelopes();
		//customer must use their way to get the envelop data
		//then put the data into the Foxit EMB SDK Envelopes
		//the to encrypt the pdf file
		byte[] EnvelopesData = new byte[10];
		for(int i=0;i<9;i++)
			EnvelopesData[i] = (byte)i;
		//this,the envelopedata must be changed 
		EMBJavaSupport.FPDFSecurityAddEnvelope(nEnvelopesHandler,EnvelopesData ,10);
		//customers should get this key data by themselves.
		byte[] key = new byte[1];
		key[0] = 0;
		int nRet = EMBJavaSupport.FPDFSecurityCertEncrypt(nPDFDocHandler, 1, true, nEnvelopesHandler, key, 1, strPKIEncryptFilePath);
		return nRet;
	}
	public int PKIDecrypt(String strPKIEncryptFilePath,String strPKIDecryptFilePath)
	{
		try {
			if(nEnvelopesHandler == 0 ) return 1;
			if(nPDFPKIDocHandler != 0)
			{
				
				EMBJavaSupport.FPDFDocClose(nPDFPKIDocHandler);
	
				EMBJavaSupport.FSFileReadRelease(nPKIFileRead);
				nPKIFileRead = 0;
				nPDFPKIDocHandler = 0;
			}
			if(nPKIFileRead == 0)
			{
				nPKIFileRead = EMBJavaSupport.FSFileReadAlloc(strPKIEncryptFilePath);
			}
			if(nPKIFileRead == 0) return 1;
			if(nPKIHandler != 0)
			{
				EMBJavaSupport.FPDFSecurityDestroyPKISecurityHandler(nPKIHandler);
			}
			nPKIHandler = EMBJavaSupport.FPDFSecurityCreatePKISecurityHandler();
			int nRet = EMBJavaSupport.FPDFSecurityRegisterHandler("Adobe.PubSec", nPKIHandler);
			if(nRet != 0) return 1;
			nPDFPKIDocHandler = EMBJavaSupport.FPDFDocLoad(nPKIFileRead, "");
			//on this step,you can use this doc handler to render and display the pdf file.
			if(nPDFPKIDocHandler == 0) return 1;
			nRet = EMBJavaSupport.FPDFSecurityRemove(nPDFPKIDocHandler,strPKIDecryptFilePath); 
			if(nRet != 0) return 1;
			EMBJavaSupport.FPDFSecurityUnRegisterHandler("Adobe.PubSec");		
			EMBJavaSupport.FPDFSecurityDestroySecurityHandler(nPKIHandler);
			nPKIHandler = 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			postToLog(e.getMessage());
		}
		return 0;
	}
}