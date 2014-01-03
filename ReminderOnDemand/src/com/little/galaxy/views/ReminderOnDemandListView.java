package com.little.galaxy.views;

import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_THREAD;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import com.little.galaxy.storages.IDBService;

public class ReminderOnDemandListView extends ListView implements OnScrollListener{
	private Context ctx;
	private Handler viewHandler;
	 //lock
    final private ReentrantLock lock = new ReentrantLock();
    @LockNeeded("lock") final private LinkedList<ReminderOnDemandEntity> doneEntities = new LinkedList<ReminderOnDemandEntity>();
    @LockNeeded("lock") private long ptrLatestExecTime = 0l;
    @LockNeeded("lock") private long ptrLastExecTime = 0l;
    
    private LoadMoreDoneListViewThread loadThread = null;
    private RefreshDoneListViewThread freshThread = null;
    
    private Scroller scroller;
    private DownListViewState state = DownListViewState.DONE;
    private float lastY = -1;
    private int totalItemNum;
    //header view
    private View headerView = null;
    private TextView tipsTextview;  
    private TextView lastUpdatedTextView;  
    private ImageView arrowImageView;  
    private ProgressBar progressBar;  
    private int headerViewHeight;
    //footer view
    private View footerView = null;
    private boolean enablePullLoad;
    private boolean pullLoading;
    private boolean isFooterReady = false;
    
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
    private final static int delta = 5;
    
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
		this.ctx = context;
		this.viewHandler = ((ReminderOnDemandActivity)context).getViewHandler();
		this.dbService = ((ReminderOnDemandActivity)context).getDBService();
		scroller = new Scroller(context, new DecelerateInterpolator());
		 
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		headerView = layoutInflater.inflate(R.layout.listview_header, null);
		arrowImageView = (ImageView)headerView.findViewById(R.id.listview_header_arrow);
		tipsTextview = (TextView)headerView.findViewById(R.id.listview_header_hint_textview);
		lastUpdatedTextView = (TextView)headerView.findViewById(R.id.listview_header_time);
		progressBar = (ProgressBar)headerView.findViewById(R.id.listview_header_progressbar);
		headerViewHeight = headerView.getHeight();
        this.addHeaderView(headerView);
        
        footerView = layoutInflater.inflate(R.layout.listview_footer, null);
        this.addFooterView(footerView);
        footerView.setVisibility(View.INVISIBLE);
        
        rotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateUpAnim.setFillAfter(true);
        rotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateDownAnim.setFillAfter(true);
	}
	
	
	@Override
    public void computeScroll() {
		if (scroller.computeScrollOffset()) {
            if (scrollBack == SCROLLBACK_HEADER) {
                    setVisiableHeaderHeight(scroller.getCurrY());
            } else {
                    //footerView.setBottomMargin(scroller.getCurrY());
            }
            postInvalidate();
            //invokeOnScrolling();
		}
		super.computeScroll();     
    }

	private void updateHeaderHeight(float delta) {
        setVisiableHeaderHeight((int) delta + getVisiableHeaderHeight());
    }
	
	private void resetHeaderHeight() {
        int height = getVisiableHeaderHeight();
        if (height == 0) 
                return;
        // refreshing and header isn't shown fully. do nothing.
        if (height <= headerViewHeight) {
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
         LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) headerView.getLayoutParams();
         lp.height = height;
         headerView.setLayoutParams(lp);
	 }

	 private int getVisiableHeaderHeight() {
         return headerView.getHeight();
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
             lastY = event.getRawY();
             if (getFirstVisiblePosition() == 0  && getVisiableHeaderHeight() > 0) {
            	 switch (state){
            	 case DONE:
            		 changeHeaderViewByState(DownListViewState.PULL_TO_REFRESH);
            		 break;
            	 case PULL_TO_REFRESH:
            		 if (deltaY / OFFSET_RADIO >= headerViewHeight){
            			 changeHeaderViewByState(DownListViewState.RELEASE_TO_REFRESH);
            		 } else if (deltaY <= 0){
            			 changeHeaderViewByState(DownListViewState.DONE);
            		 }
            		 break;
            	 case RELEASE_TO_REFRESH:
            		 if (deltaY / OFFSET_RADIO < headerViewHeight && deltaY > 0){
            			 changeHeaderViewByState(DownListViewState.PULL_TO_REFRESH);
            		 } else if (deltaY <= 0){
            			 changeHeaderViewByState(DownListViewState.DONE);
            		 }
            		 break;
            	 default:
            		 break;
            		 
            	 }
                 updateHeaderHeight(deltaY / OFFSET_RADIO);
             } else if (getLastVisiblePosition() == totalItemNum){
            	 
             }
             break;
		default:
			lastY = -1; 
            if (getFirstVisiblePosition() == 0) {
            	if (state == DownListViewState.PULL_TO_REFRESH){
            		changeHeaderViewByState(DownListViewState.DONE);
            	}	
            	if (state == DownListViewState.RELEASE_TO_REFRESH){
            		changeHeaderViewByState(DownListViewState.REFRESHING);
            		onRefresh();
            	}
                resetHeaderHeight();
                   
            } else if (getLastVisiblePosition() == getCount() - 1) {
                   
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
	
	  private class RefreshDoneListViewThread extends Thread{
	    	@Override
	    	@LockUsed("lock")
	    	public void run(){
	    		lock.lock();
				try{
					List<ReminderOnDemandEntity> entities = null;
					entities = dbService.getLatestReminders("2", ptrLatestExecTime, delta);
					final int size = entities.size();
					if (size > 0){
						for (int index= size - 1; index >= 0; index--){
							ReminderOnDemandEntity entity = entities.get(index);
							doneEntities.addFirst(entity);
							if (index == 0){
								ptrLatestExecTime = entity.getExecTime();
							}
						}
						
					}
				} finally{
					lock.unlock();
				}	
				ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandDoneViewAdaptor(
						ctx,
						doneEntities);	
				if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
					Log.d(TAG_THREAD, "Thread RefreshDoneListViewThread");
				}
				Message msg= Message.obtain(viewHandler, 3, adaptor);
				viewHandler.sendMessage(msg);	
	    	}
	    }
	    
	    private class LoadMoreDoneListViewThread extends Thread{
	    	@Override
	    	@LockUsed("lock")
	    	public void run(){
	    		lock.lock();
				try{
					List<ReminderOnDemandEntity> entities = null;
					entities = dbService.getOlderReminders("2", ptrLastExecTime, delta);
					final int size = entities.size();
					if (size > 0){
						ptrLastExecTime = entities.get(0).getExecTime();
						doneEntities.addAll(entities);
					}
				}finally{
					lock.unlock();
				}
				ReminderOnDemandViewAdaptor adaptor = new ReminderOnDemandDoneViewAdaptor(
						ctx, 
						doneEntities);	
				if (Log.isLoggable(TAG_THREAD, Log.DEBUG)){
					Log.d(TAG_THREAD, "Thread LoadMoreDoneListViewThread");
				}
				Message msg= Message.obtain(viewHandler, 3, adaptor);
				viewHandler.sendMessage(msg);	
	    	}
	    	
	    }
	    
	    private class initDoneListViewThread extends Thread{
	    	@Override
	    	@LockUsed("lock")
			public void run() {
				if (doneEntities.isEmpty()){
					doneEntities.addAll(dbService.getDoneReminders("2", delta));
					final int size = doneEntities.size();
					if (doneEntities.size() > 0){
						ptrLatestExecTime = doneEntities.get(0).getExecTime();
						ptrLastExecTime = doneEntities.get(size -1).getExecTime();
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
	    
	    private void onRefresh(){
	    	if (freshThread == null){
    			freshThread = new RefreshDoneListViewThread();
    		}
    		freshThread.start();
	    	
	    }
	    
	    private void onLoad(){
	    	if (loadThread == null){
	    		loadThread = new LoadMoreDoneListViewThread();
    		}
	    	loadThread.start();
	    }
	    
	    private void changeHeaderViewByState(DownListViewState lstate) {  
	    	if (this.state == lstate){
	    		return;
	    	}
	    	
	        switch (lstate) {  
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
	            break;  
	            
	        case DONE:  
	        	headerView.setPadding(0, -1 * headerViewHeight, 0, 0);  
	            progressBar.setVisibility(View.GONE);  
	            arrowImageView.clearAnimation();
	            tipsTextview.setText("drop down refresh");  
	            lastUpdatedTextView.setVisibility(View.VISIBLE);  
	            break;
	            
			default:
				break;  
	        }  
	        this.state = lstate;
	    }  
	    
	    private enum DownListViewState{
	    	PULL_TO_REFRESH,  
	    	RELEASE_TO_REFRESH,
	        REFRESHING,
	        DONE,
	        LOADING,
	    }

}
