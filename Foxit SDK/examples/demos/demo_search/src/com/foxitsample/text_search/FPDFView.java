package com.foxitsample.text_search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FPDFView extends ImageView {

	private Bitmap bitmap = null;
	private Bitmap highlightBitmap = null;
	private int nHighlightX = 0;
	private int nHighlightY = 0;
	public FPDFView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//setWillNotDraw(false); 
	}
	public FPDFView(Context context) {
		super(context);
		//setWillNotDraw(false);
	}
	
	public void setBitmap(Bitmap b)
	{
		bitmap = b;
	}
	public void setHighLightBitmap(Bitmap hb)
	{
		highlightBitmap = hb;
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		if(bitmap != null)
			canvas.drawBitmap(bitmap, 0, 0, null);
		if(highlightBitmap != null)
			canvas.drawBitmap(highlightBitmap, nHighlightX,nHighlightY,null);
	}
	public void setHighLightBitmapXY(int x,int y)
	{
		nHighlightX = x;
		nHighlightY = y;
	}
}
