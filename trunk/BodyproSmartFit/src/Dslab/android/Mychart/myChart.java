package Dslab.android.Mychart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class myChart extends View{
	private float npoint[] = new float[100];
	private int cnt = 0;	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public myChart(Context context){
		super(context);

		
	}
	
	public myChart(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public myChart(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
		
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		
		paint.setAntiAlias(true);
		paint.setColor(0x50000000);
		paint.setStrokeWidth(1);
		
		canvas.drawColor(Color.parseColor("#9fefe0"));
		
		int i = 0;
		int h = canvas.getHeight();		
		int w = canvas.getWidth();
		//Log.i("MyChartView size","h = "+h+"w = "+w);
		for(; i<20;i++)
		{
			canvas.drawLine( 10*i, 0, 10*i, h, paint);
			canvas.drawLine( 0, 10*i, w, 10*i, paint);
			
		//	if(cnt != 0)
		//		canvas.drawPoints(point, paint);
			
		}
		paint.setColor(0xffff0000);
		paint.setStrokeWidth(3);
		if(cnt != 0)
			canvas.drawPoints(npoint, paint);
	}
	
	public void girdGraph(int hr){
		
		
		
		//start x
		int sx, sy;
		
		sx = cnt+3;
		sy = (hr/4);
		npoint[cnt++] = sx; 
		npoint[cnt++] = sy;
		
		if(cnt == 150)
			cnt = 0;
		
		Log.i("GRAPH","sx : "+sx+" "+"sy : "+sy);
		
		//invalidate();
		
	}
}
