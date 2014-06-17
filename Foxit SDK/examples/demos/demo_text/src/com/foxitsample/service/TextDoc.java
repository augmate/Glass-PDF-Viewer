package com.foxitsample.service;

import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.memoryException;
import com.foxitsample.exception.parameterException;

import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.Rectangle;
import FoxitEMBSDK.EMBJavaSupport.RectangleF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.Toast;

public class TextDoc extends FoxitDoc {

	private int[] TextPageHandles = null;

	public TextDoc(String filePath, String password) {
		super(filePath, password);
	}

	public void close(){
		if(TextPageHandles!=null){
			for(int i=0;i<TextPageHandles.length;i++){
				if(TextPageHandles[i]>0){
					EMBJavaSupport.FPDFTextCloseTextPage(TextPageHandles[i]);
					TextPageHandles[i]=0;
				}
			}
		}
		super.close();
	}

	public void initTextPage(int pageNum){
		CountPages();
		InitPage(pageNum);
		if(TextPageHandles==null)
			TextPageHandles = new int[pageCount];
		try {
			if(TextPageHandles[pageNum]<=0){
				TextPageHandles[pageNum] = EMBJavaSupport.FPDFTextLoadPage(pageHandles[pageNum]);
			}
		} catch (Exception e){
			postToLog(e.getMessage());
		}
	}

	public int getRectCount(int pageNum, int beginning, int end){
		try {
			return EMBJavaSupport.FPDFTextCountRects(TextPageHandles[pageNum], beginning, end);
		} catch (Exception e) {
			postToLog(e.getMessage());
			return -1;
		} 
	}

	public String getText(int pageNum, int beginning, int end){
		try {
			return EMBJavaSupport.FPDFTextGetText(TextPageHandles[pageNum], beginning, end);
		} catch (Exception e) {
			postToLog(e.getMessage());
			return "";
		}
	}

	public int getCharCount(int pageNum){
		try {
			return EMBJavaSupport.FPDFTextCountChars(TextPageHandles[pageNum]);
		} catch (Exception e) {
			postToLog(e.getMessage());
			return -1;
		}
	}

	public RectF getRect(int pageNum, int charIndex){
		RectangleF rect;
		try {
			rect = EMBJavaSupport.FPDFTextGetRect(TextPageHandles[pageNum], charIndex);
			return new RectF(rect.left,rect.top,rect.right,rect.bottom);
		} catch (Exception e) {
			postToLog(e.getMessage());
			return null;
		}
	}

	public RectF getRect(int pageNum, int charIndex, int startX, int startY, int width, int height){
		RectangleF rect;
		try {
			rect = EMBJavaSupport.FPDFTextGetRect(TextPageHandles[pageNum], charIndex);
			EMBJavaSupport.FPDFPagePageToDeviceRectF(pageHandles[pageNum],startX,startY,width,height,0,rect);
			return new RectF(rect.left,rect.top,rect.right,rect.bottom);
		} catch (Exception e) {
			postToLog(e.getMessage());
			return null;
		}
	}
}