package com.fundots.toursite;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fundots.data.DataHandler;
import com.fundots.data.DataItem;
import com.fundots.deploy.R;
import com.fundots.deploy.TourAppActivity;
import com.fundots.events.CustomDotListAdapter;
import com.fundots.static_vals.PublicStaticValues;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class TourSiteActivity extends Activity 
{
	private DataHandler data;
	private static final String PARTNER = "PARTNER";
	private static final String STATE = "STATE";
	private static final String CITY = "CITY";
	
	private CustomDotListAdapter adapter;
	
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		Log.d("TourSiteActivity", "onCreate()");
		this.setContentView( R.layout.toursite );
		try
		{
			initialize();
		}
		catch (Exception e)
		{
			int i;
			for ( i = 0; i < e.getStackTrace().length; i++ )
			{
				Log.e("Tour Site Activity", e.getStackTrace()[i] + "");
			}
		}
	}
	
	public void onResume()
	{
		super.onResume();
		TourAppActivity.startingPoint = null;
	}
	
	public void onPause()
	{
		super.onPause();
		if ( data != null )
		{
			data.closeDatabase();
		}
		PublicStaticValues.isDoneDownloading = false;
	}
	
	/**
	 *  BUG HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
	 */
	private void initialize()
	{
//		data = new DataHandler_v2( this );
		data = new DataHandler( this );
		data.populateFromDeviceDB();
		if ( data.getData().size() == 0 || PublicStaticValues.isCurrentData == false )
		{
			data.updateFromServer();
//			data.populateFromDeviceDB();
		}
		
//		new BackgroundBackendUpdate( this ).execute();
		
//		PublicStaticValues.isDoneDownloading = true;
		
		initSpinners();
		
//		new BackgroundTourSite( this ).execute();
		
		initListView();
	}
	
	private void initListView()
	{
		Log.d("Initialize", "ListView");
//		LinkedList <DataItem> dataList = data.filter( DataHandler_v2.PRIMARY );
		LinkedList <DataItem> dataList = data.filter( DataHandler.EVENT_TYPES[1] );
		
		ListView listView = (ListView) this.findViewById( R.id.tourList );
		
		adapter = new CustomDotListAdapter( this, R.layout.eitem, new LinkedList<DataItem>(), 2 );
//		for ( DataItem item : dataList )
		for ( DataItem item : dataList )
		{
			adapter.add(item);
		}
		listView.setAdapter( adapter );
	}
	
//	private void updateAdapter( List <DataItem> items )
//	{
//		if ( adapter != null )
//		{
//			adapter.clear();
//			for ( DataItem item : items )
//			{
//				adapter.add(item);
//			}
//		}
//	}
	
	private void updateAdapter( List <DataItem> items )
	{
		if ( adapter != null )
		{
			adapter.clear();
			for ( DataItem item : items )
			{
				adapter.add(item);
			}
		}
	}
	
	private void initSpinners()
	{
		Log.d("Initialize", "Spinners");
//		LinkedList <DataItem> dataList = data.filter( DataHandler_v2.PRIMARY, null, null, null );
		LinkedList <DataItem> dataList = data.filter( DataHandler.EVENT_TYPES[1], null, null, null );
		
		final Spinner spinners [] = new Spinner [] 
        {
//			(Spinner) this.findViewById(R.id.spinPartner)	,
			(Spinner) this.findViewById(R.id.spinState)		,
			(Spinner) this.findViewById(R.id.spinCity)
		};
		
		@SuppressWarnings("unchecked")
		List <String> lists [] = new LinkedList []
        {
//			new LinkedList<String>(),	// Partners
			new LinkedList<String>(),	// State
			new LinkedList<String>(),	// City
        };
		
//		lists[0].add("Choose a Partner");
//		lists[1].add("Choose a State");
//		lists[2].add("Choose a City");
		// temporary
		lists[0].add("Choose a State");
		lists[1].add("Choose a City");
		// 
//		addAll( lists[0], getAllFilter( dataList, PARTNER ) );
//		addAll( lists[1], getAllFilter( dataList, STATE ) );
//		addAll( lists[2], getAllFilter( dataList, CITY ) );
		addAll( lists[0], getAllFilter( dataList, STATE ) );
		addAll( lists[1], getAllFilter( dataList, CITY ) );
		// init adapters
		ArrayAdapter<?> adapters [] = new ArrayAdapter [] 
        {
			new ArrayAdapter <String> ( this, android.R.layout.simple_spinner_item, lists[0] ),
			new ArrayAdapter <String> ( this, android.R.layout.simple_spinner_item, lists[1] ),
//			new ArrayAdapter <String> ( this, android.R.layout.simple_spinner_item, lists[2] )
		};
		// set adapter's look and feel
		adapters[0].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapters[1].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		adapters[2].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// set adapters
		spinners[0].setAdapter( adapters[0] );
		spinners[1].setAdapter( adapters[1] );
//		spinners[2].setAdapter( adapters[2] );
		
		spinners[0].setOnItemSelectedListener( new OnItemSelectedListener() 
		{
			//Alex @Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
//				updateAdapter( 
//						data.filter( DataHandler_v2.PRIMARY, 
//									 getSpinnerSelection( spinners[0] ), 
//									 getSpinnerSelection( spinners[1] ), 
//									 getSpinnerSelection( spinners[2] ) )
//				);
				updateAdapter( 
						data.filter( DataHandler.EVENT_TYPES[1], 
									 null, 
									 getSpinnerSelection( spinners[0] ), 
									 getSpinnerSelection( spinners[1] ) )
				);
			}
			//Alex @Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
		spinners[1].setOnItemSelectedListener( new OnItemSelectedListener() 
		{
			//Alex @Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
//				updateAdapter( 
//						data.filter( DataHandler_v2.PRIMARY, 
//									 getSpinnerSelection( spinners[0] ), 
//									 getSpinnerSelection( spinners[1] ), 
//									 getSpinnerSelection( spinners[2] ) )
//				);
				updateAdapter( 
						data.filter( DataHandler.EVENT_TYPES[1], 
									 null, 
									 getSpinnerSelection( spinners[0] ), 
									 getSpinnerSelection( spinners[1] ) )
				);
			}
			//Alex @Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
		/*
		spinners[2].setOnItemSelectedListener( new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				updateAdapter( 
						data.filter( DataHandler_v2.PRIMARY, 
									 getSpinnerSelection( spinners[0] ), 
									 getSpinnerSelection( spinners[1] ), 
									 getSpinnerSelection( spinners[2] ) )
				);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		}); //*/
	}
	
	private String getSpinnerSelection( Spinner spinner )
	{
		Object obj = spinner.getAdapter().getItem( spinner.getLastVisiblePosition() );
		if ( obj != null )
		{
			String str = (String) obj;
//			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
			if ( str != "Choose a Partner" && str != "Choose a State" && str != "Choose a City" )
			{
				return str;
			}
		}
		return null;
	}
	
	private void addAll( List <String> list, Set <String> set)
	{
		Iterator <String> iter = set.iterator();
		while ( iter.hasNext() )
		{
			list.add( iter.next() );
		}
	}
	
	private HashSet<String> getAllFilter( List <DataItem> list, String filter )
	{
		HashSet <String> returnlist = new HashSet<String>();
		for ( DataItem item: list )
		{
			if ( filter.equals(PARTNER) )
			{
				returnlist.add(item.variables[0]);
			}
			else if ( filter.equals(STATE) ) // state is record 7
			{
				returnlist.add(item.variables[7]);
			}
			else if ( filter.equals(CITY) ) // city is record 6
			{
				returnlist.add(item.variables[6]);
			}
		}
		
		return returnlist;
	}
}
