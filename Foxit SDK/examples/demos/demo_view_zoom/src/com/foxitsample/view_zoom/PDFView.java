package com.foxitsample.view_zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class PDFView extends ImageView{

	private Bitmap m_map = null;
	private int nStartX = 0;
	private int nStartY = 0;
	public int nCurDisplayX = 0;
	public int nCurDisplayY = 0;
	private Bitmap CurrentBitmap = null;
	private float nDisplayX = 0;
	private float nDisplayY = 0;
	private int drawConut = 0;
	public PDFView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	public PDFView(Context context) {
		super(context);
		
	}
	
	public void setBitmap(Bitmap map)
	{
		m_map = map;
		CurrentBitmap = map;
	}	
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		if(m_map != null)
			canvas.drawBitmap(CurrentBitmap, 0, 0, null);
		if(drawConut>0)
		{
			if(CurrentBitmap!=null&&!CurrentBitmap.isRecycled())
			{
				CurrentBitmap.recycle();
				CurrentBitmap = null;
				System.gc();
			}
		}
	    drawConut++;
	}
	public Bitmap GetCurBitmap()
	{
		return m_map;
	}
	public void SetMartix(float CurrentoffsetX,float CurrentoffsetY)
	{
		nStartX = nCurDisplayX - (int)CurrentoffsetX;
		nStartY = nCurDisplayY - (int)CurrentoffsetY;
		//Log.d("Darien", "nStartX:" + nStartX + " nStartY:" + nStartY);
		if(nStartX < 0) nStartX = 0;
		if(nStartX >= (m_map.getWidth() - nDisplayX)) nStartX = (int) (m_map.getWidth() - nDisplayX)-1;
		//Log.d("Darien", "m_map width:" + m_map.getWidth() + " nDisplayX:" + nDisplayX + " Diff:" + (int) (m_map.getWidth() - nDisplayX));
		if(nStartY < 0) nStartY = 0;
		if(nStartY >= (m_map.getHeight() - nDisplayY)) nStartY = (int) (m_map.getHeight() - nDisplayY)-1;
		nCurDisplayX = nStartX;
		nCurDisplayY = nStartY;
		//Log.d("Darien", "X:" + (m_map.getWidth() - nStartX) + " Y:" + (m_map.getHeight()- nStartY));
		CurrentBitmap = Bitmap.createBitmap(m_map, nStartX, nStartY,m_map.getWidth() - nStartX, m_map.getHeight()- nStartY);
	}
	
	public Bitmap pad(Bitmap Src, int padding_x, int padding_y){
	    Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x,Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
	    Canvas can = new Canvas(outputimage);
	    can.drawARGB(0x00,0x00,0x00,0x00); //This represents black color
	    can.drawBitmap(Src, padding_x, padding_y, null);
	return outputimage;
	}
		
	public void setDisplay(float nWidth,float nHeight)
	{
		nDisplayX = nWidth;
		nDisplayY = nHeight;
	}
}
