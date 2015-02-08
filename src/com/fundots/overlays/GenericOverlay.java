package com.fundots.overlays;

import java.util.Observable;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import com.fundots.data.DataItem;
import com.fundots.data.StaticCalculator;
import com.fundots.deploy.TourAppActivity;
import com.fundots.images.ImageHandler;
import com.google.android.maps.MapView;

public class GenericOverlay extends AbstractAppOverlay 
{
//	public GenericOverlay( TourAppActivity act, DataItem item )
//	{
//		super( act, item ); 
//		bmp = BitmapFactory.decodeResource( act.getResources(), R.drawable.icon_event );
//	}
	
	public GenericOverlay( TourAppActivity act, DataItem item, int icon)
	{
		//pass the r value (which is an int) for icon
		super(act, item);
		bmp = ImageHandler.decodeSampledBitmapFromResource(act.getResources(), icon, 31, 31);
	}
	
	// -------------------------------------------------------------------------
	private static Point scrnPts;
	private static RectF oval;
	// -------------------------------------------------------------------------
	
	public boolean draw( Canvas canvas, MapView mapView, boolean shadow, long when )
	{
		super.draw(canvas, mapView, shadow);
		try
		{
	//		Point scrnPts = new Point();
			scrnPts = new Point();
			// get the projection of the map and converts it to pixels on the screen given the GeoPoint from the outer class
	//		mapView.getProjection().toPixels(getPoint(), scrnPts);
			mapView.getProjection().toPixels( item.point, scrnPts );
			
			synchronized(scrnPts)
			{
				float size = AbstractAppOverlay.DOT_SIZE;
				if ( drawWithBMP == true )
				{
					// bmp 31 x 31
					
					canvas.drawBitmap( bmp, scrnPts.x-bmp.getWidth()/2f, scrnPts.y-bmp.getHeight()+size, null );
				}
				else
				{
					//*
			//		Paint paint = new Paint();
			//		paint = new Paint();
					paint.setColor(Color.BLACK);
			//		RectF oval = new RectF( (scrnPts.x-size), (scrnPts.y-size), (scrnPts.x+size), (scrnPts.y+size));
					oval = new RectF( (scrnPts.x-size), (scrnPts.y-size), (scrnPts.x+size), (scrnPts.y+size));
					canvas.drawArc(oval, 0, 360, false, paint);
					
					paint.setColor(0xFFCC33FF);
					canvas.drawCircle((float)scrnPts.x, (float)scrnPts.y, size-1, paint);
					
					float offset = 5f;
					paint.setColor(Color.WHITE);
					paint.setTextSize( StaticCalculator.TEXT_SIZE );
					canvas.drawText("E", (float)scrnPts.x-offset+1f, (float)scrnPts.y+offset, paint);
					//*/
				}
			}
			return true;
		} 
		catch ( Exception e )
		{
			for ( int i = 0; i < e.getStackTrace().length; i++ )
        	{
        		Log.e( "Event draw()", e.getStackTrace()[i]+"" );
        	}
		}
		return false;
	}

	//Alex @Override
	public void update(Observable observable, Object data) 
	{
		super.update(observable, item);
	}
}
