package com.fundots.events;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fundots.categories.CategoryListAdapter;
import com.fundots.data.DataHandler;
import com.fundots.data.DataItem;
import com.fundots.data.DataItem;
import com.fundots.deploy.R;
import com.fundots.deploy.TourAppActivity;
import com.fundots.images.ImageHandler;
import com.fundots.overlays.GenericOverlay;
import com.fundots.popup.Popup;

public class CustomDotListAdapter extends ArrayAdapter<DataItem>
{
	private int layoutResourceId;
	private Activity act;
	private LinkedList<DataItem> data;
	private int state;

	public CustomDotListAdapter(Activity act, int layoutResourceId, LinkedList<DataItem> data, int state)
	{
		super(act, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
        this.data = data;
        this.act = act;
        // state is used to handle different list adapter needs i.e. TourSite List, Stacking Dot list, Featured List
        this.state = state;
    }
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	View row = convertView;
        FunDotItem holder = null;
    	try
    	{
	        if(row == null)
	        {
	            LayoutInflater inflater = act.getLayoutInflater(); 
	            row = inflater.inflate( layoutResourceId, parent, false );
	            
	            holder = new FunDotItem();
	            holder.imgIcon = (ImageView)row.findViewById(R.id.itemImg);
	            holder.imgIcon2 = (ImageView)row.findViewById(R.id.itemImg2);
	            holder.txtTitle = (TextView)row.findViewById(R.id.itemTxt);
//	            holder.txtTitle2 = (TextView)row.findViewById(R.id.itemTxt2);
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (FunDotItem)row.getTag();
	        }
//	        final DataItem item = data.get(position);
	        final DataItem item = data.get(position);
	        
	        if ( state == 0 ) // regular dot list
	        {
//	        	holder.txtTitle.setText( item.locname + "                    " );
//		        if ( item.type.equals( DataHandler_v2.TOUR ) )
//		        {
//		        	holder.imgIcon.setImageResource(R.drawable.tour_btn_on);
//		        }
//		        else if ( item.type.equals( DataHandler_v2.EVENT ) )
//		        {
//		        	holder.imgIcon.setImageResource(R.drawable.ev_btn_on);
//		        }
//		        else if ( item.type.equals( DataHandler_v2.GENERAL ) || item.type.equals( DataHandler_v2.PRIMARY ) )
//		        {
//		        	holder.imgIcon.setImageResource(R.drawable.gen_btn_on);
//		        }
//		        else if ( item.type.equals( DataHandler_v2.RECREATION ) )
//		        {
//		        	holder.imgIcon.setImageResource(R.drawable.rec_btn_on);
//		        }
	        	
	        	holder.txtTitle.setText( item.variables[4] + "                    " );
	        	if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[0]) ) //attractions
        		{
	        		holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.attractions, 50, 50) );
        		}
        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[1]) ) //Business
        		{
        			holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.business, 50, 50) );
        		}
        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[2]) ) //Culture
        		{
        			holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.history, 50, 50) );
        		}
        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[3]) ) //Entertainment
        		{
        			holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.music, 50, 50) );
        		}
        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[4]) ) //Food
        		{
        			holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.food, 50, 50) );
        		}
        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[5]) ) //Lodging
        		{
        			holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.lodge, 50, 50) );
        		}
        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[6]) ) //Schedule
        		{
        			holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.calendar, 50, 50) );
        		}
        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[7]) ) //Shopping
        		{
        			holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.shop, 50, 50) );
        		}
        		else if ( item.variables[13].equals( CategoryListAdapter.CATEGORIES[8]) ) //Sports
        		{
        			holder.imgIcon.setImageBitmap( ImageHandler.decodeSampledBitmapFromResource(act.getResources(), R.drawable.sports, 50, 50) );
        		}
	        	
//		        if ( item.variables[12].equals( DataHandler.TOUR ) )
//		        {
//		        	holder.imgIcon.setImageResource(R.drawable.tour_btn_on);
//		        }
//		        else if ( item.variables[12].equals( DataHandler.EVENT ) )
//		        {
//		        	holder.imgIcon.setImageResource(R.drawable.ev_btn_on);
//		        }
//		        else if ( item.variables[12].equals( DataHandler.GENERAL ) || item.variables[12].equals( DataHandler.PRIMARY ) )
//		        {
//		        	holder.imgIcon.setImageResource(R.drawable.gen_btn_on);
//		        }
//		        else if ( item.variables[12].equals( DataHandler.RECREATION ) )
//		        {
//		        	holder.imgIcon.setImageResource(R.drawable.rec_btn_on);
//		        }
		        
		        // set row listener
		        row.setOnClickListener( new View.OnClickListener() 
		        {
		        	//Alex @Override
					public void onClick(View v) 
					{
						Popup.dot_popup((TourAppActivity)act, item);
					}
				});
	        }
	        else if ( state == 1 ) // featured tour list
	        {
	        	holder.txtTitle.setTextColor(Color.BLACK);
//	        	holder.txtTitle2.setTextColor(Color.BLACK);
	            row.setBackgroundColor(Color.WHITE);
//	        	holder.txtTitle.setText( item.tourSite + "                                                            " );
	        	holder.txtTitle.setText( item.variables[1] + "                                                            " );
	        	holder.imgIcon.setImageResource(R.drawable.featured);
	        	
		        // set row listener	
	        	row.setOnClickListener( new View.OnClickListener() 
		        {
	        		//Alex @Override
					public void onClick(View v) 
					{
						// go to map view and set location to animate to
//						Toast.makeText(act, item.tourSite, Toast.LENGTH_LONG).show();
						TourAppActivity.startingPoint = item.point;
						Intent intent = new Intent( act, TourAppActivity.class );
						act.startActivity( intent );
					}
				});
	        }
	        else if ( state == 2 ) // toursite list
	        {
	        	holder.txtTitle.setTextColor(Color.BLACK);
//	        	holder.txtTitle2.setTextColor(Color.BLACK);
	            row.setBackgroundColor(Color.WHITE);
	            // the space acts as a filler for the onClickListener
//	        	holder.txtTitle.setText( item.tourSite + "                                                       " );
	        	holder.txtTitle.setText( item.variables[1] + "                                                       " );
		        // set row listener
	        	row.setOnClickListener( new View.OnClickListener() 
		        {
	        		//Alex @Override
					public void onClick(View v) 
					{
						// go to map view and set location to animate to
//						Toast.makeText(act, item.tourSite, Toast.LENGTH_LONG).show();
						TourAppActivity.startingPoint = item.point;
						Intent intent = new Intent( act, TourAppActivity.class );
						act.startActivity( intent );
					}
				});
	        }
	        else
	        {
	        	Log.e("CustomDotListAdapter", "getView() ERROR: State nonexistant");
	        }
	        	
    	} 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    	}
        return row;
    }

}
