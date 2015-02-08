package com.fundots.data;

import android.graphics.drawable.Drawable;

import com.fundots.deploy.R;
import com.google.android.maps.GeoPoint;

public class DataItem
{
	public boolean played;

	public int id;
	public int toursiteid;
	
	//----------
	
	public double lon;
	public double lat;

	public int status;
	
	//----------------------
	
	public String[] variables;
	
	//----- Statistics -----
	public boolean clicked 			= false;
	public boolean picsClicked 		= false;
	public boolean moreBtnClicked 	= false;
	public boolean playBtnClicked 	= false;
	public long totalTimeInDot  	= 0;
	
	public int piccount;
	public Boolean picchecker;
	public Boolean[] picCheckArray;
	public Drawable[] dd;
	
	public GeoPoint point;
	
	public DataItem( GeoPoint p )
	{
		lat = p.getLatitudeE6()/1E6;
		lon = p.getLongitudeE6()/1E6;
		point = new GeoPoint( (int)(this.lat*1E6),(int)(this.lon*1E6) );
	}	
		
	public DataItem( String[] record )
	{
		
		piccount = 0;
		picchecker = false;
		picCheckArray = new Boolean[3];
		picCheckArray[0] = false;
		picCheckArray[1] = false;
		picCheckArray[2] = false;
		
		dd = new Drawable[3];
		clearDrawables();

		this.played = false;
		//
		variables = new String[ record.length ];
		// v3 changes to clean code and account for new changes to db
		for ( int i = 0; i < record.length; i++ )
		{
			variables[i] = record[i];
		}
		
		id 			= Integer.parseInt( record[2] );
		toursiteid	= Integer.parseInt( record[3] );
		
		lon 		= Double.parseDouble( record[10] );
		lat 		= Double.parseDouble( record[11] );
		
		point = new GeoPoint( (int)(this.lat*1E6),(int)(this.lon*1E6) );

//		status = Integer.parseInt( record[ record.length-1 ] );
		status = Integer.parseInt( record[ 47 ] );
		
	}	
	
	public void addToDotTime( long time )
	{
		totalTimeInDot += time;
	}
	
	public void clearDrawables()			{ dd[0] = null;	dd[1] = null; dd[2] = null; }
	public void setPlayed(boolean played) 	{ this.played = played; }

	public String toString() 
	{
		return 
			"DataItem [lat=" + lat + ", lon=" + lon + 
			", name=" 		+ variables[4] 	+ 
			", address=" 	+ variables[5] 	+ 
			", type=" 		+ variables[12] + "]";
	}
	
	public void setTime(long inc) 			{ totalTimeInDot = (totalTimeInDot + inc)/2; }

	public void stat_clear()
	{
		clicked = false;
		picsClicked = false;
		moreBtnClicked = false;
		playBtnClicked = false;
		totalTimeInDot = 0;
	}
	
	public Drawable getNextPic(int curr_pic) 
	{
		//to check if the next picture IS NOT the same as the current one
		Drawable temp = dd[curr_pic];
		if ( piccount >= 2 )
		{
			piccount = 0;
		}
		else
		{
			piccount++;
		}
		if(dd[piccount] == null)
			return getNextPic(curr_pic);
		if(temp != dd[piccount])
			return dd[piccount];
		else
			return null;
	}
	
	public Drawable getPrevPic(int curr_pic) 
	{
		//to check if the next picture IS NOT the same as the current one
		Drawable temp = dd[curr_pic];
		if ( piccount <= 0 )
		{
			piccount = 2;
		}
		else
		{
			piccount--;
		}
		if(dd[piccount] == null)
			return getNextPic(curr_pic);
		if(temp != dd[piccount])
			return dd[piccount];
		else
			return null;
	}
}
