package com.fundots.popup;

import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.fundots.data.DataItem;
import com.fundots.deploy.R;
import com.fundots.deploy.SettingsActivity;
import com.fundots.deploy.TourAppActivity;
import com.fundots.events.CustomDotListAdapter;
import com.fundots.images.BackgroundLoadImages;
import com.fundots.static_vals.PublicStaticFunctions;
import com.fundots.stats.StatCollector_v3;

import org.json.JSONArray;
//import org.json.JSONObject;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;

public class Popup 
{
	public static boolean isPlaying = false;
	public static boolean showingMore = false;
	private static boolean eventUp = false;
	private static StatCollector_v3 stats = null; 
	private static PublicStaticFunctions functions = new PublicStaticFunctions(); 
	private static CustomDotListAdapter adapter;
	private static Dialog popup = null;
	public static List<DataItem> alldots_list = null;
	
	// used to clean up popups
	private static void cleanup()
	{
		if ( popup != null )
		{
			popup.dismiss();
		}
		popup = null;
		eventUp = false;
	}
	
	public static void event_popup( final TourAppActivity act, DataItem data )
	{
		try
		{			
			// first call to event_popup
			if ( eventUp == false )
			{
				// instantiating Dialog only once
				popup = new Dialog( act );
				popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
				popup.setContentView(R.layout.eventpopup);
				popup.setCancelable(false);
				// instantiate EventItemAdapter only once
				adapter = new CustomDotListAdapter( act, R.layout.eitem, new LinkedList<DataItem>(), 0 );
				ListView lView = (ListView) popup.findViewById(R.id.listView);
				lView.setAdapter(adapter);
				// instantiate close button with listener
				Button close = (Button) popup.findViewById( R.id.eclose );
				close.setOnClickListener( new View.OnClickListener() 
				{
					public void onClick(View v) 
					{
						cleanup();
						act.speak("");
					}
				});
				popup.show();
				// adds the current data to list
				adapter.add(data);
				eventUp = true;
			}
			else
			{
				// all other event_popup calls
				adapter.add(data);
			}
		}
		catch ( Exception e )
		{
			for ( int i = 0; i < e.getStackTrace().length; i++)
			{
				Log.e("event_popup()", e.getStackTrace()[i]+"" );
			}
		}
	}
	
	public static void dot_popup( final TourAppActivity act, final DataItem item )
	{
		// setup stats collector
		stats = StatCollector_v3.getInstance();
		stats.di = item;
		stats.start_dot();
		
		if ( eventUp == true )
		{
			cleanup();
		}
		
		try
		{	
			final Dialog dot_popup = new Dialog( act );
			// download images
			new BackgroundLoadImages( dot_popup ).execute( new DataItem[]{item} );
			
//			dot_popup.setContentView(R.layout.popup_v2);
			dot_popup.setContentView(R.layout.popup);
			dot_popup.setTitle( item.variables[4] );
			dot_popup.show();
			dot_popup.setCancelable(true);
			//click stat
			item.clicked = true;
			//time stat
			final long start = System.nanoTime();
			Log.d( "POPUP", item.point.getLatitudeE6() + ", " + item.point.getLongitudeE6() );
//			TextView title = (TextView) pop.findViewById(R.id.title);
//			title.setText( di.getName() );
			
//			Toast.makeText(getAct(), "Something", Toast.LENGTH_SHORT);

			TextView coupon = (TextView) dot_popup.findViewById(R.id.coupon);
			if ( item.variables[46] != null )
			{
				coupon.setText( item.variables[46] );
			}
			else
			{
				coupon.setText( "" );
			}
			
			TextView addr = (TextView) dot_popup.findViewById(R.id.address);
			if ( item.variables[6] != null && item.variables[7] != null && item.variables[5] != null )
			{
				addr.setText( item.variables[5] + ", " + item.variables[6] + ", " + item.variables[7] );
			}
			else
			{
				addr.setText( item.variables[5] );
			}
			// Navigation functionality
			addr.setOnClickListener( functions.getNaviFromClick( act, item ) );
			// load images in the background
			// Hung
//			final BackgroundLoadImages loader = new BackgroundLoadImages( item );
//			Void v = null;
//			loader.execute( v );
			
			// ImageHandler
//			final ImageView img_pop = (ImageView) popup.findViewById( R.id.img_pop );
			
			
//			final WebView text = (WebView) dot_popup.findViewById(R.id.text);
//			text.getSettings().setJavaScriptEnabled(true);
//			text.getSettings().
			final TextView text = (TextView) dot_popup.findViewById(R.id.text);
			if ( item.variables[16] != null )
			{
				// decode from base 64
//				String str = Base64.decode(item.variables[16], Base64.NO_WRAP | Base64.URL_SAFE);
//				char[] char64 = new char[]
//				String str = base64);
				item.variables[16] = item.variables[16].replaceAll("&lt;", "<").
									replaceAll("&gt;", ">").
									replaceAll("&quot;", "\"").
									replaceAll("&#039;", "'").
									replaceAll("&amp;", "&");
				// display html code
//				text.loadDataWithBaseURL(null, item.variables[16], "text/html", "UTF-8", null);
//				text.loadData( str, "text/html", null);
				
				String basictour = item.variables[16];
				String linksSent = "";

				if (basictour.contains("START_JSON_LINKSTABLE")) {
					linksSent = basictour.substring(basictour.indexOf("START_JSON_LINKSTABLE"));
					basictour = basictour.substring(0,basictour.indexOf("START_JSON_LINKSTABLE"));
					item.variables[16] = basictour; 
				}

				text.setText( Html.fromHtml(basictour) + "\n");

		        Bitmap bmp_top = BitmapFactory.decodeResource(text.getResources(), R.drawable.bg_table_top);
		        BitmapDrawable bitmapDrawable_top = new BitmapDrawable(bmp_top);
		        bitmapDrawable_top.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL);

		        Bitmap bmp_bottom = BitmapFactory.decodeResource(text.getResources(), R.drawable.bg_table_bottom);
		        BitmapDrawable bitmapDrawable_bottom = new BitmapDrawable(bmp_bottom);
		        bitmapDrawable_bottom.setGravity(Gravity.BOTTOM|Gravity.FILL_HORIZONTAL);

		        Bitmap bmp_center = BitmapFactory.decodeResource(text.getResources(), R.drawable.bg_table_center);
		        BitmapDrawable bitmapDrawable_center = new BitmapDrawable(bmp_center);
		        bitmapDrawable_center.setGravity(Gravity.FILL_VERTICAL|Gravity.FILL_HORIZONTAL);

		        int leftMargin=10;
				int topMargin=2;
				int rightMargin=10;
				int bottomMargin=2;

				LayoutParams tableRowParams = new LayoutParams (LayoutParams.WRAP_CONTENT);
		        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
				
				if(item.variables[15] != null ) {
					String[] subdots = item.variables[15].split(",");

			        /* Find Tablelayout defined in popup.xml */
			        TableLayout tl = (TableLayout)dot_popup.findViewById(R.id.subdotsTable);
			        tl.setStretchAllColumns(true);
			        tl.setShrinkAllColumns(true);
			        //tl.setMargins(5, 0, 5, 0);
			        
					/* Create a new row to be added. */
			        TableRow tr = new TableRow(act);
			        tr.setLayoutParams(tableRowParams);
			        // add space
			        TextView lbl0 = new TextView(act);
			        lbl0.setText(" ");
			        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl0.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl0);
			        // add the subdot id / area
			        TextView lbl1 = new TextView(act);
			        lbl1.setText(" ");
			        lbl1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl1.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl1);
			        // add the subdot name
			        TextView lbl2 = new TextView(act);
			        lbl2.setText(" ");
			        lbl2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl2.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl2);

			        tr.setBackgroundDrawable(bitmapDrawable_top);
			        
			        /* Add row to TableLayout. */
			        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));            

					/* Create a new row to be added. */
			        tr = new TableRow(act);
			        tr.setLayoutParams(tableRowParams);
			        // add space
			        lbl0 = new TextView(act);
			        lbl0.setText(" ");
			        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl0.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl0);
			        // add the subdot id / area
			        lbl1 = new TextView(act);
			        lbl1.setText("Id");
			        lbl1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl1.setTypeface(null, Typeface.BOLD);
			        lbl1.setTextColor(Color.parseColor("#000000"));
			        tr.addView(lbl1);
			        // add the subdot name
			        lbl2 = new TextView(act);
			        lbl2.setText("Name");
			        lbl2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl2.setTypeface(null, Typeface.BOLD); 
			        lbl2.setTextColor(Color.parseColor("#000000"));
			        tr.addView(lbl2);

			        tr.setBackgroundDrawable(bitmapDrawable_center);
			        
			        Integer par = 0;
			        String color = "#000000";

			        alldots_list = act.getAllItems();
			        
			        for ( int x = 0; x < subdots.length; x++ ) 
					{
			        	Integer subDotIndex = -1;
			        	for( int sl = 0; sl < alldots_list.size(); sl++) {
			        		if(Integer.parseInt(alldots_list.get(sl).variables[2]) == Integer.parseInt(subdots[x])) {
			        			subDotIndex = sl;
			        		}
			        	}

			        	if(subDotIndex >= 0) {
				        	par = 1 - par;
					        if(par > 0) {
					        	color = "#333333";
					        } else {
					        	color = "#AA0000";				        	
					        }

				        	String subdotkName = alldots_list.get(subDotIndex).variables[4];
							String subdotDescription = alldots_list.get(subDotIndex).variables[5];

							//DataItem subdotItem = .get(subdots[x]);
							//DataItem subDot = (DataHandler)dotDataHandler.get(x);

							
					        /* Create a new row to be added. */
					        tr = new TableRow(act);
					        tr.setLayoutParams(tableRowParams);
					        // add space
					        lbl0 = new TextView(act);
					        lbl0.setText(" ");
					        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
					        lbl0.setTypeface(null, Typeface.BOLD);
					        lbl0.setTextColor(Color.parseColor(color));
					        tr.addView(lbl0);
					        // add the subdot id / area
					        lbl1 = new TextView(act);
					        lbl1.setText(subdots[x]+" / "+alldots_list.get(subDotIndex).variables[49]);
					        lbl1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
					        lbl1.setId(subDotIndex);
					        // add listener for subdot popup -------------------------------------------------<<<<<<<<<<<<<<
					        lbl1.setOnClickListener( new View.OnClickListener() 
					        {
								@Override
								public void onClick(View v) 
								{
									dot_popup(act, alldots_list.get(v.getId()));
								}
							});			        
					        lbl1.setTextColor(Color.parseColor(color));
					        tr.addView(lbl1); 
					        // add the subdot name
					        lbl2 = new TextView(act);
					        lbl2.setText(subdotkName);
					        lbl2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
					        lbl2.setId(subDotIndex);
					        lbl2.setOnClickListener( new View.OnClickListener() 
					        {
								
								@Override
								public void onClick(View v) 
								{
									dot_popup(act, alldots_list.get(v.getId()));
								}
							});
					        lbl2.setTextColor(Color.parseColor(color));
					        tr.addView(lbl2); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

					        /* Add row to TableLayout. */
					        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));            

					        /* Create a new row to be added. */
					        tr = new TableRow(act);
					        tr.setLayoutParams(tableRowParams);
					        // add space
					        lbl0 = new TextView(act);
					        lbl0.setText(" ");
					        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
					        lbl0.setTypeface(null, Typeface.BOLD);
					        lbl0.setTextColor(Color.parseColor(color));
					        tr.addView(lbl0);
					        // add the link description
					        lbl1 = new TextView(act);
					        lbl1.setText(Html.fromHtml(subdotDescription));
					        lbl1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
					        lbl1.setTextColor(Color.parseColor(color));
					        tr.addView(lbl1);
					        tr.setBackgroundDrawable(bitmapDrawable_center);

					        /* Add row to TableLayout. */
					        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));
					        
							if ( alldots_list.get(subDotIndex).variables[46] != null ) {
						        /* Add row to TableLayout. */
						        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));            

						        /* Create a new row to be added. */
						        tr = new TableRow(act);
						        tr.setLayoutParams(tableRowParams);
						        // add space
						        lbl0 = new TextView(act);
						        lbl0.setText(" ");
						        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
						        lbl0.setTypeface(null, Typeface.BOLD);
						        lbl0.setTextColor(Color.parseColor(color));
						        tr.addView(lbl0);
						        // add the link description
						        lbl1 = new TextView(act);
						        lbl1.setText(alldots_list.get(subDotIndex).variables[46]);
						        lbl1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
						        lbl1.setTextColor(Color.parseColor(color));
						        tr.addView(lbl1);
						        tr.setBackgroundDrawable(bitmapDrawable_center);

						        /* Add row to TableLayout. */
						        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));							
							}
					        
			        	}
					}

			        /* Create a new row to be added. */
			        tr = new TableRow(act);
//			        tr.setLayoutParams(tableRowParams);
			        // add space
			        lbl0 = new TextView(act);
			        lbl0.setText(" ");
			        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl0.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl0);
			        // add the subdot id
			        lbl1 = new TextView(act);
			        lbl1.setText(" ");
			        lbl1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(lbl1);
			        // add the subdot name
			        lbl2 = new TextView(act);
			        lbl2.setText(" ");
			        lbl2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(lbl2);

			        /* Add row to TableLayout. */
			        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));
				}

				if (linksSent.contains("START_JSON_LINKSTABLE")) {
					linksSent = linksSent.replaceAll("START_JSON_LINKSTABLE", "").replaceAll("END_JSON_LINKSTABLE","");
					final JSONArray linksArray;
					linksArray = new JSONArray(linksSent);

			        /* Find Tablelayout defined in popup.xml */
			        TableLayout tl = (TableLayout)dot_popup.findViewById(R.id.linksTable);
			        tl.setStretchAllColumns(true);  
			        tl.setShrinkAllColumns(true);

			        /* Create a new row to be added. */
			        TableRow tr = new TableRow(act);
			        tr.setLayoutParams(tableRowParams);
			        // add space
			        TextView lbl0 = new TextView(act);
			        lbl0.setText(" ");
			        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl0.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl0);
			        // add the link name
			        TextView lbl = new TextView(act);
			        lbl.setText(" ");
			        lbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(lbl);
			        // add the link description
			        TextView desc = new TextView(act);
			        desc.setText(" ");
			        desc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(desc);
			        tr.setBackgroundDrawable(bitmapDrawable_top);

			        /* Add row to TableLayout. */
			        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));            

			        /* Create a new row to be added. */
			        tr = new TableRow(act);
			        tr.setLayoutParams(tableRowParams);
			        // add space
			        lbl0 = new TextView(act);
			        lbl0.setText(" ");
			        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl0.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl0);
			        // add the link name
			        lbl = new TextView(act);
			        lbl.setText("Links");
			        lbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl.setTypeface(null, Typeface.BOLD);
			        lbl.setTextColor(Color.parseColor("#000000"));
			        tr.addView(lbl);
			        // add the link description
			        desc = new TextView(act);
			        desc.setText(" ");
			        desc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(desc);
			        tr.setBackgroundDrawable(bitmapDrawable_center);

			        /* Add row to TableLayout. */
			        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));            

			        /* Create a new row to be added. */
			        tr = new TableRow(act);
			        tr.setLayoutParams(tableRowParams);
			        // add space
			        lbl0 = new TextView(act);
			        lbl0.setText(" ");
			        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl0.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl0);
			        // add the link name
			        lbl = new TextView(act);
			        lbl.setText(" ");
			        lbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(lbl);
			        // add the link description
			        desc = new TextView(act);
			        desc.setText(" ");
			        desc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(desc);
			        tr.setBackgroundDrawable(bitmapDrawable_center);

			        /* Add row to TableLayout. */
			        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));            

			        /* Create a new row to be added. */
			        tr = new TableRow(act);
			        tr.setLayoutParams(tableRowParams);
			        // add space
			        lbl0 = new TextView(act);
			        lbl0.setText(" ");
			        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl0.setTypeface(null, Typeface.BOLD); 
			        tr.addView(lbl0);
			        // add the link name
			        lbl = new TextView(act);
			        lbl.setText("Name");
			        lbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        lbl.setTypeface(null, Typeface.BOLD);
			        lbl.setTextColor(Color.parseColor("#000000"));
			        tr.addView(lbl);
			        // add the link description
			        desc = new TextView(act);
			        desc.setText("Description");
			        desc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        desc.setTypeface(null, Typeface.BOLD);
			        desc.setTextColor(Color.parseColor("#000000"));
			        tr.addView(desc);
			        tr.setBackgroundDrawable(bitmapDrawable_center);

			        /* Add row to TableLayout. */
			        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));            

			        for ( int x = 0; x < linksArray.length(); x++ ) 
					{
						String linkName =linksArray.getJSONObject(x).getString("name"); 
						String linkUrl =linksArray.getJSONObject(x).getString("url"); 
						String linkDescription =linksArray.getJSONObject(x).getString("description"); 
						//String linkFrom =linksArray.getJSONObject(x).getString("from"); 
						//String linkTo =linksArray.getJSONObject(x).getString("from"); 

				        /* Create a new row to be added. */
				        tr = new TableRow(act);
				        tr.setLayoutParams(tableRowParams);
				        // add space
				        lbl0 = new TextView(act);
				        lbl0.setText(" ");
				        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
				        tr.addView(lbl0);
				        // add the link name
				        lbl = new TextView(act);
				        lbl.setText(Html.fromHtml("<a href=\""+linkUrl+"\">"+linkName+"</a>"));
				        lbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
				        tr.addView(lbl);
				        // add the link description
				        desc = new TextView(act);
				        desc.setText(Html.fromHtml(linkDescription));
				        desc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
				        desc.setTextColor(Color.parseColor("#000000"));
				        tr.addView(desc);
				        tr.setBackgroundDrawable(bitmapDrawable_center);

				        /* Add row to TableLayout. */
				        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));            
					}

			        /* Create a new row to be added. */
			        tr = new TableRow(act);
			        tr.setLayoutParams(tableRowParams);
			        // add space
			        lbl0 = new TextView(act);
			        lbl0.setText(" ");
			        lbl0.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(lbl0);
			        // add the link name
			        lbl = new TextView(act);
			        lbl.setText(" ");
			        lbl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(lbl);
			        // add the link description
			        desc = new TextView(act);
			        desc.setText(" ");
			        desc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
			        tr.addView(desc);
			        tr.setBackgroundDrawable(bitmapDrawable_bottom);

			        /* Add row to TableLayout. */
			        tl.addView(tr,new LayoutParams(LayoutParams.WRAP_CONTENT));
				}
				text.setText( Html.fromHtml(item.variables[16]) + "\n");
			}
			else
			{
//				text.loadData("\n", "text/html", null);
				text.setText( "\n" );
			}
//			Log.i( "onTap()", "Number of links: "+di.getWeblinks().length );
			try
			{
				String link_str = "";
				int len = 3;//item.weblinks.length;
				for ( int i = 0; i < len; i++ )
				{
					if ( item.variables[18+(i*2)] != null)
					{
						link_str += item.variables[18+(i*2)] + "\n";
					}
				}
				TextView links = (TextView) dot_popup.findViewById(R.id.links);
				links.setText( link_str );
				Linkify.addLinks( links, Linkify.WEB_URLS );
			} 
			catch ( Exception e )
			{
				Log.e( "popup()", "Links problem: " + e.getMessage() );
				for ( int x = 0; x < e.getStackTrace().length; x++ ) 
				{
					Log.e( "popup()", e.getStackTrace()[x].toString() );
				}
			}
			
			final ImageButton play = (ImageButton) dot_popup.findViewById(R.id.yesbtn);
			// handles the condition if audio is enabled and the button is shown for the first time
			if ( isPlaying == false && act.getSharedPreferences( SettingsActivity.PREF_NAME, Context.MODE_PRIVATE ).getBoolean( "enableAudio", true))
			{
				isPlaying = true;
				play.setImageResource(R.drawable.stop);
//				play.setText("Stop");
			}
			play.setOnClickListener(new OnClickListener()
			{
				//Alex @Override
				public void onClick(View v) 
				{
					// increment play button stats
					item.playBtnClicked = true;
					
					Log.i( "PLAY BTN", "Play pressed" );
//					if ( play.getText().equals("Play") )
//					if ( isPlaying == false && play.getText().equals("Play") )
					if ( isPlaying == false )
					{
						act.speak("");

						item.variables[16] = item.variables[16].replaceAll("&lt;", "<").
								replaceAll("&gt;", ">").
								replaceAll("&quot;", "\"").
								replaceAll("&#039;", "'").
								replaceAll("&amp;", "&");
						if (item.variables[16].contains("START_JSON_LINKSTABLE")) {
							item.variables[16] = item.variables[16].substring(0,item.variables[16].indexOf("START_JSON_LINKSTABLE"));
						}						
						
						act.speak( (item.variables[16] != null ? item.variables[16]:"" ) + "." );
						isPlaying = true;
						play.setImageResource(R.drawable.stop);
//						play.setText("Stop");
					}
					else
					{
						act.speak("");
						isPlaying = false;
						play.setImageResource(R.drawable.play);
//						play.setText("Play");
					}
				}
			});
	
			final ImageButton more = (ImageButton) dot_popup.findViewById(R.id.more);
			more.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					// increment more button stats
					item.moreBtnClicked = true;
					
					item.variables[16] = item.variables[16].replaceAll("&lt;", "<").
							replaceAll("&gt;", ">").
							replaceAll("&quot;", "\"").
							replaceAll("&#039;", "'").
							replaceAll("&amp;", "&");
					if (item.variables[16].contains("START_JSON_LINKSTABLE")) {
						item.variables[16] = item.variables[16].substring(0,item.variables[16].indexOf("START_JSON_LINKSTABLE"));
					}						

					Log.i( "MORE BTN", "More pressed" );
//					if ( more.getText().equals("More") )
//					if ( showingMore == false && more.getText().equals("More"))
					if ( showingMore == false )
					{
						if ( item.variables[16] != null && item.variables[17] != null )
						{
//							text.loadDataWithBaseURL(null,
//									item.variables[16] +"\n"+ item.variables[17] + "\n",
//									"text/html",
//									"UTF-8", 
//									null);
//							text.loadData(item.variables[16] +"\n"+ item.variables[17] + "\n", "text/html", null);
							text.setText( Html.fromHtml( item.variables[16] +"\n"+ item.variables[17] + "\n").toString() );
							act.speak("");
//							if (  ) // check for audio enabled
//							{
								act.speak( (item.variables[17] != null ? item.variables[17]:"" ) + ".");
//							}
						}
						showingMore = true;
						more.setImageResource(R.drawable.less);
//						more.setText("Less");
					}
					else
					{
						if ( item.variables[16] != null )
						{
//							text.loadData(item.variables[16] + "\n", "text/html", null);
							text.setText( Html.fromHtml( item.variables[16] + "\n" ).toString() );
						}
						showingMore = false;
//						Matt - change so less button stops voice
						act.speak(""); 
						more.setImageResource(R.drawable.more);
//						more.setText("More");
					}
					
				}
			});
			
			ImageButton close = (ImageButton) dot_popup.findViewById(R.id.nobtn);
			close.setOnClickListener(new OnClickListener()
			{
				//Alex @Override
				public void onClick(View v) 
				{
					act.speak("");
					item.picchecker = false;
//					if( loader.isCancelled() == false )
//					{
//						loader.cancel(true);
//					}
					item.clearDrawables();
					isPlaying = false;
					showingMore = false;
					
					// update stats for closing and send
					long end = System.nanoTime();
					item.addToDotTime( end - start );
					stats.end_dot();
					stats.sendStats( act );
					
					dot_popup.dismiss();
				}
			});
			
			SharedPreferences prefs = act.getSharedPreferences( SettingsActivity.PREF_NAME , 0 );
			if ( prefs.getBoolean("enableAudio", true) )
			{
				act.speak("");
				act.speak( ( item.variables[4] != null ? item.variables[4]: item.variables[5]) 
						+ "..." 
						+ ( item.variables[16] != null ? Html.fromHtml(item.variables[16]):"" )
						+ "...");
			}
		}		
		catch ( Exception e )
		{
			for ( int x = 0; x < e.getStackTrace().length; x++ )
			{
				Log.e( "popup()", e.getStackTrace()[x].toString() );
			}
		}		
	}
	
	public static void media_popup( TourAppActivity act, final DataItem item)
	{
		try
		{
			final Dialog media_pop = new Dialog( act );
			
			media_pop.requestWindowFeature(Window.FEATURE_NO_TITLE);
			// Hung
//			media_pop.setContentView(R.layout.medias);
			media_pop.setContentView(R.layout.media_v3);
			media_pop.show();
			
			if ( item.dd != null )
			{
				
			}
			// Hung
//			final ImageView image = (ImageView) media_pop.findViewById(R.id.mediaimage);
////			image.setScaleType(ScaleTyp);
////			image.setAdjustViewBounds(true);
//			
//			image.setScaleType(ScaleType.CENTER_INSIDE);
//			
//			Drawable fstImage = item.dd[0];
//			if(fstImage!=null)
//				image.setImageDrawable(fstImage);
			
			// Hung
			new BackgroundLoadImages( media_pop ).execute( new DataItem[]{ item } );
		}
		catch ( Exception e )
		{
			Log.e( "media popup()", e.getMessage() );
			for ( int x = 0; x < e.getStackTrace().length; x++ )
			{
				Log.e( "media popup()", e.getStackTrace()[x].toString() );
			}
		}
	}
}
