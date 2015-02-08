package com.fundots.multitask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import com.fundots.deploy.SettingsActivity;
import com.fundots.static_vals.PublicStaticValues;
import com.fundots.stats.StatCollector_v3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class BackgroundStatSender extends AsyncTask<StatCollector_v3, Integer , Boolean> 
{

	// http://www.tourthedots.com/
	// index.php?option=com_aidots&sTask=stats&task=aa&
	// partner_id=1&
	// tour_id=1&
	// dot_id=1&
	// type=something&
	// app_user_id=the_id&
	// device_id=the_serial
	
	private static String site[];
//	private String dev_id;
//	private String app_id;
	
	public BackgroundStatSender( Context context )
	{
//		app_id = context.getSharedPreferences(SettingsActivity.PREF_NAME, 0).getString(PublicStaticValues.APP_ID_PREF,	"" );
//		dev_id = context.getSharedPreferences(SettingsActivity.PREF_NAME, 0).getString(PublicStaticValues.DEV_ID_PREF,	"" );
//		site = "http://www.tourthedots.com/index.php?option=com_aidots&sTask=stats&task=aa&partner_id=1&tour_id=1&dot_id=1&type=something";
		site = new String[]{ 
//			"http://www.tourthedots.com/index.php?option=com_aidots" ,
			"http://www.fundottours.com/index.php?option=com_aidots" ,
			"&sTask=stats&task=aa" ,		
			"&partner_id=" , 		// < --
	    	"&tour_id=" ,			// < --	
	    	"&dot_id=" ,			// < --
	    	"&type=" ,				// < --
	    	"&app_user_id="	+ context.getSharedPreferences(SettingsActivity.PREF_NAME, 0).getString(PublicStaticValues.APP_ID_PREF,	"" ) ,
	    	"&device_id=" 	+ context.getSharedPreferences(SettingsActivity.PREF_NAME, 0).getString(PublicStaticValues.DEV_ID_PREF,	"" ) 
		};
	}

	protected Boolean doInBackground(StatCollector_v3... params) {
		try 
		{
			for ( int i = 0; i < params.length; i++ )
			{
				sendToServer(params[i]);
				params[i].clear();
			}
		}
		catch (ClientProtocolException e) 
		{
			e.printStackTrace(); return false;
		} 
		catch (IOException e) 			 
		{
			e.printStackTrace(); return false;
		}
		return true;
	}	
	protected void onPostExecute()
	{
		Log.d("Sending stats", "Done");
	}
	
	private void sendToServer(StatCollector_v3 info) throws ClientProtocolException, IOException
	{	
		Log.i( "Sending Data to server", "sendToServer(...)" );
		
		StringBuffer stat_url = new StringBuffer();
		stat_url.append( site[0] );
		stat_url.append( site[1] );
		stat_url.append( site[2] ); 				// partner
		if ( info.di != null )
			stat_url.append( info.di.variables[0] );
		else
			stat_url.append( 1 );
		stat_url.append( site[3] );					// tourid
		if ( info.di != null )
			stat_url.append( info.di.toursiteid );
		else
			stat_url.append( 1 );
		stat_url.append( site[4] );					// dot id
		if ( info.di != null )
			stat_url.append( info.di.id );
		else
			stat_url.append( 1 );
		stat_url.append( site[5] );					// type
		
		StringBuffer types;
		
		for ( int i = 0; i < StatCollector_v3.APP_STAT_TYPE.length; i++ )
		{
			types = new StringBuffer();
			switch(i)
			{
				case 0:
					if ( info.di != null && info.di.clicked == true )
					{
						types.append( StatCollector_v3.APP_STAT_TYPE[0] ); 
						sendStat( stat_url.toString() , types );
					}
					break;
				case 1:
					if ( info.di != null && info.di.picsClicked == true )
					{
						types.append( StatCollector_v3.APP_STAT_TYPE[1] );
						sendStat( stat_url.toString() , types );
					}
					break;
				case 2:
					if ( info.di != null && info.di.moreBtnClicked == true )
					{
						types.append( StatCollector_v3.APP_STAT_TYPE[2] );
						sendStat( stat_url.toString() , types );
					}
					break;
				case 3:
					if ( info.di != null && info.di.playBtnClicked == true )
					{
						types.append( StatCollector_v3.APP_STAT_TYPE[3] );
						sendStat( stat_url.toString() , types );
					}
					break;
				case 4:
					if ( info.di != null )
					{
						types.append( StatCollector_v3.APP_STAT_TYPE[4] + "(" + info.dot_time + ")" );
						sendStat( stat_url.toString() , types );
					}
					break;
				case 5:
					if ( info.endStatCollector == true )
					{
						types.append( StatCollector_v3.APP_STAT_TYPE[5] + "(" + info.app_time + ")" );
						sendStat( stat_url.toString() , types );
					}
					else
					{
						Log.i( "App time", info.app_time+"" );
					}
					break;
			}
		}
		Log.d("Stats", "Finishing sending stats");
	}
	
	private static void sendStat( String stat_url, StringBuffer types ) throws MalformedURLException, IOException
	{
		types.append( site[6] );					// app id
		types.append( site[7] );					// device id
		//
		URL url = new URL( stat_url + types.toString() );
		if ( url.openStream() == null )
		{
			Log.e( "URL Stream Error", url.toString() );
		}
		else
		{
			Log.i( "URL Stream", url.toString() );
		}
	}
	
}

