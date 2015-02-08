package com.fundots.deploy;

import com.fundots.deploy.R;
import com.fundots.tts.FunDotsTTS;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsActivity extends Activity
{	
	public static final String PREF_NAME = "fundottour";
	private SharedPreferences settings;
	private boolean trafficOn, satelliteOn, gpsOn, updateOn;
	private float view_radius, action_radius; 
	private int zoom;
	
	static final float DEFAULT_VIEW_RADIUS = 1f;
	static final float DEFAULT_ACTION_RADIUS = 0.02f;
    static final int DEFAULT_ZOOM = 18; // default zoom is lvl 14
    static final int DEFAULT_SENSE = 0;
    
    public boolean justStarted;
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.pref);
        try
        {
	        // look into preferences
	        loadPref();
	        initialize();
        }
        catch (Exception e)
        {
        	for ( int i = 0; i < e.getStackTrace().length; i++ )
        	{
        		Log.e( "onCreate() Settings Activity", e.getStackTrace()[i]+"" );
        	}
        }
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		/*
		// Commented this out for the new main menu setup
		Log.d("CDA","Back Pressed - back to map");
		onPause();
		onStop();
		Intent intent = new Intent( SettingsActivity.this, TourAppActivity.class );
		startActivity( intent );
		//*/
	}
	
	public void onStart()
	{
		super.onStart();
	}
	
	public void onStop()
	{
		super.onStop();
		finish();
	}
	
	private void loadPref()
	{
		justStarted = true;
		settings = this.getSharedPreferences( PREF_NAME , 0 );
        trafficOn = settings.getBoolean( "trafficView" , false );
        satelliteOn = settings.getBoolean( "satelliteView" , false );
        gpsOn = settings.getBoolean( "gpsCapable" , true );
        updateOn = settings.getBoolean( "mapupdate", true );
        zoom = settings.getInt( "zoom", DEFAULT_ZOOM );
        view_radius = settings.getFloat( "view_radius" , DEFAULT_VIEW_RADIUS );
        action_radius = settings.getFloat( "action_radius" , 1 ); 
	} 
	
	private void initialize()
	{	
		CheckBox traffic = (CheckBox) this.findViewById(R.id.trafficOn);
		traffic.setChecked( trafficOn );
		traffic.setOnClickListener(new View.OnClickListener() 
		{
			//Alex @Override
			public void onClick(View v) 
			{
				SharedPreferences.Editor editor = settings.edit();
				// putBoolean (key, value)
				editor.putBoolean( "trafficView" , ((CheckBox)v).isChecked()  );
				editor.commit();
			}
		});
		
		/*
		CheckBox satellite = (CheckBox) this.findViewById(R.id.satelliteOn);
		satellite.setChecked( satelliteOn );
		satellite.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = settings.edit();
				// putBoolean (key, value)
				editor.putBoolean( "satelliteView" , ((CheckBox)v).isChecked()  );
				editor.commit();
			}
		});//*/
		
		CheckBox update = (CheckBox) this.findViewById(R.id.pos_updateOn);
		update.setChecked( updateOn );
		update.setOnClickListener(new View.OnClickListener() 
		{
			//Alex @Override
			public void onClick(View v) 
			{
				SharedPreferences.Editor editor = settings.edit();
				// putBoolean (key, value)
				editor.putBoolean( "mapupdate" , ((CheckBox)v).isChecked()  );
				editor.commit();
			}
		});
		
		/*
		CheckBox gps = (CheckBox) this.findViewById(R.id.gpsOn);
		gps.setChecked( gpsOn );
		gps.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SharedPreferences.Editor editor = settings.edit();
				// putBoolean (key, value)
				editor.putBoolean( "gpsCapable" , ((CheckBox)v).isChecked()  );
				editor.commit();
			}
		});//*/
		
		CheckBox enableAudio = (CheckBox) this.findViewById(R.id.audioOn);
		enableAudio.setChecked( settings.getBoolean("enableAudio", false) );
		enableAudio.setOnClickListener(new View.OnClickListener() 
		{
			//Alex @Override
			public void onClick(View v) 
			{
				SharedPreferences.Editor editor = settings.edit();
				// putBoolean (key, value)
				editor.putBoolean( "enableAudio" , ((CheckBox)v).isChecked()  );
				editor.commit();
			}
		});
		
		CheckBox enableEvents = (CheckBox) this.findViewById(R.id.allOn);
		enableEvents.setChecked( settings.getBoolean("enableAll", true) );
		enableEvents.setOnClickListener( new View.OnClickListener() 
		{
			//Alex @Override
			public void onClick(View v) 
			{
				SharedPreferences.Editor editor = settings.edit();
				// putBoolean (key, value)
				editor.putBoolean( "enableAll" , ((CheckBox)v).isChecked()  );
				editor.commit();
			}
		});
		
		/*
		Spinner spinner = (Spinner) this.findViewById(R.id.spinner);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, 
				R.array.voice_list, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    adapter.setNotifyOnChange(true);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener( new OnItemSelectedListener()
	    {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				if ( parent != null && view != null && parent.getChildAt(0) != null ) // check for null objects
				{
					// initial start up 
					Log.i("Voice", " _ " + settings.getInt("voice", 0) + " _ " + parent.getChildAt(0).getClass().getName() );
					((TextView)parent.getChildAt( 0 )).setText(adapter.getItem(settings.getInt("voice", 0)));
					// set view enabled
	//				Log.i("VoiceAdapter", 
	//				((TextView)
					((ArrayAdapter)parent.getAdapter()).getView (
							settings.getInt("voice", 0), 
							new TextView( getApplicationContext()), 
							parent
	//				)).getText() + "" );
					).setEnabled(true);
					
					// different item selected
					if ( (position != 0 && settings.getInt("voice", 0) != 0) || justStarted == false )
					{
						SharedPreferences.Editor editor = settings.edit();
						Log.i("Spinner1", " _ " + position);
						editor.putInt("voice", position);
						view.setEnabled(true);
						adapter.notifyDataSetChanged();
						editor.commit();
					}
				}
				
				if ( justStarted == true )
				{
					justStarted = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{ 
				Log.i("Voice", "In");
				((ArrayAdapter)parent.getAdapter()).getView (
						0, 
						new TextView( getApplicationContext()), 
						parent
				).setEnabled(true);
			}
	    }); //*/
	    
		/*
		SeekBar senseBar = (SeekBar) this.findViewById(R.id.senseBar);
		senseBar.setProgress( zoom );
		senseBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() 
		{	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{
				SharedPreferences.Editor editor = settings.edit();
				if ( progress <= 1 ) 
				{ editor.putInt( "sense" , 0 ); }
				else if ( progress >= 20 )
				{ editor.putInt( "sense", 20 ); }
				else 
				{ editor.putInt( "sense", progress ); }
				
				editor.commit();
			}
		}); //*/
		
		SeekBar zoomBar = (SeekBar) this.findViewById(R.id.zoomBar);
		zoomBar.setProgress( zoom );
		zoomBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() 
		{	
			//Alex @Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			//Alex @Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			//Alex @Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{
				SharedPreferences.Editor editor = settings.edit();
				if ( progress <= 1 ) 
				{ editor.putInt( "zoom" , 1 ); }
				else if ( progress >= 20 )
				{ editor.putInt( "zoom", 20 ); }
				else 
				{ editor.putInt( "zoom", progress ); }
				
				editor.commit();
			}
		});
		
		/*
		SeekBar radiusBar = (SeekBar) this.findViewById(R.id.milesBar);
		radiusBar.setProgress( (int)view_radius );
		radiusBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() 
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{
				SharedPreferences.Editor editor = settings.edit();
				if ( progress <= 1 ) 
				{ editor.putFloat( "view_radius" , 1f ); }
				else if ( progress >= 20 )
				{ editor.putFloat( "view_radius", 20f );}
				else 
				{ editor.putFloat( "view_radius", progress ); }
				
				editor.commit();
			}
		});//*/
		
	}
}
