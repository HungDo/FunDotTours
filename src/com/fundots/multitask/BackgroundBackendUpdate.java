package com.fundots.multitask;

import com.fundots.data.DataHandler;
import com.fundots.deploy.LoadActivity;
import com.fundots.static_vals.PublicStaticValues;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

// test version
public class BackgroundBackendUpdate extends AsyncTask<String, Integer, DataHandler> {

	private Activity act;
	private DataHandler dh;
	public static boolean isDone = false;
	
	public BackgroundBackendUpdate( Activity act )
	{
		this.act = act;
	}
	
	protected void onPreExecute()
	{
		try
		{
			dh = new DataHandler( act );
			dh.deleteAll();
		}
		catch ( Exception e )
		{
			for ( int x = 0; x < e.getStackTrace().length; x++ )
			{
				Log.e( "BackgroundBackendUpdate", e.getStackTrace()[x].toString() );
			}
		}
	}
	
	@Override
	protected DataHandler doInBackground(String... params) 
	{
//		DataHandler_v2 dh = null;
		try
		{
//			dh = new DataHandler_v2( act );
			Log.d("ASYNCTASK", "Start ... ");
			dh.updateFromServer();
//			dh.populateFromDeviceDB();
//			dh.closeDatabase();
			Log.d("ASYNCTASK", "Finish ...");
			dh.closeDatabase();
			if ( dh != null )
			{
				dh.close();
			}
		}
		catch ( Exception e )
		{
			for ( int x = 0; x < e.getStackTrace().length; x++ )
			{
				Log.e( "BackgroundBackendUpdate", e.getStackTrace()[x].toString() );
			}
		}
		Log.d( "Backend", "Backend");
		PublicStaticValues.isDoneDownloading = true;
		isDone = true;
		return null;
	}

}
