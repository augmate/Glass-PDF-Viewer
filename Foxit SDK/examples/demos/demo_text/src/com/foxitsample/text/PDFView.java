package com.foxitsample.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.graphics.Rect;

public class PDFView extends ImageView {

	private Bitmap bitmap;
	private RectF selRects[] = null;
	private int m_nRectCount = 0;

	public PDFView(Context context)
	{
		super(context);
	}

	public PDFView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setImageBitmap(Bitmap b) {
		bitmap = b;
		super.setImageBitmap(b);
	}

	public void newHighlight()
	{
		m_nRectCount = 0;//reset rectCount
		selRects = null;//discard previous list of rects, if there are any
	}
	
	public void addRect(RectF rt)
	{
		selRects[m_nRectCount++] = rt;
	}
	
	public void setRectNum(int rectnum)
	{
		selRects = new RectF[rectnum];
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
		drawTextHighlight(canvas);
	}
	
	private void drawTextHighlight(Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setAlpha(128);
		for(int i=0; i<m_nRectCount; i++)
			canvas.drawRect(selRects[i], paint);
	}
}