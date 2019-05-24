package com.jwzt.caibian.widget;

import com.jwzt.cb.product.R;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MySwitchButton extends RelativeLayout {
	private SwitchClick listener;
	private View view_slide;
	private int mWidth;
	private boolean isRight;
	private TextView tv_left,tv_right;
	public MySwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		View.inflate(getContext(), R.layout.custom_layout, this);
		view_slide=findViewById(R.id.view_slide);
		tv_left=(TextView) findViewById(R.id.tv_left);
		tv_right=(TextView) findViewById(R.id.tv_right);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){
		case MotionEvent.ACTION_UP:
			float x2 = event.getX();
			if(x2>mWidth/2){
//				Toast.makeText(getContext(), "点击了右侧", 0).show();
				if(!isRight){
					animateRight();
					isRight=true;
					if(listener!=null){
						listener.rightClick();
					}
				}
				
			}else{
//				Toast.makeText(getContext(), "点击了左侧", 0).show();
				if(isRight){
					animateLeft();
					isRight=false;
					if(listener!=null){
						listener.leftClick();
					}
				}
				
			}
			break;
		}
		return true;
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth=w;
	}
	private void animateRight(){
		AnimatorSet animatorSet =new AnimatorSet();
		ObjectAnimator animatorTransationY=ObjectAnimator.ofFloat(view_slide, "translationX", 0,mWidth/2);
//		animatorTransationY.setInterpolator(new BounceInterpolator());
		animatorTransationY.setInterpolator(new LinearInterpolator());
		animatorTransationY.setDuration(200);
//		animatorTransationY.setStartDelay(200);
		animatorTransationY.start();
		
		
		 int colorA = Color.parseColor("#000000");  
	        int colorB = Color.parseColor("#878787");  
	         
	        ObjectAnimator colorAnimLeft = ObjectAnimator.ofInt(tv_left,"textColor",colorA,colorB);  
	        colorAnimLeft.setDuration(200);  
	        colorAnimLeft.setEvaluator(new ArgbEvaluator()); 
	        
	        
	        ObjectAnimator colorAnimRight = ObjectAnimator.ofInt(tv_right,"textColor",colorB,colorA);  
	        colorAnimRight.setDuration(200);  
	        colorAnimRight.setEvaluator(new ArgbEvaluator()); 
//	        objectAnimator.start();  
		colorAnimLeft.start();
		colorAnimRight.start();

		animatorSet.playTogether(animatorTransationY);
		animatorSet.setDuration(200);
		animatorSet.start();
	}
	private void animateLeft(){
//		AnimatorSet animatorSet =new AnimatorSet();
		ObjectAnimator animatorTransationY=ObjectAnimator.ofFloat(view_slide, "translationX", mWidth/2,0);
//		animatorTransationY.setInterpolator(new BounceInterpolator());
		animatorTransationY.setInterpolator(new LinearInterpolator());
		animatorTransationY.setDuration(200);
//		animatorTransationY.setStartDelay(200);
		animatorTransationY.start();
		
		
		 int colorA = Color.parseColor("#000000");  
	        int colorB = Color.parseColor("#878787");  
		 ObjectAnimator colorAnimLeft = ObjectAnimator.ofInt(tv_left,"textColor",colorB,colorA);  
	        colorAnimLeft.setDuration(200);  
	        colorAnimLeft.setEvaluator(new ArgbEvaluator()); 
	        
	        
	        ObjectAnimator colorAnimRight = ObjectAnimator.ofInt(tv_right,"textColor",colorA,colorB);  
	        colorAnimRight.setDuration(200);  
	        colorAnimRight.setEvaluator(new ArgbEvaluator()); 
//	        objectAnimator.start();  
		colorAnimLeft.start();
		colorAnimRight.start();
	
//		animatorSet.playTogether(animatorTransationY);
//		animatorSet.start();
	}
	/**
	 * 模式切换的接口的回调
	 * @author howie
	 *
	 */
	public interface SwitchClick{
		/**
		 * 点击了左侧即点击了“正常模式”
		 */
		void leftClick();
		/**点击了右侧即点击了“突出原声”*/
		void rightClick();
	}
	public void setListener(SwitchClick listener) {
		this.listener = listener;
	}
	

}
