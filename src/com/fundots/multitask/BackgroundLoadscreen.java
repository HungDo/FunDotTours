package com.fundots.multitask;

import java.util.LinkedList;

import com.fundots.data.DataHandler;
import com.fundots.deploy.LoadActivity;
import com.fundots.deploy.R;
import com.fundots.events.CustomDotListAdapter;
import com.fundots.static_vals.PublicStaticValues;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

public class BackgroundLoadscreen extends AsyncTask<Void, Void, Void> 
{

	private LoadActivity act;
	private int[] ids;
	
	public BackgroundLoadscreen( LoadActivity load, int[] ids )
	{
		this.act = load;
		this.ids = ids;
	}
	@Override
	protected Void doInBackground(Void... params) 
	{
		int i = 0;
		try 
		{
			// 
			while ( PublicStaticValues.isDoneDownloading == false )
			{
				Thread.sleep(2000);
				i++;
			}
			PublicStaticValues.isDoneDownloading = false;
		}
		catch (InterruptedException e) 
		{
			Log.e( "Loadscreen", e.getMessage() ); 
		}
		Log.d( "Loadscreen", "Loadscreen :: " + (i*2) + " secs");
		return null;
	}
	
	protected void onPostExecute( Void result )
	{
		ListView list = (ListView) act.findViewById(R.id.featured);
		DataHandler handler = new DataHandler( act );
		handler.populateFromDeviceDB();
		handler.closeDatabase();
		if ( list.getAdapter() == null )
		{
			list.setAdapter( new CustomDotListAdapter( act, R.layout.eitem, handler.filter(ids), 1 ) );
		}
		ProgressBar bar = (ProgressBar) act.findViewById(R.id.featureProgressBar);
		bar.setVisibility(ProgressBar.INVISIBLE);
//		TourSiteListAdapter.STATE = 0; // return state to previous state
		
		Log.d("Loadscreen", "Done");
	}

}
