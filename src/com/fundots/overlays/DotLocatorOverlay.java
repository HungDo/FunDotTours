package com.fundots.overlays;

import android.graphics.Canvas;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class DotLocatorOverlay extends Overlay 
{
	
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
	{
		return false;
	}
	
	public boolean onTap(GeoPoint p, MapView mv) 
	{
		return false;
	}
}
