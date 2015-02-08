package com.fundots.events;

import java.util.Observable;
import java.util.Observer;
import android.util.Log;

import com.fundots.data.DataItem;
import com.fundots.deploy.TourAppActivity;
import com.fundots.popup.Popup;

public class EventListener implements Observer 
{
	private TourAppActivity act;
	public static int numEvents = 0;
	
	public EventListener( TourAppActivity act )
	{ 
		this.act = act;
		// initialize EventBus
		EventBus.initialize();
		// subscribe to QUEUE EVENT
		EventBus.subscribeTo(EventBus.QUEUE_EVENT, this);
	}
	
	//Alex @Override
	public void update(Observable observable, Object data) 
	{
		//list.add( (DataItem) data );
		Log.i( "EventListener", ((DataItem) data).variables[4] );
		Popup.event_popup(act, (DataItem) data);
		
	}

}
