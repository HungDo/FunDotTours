package com.fundots.data;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class StaticCalculator 
{

	// constants
	public static final double KM_TO_MI = 0.621371192;
	public static final double RADIUS_OF_EARTH = 6371.0; // (km)
	public static final float MI_TO_M = 1609.344f; 
	public static final float TEXT_SIZE = 15.0f;
	
	// returns value in miles
	public static double km_to_mi(double kilometers)
	{
		return KM_TO_MI * kilometers;
	}
	
	//miles to meters added.
	public static float mi_to_m(double miles)
	{
		return (float)(miles * MI_TO_M);
	}
	
	//
	public static double distance_km( GeoPoint loc1, GeoPoint loc2 )
	{
		double d_lat = Math.toRadians( loc2.getLatitudeE6()/1E6 - loc1.getLatitudeE6()/1E6);
		double d_lon = Math.toRadians( loc2.getLongitudeE6()/1E6 - loc1.getLongitudeE6()/1E6);
		
		double lat1 = Math.toRadians( loc1.getLatitudeE6()/1E6 );
		double lat2 = Math.toRadians( loc2.getLatitudeE6()/1E6 );
		
		double a = Math.sin(d_lat/2) * Math.sin(d_lat/2) + Math.sin(d_lon/2) * Math.sin(d_lon/2) * Math.cos(lat1/2) * Math.cos(lat2/2);
		
		double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1.0-a));
		
		return RADIUS_OF_EARTH * c;
	}
	
	public static GeoPoint locationToGeoPoint( Location loc )
	{
		return new GeoPoint( (int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6) );
	}
	
	
}
