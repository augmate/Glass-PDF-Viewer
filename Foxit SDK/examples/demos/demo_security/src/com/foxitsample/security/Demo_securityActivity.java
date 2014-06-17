package com.foxitsample.security;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.service.WrapPDFFunc;

public class Demo_securityActivity extends Activity {
    /** Called when the activity is first created. */
	/** instance variables*/
	public WrapPDFFunc func = null;
	public ImageView imageView = null;
	static final int M_SECURITY_ENCRYPT = 0;
	static final int M_SECURITY_Decrypt = 1;
	static final int M_PKI_ENCRYPT = 2;
	static final int M_PKI_Decrypt = 3;
	private static final String strCustomEncryptFilePath = "/data/data/com.foxitsample.security/FoxitEncryptd.pdf";
	private static final String strPKIEncryptFilePath = "/data/data/com.foxitsample.security/FoxitPKIEncryptd.pdf";
	private static final String strCustomDecryptFilePath = "/data/data/com.foxitsample.security/FoxitDecryptd.pdf";
	private static final String strPKIDecryptFilePath = "/data/data/com.foxitsample.security/FoxitPKIDecryptd.pdf";
	private static final String fileName = "/mnt/sdcard/FoxitBigPreview.pdf";
	private static boolean isEncrypt = false;
	private static boolean isPKIEncrypt = false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);   
        
        imageView = (ImageView)findViewById(R.id.display);
        
      //code start
      		func = new WrapPDFFunc();
      		boolean bRet = false;
			try {
				bRet = func.InitFoxitFixedMemory();
			} catch (Exception e) {
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
      		int nRet = func.InitPDFDoc(fileName);
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
			
			boolean bRet = func.DestroyFoxitFixedMemory();
			if (bRet != true){
				System.exit(0);
			}
			
			super.onDestroy();
			
	}   
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(0, M_SECURITY_ENCRYPT, 0, "Custom Encrypt");
		menu.add(0, M_SECURITY_Decrypt, 1,"Custom Decrypt");
		menu.add(0, M_PKI_ENCRYPT,2,"PKI Encrypt");
		menu.add(0, M_PKI_Decrypt,2,"PKI Decrypt");
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int ret  =-1;
		switch (item.getItemId()) 
		{	
			case M_SECURITY_ENCRYPT:
				if(isEncrypt == true)
				{
					Toast.makeText(this, "you have already encrypt the document!", Toast.LENGTH_SHORT).show();
					break;
				}
				ret = func.CustomEncrypt(strCustomEncryptFilePath);
				if(ret == 0)
				{
					func.ClosePDFPage();
					func.ClosePDFDoc();
					int nRet = func.InitPDFDoc(strCustomEncryptFilePath);
		     		int nPageCount = func.GetPageCounts();
		      		nRet = func.InitPDFPage(0);
		      		Display display = getWindowManager().getDefaultDisplay();
		      		int nWidth = display.getWidth();
		      		int nHeight = display.getHeight();
		      		imageView.setImageBitmap(func.getPageBitmap(nWidth, nHeight));
		      		imageView.invalidate();	
		      		isEncrypt = true;
		      		Toast.makeText(this, "Encrypt the PDF Successfully!", Toast.LENGTH_SHORT).show();
				}
				
				break;
			case M_SECURITY_Decrypt:
				if(isEncrypt != true)
				{
					String textout = "You must encrypt the pdf file first";
					Toast.makeText(this, textout, Toast.LENGTH_SHORT).show();
					break;
				}
				ret = func.CustomDecrypt(strCustomEncryptFilePath,strCustomDecryptFilePath);
				if(ret == 0)
				{
					func.ClosePDFPage();
					func.ClosePDFDoc();
					int nRet = func.InitPDFDoc(strCustomDecryptFilePath);
		     		int nPageCount = func.GetPageCounts();
		      		nRet = func.InitPDFPage(0);
		      		Display display = getWindowManager().getDefaultDisplay();
		      		int nWidth = display.getWidth();
		      		int nHeight = display.getHeight();
		      		imageView.setImageBitmap(func.getPageBitmap(nWidth, nHeight));
		      		imageView.invalidate();	
		      		Toast.makeText(this, "Decrypt the PDF Successfully!", Toast.LENGTH_SHORT).show();
				}
				break;
			case M_PKI_ENCRYPT:
				if(isPKIEncrypt == true)
				{
					Toast.makeText(this, "you have already PIK encrypt the document!", Toast.LENGTH_SHORT).show();
					break;
				}
				ret = func.PKIEncrypt(strPKIEncryptFilePath);
				if(ret == 0)
				{
					func.ClosePDFPage();
					func.ClosePDFDoc();
					int nRet = func.InitPDFDoc(strPKIEncryptFilePath);
		     		int nPageCount = func.GetPageCounts();
		      		nRet = func.InitPDFPage(0);
		      		Display display = getWindowManager().getDefaultDisplay();
		      		int nWidth = display.getWidth();
		      		int nHeight = display.getHeight();
		      		imageView.setImageBitmap(func.getPageBitmap(nWidth, nHeight));
		      		imageView.invalidate();	
		      		isPKIEncrypt = true;
		      		Toast.makeText(this, "PKI Encrypt the PDF Successfully!", Toast.LENGTH_SHORT).show();
		      		
				}
				break;
			case M_PKI_Decrypt:
				if(isPKIEncrypt != true)
				{
					String textout = "You must PKI encrypt the pdf file first";
					Toast.makeText(this, textout, Toast.LENGTH_SHORT).show();
					break;
				}
				ret = func.PKIDecrypt(strPKIEncryptFilePath,strPKIDecryptFilePath);
				if(ret == 0)
				{
					func.ClosePDFPage();
					func.ClosePDFDoc();
					int nRet = func.InitPDFDoc(strPKIDecryptFilePath);
		     		int nPageCount = func.GetPageCounts();
		      		nRet = func.InitPDFPage(0);
		      		Display display = getWindowManager().getDefaultDisplay();
		      		int nWidth = display.getWidth();
		      		int nHeight = display.getHeight();
		      		imageView.setImageBitmap(func.getPageBitmap(nWidth, nHeight));
		      		imageView.invalidate();	
		      		Toast.makeText(this, "PKI Decrypt the PDF Successfully!", Toast.LENGTH_SHORT).show();
				}
				break;
		}
		return true;
	}
	
}