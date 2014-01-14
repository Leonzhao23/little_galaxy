package com.little.galaxy.views;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_THREAD;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.little.galaxy.R;
import com.little.galaxy.activities.ReminderOnDemandActivity;
import com.little.galaxy.adaptors.ReminderOnDemandDoneViewAdaptor;
import com.little.galaxy.adaptors.ReminderOnDemandViewAdaptor;
import com.little.galaxy.annotations.LockNeeded;
import com.little.galaxy.annotations.LockUsed;
import com.little.galaxy.entities.ReminderOnDemandEntity;
import com.little.galaxy.layouts.ReminderOnDemandFooterViewLinearLayout;
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandListView extends ListView implements OnScrollListener{
	private Context ctx;
	private Handler viewHandler;
	 //lock
    final private ReentrantLock lock = new ReentrantLock();
    @LockNeeded("lock") final private LinkedList<ReminderOnDemandEntity> doneEntities = new LinkedList<ReminderOnDemandEntity>();
    private AtomicLong ptrLatestExecTime = new AtomicLong(0);
    private AtomicLong ptrLastExecTime = new AtomicLong(0);
    
    private LoadMoreDoneListViewThread loadThread = null;
    private RefreshDoneListViewThread freshThread = null;
    
    private Scroller scroller;
    private DownListViewState state = DownListViewState.NORMAL;
    private float lastY = -1;
    private int totalItemNum;
    //header view
    private View headerView = null;
    private LinearLayout composite;
    private TextView tipsTextview;  
    private TextView lastUpdatedTextView;  
    private ImageView arrowImageView;  
    private ProgressBar progressBar;  
    private int headerViewHeight;
    //footer view
    private ReminderOnDemandFooterViewLinearLayout footerView = null;
    private boolean pullRefreshing = false;
    private boolean enablePullRefresh = true;
    private boolean pullLoading = false;
    private boolean enablePullLoad = true;
    private boolean enableFooterView = true;
    
    private Animation rotateUpAnim;
    private Animation rotateDownAnim;
    private final int ROTATE_ANIM_DURATION = 180;
 
    // for mScroller, scroll back from header or footer.
    private int scrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; 
    private final static int PULL_LOAD_MORE_DELTA = 50;
    private final static float OFFSET_RADIO = 1.8f;
    private final static int DELTA = 1;
    
	private IDBService dbService = null;

	public ReminderOnDemandListView(Context context) {
		super(context);
		initViewElements(context);
	}
	
	public ReminderOnDemandListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewElements(context);
	}

	public ReminderOnDemandListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViewElements(context);
	}
	
	public void startInitDoneListView(){
		new initDoneListViewThread().start();
	}
	
	private void initViewElements(final Context context){
		ctx = context;
		viewHandler = ((ReminderOnDemandActivity)context).getViewHandler();
		dbService = ((ReminderOnDemandActivity)context).getDBService();
		scroller = new Scroller(context, new DecelerateInterpolator());
		 
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		headerView = layoutInflater.inflate(R.layout.listview_header, null);
		composite = (LinearLayout)headerView.findViewById(R.id.listview_header_composite);
		arrowImageView = (ImageView)headerView.findViewById(R.id.listview_header_arrow);
		tipsTextview = (TextView)headerView.findViewById(R.id.listview_header_hint_textview);
		lastUpdatedTextView = (TextView)headerView.findViewById(R.id.listview_header_time);
		progressBar = (ProgressBar)headerView.findViewById(R.id.listview_header_progressbar);
		headerView.setVisibility(View.INVISIBLE);
        addHeaderView(headerView);
        
        footerView = new ReminderOnDemandFooterViewLinearLayout(context);
        footerView.setViewState(DownListViewState.NORMAL);
        footerView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onLoad();
			}
        	
        });
        
        rotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateUpAnim.setFillAfter(true);
        rotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateDownAnim.setFillAfter(true);
        
        //init header height
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(
                   new OnGlobalLayoutListener() {
                       @Override
                       public void onGlobalLayout() {
                    	   headerViewHeight = composite.getHeight();
                    	   setVisiableHeaderHeight(0);//invisible by default.
                           getViewTreeObserver().removeGlobalOnLayoutListener(this);
                      }
        });
	}
	
	
	@Override
    public void computeScroll() {
		if (scroller.computeScrollOffset()) {
            if (scrollBack == SCROLLBACK_HEADER) {
                    setVisiableHeaderHeight(scroller.getCurrY());
            } else {
                    footerView.setBottomMargin(scroller.getCurrY());
            }
            postInvalidate();
		}
		super.computeScroll();     
    }

	private void updateHeaderHeight(float delta) {
		int height = (int) delta + getVisiableHeaderHeight();
        setVisiableHeaderHeight(height >  headerViewHeight ?  headerViewHeight: height);
        setSelection(0);
    }
	
	private void resetHeaderHeight() {
        int height = getVisiableHeaderHeight();
        if (height == 0) 
                return;
        // refreshing and header isn't shown fully. do nothing.
        if (pullRefreshing && height <= headerViewHeight) {
                return;
        }
        int finalHeight = 0;
        if (height > headerViewHeight) {
                finalHeight = headerViewHeight;
        }
        scrollBack = SCROLLBACK_HEADER;
        scroller.startScroll(0, height, 0, finalHeight - height,
                        SCROLL_DURATION);
        invalidate();
    }
	
	 private void setVisiableHeaderHeight(int height) {
         if (height < 0){
        	 height = 0;
         }
         LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)composite.getLayoutParams();
         lp.height = height;
         composite.setLayoutParams(lp);
	 }

	 private int getVisiableHeaderHeight() {
         return composite.getHeight();
	 }
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (lastY == -1){
			lastY = event.getY();
		}
		
		switch (event.getAction()){
		case MotionEvent.ACTION_DOWN:
			 lastY = event.getRawY();
			 break;
		case MotionEvent.ACTION_MOVE:
			 final float deltaY = event.getRawY() - lastY;
			 Log.v("Move", "deltaY=" + deltaY + ",headerViewHeight=" + headerViewHeight);
            //v lastY = event.getRawY();
             if (getFirstVisiblePosition() == 0  
            		 && (getVisiableHeaderHeight() > 0 || deltaY > 0)
            		 && enablePullRefresh) {
            	 switch (state){
            	 case NORMAL:
            		 changeHeaderViewByState(DownListViewState.PULL_TO_REFRESH);
            		 break;
            	 case PULL_TO_REFRESH:
            		 if (headerViewHeight > 0 && deltaY / OFFSET_RADIO >= headerViewHeight){
            			 changeHeaderViewByState(DownListViewState.RELEASE_TO_REFRESH);
            		 } else if (deltaY <= 0){
            			 changeHeaderViewByState(DownListViewState.NORMAL);
            		 }
            		 break;
            	 case RELEASE_TO_REFRESH:
            		 if (headerViewHeight > 0 && deltaY / OFFSET_RADIO < headerViewHeight && deltaY > 0){
            			 changeHeaderViewByState(DownListViewState.PULL_TO_REFRESH);
            		 } else if (deltaY <= 0){
            			 changeHeaderViewByState(DownListViewState.NORMAL);
            		 }
            		 break;
            	 default:
            		 break;
            		 
            	 }
            	 updateHeaderHeight(deltaY / OFFSET_RADIO);
             } else if (getLastVisiblePosition() == getCount() - 1
            		 && (footerView.getBottomMargin() > 0 || deltaY < 0)
            		 && enablePullLoad){           	 
            	 
            	 updateFooterHeight(-deltaY / OFFSET_RADIO);
            	 if (footerView.getBottomMargin() > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                     footerView.setViewState(DownListViewState.LOADING);
                 } else {
                     footerView.setViewState(DownListViewState.NORMAL);
                 }       
            	
             }
             break;
		default:
			lastY = -1; 
            if (getFirstVisiblePosition() == 0 && enablePullRefresh) {	
            	if (state == DownListViewState.RELEASE_TO_REFRESH){
            		changeHeaderViewByState(DownListViewState.REFRESHING);
            		pullRefreshing = true;
            		onRefresh();
            	} else{
            		changeHeaderViewByState(DownListViewState.NORMAL);
            	}
                resetHeaderHeight();
                   
            } else if (getLastVisiblePosition() == getCount() - 1) {
            	if (enablePullLoad && footerView.getBottomMargin() > PULL_LOAD_MORE_DELTA){
            		pullLoading = true;
            		onLoad();
            	}
            	resetFooterHeight();
            }
            break;	
		
		}
		return super.onTouchEvent(event);
	}  

	@Override
	public void onScroll(AbsListView listview, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		totalItemNum = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView listview, int scrollState) {
		
		int state = scrollState;
		int count = getCount();
//		switch (scrollState){
//			case OnScrollListener.SCROLL_STATE_IDLE:
//				// for footer
//				if (listview.getLastVisiblePosition() == (listview.getCount() - 1)) {
//					if (footerView != null){
//						footerView.setVisibility(View.VISIBLE);
//					}
//					if (listview.getCount() < delta){
//						
//					} else{
//					
//						if(loadThread == null){
//							loadThread = new LoadMoreDoneListViewThread();
//						}
//						if (!loadThread.isAlive()){
//							loadThread.start();
//						}
//					}
//					
//				} else {
//					if (footerView != null){
//						footerView.setVisibility(View.INVISIBLE);
//					}
//				}
//				
//				// for header
//				if(listview.getFirstVisiblePosition() == 0){
//					if (headerView != null){
//		        		headerView.setVisibility(View.VISIBLE);
//					}
//				
//			        
//		        } else {
//		        	if (headerView != null){
//		        		headerView.setVisibility(View.INVISIBLE);
//					}
//		        }		
//
//		     break;
//		}			
		
	}
	
	@LockUsed("lock")
	private void addFirst(final ReminderOnDemandEntity entity){
		lock.lock();
		try{
			doneEntities.addFirst(entity);
		} finally{
			lock.unlock();
		}	
	}
	
	@LockUsed("lock")
	private void addAll(List<ReminderOnDemandEntity> entities){
		lock.lock();
		try{
			doneEntities.addAll(entities);
		} finally{
			lock.unlock();
		}	
	}
	
	  private class RefreshDoneListViewThread extends Thread{
	    	@Override
	    	public void run(){
	    		List<ReminderOnDemandEntity> entities =dbService.getLatestReminders("2", ptrLatestExecTime.get(), DELTA);
				final int size = entities.size();
				if (size > 0){
					for (int index= size - 1; index >= 0; index--){
						ReminderOnDemandEntity entity = entities.get(index);
						if (index == 0){
							ptrLatestExecTime.compareAndSet(ptrLatestExecTime.get(), entity.getExecTime());
						}
						addFirst(entity);
					}
				}
				ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandDoneViewAdaptor(
						ctx,
						doneEntities);	
				if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
					Log.d(TAG_THREAD, "Thread RefreshDoneListViewThread");
				}
				Message msg= Message.obtain(viewHandler, 3, adaptor);
				viewHandler.sendMessage(msg);
				onStopRefresh();
	    	}
	    }
	    
	    private class LoadMoreDoneListViewThread extends Thread{
	    	@Override
	    	public void run(){
	    		List<ReminderOnDemandEntity> entities = dbService.getOlderReminders("2", ptrLastExecTime.get(), DELTA);
				final int size = entities.size();
				if (size > 0){
					ptrLastExecTime.compareAndSet(ptrLastExecTime.get(), entities.get(0).getExecTime());
					addAll(entities);
				}	
				
				ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandDoneViewAdaptor(
						ctx, 
						doneEntities);	
				if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
					Log.d(TAG_THREAD, "Thread LoadMoreDoneListViewThread");
				}
				Message msg= Message.obtain(viewHandler, 3, adaptor);
				viewHandler.sendMessage(msg);	
				onStopLoad();
	    	}
	    	
	    }
	    
	    private class initDoneListViewThread extends Thread{
	    	@Override
	    	@LockUsed("lock")
			public void run() {
				if (doneEntities.isEmpty()){
					List<ReminderOnDemandEntity> entities = dbService.getDoneReminders("2", DELTA);
					final int initFetchSize = entities.size();
					if (initFetchSize > 0){
						ptrLatestExecTime.compareAndSet(0, entities.get(0).getExecTime());
						ptrLastExecTime.compareAndSet(0, entities.get(initFetchSize -1).getExecTime());
						doneEntities.addAll(entities);
					}
					if (initFetchSize == DELTA){
						addFooterView(footerView);
					}	
				} 
				if (!doneEntities.isEmpty()){
					ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandDoneViewAdaptor(
							ctx, 
							doneEntities);	
					if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
						Log.d(TAG_THREAD, "Thread refreshDoneViewTask");
					}
					Message msg= Message.obtain(viewHandler, 3, adaptor);
					viewHandler.sendMessage(msg);	
				}
				
			}
	    }
	    
	    private void updateFooterHeight(float delta) {
            int height = footerView.getBottomMargin() + (int) delta;
            footerView.setBottomMargin(height);
	    }
	    
	    private void resetFooterHeight() {
            int bottomMargin = footerView.getBottomMargin();
            if (bottomMargin > 0) {
                    scrollBack = SCROLLBACK_FOOTER;
                    scroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                                    SCROLL_DURATION);
                    invalidate();
            }
	    }
	    
	    private void onStopRefresh(){
	    	if(pullRefreshing){
	    		pullRefreshing = false;
	    		enablePullRefresh = true;
	            resetHeaderHeight();
	    	}
	    	
	    }
	    
	    private void onStopLoad(){
	    	if(pullLoading){
	    		pullLoading = false;
	    		enablePullLoad = true;
	            resetFooterHeight();
	    	}
	    	
	    }
	    
	    private void onRefresh(){
	    	freshThread = new RefreshDoneListViewThread();
	    	freshThread.start();	
	    	enablePullRefresh = false;
	    }
	    
	    private void onLoad(){
	    	loadThread = new LoadMoreDoneListViewThread();
	    	loadThread.start(); 
	    	enablePullLoad = false;
	    }
	    
	    private void changeHeaderViewByState(DownListViewState lstate) {  
	    	if (this.state == lstate){
	    		return;
	    	}
	    	
	        switch (lstate) {  
	        case NORMAL:  
	        	//headerView.setPadding(0, -1 * headerViewHeight, 0, 0);  
	            progressBar.setVisibility(View.GONE);  
	            arrowImageView.setVisibility(View.INVISIBLE);
	            arrowImageView.clearAnimation();
	            tipsTextview.setVisibility(View.INVISIBLE); 
	            lastUpdatedTextView.setVisibility(View.INVISIBLE);  
	            break;
	        case PULL_TO_REFRESH:  
	            progressBar.setVisibility(View.GONE);  
	            tipsTextview.setVisibility(View.VISIBLE);  
	            lastUpdatedTextView.setVisibility(View.VISIBLE);  
	            arrowImageView.clearAnimation();  
	            arrowImageView.setVisibility(View.VISIBLE);  
	            arrowImageView.startAnimation(rotateDownAnim);
	            tipsTextview.setText("pull to refresh");  
	            break;  
	        case RELEASE_TO_REFRESH:  
	            progressBar.setVisibility(View.GONE);  
	            tipsTextview.setVisibility(View.VISIBLE);  
	            lastUpdatedTextView.setVisibility(View.VISIBLE); 
	            
	            arrowImageView.setVisibility(View.VISIBLE);  
	            arrowImageView.clearAnimation();  
	            arrowImageView.startAnimation(rotateUpAnim);  
	            tipsTextview.setText("release fresh");  
	            break;  
	  
	        case REFRESHING:  
	        	headerView.setPadding(0, 0, 0, 0);  
	            progressBar.setVisibility(View.VISIBLE);  
	            arrowImageView.clearAnimation();  
	            arrowImageView.setVisibility(View.GONE);  
	            tipsTextview.setText("refreshing");  
	            lastUpdatedTextView.setVisibility(View.VISIBLE);  
	            lastUpdatedTextView.setText("last updated time");
	            break;  
	            
			default:
				break;  
	        }  
	        this.state = lstate;
	    }  
	    
	    public enum DownListViewState{
	    	NORMAL,
	    	PULL_TO_REFRESH,  
	    	RELEASE_TO_REFRESH,
	        REFRESHING,
	        LOADING,
	    }

}
