package com.fundots.overlays;

import java.util.LinkedList;
import java.util.Observable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import com.fundots.data.DataItem;
import com.fundots.data.StaticCalculator;
import com.fundots.deploy.R;
import com.fundots.deploy.TourAppActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class LocationOverlay extends AbstractAppOverlay
{
	private float radius; // radius
	private Bitmap bearing;
	public LinkedList <GeoPoint> positions;
	
	public LocationOverlay( TourAppActivity act, DataItem item, float radius)
	{
		super( act, item );
		this.radius = radius;
		bmp 	= BitmapFactory.decodeResource( act.getResources(), R.drawable.hq32x32 );
		bearing = BitmapFactory.decodeResource( act.getResources(), R.drawable.arrow60x60 );
		if ( positions == null )
		{
			positions = new LinkedList <GeoPoint> ();
		}
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
			// draw paths
			drawPath( canvas, mapView );
			
			scrnPts = new Point();
			// get the projection of the map and converts it to pixels on the screen given the GeoPoint from the outer class
			mapView.getProjection().toPixels( item.point, scrnPts );
			
			if ( this.drawWithBMP == true ) 
			{
				// bmp 32 x 32
				canvas.drawBitmap( 
						bmp, 
						scrnPts.x-bmp.getWidth()/2f, 
						scrnPts.y-bmp.getHeight()+AbstractAppOverlay.DOT_SIZE, 
						null );
//				int w = canvas.getWidth();
//		        int h = canvas.getHeight();
//		        int cx = w / 2;
//		        int cy = h / 2;
//
//		        canvas.save();
//		        canvas.translate(scrnPts.x, 
//		        		scrnPts.y);
//				canvas.rotate(this.act.getBearing());
//				// arrow 60 x 60
//				canvas.drawBitmap( 
//						bearing, 
//						scrnPts.x-bearing.getWidth()/2f, 
//						scrnPts.y-bearing.getHeight()+3*AbstractAppOverlay.DOT_SIZE, 
//						null );
//				canvas.restore();
			}
			else
			{
				float projRadius = mapView.getProjection().metersToEquatorPixels( StaticCalculator.mi_to_m(radius/2) );
				synchronized(scrnPts)
				{
					paint.setColor(Color.BLUE);
					paint.setAlpha(25);
					
					oval = new RectF( scrnPts.x-projRadius, scrnPts.y-projRadius, scrnPts.x+projRadius, scrnPts.y+projRadius);
					canvas.drawArc(oval, 0, 360, false, paint);
					
					paint.setColor(Color.BLACK);
					canvas.drawCircle( scrnPts.x, scrnPts.y, 4f, paint );
				}
			}
			return true;
		} 
		catch ( Exception e )
		{
			for ( int i = 0; i < e.getStackTrace().length; i++ )
			{
				Log.e( "Location draw()", e.getStackTrace()[i]+"" );
			}
		}
		return false;
	}
	
	public boolean onTap(GeoPoint p, MapView mv) 
	{
		return false;
	}
	
	public void drawPath( Canvas canvas, MapView mv )
	{
		float projRadius = mv.getProjection().metersToEquatorPixels( StaticCalculator.mi_to_m( radius/10f ) );
		Point p;
		int i = 0;
		for ( GeoPoint gp: positions )
		{
			if ( gp != item.point )
			{
				p = new Point();
				mv.getProjection().toPixels( gp, p );
				paint.setAlpha(25);
				paint.setColor(Color.BLACK);
				canvas.drawCircle( p.x, p.y, projRadius+1.5f, paint );
				
//				if ( i == 0 )
//				{
//					paint.setColor(0xFFFFFF33);
//				}
//				else if ( i == 1 )
//				{
//					paint.setColor(0xFF3333FF);
//				}
//				else if ( i == 2 )
//				{
//					paint.setColor(0xFFCC33FF);
//				}
//				else if ( i == 3 )
//				{
					paint.setColor(0xFF00FF33);
//				}
				oval = new RectF( p.x-projRadius, p.y-projRadius, p.x+projRadius, p.y+projRadius);
				canvas.drawArc(oval, 0, 360, false, paint);
				
				i++;
				if ( i == 4 ) { i = 0; }
			}
		}
	}

	//Alex @Override
	public void update(Observable observable, Object data) { }
	
	public void updatePos( DataItem newItem )
	{
		// only add to position list if the change in distance is significant
		// change in distance is >= 1 meter
		// km to m = 1000x
		Log.d("Distance for Update", (StaticCalculator.distance_km(newItem.point, item.point)*1000.0 )+"" );
		if ( (StaticCalculator.distance_km(newItem.point, item.point)*1000.0) >= 2f && arePointsToClose(newItem.point) == false  )
		{
			positions.add(newItem.point);
			Log.i( "Position List", "Added" );
		}
		else if ( positions.size() == 0 )
		{
			positions.add(newItem.point);
			Log.i( "Position List", "Added" );
		}
		
		this.setDataItem(newItem);
	}
	
	
	// Hung
	/**
	 *  This method's purpose is to search for all nearby dots in the list to decrease redundant data
	 * @param point
	 * @return
	 */
	private boolean arePointsToClose( GeoPoint point )
	{
		for ( int i = 0; i < positions.size(); i++ )
		{	
			if ( StaticCalculator.distance_km( positions.get(i), point )*1000 < 2f )
			{
				Log.i( "arePointsToClose()", "Yes");
				return true;
			}
		}
		return false;
	}
	
	public void clearPos()
	{
		positions.clear();
	}

}
