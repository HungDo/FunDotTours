package com.fundots.deploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import com.fundots.multitask.BackgroundBackendUpdate;
import com.fundots.multitask.BackgroundLoadscreen;
import com.fundots.static_vals.PublicStaticValues;
import com.fundots.toursite.TourSiteActivity;
import com.fundots.deploy.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

// test version
public class LoadActivity extends Activity 
{
	private LocationManager lm;
	private LocationListener locListener;
	private boolean run = false;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d("LoadActivity_v2", "onCreate()");
		this.setContentView(R.layout.loadscreen_v2);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		Toast.makeText( this.getApplicationContext(), "Loading ...", Toast.LENGTH_SHORT).show();
		
		// start collecting data time
//		StatCollector_v3.getInstance().start_app();
		try
		{
			// initialize all the buttons
			initialize();
			// get Featured tours
			getFeaturedTours();
			// load database
			new BackgroundBackendUpdate( this ).execute();
			// get ids for statistics
			getIDs();
			// update gps location for future use
//			getUpdateGPS();
			// no more need if there is a menu
//			Toast.makeText( this.getApplicationContext(), "Press map dots for information", Toast.LENGTH_LONG).show();
		}
		catch ( Exception e )
		{
			for ( int i = 0; i < e.getStackTrace().length; i++ )
        	{
        		Log.e( "onCreate() Loadscreen Activity", e.getStackTrace()[i]+"" );
        	}
		}
	}
	
	public void onStart()
	{
		super.onStart();
		Log.d("LoadActivity_v2", "onStart()");
	}
	
	/**
	 *  BUG HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
	 */
	public void onResume()
	{
		super.onResume();
		Log.d("LoadActivity_v2", "onResume()");
//		if ( PublicStaticValues.isCurrentData == true ) // condition when the data is already in the db
//		{
//			PublicStaticValues.isDoneDownloading = true;
//			try 
//			{
//				getFeaturedTours();
//			} 
//			catch (Exception e) 
//			{
//				for ( int i = 0; i < e.getStackTrace().length; i++ )
//	        	{
//	        		Log.e( "onResume() Loadscreen", e.getStackTrace()[i]+"" );
//	        	}
//			}
//		}
//		else // condition when data isnt in database and should be downloaded again
//		{
//			try 
//			{
//				new BackgroundBackendUpdate( this ).execute();
//				getFeaturedTours();
//			} 
//			catch (Exception e) 
//			{
//				for ( int i = 0; i < e.getStackTrace().length; i++ )
//	        	{
//	        		Log.e( "onResume() Loadscreen", e.getStackTrace()[i]+"" );
//	        	}
//			}
//		}
	}
	
	/**
	 *  BUG HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
	 */
	public void onPause()
	{
		super.onPause();
		Log.d("LoadActivity_v2", "onPause()");
		PublicStaticValues.isDoneDownloading = false;
		if ( locListener != null )
		{
			lm.removeUpdates(locListener);
			locListener = null;
		}
	}
	
	public void onStop()
	{
		super.onStop();
		Log.d("LoadActivity_v2", "onStop()");
	}
	
	/**
	 * DISABLED
	 * - Disabled onBackPressed due to technical issues where the background loses its color and goes white
	 */
	public void onBackPressed() { ; }
	
	/**
	 * 
	 */
	private void initialize()
	{
		// Hung
		ImageView imgView = (ImageView) this.findViewById(R.id.loadscreen_img);
//		ImageView imgView = (ImageView) this.findViewById(R.id.home_screen_1);
		imgView.setImageResource( R.drawable.home_screen_1 );
		imgView.setScaleType(ScaleType.CENTER_CROP);
//		imgView.setBackgroundResource(R.anim.loadscreen_img_anim);
		
		Button explore = (Button)this.findViewById( R.id.explore );
		explore.setOnClickListener( new View.OnClickListener() 
		{
			//Alex @Override
			public void onClick(View v)
			{
				openDifferentActivity( TourAppActivity.class );
			}
		});
		
		Button search = (Button)this.findViewById( R.id.search );
		search.setOnClickListener( new View.OnClickListener() 
		{
			//Alex @Override
			public void onClick(View v) 
			{
				openDifferentActivity( TourSiteActivity.class ); // search activity class
			}
		});
		
		Button menu = (Button) this.findViewById(R.id.settings);
    	menu.setOnClickListener( new View.OnClickListener() 
    	{
    		//Alex @Override
			public void onClick(View v) 
			{
				openDifferentActivity( SettingsActivity.class );
			} 
		});    	
		
		Button exit = (Button)this.findViewById( R.id.exit );
		exit.setOnClickListener( new View.OnClickListener() 
		{
			//Alex @Override
			public void onClick(View v) {
				// exiting an app
				// Log.d("Main","Back Pressed - closing ");
				// lets OS handle the closing of the app
//				Intent intent = new Intent(Intent.ACTION_MAIN);
//				intent.addCategory(Intent.CATEGORY_HOME);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent); 
				// call on finish to let Android os handle the exiting
				finish();
			}
		});
	}
	
	private void getFeaturedTours() throws MalformedURLException, IOException
	{
		URL url = new URL( PublicStaticValues.FEATURED );
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    String str;
	    StringTokenizer token;
	    int [] list = new int[] { -1,-1,-1, 20, 10 };
		while ((str = in.readLine()) != null) 
		{ 
			token = new StringTokenizer(str, ",");
			int i = 0;
			while( token.hasMoreTokens() )
			{
				list[i] = Integer.parseInt( token.nextToken() );
				i++;
			}
		}
		new BackgroundLoadscreen( this, list ).execute();
	}
	
	/**
	 * Obtains IDs for statistics
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void getIDs() throws MalformedURLException, IOException
	{
		SharedPreferences settings = this.getSharedPreferences( SettingsActivity.PREF_NAME , 0 );
		SharedPreferences.Editor editor = settings.edit();
		String a_id = null;
		String imei = null;
		if ( settings.getString( PublicStaticValues.DEV_ID_PREF, "" ).equals("") )
		{
			Log.d("DEV_ID", "No id found");
			TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE); 
			imei = manager.getDeviceId();
			System.out.println( imei );
			// add to shared prefs
			editor.putString( PublicStaticValues.DEV_ID_PREF, imei );
		}
		else
		{
			imei = settings.getString( PublicStaticValues.DEV_ID_PREF, "" );
		}
		if ( settings.getString( PublicStaticValues.APP_ID_PREF, "" ).equals("") )
		{
			Log.d("APP_ID", "No id found");
			URL url = new URL( PublicStaticValues.APP_ID_PING );
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		    String str;
			while ((str = in.readLine()) != null) { a_id = str; }
			in.close();
			a_id = a_id.substring( a_id.indexOf("(")+1, a_id.indexOf(")") ).trim();
			editor.putString( PublicStaticValues.APP_ID_PREF, a_id );
		}
		else
		{
			a_id = settings.getString( PublicStaticValues.APP_ID_PREF, "" );
		}
		editor.commit();
		Log.d( "IDs" , "Device ID: -" + imei + "- :: App ID: -" + a_id +"-" );
	}
	
	private void openDifferentActivity( Class<?> cls )
	{
		Intent intent = new Intent( LoadActivity.this, cls );
		startActivity( intent );
	}
	
	private void getUpdateGPS()
	{
		lm = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
		if ( getLocationProvider()!= null )
		{
			Location loc = lm.getLastKnownLocation( getLocationProvider() );
			if ( loc != null )
			{
				Log.i( "Load Location provider", loc.getLatitude() + ", " +  loc.getLongitude() );
			}
		}
		
		locListener = new LocationListener()
    	{
			//Alex @Override
			public void onLocationChanged(Location location) {}

			//Alex @Override
			public void onProviderDisabled(String provider) {}

			//Alex @Override
			public void onProviderEnabled(String provider) {}

			//Alex @Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
    	};
    	
        if ( getLocationProvider() != null )
        {
	    	// this line MUST come after locListener has been made
	    	lm.requestLocationUpdates( getLocationProvider(), 0, 0, locListener );
        }
	}
	
	private String getLocationProvider()
	{
		Criteria criteria = new Criteria();
		criteria.setAccuracy( Criteria.ACCURACY_FINE );
		criteria.setPowerRequirement( Criteria.POWER_MEDIUM );
		
		// determines if the device has gps hardware 
		PackageManager pm = getPackageManager();
		boolean hasGps = pm.hasSystemFeature( PackageManager.FEATURE_LOCATION_GPS );
		
		// If the user wants GPS disabled, that would be from the settings menu in a SharedPreference 
		if ( hasGps == false )
		{
			if ( lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null )
			{
				Log.d( "LocationProvider", LocationManager.NETWORK_PROVIDER );
				return LocationManager.NETWORK_PROVIDER;
			}
			else if ( lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) != null )
			{
				Log.d( "LocationProvider", LocationManager.PASSIVE_PROVIDER );
				return LocationManager.PASSIVE_PROVIDER;
			}
			else
			{
				Log.d( "LocationProvider", ""+ lm.getBestProvider( criteria, true ) );
				return lm.getBestProvider( criteria, true );
			} //*/ 
		}
		else
		{
			// Toast.makeText( getBaseContext(), lm.getBestProvider( criteria, true ), Toast.LENGTH_SHORT ).show();
			return LocationManager.GPS_PROVIDER;//lm.getBestProvider( criteria, true );
		}
	}

}
