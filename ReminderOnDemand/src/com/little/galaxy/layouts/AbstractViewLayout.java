package com.little.galaxy.layouts;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class AbstractViewLayout extends LinearLayout{
	public AbstractViewLayout(Context context) {
		super(context);
	}
	protected TextView subject;
	protected TextView Description;
    protected ImageButton play;
    protected ImageButton stop;
    protected ImageButton del;

}
