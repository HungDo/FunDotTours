package com.fundots.events;

import java.util.Observable;
import java.util.Observer;

import com.fundots.data.DataItem;


public class EventBus 
{
	public static final int QUEUE_EVENT = 0;
	
	public static final int DOT_EVENT = 1;
	
	public static final int MAX_NUM_EVENTS  = 20;
	
	public static class Event extends Observable
	{
		public void setChanged()
		{
			super.setChanged();
		}
	}
	
	protected static Event[] aEvent = new Event[MAX_NUM_EVENTS];

	public static void initialize() 
	{
		for (int i = 0; i < MAX_NUM_EVENTS; i++) 
		{
			aEvent[i] = new Event();
		}
	}
	
	public static void subscribeTo(int iEventCode, Observer objSubscriber) 
	{
        aEvent[iEventCode].addObserver(objSubscriber);
        //System.out.println("Subscribe: " + iEventCode + ", " + objSubscriber + "\n");
    }
	
//	public static void announce(int iEventCode, DataItem sEventParam) 
//	{
//		// System.out.println( "Announcement: " + iEventCode + ", " + sEventParam + "\n");
//		aEvent[iEventCode].setChanged();
//		aEvent[iEventCode].notifyObservers(sEventParam);
//	}
	
	public static void announce(int iEventCode, DataItem sEventParam) 
	{
		// System.out.println( "Announcement: " + iEventCode + ", " + sEventParam + "\n");
		aEvent[iEventCode].setChanged();
		aEvent[iEventCode].notifyObservers(sEventParam);
	}
}
