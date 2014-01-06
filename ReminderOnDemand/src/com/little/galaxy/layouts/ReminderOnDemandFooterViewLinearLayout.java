package com.little.galaxy.layouts;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.little.galaxy.R;
import com.little.galaxy.views.ReminderOnDemandListView.DownListViewState;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ReminderOnDemandFooterViewLinearLayout extends LinearLayout {

	private Context context;

    private View footerView;
    private View progressBar;
    private TextView hintView;
    
	public ReminderOnDemandFooterViewLinearLayout(Context context) {
		super(context);
		initFooterView(context);
	}
	
	public ReminderOnDemandFooterViewLinearLayout(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		initFooterView(context);
	}
	
	public ReminderOnDemandFooterViewLinearLayout(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initFooterView(context);
	}
	
	
	private void initFooterView(Context context){
		this.context = context;
		LinearLayout footerViewRoot = (LinearLayout)LayoutInflater.from(this.context).inflate(R.layout.listview_footer, null);
        addView(footerViewRoot);
        footerViewRoot.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.footerView = footerViewRoot.findViewById(R.id.listview_footer);
        this.progressBar = footerViewRoot.findViewById(R.id.listview_footer_progressbar);
        this.hintView = (TextView)footerViewRoot.findViewById(R.id.listview_footer_hint_textview);
	}

	public void setBottomMargin(int height) {
        if (height < 0) return ;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)footerView.getLayoutParams();
        lp.bottomMargin = height;
        this.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)footerView.getLayoutParams();
        return lp.bottomMargin;
    }
    
    public void setViewState(DownListViewState state){
    	switch(state){
    	case NORMAL:
    		hintView.setVisibility(View.VISIBLE);
    		progressBar.setVisibility(View.GONE);
    		break;
    	case LOADING:
    		 hintView.setVisibility(View.GONE);
             progressBar.setVisibility(View.VISIBLE);
    		break;
    	default:
    		break;
    	}
    }
    
    public void hide() {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)footerView.getLayoutParams();
            lp.height = 0;
            footerView.setLayoutParams(lp);
    }
    
 
    public void show() {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)footerView.getLayoutParams();
            lp.height = LayoutParams.WRAP_CONTENT;
            footerView.setLayoutParams(lp);
    }
}
