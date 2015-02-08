package com.fundots.overlays;

import java.io.InputStream;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.fundots.data.DataItem;
import com.fundots.data.DataItem;
import com.fundots.deploy.TourAppActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.fundots.deploy.R;
import com.fundots.events.EventBus;

public abstract class AbstractAppOverlay extends Overlay implements Observer
{
	protected Bitmap bmp;
//	protected DataItem item;
	protected DataItem item;
	protected TourAppActivity act;
	public static final float DOT_SIZE = 15f;
	public static Paint paint = new Paint();
	boolean isPlay = true;
	boolean isMore = true;
	boolean drawWithBMP = true; // purpose of testing imgs vs canvas drawing
	
//	public AbstractAppOverlay( TourAppActivity act, DataItem item ) // original
	public AbstractAppOverlay( TourAppActivity act, DataItem item ) // updated
	{
		this.item = item;
		this.act = act;
	}
	public abstract boolean draw( Canvas canvas, MapView mapView, boolean shadow, long when );
	
//	public void update( Observable event, DataItem item )
//	{
//		EventBus.announce( EventBus.DOT_EVENT, execute(item) );
//	}
	public void update( Observable event, DataItem item )
	{
		EventBus.announce( EventBus.DOT_EVENT, execute(item) );
	}

	
//	protected void setDataItem( DataItem item )
//	{
//		this.item = item;
//	}
	protected void setDataItem( DataItem item )
	{
		this.item = item;
	}
	
//	protected DataItem execute(DataItem item)
//	{
//		Log.i( "Observer execute()", item.locname );
//		return item;
//	}
	protected DataItem execute(DataItem item)
	{
		Log.i( "Observer execute()", item.variables[4] );
		return item;
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mv) 
	{	
//		int latDiff = Math.abs(p.getLatitudeE6()-getPoint().getLatitudeE6());
//		int longDiff = Math.abs(p.getLongitudeE6()-getPoint().getLongitudeE6());
		int latDiff 	= Math.abs(p.getLatitudeE6()  - item.point.getLatitudeE6());
		int longDiff 	= Math.abs(p.getLongitudeE6() - item.point.getLongitudeE6());
		// ( 5E+07 * Math.pow( Math.E, -0.6454 * mv.getZoomLevel() ) )
		// ( 3E+07 * Math.pow( Math.E, -0.6454 * mv.getZoomLevel() ) )
		// ( 2E+07 * Math.pow( Math.E, -0.6454 * mv.getZoomLevel() ) )
		// ( 4E+07 * Math.pow( Math.E, -0.6867 * mv.getZoomLevel() ) ) // current
		float val = (float) ( 5E+07 * Math.pow( Math.E, -0.6454 * mv.getZoomLevel() ) );
//		Log.d("Overlay", "Lat:" + latDiff + 
//			  " Long:" 	+ longDiff + 
//			  " Zoom:" 	+ mv.getZoomLevel() + 
//			  " val:" 	+ val );
		if( latDiff <= val && longDiff <= val )
		{							
			try
			{
//				Log.i( "onTap()", "Dot selected :: " + item.getName() );
				// pub-sub announcement
				EventBus.announce( EventBus.QUEUE_EVENT, item );
			}
			catch(Exception e)
			{
//				Log.e( "onTap()", "Error " + e + " :: " + (p==null?"null":"nope") + " :: " + (mv==null?"null":"nope") );
				for ( int i = 0; i < e.getStackTrace().length; i++)
				{
					Log.e("onTap()", e.getStackTrace()[i]+"" );
				}
			} 
		}
		return false; 
	}
}

