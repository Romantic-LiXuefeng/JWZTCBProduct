package com.jwzt.caibian.util;


import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
/**
 * 动画工具类
 * @author howie
 *
 */
public class AnimationUtils {
	/**
	 * 逐渐由透明变得不透明的动画
	 * 
	 * @param v
	 */
	public static void showAlpha(View v) {
		v.setVisibility(View.VISIBLE);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(200);
		v.startAnimation(alphaAnimation);

	}

	/**
	 * 由不透明逐渐变透明的动画
	 * 
	 * @param v
	 */
	public static void hideAlpha(View v) {
		
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(200);
		v.startAnimation(alphaAnimation);
		v.setVisibility(View.INVISIBLE);
	}

	/**
	 * 逐渐变大，并由透明变得不透明的动画
	 * 
	 * @param v
	 */
	public static void scaleUpAndShowAlpha(View v) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(200);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f,
				1.0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(200);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setFillAfter(true);
		v.startAnimation(animationSet);
	}

	/**
	 * 逐渐缩小，并由不透明变透明的动画
	 * 
	 * @param v
	 */
	public static void scaleDownAndHideAlpha(View v) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(200);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.5f, 1.0f,
				0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(200);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setFillAfter(true);
		v.startAnimation(animationSet);
//		v.setVisibility(View.INVISIBLE);//注释此句是因为当点击回复时输入文字后会出现发送和取消两个按钮，此时如果清空文字后发送取消按钮消失显示选择图片的按钮此时右侧会有空白（占位的）

	}

	/**
	 * 从顶部中间位置缩放变大的动画
	 * 
	 * @param view
	 */
	public static void scaleUpFromTop(View view) {
		ScaleAnimation sa=new ScaleAnimation(1, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0);
    	sa.setDuration(200);
    	view.startAnimation(sa);
	}

	/**
	 * 从顶部中间位置逐渐缩放变小的动画
	 * 
	 * @param view
	 */
	public static void scaleDownFromTop(View view) {
		ScaleAnimation sa=new ScaleAnimation(1, 1, 1, 0, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0);
    	sa.setDuration(200);
    	view.startAnimation(sa);
	}
	/**
	 * 顺时针从0度旋转到180度
	 */
	public static void rotateTo180(View view){
		RotateAnimation ra=new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(500);
		ra.setFillAfter(true);
		view.startAnimation(ra);
		
	}
	/**
	 * 顺时针从1800度旋转到360度
	 */
	public static void rotateTo360(View view){
		RotateAnimation ra=new RotateAnimation(180, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(500);
		ra.setFillAfter(true);
		view.startAnimation(ra);
		
	}
	/**
	 * 由不透明逐渐变透明的动画
	 * 
	 * @param v
	 */
	public static void hide(View v) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);
		v.startAnimation(alphaAnimation);
	}
	/**
	 * 由透明逐渐变不透明的动画
	 * 
	 * @param v
	 */
	public static void show(View v) {
		
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);
		v.startAnimation(alphaAnimation);
	}

}
