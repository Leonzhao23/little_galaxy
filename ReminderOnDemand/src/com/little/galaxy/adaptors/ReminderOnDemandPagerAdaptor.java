package com.little.galaxy.adaptors;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ReminderOnDemandPagerAdaptor extends PagerAdapter {
	private List<View> listViews;
	private List<String> titles;
	

	public ReminderOnDemandPagerAdaptor(List<View> listViews, List<String> titles) {
		super();
		this.listViews = listViews;
		this.titles = titles;
	}

	@Override
	public int getCount() {
		return listViews == null? 0 : listViews.size();
	}
	

	@Override
	public CharSequence getPageTitle(int position) {
		return titles == null? "" : titles.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(listViews.get(position));  
		super.destroyItem(container, position, object);
	}

	@Override
    public Object instantiateItem(View arg0, int arg1) {
        Log.d("instantiateItem", ""+arg0+" "+arg1);
        try { 
            if(listViews.get(arg1).getParent()==null)
                ((ViewPager) arg0).addView(listViews.get(arg1), 0);  
            else{
                ((ViewGroup)listViews.get(arg1).getParent()).removeView(listViews.get(arg1));
                ((ViewPager) arg0).addView(listViews.get(arg1), 0); 
            }
        } catch (Exception e) {  
            Log.d("parent=", ""+listViews.get(arg1).getParent()); 
            e.printStackTrace();  
        }  
        return listViews.get(arg1);
//		((ViewPager) arg0).addView(listViews.get(arg1));  
//        return listViews.get(arg1);  
    }

}
