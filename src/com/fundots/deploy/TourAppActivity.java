package com.fundots.deploy;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fundots.categories.CategoryListAdapter;
import com.fundots.data.*;
import com.fundots.overlays.*;
import com.fundots.popup.Popup;
import com.fundots.events.EventListener;
import com.fundots.stats.StatCollector_v3;
import com.fundots.toursite.TourSiteActivity;
import com.fundots.tts.FunDotsTTS;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class TourAppActivity extends MapActivity
{
	private MapView mv;
	private MapController mc;
	private DataHandler data;
	private FunDotsTTS tts;
	// preference options
	private boolean mapUpdateOn = true;

	// initially false
	public boolean enabledArry[] = new boolean[ CategoryListAdapter.CATEGORIES.length ];
	
	private int zoom;
	private boolean gps_on = true;
	private boolean enableAudio = false;
	private boolean enableAll = true;
	// value is in miles will be converted to meters in the StaticCalculator class
	private float view_radius = 1;
	private float action_radius = 0.3f;
	int SECONDS = 3; //seconds
	int MIN_DISTANCE = 0; //meters
	// 
	private LocationManager lm;
	private LocationListener locListener;
	private Location currLocation;
	private GeoPoint currGeoPoint;
	public static GeoPoint startingPoint = null;
	private EventListener listener;
	private StatCollector_v3 statistics;
	// used to get the most up to date location information
	private long locationTimestamp;
    private float bearing = 0.0f;
	// -------------------static vars for refreshOverlay() -----------------------
	private static DataItem item;
	private LocationOverlay myLocation;
	private List <Overlay> dots;
	// ------------------- static vars for getClosestLocation() -----------------
    private static DataItem closest;
    private static DataItem temp;
    // --------------------------------------------------------------------------	
	
	//
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
//        Log.d("Main","On Create - Loading Preferences ");
        setContentView(R.layout.main_v2);
        
		// made this method to make onCreate cleaner to look at -- Hung
        try
        {
        	doEverything();
        	Log.i( "TourApp onCreate()", "Finish doEverything()");
        }
        catch ( Exception e )
        {
        	int len = e.getStackTrace().length;
        	for ( int i = 0; i < len; i++ )
        	{
        		Log.e( "onCreate() TourApp Activity", e.getStackTrace()[i]+"" );
        	}
        	// log and send message over email
        }
    }
    
    /**
     * 
     */
    @Override
    public void onStart() 
    {
    	super.onStart();
    }
    
    /**
     * 
     */
    public void onResume()
    {
    	super.onResume();
    	Log.d("Main","On Resume");
        // start up statistics
		statistics = StatCollector_v3.getInstance();
		statistics.endStatCollector = false;
		statistics.start_app();
    	// when using Google's TTS, if it gets shutdown it need to be reinitialized
    	tts = new FunDotsTTS( this );
    	// reinitializes the EventListener 
    	listener = new EventListener( this );
    	// load preferences
    	loadPreferences();
    	// refreshes the overlays: radius, tour, public, event, recreational
    	refreshOverlay();
    	
    	// checks if there is a starting point and if it does, animates to that location
    	// featured in toursite and featured tour functionality
    	if ( startingPoint != null )
        {
        	mc.animateTo(startingPoint);
        	mapUpdateOn = false;
        	mv.invalidate();
        }
    }
    
	 
	/**
	 * 
	 */
	public void onPause() 
	{
		try
		{
			super.onPause();
			Log.d("Main","On Pause - Saving Preferences ");
			savePreferences();
			cleanup();
			// need to reset starting point to null so that next time the map is opened it would open like normal
			startingPoint = null;
		}
		catch ( Exception e )
		{
			Log.e( "TourApp onPause()", "error" );
		}
	} 
	
	/**
	 * 
	 */
	@Override
	public void onStop()
	{
		super.onStop();
		Log.d("Main","On Stop");
		if ( tts != null )
		{
			tts.close();
			tts = null;
		}
		statistics.end_app();
		statistics.endStatCollector = true;
		statistics.sendStats( this );
	}
    
	/**
	 *  The purpose of this method is to clean up some of the data and allow the GC to free it up
	 *  and close anything that may run in the background still
	 */
	private void cleanup()
	{
		try
		{
			Popup.isPlaying = false;
			Popup.showingMore = false;
//			nearest = null;
			data.closeDatabase();
			if ( locListener != null )
			{
				lm.removeUpdates(locListener);
			}
			
		}
		catch ( Exception e )
		{
			int len = e.getStackTrace().length;
			for ( int x = 0; x < len; x++ )
			{
				Log.e( "cleanup()", e.getStackTrace()[x].toString() );
			}
		}
	}
	/**
	 * 
	 */
	private void doEverything()
	{

        // initialize database
//		new BackgroundBackendUpdate( this ).execute(); // background threads
		initDatabase();
        // initalize MapView and MapController
        mv = (MapView) this.findViewById(R.id.mapView);
        mc = mv.getController();
        // sets up the LocationListener and the LocationManager
        setupLocationListener();
    	// loads all the saved preferences and initializes the SharedPreferences
        loadPreferences();
        // initializes the LocationListener, ToggleButton, LocationManager, and Menu 
        initialize();
        
	}
	
	private void quit() 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								TourAppActivity.this.onPause();
								TourAppActivity.this.finish();
								Intent intent = new Intent(Intent.ACTION_MAIN);
								intent.addCategory(Intent.CATEGORY_HOME);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
    /**
     *  Initialize all the buttons, listeners
     */
    private void initialize()
    {	
    	try
    	{
	    	// ------------- initialize map components or views ------------------
	    	mc.setZoom(zoom);
	    	// this looked ugly on the screen so i removed it
	    	mv.setBuiltInZoomControls(true); 
	    	
	    	final ListView cat_list = (ListView)this.findViewById(R.id.category_list);
	    	cat_list.setAdapter( new CategoryListAdapter( this ) );

	    	final Button closecat_btn = (Button)this.findViewById(R.id.close_cat);
	    	closecat_btn.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					cat_list.setVisibility(View.GONE);
					closecat_btn.setVisibility(View.GONE);
//					cat_list.setAdapter(null);
				}
			});
	    	
	    	final Activity act = this;
	    	
	    	Button cat_btn = (Button)this.findViewById(R.id.cat_btn);
	    	cat_btn.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
//					cat_list.setAdapter( new CategoryListAdapter( act ) );
					cat_list.setVisibility(View.VISIBLE);
					closecat_btn.setVisibility(View.VISIBLE);
				}
			});
	    	
	    	Button browse_btn = (Button)this.findViewById(R.id.browse_btn);
	    	browse_btn.setOnClickListener( new View.OnClickListener() 
	    	{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent( TourAppActivity.this, TourSiteActivity.class );
					startActivity( intent );
				}
			});
	    	
	    	Button set_btn = (Button)this.findViewById(R.id.set_btn);
	    	set_btn.setOnClickListener( new View.OnClickListener() 
	    	{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent( TourAppActivity.this, SettingsActivity.class );
					startActivity( intent );
				}
			});
	    	
	    	Button exit_btn = (Button)this.findViewById(R.id.exit_btn);
	    	exit_btn.setOnClickListener( new View.OnClickListener() 
	    	{
				@Override
				public void onClick(View v) 
				{
					quit();
				}
			});
	    	
	    	// handles all TTS commands
	    	tts = new FunDotsTTS( this );
    	}
    	catch ( Exception e ) 
    	{
    		int len = e.getStackTrace().length;
        	for ( int i = 0; i < len; i++ )
        	{
        		Log.e( "initialize() TourApp Activity", e.getStackTrace()[i]+"" );
        	}
    	}
    }
    
	private void initDatabase()
	{
		Log.d("Database", "Initialize database");
		// create a DataHandler instance and create/open database 
		data = new DataHandler( this );
//		new BackgroundBackendUpdate( this ).execute();
		data.populateFromDeviceDB();
		data.closeDatabase();
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
		if ( gps_on == false || hasGps == false )
		{
			Toast toast = Toast.makeText( this.getApplicationContext(), "GPS is not activated", Toast.LENGTH_LONG);
			toast.show();
			
			if ( lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null )
			{
				Log.d( "LocationProvider1", LocationManager.NETWORK_PROVIDER );
				return LocationManager.NETWORK_PROVIDER;
			}
			else if ( lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) != null )
			{
				Log.d( "LocationProvider2", LocationManager.PASSIVE_PROVIDER );
				return LocationManager.PASSIVE_PROVIDER;
			}
			else
			{
				Log.d( "LocationProvider3", lm.getBestProvider( criteria, true ) );
				return lm.getBestProvider( criteria, true );
			} //*/ 
		}
		else
		{
			Log.d( "LocationProvider4", ""+ lm.getBestProvider( criteria, true ) );
			return lm.getBestProvider( criteria, true ); //LocationManager.GPS_PROVIDER;//
		}
	}
	
    /**
     * 
     */
    private void loadPreferences()
    {
    	SharedPreferences settings = this.getSharedPreferences( SettingsActivity.PREF_NAME, MODE_PRIVATE );
    	// load preferences 
    	// traffic and satellite
        mv.setTraffic( settings.getBoolean( "trafficView", false ) );
        
        // disabled satellite for now 
        // mv.setSatellite( settings.getBoolean( "satelliteView", false ) );
        
        // all events enabled
        enableAll = settings.getBoolean("enableAll", true);
        
        for ( int i = 0; i < enabledArry.length; i++ )
        {
        	if ( enableAll == true )
        	{
        		enabledArry[i] = true;
        	}
        	else
        	{
        		enabledArry[i] = settings.getBoolean( CategoryListAdapter.CATEGORIES[i], false );
        	}
        }
        
    	// zoom, view radius, action radius, gps
        zoom = settings.getInt( "zoom", SettingsActivity.DEFAULT_ZOOM );
        gps_on = settings.getBoolean( "gpsCapable", true );
        mapUpdateOn = settings.getBoolean( "mapupdate", true ); 
        
        view_radius = settings.getFloat( "view_radius", SettingsActivity.DEFAULT_VIEW_RADIUS );
        action_radius = settings.getFloat( "action_radius", SettingsActivity.DEFAULT_ACTION_RADIUS );
        
        // audio enabled
        enableAudio = settings.getBoolean("enableAudio", true);
        Popup.isPlaying = false;
        // open the database if the database is not open
//        if ( !data.isOpen() )
//        {
//        	data.openDataBase();
//        }
    }
    
    /**
     * 
     */
    private void savePreferences()
    {
		// save map preferences
    	// tour, public/general, event, recreation
    	// zoom, view radius, action radius
    	// close the database
    	SharedPreferences settings = this.getSharedPreferences( SettingsActivity.PREF_NAME, MODE_PRIVATE );
		SharedPreferences.Editor editor = settings.edit();
		
		for ( int i = 0; i < enabledArry.length; i++ )
        {
    		editor.putBoolean( CategoryListAdapter.CATEGORIES[i], enabledArry[i] );	
        }
//		editor.putBoolean( "tour" 		, tourOn );
//		editor.putBoolean( "public" 	, publicOn );
//		editor.putBoolean( "event" 		, eventOn );
//		editor.putBoolean( "recreation" , recOn );
		
		//editor.putInt( "zoom", mv.getZoomLevel() );
		editor.putFloat( "view_radius", view_radius );
		editor.putFloat( "action_radius", action_radius );
		editor.putBoolean( "enableAll", enableAll );
		editor.putBoolean( "enableAudio", enableAudio );
			
		editor.commit();
    }
    
	/**
	 * 
	 */
    private void refreshOverlay()
    {
    	try
    	{
	    	Log.d("Refresh Overlay", "Start ...");
	    	// asynch overlay - separate thread - Hung
	    	// comment: this didnt work well, lots of concurrency issues
//	    	new BackgroundOverlay( this, data, action_radius ).execute();
	    	
	    	// synchr overlay - main thread
	    	try
			{
	    		dots = mv.getOverlays();
	    		dots.clear();
	    		
				GeoPoint point = getCurrGeoPoint(); // current location
				// boolean[] tog = this.enabledArry;//getToggleConfig(); // toggle states
				// 0 - tour
				// 1 - general
				// 2 - event
				// 3 - recreation
				
				GenericOverlay overlay = null;
//				Log.d( "Data" , " Number of records :: " + data.getSize());
				// draw all dots on map
		        for ( int x = 0; x < data.getSize(); x++ )
		        {
		        	overlay = null;
//		        	DataItem item = data.get(x);
		        	item = data.get(x);
//		        	Log.i("From Device Database", item.getName() + " :: " + item.getAddress() + " :: " + item.getType() );
		        	if (item != null)
		        	{
//		        		Log.i( "ITEM REFRESH OVERLAY", item);
		        		if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[0]) 
		        				&& enabledArry[0] ) //attractions
		        		{
		        			overlay = new GenericOverlay( this, item, R.drawable.attractions );
		        		}
		        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[1]) 
		        				&& enabledArry[1] ) //Business
		        		{
		        			overlay = new GenericOverlay( this, item, R.drawable.business );
		        		}
		        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[2]) 
		        				&& enabledArry[2] ) //Culture
		        		{
		        			overlay = new GenericOverlay( this, item, R.drawable.history );
		        		}
		        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[3]) 
		        				&& enabledArry[3] ) //Entertainment
		        		{
		        			overlay = new GenericOverlay( this, item, R.drawable.music );
		        		}
		        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[4]) 
		        				&& enabledArry[4] ) //Food
		        		{
		        			overlay = new GenericOverlay( this, item, R.drawable.food );
		        		}
		        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[5]) 
		        				&& enabledArry[5] ) //Lodging
		        		{
		        			overlay = new GenericOverlay( this, item, R.drawable.lodge );
		        		}
		        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[6]) 
		        				&& enabledArry[6] ) //Schedule
		        		{
		        			overlay = new GenericOverlay( this, item, R.drawable.calendar );
		        		}
		        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[7]) 
		        				&& enabledArry[7] ) //Shopping
		        		{
		        			overlay = new GenericOverlay( this, item, R.drawable.shop );
		        		}

		        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[8]) 
		        				&& enabledArry[8] ) //Sports
		        		{
			        		overlay = new GenericOverlay( this, item, R.drawable.sports );
		        		}
		        		
		        		if ( overlay != null )
		        		{
		        			dots.add(overlay);
		        		}
		        		
//			        	if ( ( ( item.variables[12].equals(DataHandler.GENERAL) ) || 
//			        			    ( item.variables[12].equals(DataHandler.PRIMARY) ) )  
//			        			&& tog[1] == true)
//			        	{
//			        		pub = new GeneralOverlay( this, item );
//			        		dots.add(pub);
//			        	}
//			        	else if (item.variables[12].equals(DataHandler.TOUR) 
//			        			&& tog[0] == true)
//			        	{
//			        		tour = new TourOverlay( this, item );
//			        		dots.add(tour);
//			        	}
//			        	else if (item.variables[12].equals(DataHandler.EVENT) 
//			        			&& tog[2] == true)
//			        	{
//			        		event = new EventOverlay( this, item );
//			        		dots.add(event);
//			        	}
//			        	else if (item.variables[12].equals(DataHandler.RECREATION)  
//			        			&& tog[3] == true)
//			        	{
//			        		rec = new RecreationOverlay( this, item );
//			        		dots.add(rec);
//			        	}
		        	}
		        }
		        
		        // draw you location on top of everything else
		        if ( point == null)
		    	{
		//    		Toast.makeText( getBaseContext(), "null", Toast.LENGTH_SHORT ).show();
		    	}
		    	else
		    	{
		    		// draws the current location on map
		    		if ( point != null )
		    		{
			    		if ( myLocation == null )
			    		{
			    			myLocation = new LocationOverlay( this, new DataItem( point ), action_radius );
			    		}
			    		else
			    		{
			    			myLocation.updatePos( new DataItem( point ) );
			    		}
			    		dots.add( myLocation );
		    		}
		    	}
		        Log.d("Refresh Overlay", "... Finished");
		        // non-UI call to redraw the map and all overlays on it
//		        mv.postInvalidate();
		        // UI thread call
//		        mv.invalidate();
		        
			}
			catch ( Exception e )
			{
				for ( int i = 0; i < e.getStackTrace().length; i++ )
				{
					Log.e("refreshOverlay", e.getStackTrace()[i].toString() );
				}
			}
    	}
    	catch ( Exception e )
    	{
    		Log.e( "refreshOverlay()", "Error" );
    	}
        Log.d("Refresh Overlay", "Done ...");
        mv.invalidate();
    }
    
    /**
     * 
     */
    private void setupLocationListener()
    {
    	try
    	{
	    	lm = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
	    	
	    	String locProvider = getLocationProvider();
	        // gets the current location using the best provider
	    	if ( locProvider != null && locProvider.equals(LocationManager.GPS_PROVIDER) )
	    	{
    			currLocation = lm.getLastKnownLocation( locProvider );
    			if ( currLocation != null && currLocation.hasBearing() )
    			{
    				bearing = currLocation.getBearing();
    				Log.d( "Bearing", bearing + " East of True North");
    			}
	    	}
	    	else
	    	{ 
	    		if ( locProvider != null && lm.getLastKnownLocation( locProvider ) != null )
	    				//&& pointExists() == true )
	    		{
	    			Log.i( "Provider Not GPS", locProvider + "" );
	    			if ( startingPoint == null )
	    			{
			    		mc.animateTo( StaticCalculator.locationToGeoPoint( lm.getLastKnownLocation( locProvider ) ) ); 
			        	mv.invalidate();
	    			}
	    			// currLocation = lm.getLastKnownLocation( getLocationProvider() );
	    		} 
//	    		Toast.makeText( getBaseContext(), "GPS is not activated", Toast.LENGTH_SHORT ).show();
				Log.i( "GPS Listener1 - setupLocationListener()", "GPS not available" );
	    	}
	    	
	        if ( currLocation != null )//&& pointExists() == true )
	        {
	        	if ( locationTimestamp == 0 || locationTimestamp < currLocation.getTime() )
	        	{
	        		locationTimestamp = currLocation.getTime();
	        		Log.i( "GPS", "New gps data :: " + locationTimestamp );
	        		
		        	currGeoPoint = StaticCalculator.locationToGeoPoint( currLocation );
		        	// adds a user setting to turn off location updating
		        	if ( mapUpdateOn == true )
					{
		        		mc.animateTo( currGeoPoint );
		        	}
		        	mv.invalidate(); 
	        	}
	        	if ( currLocation.hasBearing() )
    			{
    				bearing = currLocation.getBearing();
    			}
	        }
	        else
	        {
	//        	Toast.makeText( getBaseContext(), "GPS is not activated", Toast.LENGTH_LONG ).show();
	        	Log.i( "GPS Listener2 - setupLocationListener()", "GPS not available" );
	        }

	    	locListener = new LocationListener()
	    	{
	    		//Alex @Override
				public void onLocationChanged(Location location) 
				{
					Log.d( "GPS Update", location.getLatitude() + ", " + location.getLongitude() );
					
					// updates map to current location if moving or change in location
					if ( !location.equals( (Location) currLocation ) )
					{
						currLocation = location;
						locationTimestamp = currLocation.getTime();
						currGeoPoint = StaticCalculator.locationToGeoPoint( currLocation );
						if ( currLocation.hasBearing() )
		    			{
		    				bearing = currLocation.getBearing();
		    			}
						// redraw all the overlays
						refreshOverlay();
			        	// adds a user setting to turn off location updating
						if ( mapUpdateOn == true )
						{	
							mc.animateTo( currGeoPoint );
						}
						// this displays one's long and lat on to the screen - for debugging
						mv.invalidate();
					}
				}
				
				//Alex @Override
				public void onProviderDisabled(String provider) {}
	
				//Alex @Override
				public void onProviderEnabled(String provider) {}
	
				//Alex @Override
				public void onStatusChanged(String provider, int status, Bundle extras) {}
	    	};
	        if ( getLocationProvider() != null )
	        {
	        	Log.d( "LocationProvider", "Request rate: " + SECONDS + " sec :: " + MIN_DISTANCE + " m" );
		    	// this line MUST come after locListener has been made
		    	lm.requestLocationUpdates( getLocationProvider(), (long) (SECONDS*1000), MIN_DISTANCE, locListener );
	        }
    	}
    	catch (Exception e)
    	{
			for ( int x = 0; x < e.getStackTrace().length; x++ )
			{
				Log.e( "setupLocationListener()", e.getStackTrace()[x].toString() );
			}
    	}
    }
    
    /**
     *  Searches for the closest point out of all points
     * @return
     */
	private DataItem getClosestLocation()
	{
		Log.d( "Search Algorithm", "Running ...");
		
		Iterator <DataItem> i = data.getData().iterator();
		
		GeoPoint point = null;
		if ( currGeoPoint != null )
		{
			point = currGeoPoint;
		}
		else
		{
			point = mv.getMapCenter();
		}
//		Log.d( "Search Algorithm", "Lat: " + point.getLatitudeE6()/1000000.0 + " Lon: " + point.getLongitudeE6()/1000000.0 );
		
		closest = (i.hasNext()? i.next():null);
		if ( closest != null )
		{
			double closest_dist = ( StaticCalculator.distance_km( point, closest.point ) );
			while( i.hasNext() )
			{
				temp = i.next();
				double temp_val = ( StaticCalculator.distance_km( point, temp.point ) );
//				Log.d( "Search Algorithm" , temp.getName() + " - " + temp_val + " :: " + closest_dist );
				if ( temp_val < closest_dist )
				{
					if ( (item.variables[13].equals( CategoryListAdapter.CATEGORIES[0] ) && this.enabledArry[0] == true) ||
						 (item.variables[13].equals( CategoryListAdapter.CATEGORIES[1] ) && this.enabledArry[1] == true) ||
						 (item.variables[13].equals( CategoryListAdapter.CATEGORIES[2] ) && this.enabledArry[2] == true) ||
						 (item.variables[13].equals( CategoryListAdapter.CATEGORIES[3] ) && this.enabledArry[3] == true) ||
						 (item.variables[13].equals( CategoryListAdapter.CATEGORIES[4] ) && this.enabledArry[4] == true) ||
						 (item.variables[13].equals( CategoryListAdapter.CATEGORIES[5] ) && this.enabledArry[5] == true) ||
						 (item.variables[13].equals( CategoryListAdapter.CATEGORIES[6] ) && this.enabledArry[6] == true) ||
						 (item.variables[13].equals( CategoryListAdapter.CATEGORIES[7] ) && this.enabledArry[7] == true) ||
						 (item.variables[13].equals( CategoryListAdapter.CATEGORIES[8] ) && this.enabledArry[8] == true) )
	        		{
						closest = temp;
						closest_dist = temp_val;
	        		}
//					if ( (temp.variables[12].equals(DataHandler.RECREATION) && recOn 	== true) || 
//						 (temp.variables[12].equals(DataHandler.EVENT) 		&& eventOn 	== true) || 
//					   ( (temp.variables[12].equals(DataHandler.GENERAL) 						 || 
//					     (temp.variables[12].equals(DataHandler.PRIMARY)))	&& publicOn == true) ||
//						 (temp.variables[12].equals(DataHandler.TOUR) 		&& tourOn 	== true) )
//					{
//						closest = temp;
//						closest_dist = temp_val;
//					}
				}
			}
			Log.d( "Search Algorithm", "... Finished - " + closest.variables[4] );
		}
		return closest;
	}
	
	public float getBearing()
	{
		Log.d("TourApp", "Bearing : " + bearing );
		return bearing;
	}
	
    public void speak( String str )
    {
    	tts.speak( Html.fromHtml(str).toString() );
    }
	
	public GeoPoint getCurrGeoPoint()
	{
		return currGeoPoint;
	}
    
	protected boolean isRouteDisplayed() 
	{
		return false;
	}

	public MapView getMv() 
	{
		return mv;
	}
	
	public void refresh()
	{
		this.refreshOverlay();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<DataItem> getItems( int[] ids )
	{
		if ( data != null )
		{
//			return data.filter(ids);
			return data.getData();
		}
		return null;
	}

	public List<DataItem> getAllItems()
	{
		return data.getData();
	}
}