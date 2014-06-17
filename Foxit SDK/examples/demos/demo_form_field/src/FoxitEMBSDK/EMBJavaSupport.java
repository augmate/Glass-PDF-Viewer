package FoxitEMBSDK;

import java.util.Timer;
import java.util.TimerTask;

import com.foxitsample.exception.*;
import com.foxitsample.formfiled.mainActivity;


public class EMBJavaSupport {
	
	public final static int EMBJavaSupport_RESULT_SUCCESS = 0;
	public final static int EMBJavaSupport_RESULT_MEMORY = 1;
	public final static int EMBJavaSupport_RESULT_ERROR = -1;	
	
	private final static int TIMER_EVENT_ID = 10;
	
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
	
	public class RectangleF
	{
		public float		left;
		public float		top;
		public float		right;
		public float		bottom;
	}

	////////////////////
	//View Module
	public class PointF{
		public float   x;
		public float   y;
	}
	
	public static native int FPDFRenderPageStart(int dib, int page, int start_x, int start_y, 
											int size_x, int size_y, int rotate, int flags,
											Rectangle clip, int pauseHandler) throws toBeContinuedException, parameterException, memoryException, statusException, errorException;
	public static native int FPDFRenderPageContinue(int page, int pauseHandler) throws toBeContinuedException, parameterException, memoryException, statusException, errorException;
	public static native void FPDFPageDeviceToPagePointF(int page, int startx, int starty, int sizex, int sizey,
											int rotate, PointF point);
	public static native void FPDFPagePageToDeviceRectF(int page, int startx, int starty, int sizex, int sizey,
											int rotate, RectangleF rect);
	
	////////////////////
	//Base data Module
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
	
	/////formfiller
	public class CPDFSystemTime{
		int nYear;
		int nMonth;
		int nDayofWeek;
		int nDay;
		int nHour;
		int nMinute;
		int nSecond;
		int nMilliseconds;
	}
	
	public class CPDFFormFillerInfo{
		
		public int nCallBackAddr = 0;
		public int nTimeElapse = 0;
		private mainActivity mainView = null;
		private Timer time = null;
		
		public CPDFFormFillerInfo(mainActivity view){
			mainView = view;
		}
		
		public void Release(){
			
		}
		
		public void FFI_Invalidate(int page, float left, float top, float right, float bottom){
			if (mainView != null){
				int nCurPage = mainView.func.getCurPDFPageHandler();
				if (nCurPage != page) return;
			//	mainView.displayPDFView();
				mainView.invalidate(left, bottom, right, top);
			}
		}
		
		public void FFI_OutputSelectedRect(int page, double left, double top, double right, double bottom){
			
		}
		
		public void FFI_SetCursor(int nCursorType){
			
		}
		
		public int FFI_SetTimer(int uElapse, int callbackaddr){
			nCallBackAddr = callbackaddr;
			nTimeElapse = uElapse;
			time = new Timer();			
			TimerTask task = new TimerTask(){
				 public void run() {   
					 EMBJavaSupport.FPDFExecCallBack(nCallBackAddr, true);
				 }
			};
			time.schedule(task, uElapse, uElapse);
			return TIMER_EVENT_ID;
		}
		
		public void FFI_KillTimer(int nTimerID){
			time.cancel();
		}
		
		public CPDFSystemTime FFI_GetLocalTime(){
			return null;			
		}
		
		public void FFI_OnChange(){
			
		}
		
		public int FFI_GetPage(int document, int page_index){
			if (mainView != null){
				int nPageHandler = mainView.func.getPageHandler(page_index);
				return nPageHandler;
			}
			return 0;
		}
		
		public int FFI_GetCurrentPage(int document){
			if (mainView != null){
				return mainView.func.getCurPDFPageHandler();
			}
			return 0;
		}
		
		public int FFI_GetRotation(int page){
			return 0;
		}		
		
		public void FFI_ExecuteNamedAction(String nameAction){
			
		}
		
		public void FFI_OnSetFieldInputFocus(int field, String focustext, int nTextLen){
			mainView.createAndroidTextField(focustext);
		}
	}
	
	public class CPDFJsPlatform{
		
		public int app_alert(String msg, String title, int type, int icon){
			return 0;
		}
		
		public int app_beep(int type){
			return 0;
		}
		
		public String app_response(String question, String title, String defval, String label, boolean password){
			return "";
		}
		
		public String Doc_getFilePath(){
			return "";
		}
		
		public void Doc_mail(byte[] maildata, int length, boolean ui, String to, String subject, String cc, String bcc, String msg){
			
		}
		
		public void Doc_print(boolean ui, int start, int end, boolean slient, boolean shrinktofit, boolean printasimage, boolean reverse, boolean annotations){
			
		}
		
		public void Doc_submitForm(byte[] formdata, int size, String url){
			
		}
		
		public String Field_browse(String filepath){
			return "";
		}
	}
	
	public static native int FPDFFormFillerInfoAlloc(CPDFFormFillerInfo info);
	public static native void FPDFFormFillerInfoRelease(int nFormFillerInfo);
	public static native int FPDFJsPlatformAlloc(CPDFJsPlatform jsPlatform);
	public static native void FPDFJsPlatformRelease(int nJsPlatform);
	public static native void FPDFFormFillerInfoSetJsPlatform(int nFormFillerInfo, int nJsPlatform);
	public static native void FPDFJsPlatformSetFormFillerInfo(int nJsPlatform, int nFormFillerInfo);
	
	public static native int FPDFDocInitFormFillEnviroument(int document, int nFormFillerInfo);
	public static native void FPDFDocExitFormFillEnviroument(int nFormHandler);
	
	public static native void FPDFFormFillOnAfterLoadPage(int nFormHandler, int page);
	public static native void FPDFFormFillDraw(int nFormHandler, int dib, int page, int startx, int starty, 
										int sizex, int sizey, int rotate, int flags);
	
	public static native void FPDFFormFillOnKillFocus(int nFormHandler);
	public static native int FPDFFormCreateInterForm(int document, boolean updateap);
	public static native void FPDFFormReleaseInterForm(int nInterForm);
	public static native boolean FPDFFormExportToXML(int nInterForm, int nFileWriter);
	public static native boolean FPDFFormImportFromXML(int nInterForm, int nFileReader);
	public static native void FPDFFormFillUpdatForm(int nFormHandler);
	public static native boolean FPDFFormFillOnMouseMove(int nFormHandler, int page, int evenflag, double pagex, double pagey);
	public static native boolean FPDFFormFillOnLButtonUp(int nFormHandler, int page, int evenflag, double pagex, double pagey);
	public static native boolean FPDFFormFillOnLButtonDown(int nFormHandler, int page, int evenflag, double pagex, double pagey);
	public static native boolean FPDFFormFillOnSetText(int nFormHandler, int page, String text, int evenflag);
	public static native void FPDFExecCallBack(int callbackaddr, boolean newThread);
	
	public static native void FSUnlock(String sn,String code);
	
	public static native void FSSetFileFontmap(String filepath) throws parameterException;	
}