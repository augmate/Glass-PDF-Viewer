package com.foxitsample.service;


import com.foxitsample.exception.errorException;
import com.foxitsample.exception.fileAccessException;
import com.foxitsample.exception.formatException;
import com.foxitsample.exception.invalidLicenseException;
import com.foxitsample.exception.memoryException;
import com.foxitsample.exception.parameterException;
import com.foxitsample.exception.passwordException;


import FoxitEMBSDK.EMBJavaSupport;
import FoxitEMBSDK.EMBJavaSupport.RectangleF;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;

public class TextDoc extends FoxitDoc {


	private static final String fileName = "/mnt/sdcard/FoxitText.pdf";

	private static final String password = "";

	private int nFileRead = 0;
	private int nPDFDocHandler = 0;
	//private int pageHandles[currentIndex] = 0;
	private int nPDFCurTextPageHandler = 0;
	private String strFindWhat = "Foxit";
	private int nFindHandler = 0;
	private boolean bFindFirst = true;
	private int nCurFindIndex = -1;
	private int nFindRects = -1;
	private EMBJavaSupport.RectangleF[] rcFind= null;
	//private int DisplayW = 0;
	//private int DisplayH = 0;
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
			return new RectF(rect.left,rect.bottom,rect.right,rect.top);
		} catch (Exception e) {
			postToLog(e.getMessage());
			return null;
		}
	}

	public RectF getRect(int pageNum, int charIndex, int startX, int startY, int width, int height){
		RectangleF rect;
		try {
			rect = EMBJavaSupport.FPDFTextGetRect(TextPageHandles[pageNum], charIndex);
			EMBJavaSupport.FPDFPagePageToDeviceRectF(TextPageHandles[pageNum],startX,startY,width,height,0,rect);
			return new RectF(rect.left,rect.bottom,rect.right,rect.top);
		} catch (Exception e) {
			postToLog(e.getMessage());
			return null;
		}
	}
	
	
	 /* @throws memoryException 
	 * @throws errorException 
	 * @throws passwordException 
	 * @throws formatException 
	 * @throws fileAccessException 
	 * @throws invalidLicenseException 
	 * @throws parameterException */
	public boolean InitPDFDoc() throws memoryException, parameterException, invalidLicenseException, fileAccessException, formatException, passwordException, errorException{
			
		/** Init a FS_FileRead structure*/
		nFileRead = EMBJavaSupport.FSFileReadAlloc(fileName);
		
		nPDFDocHandler = EMBJavaSupport.FPDFDocLoad(nFileRead, password);
		return true;
	}
	
	/** Close PDF Document
	 * @throws parameterException */
public boolean ClosePDFDoc() throws parameterException{
		
		if (nPDFDocHandler == 0){
			return true;
		}
		
		EMBJavaSupport.FPDFDocClose(nPDFDocHandler);
		nPDFDocHandler = 0;
		
		EMBJavaSupport.FSFileReadRelease(nFileRead);
		nFileRead = 0;
		
		return true;
	}
	

	
	/** Count PDF page
	 * @throws memoryException 
	 * @throws parameterException */
	public int GetPageCounts() throws parameterException, memoryException{
		
		if (nPDFDocHandler == 0){
			return -1;
		}
		
		return EMBJavaSupport.FPDFDocGetPageCount(nPDFDocHandler);
	}
	
	public int CloseFindHandler(){
		
		if (nFindHandler == 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		int nRet = EMBJavaSupport.FPDFTextFindClose(nFindHandler);
		nFindHandler = 0;
		if (nRet != 0){
			return EMBJavaSupport.EMBJavaSupport_RESULT_ERROR;
		}
		
		return EMBJavaSupport.EMBJavaSupport_RESULT_SUCCESS;
	}
	
	/*public Bitmap getPageBitmap(int displayWidth, int displayHeight) throws parameterException, memoryException, errorException, toBeContinuedException, statusException{
		if(pageHandles[currentIndex] == 0) {
			return null;
		} 
								
		Bitmap bm;
		bm = Bitmap.createBitmap(displayWidth,displayHeight,Bitmap.Config.ARGB_8888);
		
		int dib = EMBJavaSupport.FSBitmapCreate(displayWidth, displayHeight, 7, null, 0);
		EMBJavaSupport.FSBitmapFillColor(dib,0xff);
		EMBJavaSupport.FPDFRenderPageStart(dib, pageHandles[currentIndex], 0, 0, displayWidth, displayHeight, 0, 0, null, 0); 
		byte[] bmpbuf=EMBJavaSupport.FSBitmapGetBuffer(dib);
		
		ByteBuffer bmBuffer = ByteBuffer.wrap(bmpbuf); 
		bm.copyPixelsFromBuffer(bmBuffer);
		DisplayW = displayWidth;
		DisplayH = displayHeight;		
		return bm;
	}*/
	
	/*public void setDisplayWH(int displayWidth, int displayHeight)
	{
		DisplayW = displayWidth;
		DisplayH = displayHeight;	
	}*/
	
	public int FindPrev()
	{
		if(nPDFCurTextPageHandler == 0 || nFindHandler == 0)
			return 0;
		int ret = EMBJavaSupport.FPDFTextFindPrev(nFindHandler);
		if(ret != 1)
			return 0;
		nCurFindIndex = EMBJavaSupport.FPDFTextGetSchResultIndex(nFindHandler);
		int nCount = EMBJavaSupport.FPDFTextGetSchCount(nFindHandler);
		nFindRects = EMBJavaSupport.FPDFTextCountRects(nPDFCurTextPageHandler, nCurFindIndex, nCount);
		rcFind = new EMBJavaSupport.RectangleF[nFindRects];
		for (int i=0; i<nFindRects; i++)
		{	
			EMBJavaSupport.RectangleF rcFindTemp =  (new EMBJavaSupport()).new RectangleF();			
			rcFindTemp = EMBJavaSupport.FPDFTextGetRect(nPDFCurTextPageHandler, i);
			rcFind[i] = (new EMBJavaSupport()).new RectangleF();	 
			rcFind[i].left = rcFindTemp.left ;
			rcFind[i].top = rcFindTemp.top ;
			rcFind[i].right = rcFindTemp.right;
			rcFind[i].bottom = rcFindTemp.bottom;
		}
		return nFindRects;
	}
		
	public int SearchStart()
	{
		
		if(bFindFirst == false || nPDFCurTextPageHandler == 0 )
			return 0 ;
		nFindHandler = EMBJavaSupport.FPDFTextFindStart(nPDFCurTextPageHandler, strFindWhat, 4, 0);
		if(nFindHandler == 0 )
			return 0;
		bFindFirst = false;		
		int rectnum = FindNext();
		return rectnum;
	}
	
	public int FindNext()
	{
		if(nPDFCurTextPageHandler == 0 || nFindHandler == 0)
			return 0;
		int ret = EMBJavaSupport.FPDFTextFindNext(nFindHandler);
		EMBJavaSupport.FPDFTextGetText(nPDFCurTextPageHandler, 0, 100);
		if(ret != 1)
			return 0;
		nCurFindIndex = EMBJavaSupport.FPDFTextGetSchResultIndex(nFindHandler);
		int nCount = EMBJavaSupport.FPDFTextGetSchCount(nFindHandler);
		nFindRects = EMBJavaSupport.FPDFTextCountRects(nPDFCurTextPageHandler, nCurFindIndex, nCount);
		rcFind = new EMBJavaSupport.RectangleF[nFindRects];
		for (int i=0; i<nFindRects; i++)
		{	
			EMBJavaSupport.RectangleF rcFindTemp =  (new EMBJavaSupport()).new RectangleF();			
			rcFindTemp = EMBJavaSupport.FPDFTextGetRect(nPDFCurTextPageHandler, i);
			rcFind[i] = (new EMBJavaSupport()).new RectangleF();	 
			rcFind[i].left = rcFindTemp.left ;
			rcFind[i].top = rcFindTemp.top ;
			rcFind[i].right = rcFindTemp.right;
			rcFind[i].bottom = rcFindTemp.bottom;
		}
		return nFindRects;
	}
	
	
	public boolean InitPDFTextPage()
	{
		nPDFCurTextPageHandler = EMBJavaSupport.FPDFTextLoadPage(pageHandles[currentIndex]);
		//if(nPDFCurTextPageHandler == 0)
		//	return false;//throw later
		return true;
	}
	
	public void CloseTextPage()
	{
		EMBJavaSupport.FPDFTextCloseTextPage(nPDFCurTextPageHandler);
		nPDFCurTextPageHandler = 0;	
	}
	public RectangleF GetHighLightMarkedRect(int index)
	{
		if(nFindRects <= 0)
			return null;
		float left = rcFind[index].left;
		float bottom = rcFind[index].bottom;
		float right = rcFind[index].right;
		float top = rcFind[index].top;
		EMBJavaSupport.PointF point = (new EMBJavaSupport()).new PointF();
		EMBJavaSupport.RectangleF rect = (new EMBJavaSupport()).new RectangleF();
		point.x = left ;
		point.y = top ;
		EMBJavaSupport.FPDFPagePageToDevicePointF(pageHandles[currentIndex], 0, 0, DisplayW, DisplayH, 0, point);
		rect.left = point.x;
		rect.top = point.y;
		point.x = right ;
		point.y = bottom ;
		EMBJavaSupport.FPDFPagePageToDevicePointF(pageHandles[currentIndex], 0, 0, DisplayW, DisplayH, 0, point);
		rect.right = point.x;
		rect.bottom = point.y;
		
		return rect;
	}
	
	public Bitmap GetHighLightMarkedRectBitmap(int width,int height,float stride)
	{
		int[] colors=new int[width*height]; 
		for(int i = 0;i< width * height;i++)
			colors[i] = 0;
		int r = 0;  
       int g = 0;  
       int b = 255;  
       int a = 50;  
       int color_blue = Color.argb(a, r, g, b);
       for(int j =0;j< width * height;j++)
       	colors[j] = color_blue;
		Bitmap map = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
		return map;
	}
}