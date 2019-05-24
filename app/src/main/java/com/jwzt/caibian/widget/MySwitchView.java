package com.jwzt.caibian.widget;

import com.jwzt.cb.product.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
/**
 * 切换控件
 * @author howie
 *
 */
public class MySwitchView extends View {
	private int mWidth,mHeight;
	/**蓝色描边画笔*/
	private Paint blueStrokePaint;
	/**蓝色实心画笔*/
	private Paint blueFillPaint;
	/**蓝色边界线圆角矩形*/
	private RectF rect_border;
	/**蓝色色块的圆角矩形*/
	private RectF rectBlue;
	/**蓝色色块的圆角矩形的left值*/
	private int mLeft=0;
	/**蓝色色块的圆角矩形的top值*/
	private int mTop=0;
	
	public MySwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		blueStrokePaint=new Paint();
		blueStrokePaint.setColor(context.getResources().getColor(R.color.a5));
		blueStrokePaint.setStyle(Style.STROKE);
		
		blueStrokePaint.setStrokeWidth(2);
		blueStrokePaint.setAntiAlias(true);
		rect_border = new RectF();
		rectBlue = new RectF();
		blueFillPaint=new Paint();
		blueFillPaint.setAntiAlias(true);
		blueFillPaint.setColor(context.getResources().getColor(R.color.a5));
		
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		rect_border.left=0;
		rect_border.top=0;
		rect_border.right=mWidth;
		rect_border.bottom=mHeight;
		canvas.drawRoundRect(rect_border, 10, 10, blueStrokePaint);
		rectBlue.left=mLeft;
		rectBlue.top=0;
		rectBlue.right=mLeft+mWidth/2;
		rectBlue.bottom=mHeight;
		canvas.drawRoundRect(rectBlue, 10, 10, blueFillPaint);
		
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth=w;
		mHeight=h;
	}

}
