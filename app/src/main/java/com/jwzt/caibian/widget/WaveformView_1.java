/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jwzt.caibian.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.SoundFile;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



/**
 * WaveformView 这个根据你的音频进行处理成完整的波形
 * 如果文件很大可能会很慢哦
 */
public class WaveformView_1 extends View {
    // Colors
    private int line_offset;
    private Paint mGridPaint;
    private Paint mSelectedLinePaint;
    private Paint mUnselectedLinePaint;
    private Paint mUnselectedBkgndLinePaint;
    private Paint mBorderLinePaint;
    private Paint mPlaybackLinePaint;
    private Paint mTimecodePaint;
    private Paint circlePaint;
    Paint paintLine;
    private int playFinish;
    private List<Float> cutPoint=new ArrayList<Float>();

    
    private Map<Float ,float[]> selectAreas=new HashMap<Float, float[]>();
    
    private List<float[]> selectPoints=new ArrayList<float[]>();
    
    private SoundFile mSoundFile;
    private int[] mLenByZoomLevel;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int[] mHeightsAtThisZoomLevel;
    private int mZoomLevel;
    private int mNumZoomLevels;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private int mOffset;
    private int mSelectionStart;
    private int mSelectionEnd;
    private int mPlaybackPos;
    private float mDensity;
    private float mInitialScaleSpan;
    private boolean mInitialized;
    private int state=0;
	private Paint cutPaint;
	private Paint mSelectedPaint;

    public int getPlayFinish() {
		return playFinish;
	}

	public void setPlayFinish(int playFinish) {
		this.playFinish = playFinish;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getLine_offset() {
		return line_offset;
	}

	public void setLine_offset(int line_offset) {
		this.line_offset = line_offset;
	}

	public WaveformView_1(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We don't want keys, the markers get these
        setFocusable(false);
        circlePaint = new Paint();//画圆
        circlePaint.setColor(Color.rgb(246, 131, 126));
        circlePaint.setAntiAlias(true);

        
        //进行切割标记
        cutPaint = new Paint();//切割线一
        cutPaint.setColor(Color.rgb(255, 255, 255));
        cutPaint.setStrokeWidth(2);
        cutPaint.setAntiAlias(true);
        
        mSelectedPaint = new Paint();//选择区域
         mSelectedPaint.setAntiAlias(false);
        mSelectedPaint.setColor(
        		Color.argb(55,255, 0, 0));
        
        
        
        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(false);
        mGridPaint.setColor(getResources().getColor(R.color.grid_line));
        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(getResources().getColor(R.color.waveform_selected));
        mUnselectedLinePaint = new Paint();
        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(getResources().getColor(R.color.waveform_unselected1));
        mUnselectedBkgndLinePaint = new Paint();
        mUnselectedBkgndLinePaint.setAntiAlias(false);
        mUnselectedBkgndLinePaint.setColor(getResources().getColor(R.color.waveform_unselected_bkgnd_overlay));
        mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(1.5f);
        mBorderLinePaint.setPathEffect(new DashPathEffect(new float[] { 3.0f, 2.0f }, 0.0f));
        mBorderLinePaint.setColor(getResources().getColor(R.color.selection_border));
        mPlaybackLinePaint = new Paint();
        mPlaybackLinePaint.setAntiAlias(false);
        mPlaybackLinePaint.setColor(getResources().getColor(R.color.playback_indicator));
        mTimecodePaint = new Paint();
        mTimecodePaint.setTextSize(12);
        mTimecodePaint.setAntiAlias(true);
        mTimecodePaint.setColor(getResources().getColor(R.color.timecode));
        mTimecodePaint.setShadowLayer(2, 1, 1,getResources().getColor(R.color.timecode_shadow));

        mSoundFile = null;
        mLenByZoomLevel = null;
        mValuesByZoomLevel = null;
        mHeightsAtThisZoomLevel = null;
        mOffset = 0;
        mPlaybackPos = -1;
        mSelectionStart = 0;
        mSelectionEnd = 0;
        mDensity = 1.0f;
        mInitialized = false;
    }


    public boolean hasSoundFile() {
        return mSoundFile != null;
    }

    public void setSoundFile(SoundFile soundFile) {
        mSoundFile = soundFile;
        mSampleRate = mSoundFile.getSampleRate();
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();
        computeDoublesForAllZoomLevels();
        mHeightsAtThisZoomLevel = null;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public int getZoomLevel() {
    	
        return mZoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        while (mZoomLevel > zoomLevel) {
            zoomIn();
        }
        while (mZoomLevel < zoomLevel) {
            zoomOut();
        }
    }

    public boolean canZoomIn() {
        return (mZoomLevel > 0);
    }

    public void zoomIn() {
        if (canZoomIn()) {
            mZoomLevel--;
            mSelectionStart *= 2;
            mSelectionEnd *= 2;
            mHeightsAtThisZoomLevel = null;
            int offsetCenter = mOffset + getMeasuredWidth() / 2;
            offsetCenter *= 2;
            mOffset = offsetCenter - getMeasuredWidth() / 2;
            if (mOffset < 0)
                mOffset = 0;
            invalidate();
        }
    }

    public boolean canZoomOut() {
        return (mZoomLevel < mNumZoomLevels - 1);
    }

    public void zoomOut() {
        if (canZoomOut()) {
            mZoomLevel++;
            mSelectionStart /= 2;
            mSelectionEnd /= 2;
            int offsetCenter = mOffset + getMeasuredWidth() / 2;
            offsetCenter /= 2;
            mOffset = offsetCenter - getMeasuredWidth() / 2;
            if (mOffset < 0)
                mOffset = 0;
            mHeightsAtThisZoomLevel = null;
            invalidate();
        }
    }

    public int maxPos() {
        return mLenByZoomLevel[mZoomLevel];
    }

    public int secondsToFrames(double seconds) {
        return (int)(1.0 * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public int secondsToPixels(double seconds) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int)(z * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    
//    public double pixelsToSeconds(int pixels) {
//        double z = mZoomFactorByZoomLevel[mZoomLevel];
//        return (pixels * (double)mSamplesPerFrame / (mSampleRate * z));
//    }
    
    
    public double pixelsToSeconds(int pixels) {
        double z = mZoomFactorByZoomLevel[0];
        return (mSoundFile.getmNumFramesFloat()*2 * (double)mSamplesPerFrame / (mSampleRate * z));
    }

    public int millisecsToPixels(int msecs) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int)((msecs * 1.0 * mSampleRate * z) /
                     (1000.0 * mSamplesPerFrame) + 0.5);
    }

    public int pixelsToMillisecs(int pixels) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int)(pixels * (1000.0 * mSamplesPerFrame) /
                     (mSampleRate * z) + 0.5);
    }
    
    public int pixelsToMillisecsTotal() {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int)(mSoundFile.getmNumFramesFloat()* 1 * (1000.0 * mSamplesPerFrame) /
                     (mSampleRate * 1) + 0.5);
    }

    public void setParameters(int start, int end, int offset) {
        mSelectionStart = start;
        mSelectionEnd = end;
        mOffset = offset;
    }

    public int getStart() {
        return mSelectionStart;
    }

    public int getEnd() {
        return mSelectionEnd;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setPlayback(int pos) {
        mPlaybackPos = pos;
    }



    public void recomputeHeights(float density) {
        mHeightsAtThisZoomLevel = null;
        mDensity = density;
        mTimecodePaint.setTextSize((int)(12 * density));
        invalidate();
    }

   
    protected void drawWaveformLine(Canvas canvas,
                                    int x, int y0, int y1,
                                    Paint paint) {

    	int pos = maxPos();
    	float rat =((float)getMeasuredWidth()/pos);
        canvas.drawLine((int)(x*rat), y0, (int)(x*rat)+1, y1, paint);
    }
    
    
    
    
    
    

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int height =measuredHeight-line_offset;

        canvas.drawARGB(0, 0, 0, 0);
//        canvas.drawARGB(255, 42, 53, 82);

            Paint centerLine = new Paint();
            centerLine.setColor(Color.rgb(255, 255, 255));
            canvas.drawLine(0, height*0.5f+line_offset/2, measuredWidth, height*0.5f+line_offset/2, centerLine);//中心线
            
            paintLine =new Paint();
            paintLine.setColor(Color.rgb(169, 169, 169));
        if(state == 1){
        	mSoundFile = null;
        	state = 0;
        	return;
        }
        
        
        
        
        for(int i=0;i<cutPoint.size();i++){
        	canvas.drawLine(cutPoint.get(i), 0, cutPoint.get(i), measuredHeight, cutPaint);
        }
        
        
        
        
        //选中区域选择
        for(int j=0;j<cutPoint.size();j++){
        		if(j==0&&touchX!=0){
        			if(touchX<cutPoint.get(j)){
        				float[] cutPostion=new float[2];
        				if(selectAreas.containsKey(0.0F)){
        					selectAreas.remove(0.0F);
        					
        				}else{
            				cutPostion[0]=0;
            				cutPostion[1]=cutPoint.get(j);
            				selectAreas.put(0.0F, cutPostion);
        				}
        				touchX=0;
        				break;
                	}
        		} 
        		if(j==cutPoint.size()-1){
        			if(touchX>cutPoint.get(j)){
        				float[] cutPostion=new float[2];
        				if(selectAreas.containsKey(cutPoint.get(j))){
        					selectAreas.remove(cutPoint.get(j));
        					
        				}else{
            				cutPostion[0]=cutPoint.get(j);
            				cutPostion[1]=getWidth();
            				selectAreas.put(cutPoint.get(j), cutPostion);
        				}
        				touchX=0;
        				break;
                	}
        		}
        		
        		if(j<cutPoint.size()){
        			if(touchX>cutPoint.get(j)&&touchX<cutPoint.get(j+1)&&touchX!=0){
        				float[] cutPostion=new float[2];
        				if(selectAreas.containsKey(cutPoint.get(j))){
        					selectAreas.remove(cutPoint.get(j));
        				}else{
//        					canvas.drawRect(cutPoint.get(j), 0.0F, cutPoint.get(j+1),
//            	        			measuredHeight, mSelectedPaint);
            				cutPostion[0]=cutPoint.get(j);
            				cutPostion[1]=cutPoint.get(j+1);
            				selectAreas.put(cutPoint.get(j), cutPostion);
        				}
        				touchX=0;
        				break;
                	}
        		}
        	}
        
        
        
        selectPoints.clear();//每次进行刷新的操作，重新填充集合
        Iterator<Float> iterator = selectAreas.keySet().iterator();
        while (iterator.hasNext()) {
        	//获取key值
        	Float next = iterator.next();
        	float[] fs = selectAreas.get(next);
        	canvas.drawRect(fs[0], 0.0F, fs[1],
        			measuredHeight, mSelectedPaint);
        }
        
        
        
        
        if (mSoundFile == null){
            height =measuredHeight-line_offset;
            centerLine = new Paint();
            centerLine.setColor(Color.rgb(39, 199, 175));
            canvas.drawLine(0, height*0.5f+line_offset/2, measuredWidth, height*0.5f+line_offset/2, centerLine);//中心线  
            paintLine =new Paint();
            paintLine.setColor(Color.rgb(169, 169, 169));
//            canvas.drawLine(0, line_offset/2,measuredWidth, line_offset/2, paintLine);//最上面的那根线
//           canvas.drawLine(0, height*0.25f+20, measuredWidth, height*0.25f+20, paintLine);//第二根线
//           canvas.drawLine(0, height*0.75f+20, measuredWidth, height*0.75f+20, paintLine);//第3根线
//            canvas.drawLine(0, measuredHeight-line_offset/2-1, measuredWidth, measuredHeight-line_offset/2-1, paintLine);//最下面的那根线
            return;
        }
        if (mHeightsAtThisZoomLevel == null)
            computeIntsForThisZoomLevel();

        // Draw waveform
        int start = mOffset;
        int width = mHeightsAtThisZoomLevel.length - start;
        int ctr = measuredHeight / 2;
        if (width > measuredWidth)
            width = measuredWidth;

        // Draw grid
        double onePixelInSecs = pixelsToSeconds(1);
        boolean onlyEveryFiveSecs = (onePixelInSecs > 1.0 / 50.0);
        double fractionalSecs = mOffset * onePixelInSecs;
        int integerSecs = (int) fractionalSecs;
        int i = 0;
        while (i < width) {
            i++;
            fractionalSecs += onePixelInSecs;
            int integerSecsNew = (int) fractionalSecs;
            if (integerSecsNew != integerSecs) {
                integerSecs = integerSecsNew;
            }
        }
        
      
        // Draw waveform
        for (i = 0; i < maxPos(); i++) {
        	Paint paint;
            if (i + start >= mSelectionStart &&
                i + start < mSelectionEnd) {
                paint = mSelectedLinePaint;
            } else {
                paint = mUnselectedLinePaint;
            }
            paint.setColor(Color.rgb(255, 255, 255));
            paint.setStrokeWidth(2);
            drawWaveformLine(
                canvas, i,
                (ctr - mHeightsAtThisZoomLevel[start + i]),
                (ctr + 1 + mHeightsAtThisZoomLevel[start + i]),
                paint);
          
            if (i + start == mPlaybackPos && playFinish != 1) {
     	        canvas.drawCircle(i*getMeasuredWidth()/maxPos(), line_offset/4, line_offset/4, circlePaint);// 上圆
     	        canvas.drawCircle(i*getMeasuredWidth()/maxPos(), getMeasuredHeight()-line_offset/4, line_offset/4, circlePaint);// 下圆
     	        canvas.drawLine(i*getMeasuredWidth()/maxPos(), 0, i*getMeasuredWidth()/maxPos(), getMeasuredHeight(), circlePaint);//垂直的线
            }
        }

        // Draw timecode
        double timecodeIntervalSecs = 1.0;
        if (timecodeIntervalSecs / onePixelInSecs < 50) {
            timecodeIntervalSecs = 5.0;
        }
        if (timecodeIntervalSecs / onePixelInSecs < 50) {
            timecodeIntervalSecs = 15.0;
        }
    }
    


    /**
     * Called once when a new sound file is added
     */
    private void computeDoublesForAllZoomLevels() {
        int numFrames = mSoundFile.getNumFrames();
        int[] frameGains = mSoundFile.getFrameGains();
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1) {
            smoothedGains[0] = frameGains[0];
        } else if (numFrames == 2) {
            smoothedGains[0] = frameGains[0];
            smoothedGains[1] = frameGains[1];
        } else if (numFrames > 2) {
            smoothedGains[0] = (double)(
                (frameGains[0] / 2.0) +
                (frameGains[1] / 2.0));
            for (int i = 1; i < numFrames - 1; i++) {
                smoothedGains[i] = (double)(
                    (frameGains[i - 1] / 3.0) +
                    (frameGains[i    ] / 3.0) +
                    (frameGains[i + 1] / 3.0));
            }
            smoothedGains[numFrames - 1] = (double)(
                (frameGains[numFrames - 2] / 2.0) +
                (frameGains[numFrames - 1] / 2.0));
        }

        double maxGain = 1.0;
        for (int i = 0; i < numFrames; i++) {
            if (smoothedGains[i] > maxGain) {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0;
        if (maxGain > 255.0) {
            scaleFactor = 255 / maxGain;
        }

        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int)(smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0)
                smoothedGain = 0;
            if (smoothedGain > 255)
                smoothedGain = 255;
            if (smoothedGain > maxGain)
                maxGain = smoothedGain;

            gainHist[smoothedGain]++;
        }

        double minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int)minGain];
            minGain++;
        }

        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {
            sum += gainHist[(int)maxGain];
            maxGain--;
        }
        if(maxGain <=50){
        	maxGain = 80;
        }else if(maxGain>50 && maxGain < 120){
        maxGain = 142;
        }else{
        maxGain+=10;
        }
        
       
        double[] heights = new double[numFrames];
        double range = maxGain - minGain;
        for (int i = 0; i < numFrames; i++) {
            double value = (smoothedGains[i] * scaleFactor - minGain) / range;
            if (value < 0.0)
                value = 0.0;
            if (value > 1.0)
                value = 1.0;
            heights[i] = value * value;
        }

        mNumZoomLevels = 5;
        mLenByZoomLevel = new int[5];
        mZoomFactorByZoomLevel = new double[5];
        mValuesByZoomLevel = new double[5][];

        // Level 0 is doubled, with interpolated values
        mLenByZoomLevel[0] = numFrames * 2;
        System.out.println("ssnum"+numFrames);
        mZoomFactorByZoomLevel[0] = 2.0;
        mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];
        if (numFrames > 0) {
            mValuesByZoomLevel[0][0] = 0.5 * heights[0];
            mValuesByZoomLevel[0][1] = heights[0];
        }
        for (int i = 1; i < numFrames; i++) {
            mValuesByZoomLevel[0][2 * i] = 0.5 * (heights[i - 1] + heights[i]);
            mValuesByZoomLevel[0][2 * i + 1] = heights[i];
        }

        // Level 1 is normal
        mLenByZoomLevel[1] = numFrames;
        mValuesByZoomLevel[1] = new double[mLenByZoomLevel[1]];
        mZoomFactorByZoomLevel[1] = 1.0;
        for (int i = 0; i < mLenByZoomLevel[1]; i++) {
            mValuesByZoomLevel[1][i] = heights[i];
        }

        // 3 more levels are each halved
        for (int j = 2; j < 5; j++) {
            mLenByZoomLevel[j] = mLenByZoomLevel[j - 1] / 2;
            mValuesByZoomLevel[j] = new double[mLenByZoomLevel[j]];
            mZoomFactorByZoomLevel[j] = mZoomFactorByZoomLevel[j - 1] / 2.0;
            for (int i = 0; i < mLenByZoomLevel[j]; i++) {
                mValuesByZoomLevel[j][i] =
                    0.5 * (mValuesByZoomLevel[j - 1][2 * i] +
                           mValuesByZoomLevel[j - 1][2 * i + 1]);
            }
        }

      
        
        if (numFrames > 5000) {
            mZoomLevel = 3;
        } else if (numFrames > 1000) {
            mZoomLevel = 2;
        } else if (numFrames > 300) {
            mZoomLevel = 1;
        } else {
            mZoomLevel = 0;
        }

        mInitialized = true;
    }

    /**
     * Called the first time we need to draw when the zoom level has changed
     * or the screen is resized
     */
    private void computeIntsForThisZoomLevel() {
    	
        int halfHeight = (getMeasuredHeight() / 2) - 1;
        mHeightsAtThisZoomLevel = new int[mLenByZoomLevel[mZoomLevel]];
        for (int i = 0; i < mLenByZoomLevel[mZoomLevel]; i++) {
            mHeightsAtThisZoomLevel[i] =
                (int)(mValuesByZoomLevel[mZoomLevel][i] * halfHeight);
        }
    }
    
    
    
    /**
     * 设置剪辑位置
     * @param position
     */
    public void setCutPostion(int position){
    	touchX=0;
    	int temp=cutPoint.size();
    	for(int i=0;i<cutPoint.size();i++){
    		if(cutPoint.get(i)>position){
    			temp=i;
    			break;
    		}
    	}
    	cutPoint.add(temp,(float) position);
    	invalidate();
    }
    
    
    private float touchX=0;
	private float touchX1;
	
	
	
	/**
	 * 返回分割点的集合
	 * @return
	 */
	public List<float[]> getCutPostion(){
		
		float[] tempArray=new float[selectAreas.size()];
		
		 Iterator<Float> iterator = selectAreas.keySet().iterator();
		 int flag=0;
	     while (iterator.hasNext()) {
	        	//获取key值
	        	Float next = iterator.next();
	        	tempArray[flag]=next;
	        	flag=flag+1;
	        }
	        for(int i=0;i<tempArray.length;i++){
	        	for(int j=0;j<tempArray.length;j++){
	        		if(tempArray[i]<tempArray[j]){
	        			float temp=0;
	        			temp=tempArray[j];
	        			tempArray[j]=tempArray[i];
	        			tempArray[i]=temp;
	        		}
	        	}
	        }
	        for(int i=0;i<tempArray.length;i++){
	        	selectPoints.add(selectAreas.get(tempArray[i]));
	        }
		return selectPoints;
	}
	
    
	/**
	 * 显示选中的区域
	 * @param isFling
	 */
	public void showSelectArea(boolean isFling){
		if(!isFling){
			//非滑动状态
			touchX=touchX1;
			invalidate();
		}
	}
    
	
	
	/**
	 * 返回选择区域的坐标
	 * @return
	 */
	public float getSelcetPoint(){
		return touchX;
	}
	
	
	/**
	 * 清除选中的断点位置，以及选中的区域
	 */
	public void clearPosition(){
		cutPoint.clear();
		touchX=0;
		//TODO
		selectAreas.clear();
		selectPoints.clear();
		invalidate();//重新绘制界面
	}
	
    
    /**
     * 进行控件的触摸事件处理（记录触摸的坐标）
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchX1=event.getX();
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			break;

		default:
			break;
		}
    	
    	return super.onTouchEvent(event);
    }
    
    
    
}
