package com.jwzt.caibian.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.CutBean;
import com.jwzt.caibian.bean.SeekBean;
import com.jwzt.caibian.interfaces.CurrentPosInterface;


public class MyRecyclerView extends RecyclerView {
	private int mScrollState;
	/**用于覆盖在画布上取交集显示下次的遮罩层*/
	private Drawable round_corner;
	
	private SelectionCompartor selectionComparator;
	public int getmScrollState() {
		return mScrollState;
	}
	public void setmScrollState(int mScrollState) {
		this.mScrollState = mScrollState;
	}
	
	
	/**交集混合模式的画笔*/
	private Paint mixPaint;
	private int duration;
	private ArrayList<SeekBean> seekBeans;
	private MyComparator mComparator;
	/**滚动的距离*/
	private int scrollDistance;
	/**裁剪区域开始那一秒对应的bean*/
	private CutBean firstSmallBean;
	/**裁剪区域结束那一秒对应的bean*/
	private CutBean firstBigBean;
	private RedDeleteListener listener;
	
	private List<CutBean> firstSmallList=new ArrayList<CutBean>();
	private List<CutBean> firstBigList=new ArrayList<CutBean>();
	private List<Float> scrollerList=new ArrayList<Float>();
	
	public void setListener(RedDeleteListener listener) {
		this.listener = listener;
	}
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		invalidate();
	}
	/**
	 * 得到所有的需要跳过的播放区间
	 * @return
	 */
	public ArrayList<SeekBean> getAllSeeks(){
		return seekBeans;
	}
	public void addSeekSelection(SeekBean bean){
		if(seekBeans==null){
			seekBeans=new ArrayList<SeekBean>();
		}
		seekBeans.add(bean);
		sort(seekBeans);  
		invalidate();
	}
	/**
	 * 对删除的区间进行排序
	 * @param seekBeans2
	 */
	private void sort(ArrayList<SeekBean> seekBeans2) {
		if(selectionComparator==null){
			selectionComparator=new SelectionCompartor();
		}
		
		Collections.sort(seekBeans2, selectionComparator);
	}
	/**
	 * 对裁剪的区间进行排序
	 * @author howie
	 *
	 */
	class SelectionCompartor implements Comparator<SeekBean>{

		@Override
		public int compare(SeekBean lhs, SeekBean rhs) {
			if(lhs.getStart()<rhs.getStart()){
				return -1;
			}else if(lhs.getStart()>rhs.getStart()){
				return 1;
			}
			return 0;
		}
	}
	public int getTotalDistance() {
		return totalDistance;
	}
	public CutBean getFirstSmallBean() {
		return firstSmallBean;
	}
	public void setFirstSmallBean(CutBean firstSmallBean) {
		this.firstSmallBean = firstSmallBean;
	}
	public CutBean getFirstBigBean() {
		return firstBigBean;
	}
	public void setFirstBigBean(CutBean firstBigBean) {
		this.firstBigBean = firstBigBean;
	}

	private int screenWidth;
	private int halfScreenWidth;
	private boolean isSelected;
	private ArrayList<Integer> touchSection;
	
	private int items=0;
	private int mHeight;
	Paint mPaint;
	private Paint redStrokePaint;//红色描边画笔
	private Paint tmStrokePaint;//透明画笔
	private float firstSmall;
	private float firstBig;
	/**水平方向可以滑动的最大长度*/
	private int totalDistance;
	private RectF rect;
	
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public void setTotalDistance(int totalDistance) {
		this.totalDistance = totalDistance;
	}

	private ArrayList<Integer> cuts;
	private ArrayList<CutBean> cutBeans;
	private float touchX=0;
	private float touchX1=0;
	private Map<Float ,CutBean[]> selectAreas=new HashMap<Float, CutBean[]>();
	private int screenWidth2;
	
	private CurrentPosInterface mCurrentPosInterface;
	private Paint redSolidePaint;
	
	public void addCut(int cut){
		if(cuts==null){
			cuts=new ArrayList<Integer>();
		}
		
		cuts.add(cut);
		Collections.sort(cuts);
		postInvalidate();
	}
	
	public void addCut(CutBean bean){
		if(cutBeans==null){
			cutBeans=new ArrayList<CutBean>();
			cutBeans.add(new CutBean(halfScreenWidth, 0));
			cutBeans.add(new CutBean((halfScreenWidth)*(items-1), duration));
		}
		if(mComparator==null){
			mComparator=new MyComparator();
		}
		int item=((int)bean.getCut()-halfScreenWidth)/halfScreenWidth;//获取当前切点在哪个item中
		bean.setItem(item);
		cutBeans.add(bean);
		Collections.sort(cutBeans,mComparator);//排序
		invalidate();
	}
	/**
	 * 得到所有的封装切点信息的bean的集合
	 * @return
	 */
	public ArrayList<CutBean> getAllCutBeans(){
		return cutBeans;
	}
	

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
		this.halfScreenWidth=screenWidth/2;
	}

	public int getScrollDistance() {
		return scrollDistance;
	}

	public void setScrollDistance(int scrollDistance,int items) {
		this.items=items;
		this.scrollDistance = scrollDistance;
		postInvalidate();
	}

	public void setCurrentPosListener(CurrentPosInterface currentPosInterface) {  
        this.mCurrentPosInterface = currentPosInterface;  
    }

	public MyRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);//白色
		mPaint.setStrokeWidth(5);
		redStrokePaint = new Paint();//选择区域
		redStrokePaint.setAntiAlias(false);
		redStrokePaint.setColor(Color.argb(55,255, 0, 0));
		
		tmStrokePaint = new Paint();//选择区域
		tmStrokePaint.setAntiAlias(false);
		tmStrokePaint.setColor(Color.argb(0,255, 0, 0));
		
		
		redSolidePaint=new Paint();
		redSolidePaint.setColor(Color.argb(10,255, 0, 0));
		round_corner=getResources().getDrawable(R.drawable.round_corner);
		
		mixPaint=new Paint();
		mixPaint.setColor(getResources().getColor(android.R.color.white));
		
		mixPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
	}

	
	private boolean isFling=false;
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		isFling=true;
		showSelectArea(true);//滑动状态
		if(mCurrentPosInterface!=null){
			mCurrentPosInterface.onCurrentPosChanged(l);
		}
		
	}

	
	
	
	
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	private float touchX2;
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(cutBeans!=null&&!cutBeans.isEmpty()){
			for (CutBean bean : cutBeans) {
				float cut =bean.getCut();
				float x=cut-scrollDistance;
				canvas.drawLine(x, 0, x, 250, mPaint);
			}
		
		//选中区域选择
        for(int j=0;j<cutBeans.size();j++){
        		if(j==0&&touchX!=0){
        			if((touchX+scrollDistance)<(cutBeans.get(j).getCut())){
//        				CutBean[] cutPostion=new CutBean[2];
//        				if(selectAreas.containsKey((float)halfScreenWidth)){
//        					selectAreas.remove((float)halfScreenWidth);
//        					
//        				}else{
//            				cutPostion[0]=new CutBean(halfScreenWidth, 0);//距离半屏的距离，播放进度为0的bean对象
//            				cutPostion[1]=(cutBeans.get(j));
//            				selectAreas.put((float)halfScreenWidth, cutPostion);
//        				}
        				touchX=0;
        				break;
                	}
        		} 
        		if(j==cutBeans.size()-1){
        			if((touchX+scrollDistance-halfScreenWidth)>(cutBeans.get(j).getCut())){
//        				CutBean[] cutPostion=new CutBean[2];
//        				if(selectAreas.containsKey((cutBeans.get(j).getCut()))){
//        					selectAreas.remove((cutBeans.get(j).getCut()));
//        					
//        				}else{
//            				cutPostion[0]=(cutBeans.get(j));
//            				cutPostion[1]=new CutBean((halfScreenWidth-2)*items, duration);
//            				selectAreas.put((cutBeans.get(j).getCut()), cutPostion);
//        				}
//        				touchX=0;
        				break;
                	}
        			
        		}
        		if(j<cutBeans.size()){
        			if((touchX+scrollDistance)>(cutBeans.get(j).getCut())&&(touchX+scrollDistance)<(cutBeans.get(j+1).getCut())&&touchX!=0){
        				CutBean[] cutPostion=new CutBean[2];
        				if(selectAreas.containsKey((cutBeans.get(j).getCut()))){
        					selectAreas.remove((cutBeans.get(j).getCut()));
        				}else{
            				cutPostion[0]=(cutBeans.get(j));
            				cutPostion[1]=(cutBeans.get(j+1));
            				selectAreas.put(cutBeans.get(j).getCut(), cutPostion);
        				}
        				touchX=0;
        				break;
                	}
        		}
        	}
		
        
        if(seekBeans!=null){
        	seekBeans.clear();//每次需清空
        }
		
        //选中的区间集合
        Iterator<Float> iterator = selectAreas.keySet().iterator();
        while (iterator.hasNext()) {
        	//获取key值
        	Float next = iterator.next();
        	CutBean[] fs = selectAreas.get(next);
        	
        	SeekBean seekBean=new SeekBean();
        	seekBean.setStart(fs[0].getProgress());
        	seekBean.setEnd(fs[1].getProgress());
        	seekBean.setStartDistance((int) fs[0].getCut());
        	seekBean.setEndDistance((int) fs[1].getCut());
        	
        	
        	addSeekSelection(seekBean);
        	
        	canvas.drawRect(fs[0].getCut()-scrollDistance, 0.0F, fs[1].getCut()-scrollDistance,
        			250, redStrokePaint);
        }
		}
        
		
		
		
		
		
		
		
  
	
		
		
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		switch(motionEvent.getAction()){
		case MotionEvent.ACTION_UP:
			touchX2=motionEvent.getX();
			if((touchX2-touchX1)==0){
				touchX=touchX1;
				invalidate();
			}
			break;
		case MotionEvent.ACTION_DOWN:
			touchX1=motionEvent.getX();
			
			break;
		}
		return super.onTouchEvent(motionEvent);
	}
	
	private boolean isExitSmall(CutBean bean){
		if(firstSmallList.size()==0){
			return true;
		}else{
			for(int i=0;i<firstSmallList.size();i++){
				if(firstSmallList.get(i)==bean){
					return false;
				}else{
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isExitBig(CutBean bean){
		if(firstBigList.size()==0){
			return true;
		}else{
			for(int i=0;i<firstBigList.size();i++){
				if(firstBigList.get(i)==bean){
					return false;
				}else{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean isStart(float scroll){
		if(scrollerList.size()==0){
			return true;
		}else{
			for(int i=0;i<scrollerList.size();i++){
				if(scrollerList.get(i)==scroll){
					return false;
				}else{
					return true;
				}
			}
		}
		return false;
	}
	
	public void showSelectArea(boolean isFling){
		this.isFling=isFling;
		if(!isFling){
			//非滑动状态
			touchX=touchX1;
			invalidate();
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mHeight=h;
	}
	
	
	/**
	 * 清除点集合
	 */
	public void clearPoint(){
		touchX=0;
		if(cutBeans!=null){
			cutBeans.clear();
			cutBeans=null;//需重新设置集合
		}
		selectAreas.clear();
		scrollDistance=0;
		
	}
	
	
	/**
	 * 排序的规则
	 * @author howie
	 *
	 */
	class MyComparator implements Comparator<CutBean>{

		@Override
		public int compare(CutBean lhs, CutBean rhs) {
			if(lhs.getCut()<rhs.getCut()){
				return -1;
			}else if(lhs.getCut()>rhs.getCut()){
				return 1;
			}
			return 0;
		}
		
	}
	public interface RedDeleteListener {
		/**
		 * 回调事件，把删除按钮变为红色
		 */
		void redDelete();
	}
	
	
	
}
