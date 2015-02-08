package com.fundots.stats;

import android.content.Context;

import com.fundots.data.DataItem;
import com.fundots.multitask.BackgroundStatSender;

public class StatCollector_v3 
{
	public static final String[] APP_STAT_TYPE = new String[]
	{
		"ITEM_C", "PIC_V", "MORE_C", "PLAY_C", "DOT_TIME", "APP_TIME"
	};
	
	private static final StatCollector_v3 singleton = new StatCollector_v3();
	public DataItem di;
	public long app_time;
	public long dot_time;
	public boolean endStatCollector = false;
	
	private long startTime;
	private long dotStart, dotEnd;
	
	public static StatCollector_v3 getInstance()
	{
		return singleton;
	}
	
	public void clear()
	{
		if (di != null)
		{
			di.stat_clear();
		}
		dot_time = 0; 
		di = null;
	}
	
	public void start_app()	
	{
		startTime = System.currentTimeMillis();
	}
	public void end_app()
	{
		app_time = (System.currentTimeMillis() - startTime);
	}
	public void start_dot()					
	{ 
		dotStart = System.currentTimeMillis(); 
	}
	public void end_dot()						
	{ 
		dot_time = (System.currentTimeMillis() - dotStart);
	}
	
	/**
	 *  Sends and clears statistics
	 * @param context
	 */
	public void sendStats( Context context )
	{
		new BackgroundStatSender( context ).execute( getInstance() );
	}
}
