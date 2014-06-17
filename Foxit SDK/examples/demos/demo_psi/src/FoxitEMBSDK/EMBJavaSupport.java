package FoxitEMBSDK;

import android.graphics.Bitmap;
import android.util.Log;

import com.foxitsample.psi.mainActivity;
import com.foxitsample.exception.*;

public class EMBJavaSupport {
	
	public final static int EMBJavaSupport_RESULT_SUCCESS = 0;
	public final static int EMBJavaSupport_RESULT_MEMORY = 1;
	public final static int EMBJavaSupport_RESULT_ERROR = -1;	
	
	public static final int FXG_PT_LINETO = 2;
	public static final int FXG_PT_MOVETO = 6;
    public static final int FXG_PT_ENDPATH = 8;
    
    public static final	int PSI_ACTION_DOWN = 1;
    public static final int PSI_ACTION_UP = 2;
    public static final int PSI_ACTION_MOVE = 3;
	
	//Define interfaces of EMB SDK. Users can define them according to their own requirement.
	//Users can get descriptions of these interfaces from FoxitEMBSDK Help Document.
	///////////////////
	static{
		System.loadLibrary("fpdfembedsdk");
	}
	
	////////////////////
	//Document Module
	public static native int FPDFDocLoad(int fileRead, String password) throws parameterException, invalidLicenseException, fileAccessException, formatException, memoryException, passwordException, errorException;
	public static native void FPDFDocClose(int document) throws parameterException;	  
    public static native int FPDFDocGetPageCount(int document) throws parameterException, memoryException;
	public static native int FPDFPageLoad(int document, int index) throws parameterException;
	public static native void FPDFPageClose(int page) throws parameterException;
	public static native int FPDFPageStartParse(int page, int text_only, int pauseHandler) throws toBeContinuedException, parameterException, statusException, memoryException;
	public static native int FPDFPageContinueParse(int page, int pauseHandler) throws toBeContinuedException, parameterException, statusException, memoryException;

	
	public class Rectangle
	{
		public int		left;
		public int		top;
		public int		right;
		public int		bottom;

	}

	////////////////////
	//View Module
	public static native int FPDFRenderPageStart(int dib, int page, int start_x, int start_y, 
											int size_x, int size_y, int rotate, int flags,
											Rectangle clip, int pauseHandler) throws toBeContinuedException, parameterException, memoryException, statusException, errorException;
	public static native int FPDFRenderPageContinue(int page, int pauseHandler) throws toBeContinuedException, parameterException, memoryException, statusException, errorException;
	
//	public static native int FPDF_RenderPageWithBitmap_Start(Bitmap bitmap, int page, int start_x, int start_y, 
	//										int size_x, int size_y, int rotate, int flags,
	//										Rectangle clip, int pauseHandler);	
	
	////////////////////
	//Base data Module
	public static native void FSUnlock(String sn,String code);	
	public static native void FSSetFileFontmap(String filepath) throws parameterException;		  

	public static native void FSInitLibrary(int hInstance);
	public static native void FSDestroyLibrary();
	public static native void FSMemInitFixedMemory(int size) throws parameterException, invalidLicenseException;
	public static native void FSMemDestroyMemory();
	
	public static native int FSBitmapCreate(int width, int height, int format, byte[] buffer, int stride) throws parameterException, memoryException, errorException;
	public static native void FSBitmapDestroy(int dib) throws parameterException;
	public static native byte[] FSBitmapGetBuffer(int dib) throws parameterException;
	public static native void FSBitmapFillColor(int dib, int argb) throws parameterException;	
	
	public static native void FSFontLoadGBCMap();
	public static native void FSFontLoadGBExtCMap();
	public static native void FSFontLoadCNSCMap();
	public static native void FSFontLoadKoreaCMap();
	public static native void FSFontLoadJapanCMap();
	public static native void FSFontLoadJapanExtCMap();
	public static native void FSLoadJbig2Decoder();
	public static native void FSLoadJpeg2000Decoder();
	
	public static native int FSFileReadAlloc(String filePath) throws memoryException;
	public static native void FSFileReadRelease(int fileRead);
	
	public static native int FSPauseHandlerAlloc() throws memoryException;
	public static native void FSPauseHandlerRelease(int pauseHandler);
	

	
	////PSI
	public class CPDFPSI{
		private mainActivity mainView = null;
		
		public CPDFPSI(mainActivity view){
			mainView = view;
		}
		
		public void FPSI_Invalidate(int left, int top, int right, int bottom){
			Log.e("xxxxxxxxxxjavasupport","FPSI_Invalidate");
			if (mainView != null){
				mainView.invalidate(left, top, right, bottom);
			}
		}
		
		public float FPSI_GetOpacity(){
			return 1.0f;
		}
		
	}
	
	public static native int FPSIInitAppCallback(CPDFPSI pCallBack);
	public static native void FPSIReleaseAppCallback(int nCallback);
	
	public static native int FPSIInitEnvironment(int nCallback, boolean bSimulate);
	public static native void FPSIDestroyEnvironment(int nPSIHandler);
	
	public static native int FPSIInitCanvas(int nPSIHandler, int width, int height);
	public static native int FPSISetInkColor(int nPSIHandler, long color);
	public static native int FPSISetInkDiameter(int nPSIHandler, int diameter);
	public static native int FPSIAddPoint(int nPSIHandler, float x, float y, float pressure, int flag);
	public static native int FPSIRender(int nPSIHandler, int bitmap, int left, int top, int right, int bottom, int src_left, int src_top);
//	public static native int FPSIRenderWithBitmap(int nPSIHandle, Bitmap bitmap, int left, int top, int right, int bottom);
	public static native int FPDFPage_GeneratePSIAnnot(int nPSIHandler, int page, Rectangle pageRect, float left, float top, float right, float bottom);

}