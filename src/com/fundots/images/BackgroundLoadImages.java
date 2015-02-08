package com.fundots.images;

import java.io.InputStream;
import java.net.URL;

import com.fundots.data.DataItem;
import com.fundots.deploy.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

// Hung

public class BackgroundLoadImages <T extends Object> extends AsyncTask<DataItem, Void, DataItem> {

	private final String LOG = "BackgroundLoadImages_v2";
	private T act;
	private int count = 0;
	
	public BackgroundLoadImages( T act )
	{
		if ( act instanceof Activity || act instanceof Dialog )
		{
			this.act = act;
		}
	}
	
	@Override
	protected DataItem doInBackground(DataItem... items) 
	{
		Log.i(LOG, "doInBackGround()");
		int i;
		count = items.length;
		
		// skips the downloading process if it already has been done before
		// improves speed by 2x
		if ( items != null && items[0].dd != null && items[0].dd[0] != null )
		{
//			return items[0].dd[0];
			return items[0];
		}
		
		Drawable first_img = null;
		// This should only be used for one DataItem at a time but can be used to handle multiple data items
		for ( i = 0; i < items.length && act != null; i++ )
		{
			// check if the drawable arry for each DataItem is initialized
			if ( items[i].dd != null && 
				( items[i].variables[29] != null || items[i].variables[32] != null || items[i].variables[35] != null ) 
				)
			{
				int j;
				// there are only 3 pictures to a dot
				for ( j = 0; j < 3; j++ )
				{
					// checks if an element in the path arry is null
					if ( items[i].variables[29+(j*3)] != null )
					{
						// load images to item drawables
						items[i].dd[j] = LoadImageFromWebOperations( items[i].variables[29+(j*3)] );
						if ( first_img == null )
						{
							first_img = items[i].dd[j]; 
							Log.i(LOG, "set");
						}
						if ( items[i].dd[j] != null )
						{
							Log.i( LOG, "Image " + j + " not null" );
						}
						else
						{
							Log.i( LOG, "Image " + j + " null" );
						}
					}
				}
			}
			else
			{
				Log.i( LOG, "Images null" );
			}
		}
		
		if ( first_img == null )
		{
			Log.i(LOG, "1st Image null");
		}
		return items[0];
//		return first_img;
	}
	
	/**
	 *  UI thread method that sets the first image and removes the progress bar
	 */
	protected void onPostExecute( DataItem result )
	{
		
		Log.i(LOG, "onPostExecute()");
		ImageView img1 = null; 
		ImageView img2 = null;
		ImageView img3 = null;
		
		if ( act instanceof Activity && result != null )
		{
			Log.i(LOG, "Activity");
			img1 = (ImageView) ((Activity) act).findViewById(R.id.popimg1);
			img2 = (ImageView) ((Activity) act).findViewById(R.id.popimg2);
			img3 = (ImageView) ((Activity) act).findViewById(R.id.popimg3);
		}
		else if ( act instanceof Dialog && result != null )
		{
			Log.i(LOG, "Dialog"); 
			img1 = (ImageView) ((Dialog) act).findViewById(R.id.popimg1);
			img2 = (ImageView) ((Dialog) act).findViewById(R.id.popimg2);
			img3 = (ImageView) ((Dialog) act).findViewById(R.id.popimg3);
		}
		
		setHorizontalScrollView( result, act );
		
		//*
		int i;
		// img 1
		if ( result.dd[0] != null )
		{
			Log.i(LOG, "Image 1 set");
			img1.setScaleType(ScaleType.FIT_CENTER);
			img1.setImageDrawable( result.dd[0] );
		}
		else
		{
			img1.setVisibility(View.GONE);
		}
		
		// img2
		if ( result.dd[1] != null && img2 != null )
		{
			Log.i(LOG, "Image 2 set");
			img2.setScaleType(ScaleType.FIT_CENTER);
			img2.setImageDrawable( result.dd[1] );
		}
		else
		{
			img2.setVisibility(View.GONE);
		}
		
		// img3
		if ( result.dd[2] != null && img3 != null )
		{
			Log.i(LOG, "Image 3 set");
			img3.setScaleType(ScaleType.FIT_CENTER);
			img3.setImageDrawable( result.dd[2] );
		}
		else
		{
			img3.setVisibility(View.GONE);
		}//*/
	}
	
	private void setHorizontalScrollView( DataItem result, T act )
	{
		if ( result.dd != null && ( result.dd[0] != null || result.dd[1] != null || result.dd[2] != null ) )
		{
			if ( act instanceof Activity )
			{
				HorizontalScrollView hsv = (HorizontalScrollView) ((Activity)act).findViewById(R.id.hsv); 
				hsv.setVisibility(View.VISIBLE);
			}
			else if ( act instanceof Dialog )
			{
				HorizontalScrollView hsv = (HorizontalScrollView) ((Dialog)act).findViewById(R.id.hsv); 
				hsv.setVisibility(View.VISIBLE);
			}
		}
	}
	/*
	protected void onPostExecute( Drawable result )
	{
		Log.i(LOG, "onPostExecute()");
		try
		{
			ImageView image = null;
			ProgressBar bar = null;
			if ( count == 1 && act != null && result != null )
			{
				if ( act instanceof Activity )
				{
					image = (ImageView) ((Activity) act).findViewById(R.id.popimg1);
					// make progressbar invisible
//					bar = (ProgressBar) ((Activity) act).findViewById(R.id.imgProgressBar);
				}
				else if ( act instanceof Dialog )
				{
					image = (ImageView) ((Dialog) act).findViewById(R.id.popimg1);
					Log.i(LOG, "img set");
					// make progressbar invisible
//					bar = (ProgressBar) ((Dialog) act).findViewById(R.id.imgProgressBar);
				}
				
				// set the first image to image view
				if ( image != null && result != null )
				{
					Log.i(LOG, "img set");
					image.setScaleType(ScaleType.FIT_CENTER);
					image.setImageDrawable( result );
					image.setVisibility(View.VISIBLE);
				}
				// make progress bar invisible if 
				if ( bar != null && result != null && image != null )
				{
					// bar.setVisibility(ProgressBar.INVISIBLE);
				}
				else
				{
					// error message may be needed to notify users
				}
			}
			else
			{
				// multiple items just load the drawables into the item
			}
		} 
		catch ( Exception e )
		{
			Log.e( LOG, "onPostExecute()" );
			for ( int i = 0; i < e.getStackTrace().length; i++ )
			{
				Log.e( LOG, e.getStackTrace()[i].toString() );
			}
		}
	}//*/

	/**
	 *  Downloads the image from a url and stores it in memory for easy loading and access
	 * @param url
	 * @return
	 */
	private Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
        	Log.i( "LoadImageFromWebOperations" , url); 
	        InputStream is = (InputStream) new URL(url).getContent();
	        return Drawable.createFromStream(is, "src name");
        }
        catch (Exception e) 
        {
	        System.out.println("Exc="+e);
	        return null;
        }
    }

}
