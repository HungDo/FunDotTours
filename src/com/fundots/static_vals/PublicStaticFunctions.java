package com.fundots.static_vals;

import com.fundots.data.DataItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class PublicStaticFunctions 
{
	public PublicStaticFunctions(){};
	/**
	 * 
	 * @param context
	 * @param item
	 * @return
	 */
	public View.OnClickListener getNaviFromClick( final Context context, final DataItem item )
	{
		return new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// Hung - incorporate navigation for the user
				// needs dialog asking user if they want to leave the app to get directions
				//*
				AlertDialog.Builder builder = new AlertDialog.Builder( context );
				builder.setMessage("Do you want to get directions to " + item.variables[4] + "?")
					.setCancelable(false)
					.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() 
								{
									public void onClick(DialogInterface dialog, int id) 
									{
										String query = item.variables[5].replaceAll(" ", "+");
										Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+query)); 
										context.startActivity(intent);
									}
								})
								.setNegativeButton("No", new DialogInterface.OnClickListener() 
								{
									public void onClick(DialogInterface dialog, int id) 
									{
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
				
				// */
			}
		};
	}
}
