package com.fundots.categories;

import java.util.LinkedList;
import java.util.List;

import com.fundots.data.DataHandler;
import com.fundots.data.DataItem;
import com.fundots.deploy.R;
import com.fundots.deploy.TourAppActivity;
import com.fundots.events.FunDotItem;
import com.fundots.popup.Popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.TableRow.LayoutParams;

public class CategoryListAdapter extends ArrayAdapter<String> 
{

	private int layoutResourceId;
	private Activity act;
	public static final String[] CATEGORIES = new String[]
	{ 
		"Attractions", "Business", "Cultural / History", "Entertainment", "Food", 
		"Lodging", "Schedule", "Shopping", "Sports", ""
	};
	private int state;
	private boolean toggleCheck;
	private boolean DEBUG = true;
	
	public CategoryListAdapter(Activity act) 
	{
		super(act, R.layout.eitem, CATEGORIES);
		layoutResourceId = R.layout.eitem;
		this.act = act;
		toggleCheck = false;
	}
	
    @Override
	public int getCount() {
		return (CATEGORIES != null) ? CATEGORIES.length : 0;
	}

	@Override
	public String getItem(int idx) {
		return (CATEGORIES != null) ? CATEGORIES[idx] : null;
	}

	@Override
	public long getItemId(int position) 
	{
		return  position;
	}

	@Override
	public boolean hasStableIds(){
		return true;
	}

	@Override
	public int getItemViewType(int pos){
		return IGNORE_ITEM_VIEW_TYPE;
	}

	@Override
	public int getViewTypeCount(){
		return 1;
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
	            holder.imgIcon.setVisibility(View.GONE);
	            holder.txtTitle = (TextView)row.findViewById(R.id.itemTxt);
	            holder.txtTitle2 = (TextView)row.findViewById(R.id.itemTxt2);
	            holder.txtTitle2.setVisibility(View.GONE);
	            holder.imgIcon2 = (ImageView)row.findViewById(R.id.itemImg2);
	            holder.imgIcon2.setVisibility(View.VISIBLE);
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (FunDotItem)row.getTag();
	        }
	        
//	        "Attractions", "Business", "Culture", "Entertainment", "Food", 
//			"Lodging", "Schedule", "Shopping", "Sports"
	        
	        holder.txtTitle.setText( CATEGORIES[ position ] );
	        holder.txtTitle.setTextColor(Color.WHITE);
	        holder.txtTitle.setLayoutParams(new LayoutParams( 200, 75 ) );
	        
	        holder.imgIcon2.setLayoutParams(new LayoutParams( 75, 75 ) );
	        holder.imgIcon2.setScaleType(ScaleType.FIT_CENTER);
	        
	        final int pos = position;
	        
			//need to create checkbox here because row can't be made final while using inflater above
	        final CheckBox ckb = (CheckBox) row.findViewById(R.id.itemChk);
	        ckb.setVisibility(View.VISIBLE);
	       	ckb.toggle();
	        
	       	View.OnClickListener clickListener = new View.OnClickListener() 
	        {
				@Override
				public void onClick(View v) 
				{
					((TourAppActivity)act).enabledArry[ pos ] = !((TourAppActivity)act).enabledArry[ pos ];
					((TourAppActivity)act).refresh(); 	
					//simple toggle here, making sure that the checkbox was actually found first, so we don't crash
					if ( !(v instanceof CheckBox) )
					{
						if(ckb!=null)
							ckb.toggle();
					}	
				}
			};
	        
	        // set listeners
	        row.setOnClickListener( clickListener );
	        //this doesn't have the toggle() method because the checkbox automatically does it on click
	        ckb.setOnClickListener( clickListener );
	        
//	        if(DEBUG) Log.d("Adapter", "Position: "+position);
//	        if(DEBUG) Log.d("Adapter", "Value: "+holder.txtTitle.getText());
//	        if(DEBUG) Log.d("Adapter 2","Value2: "+holder.txtTitle2.getText());
	        
	        // set appropriate images for each category
	        switch ( position )
	        {
	        	case 0 :
	        		// attractions
	        		holder.imgIcon2.setImageResource(R.drawable.attractions);
	        		break;
	        	case 1 :
	        		// business
	        		holder.imgIcon2.setImageResource(R.drawable.business);
	        		break;
	        	case 2 :
	        		// culture
	        		holder.imgIcon2.setImageResource(R.drawable.history);
	        		break;
	        	case 3 :
	        		// entertainment
	        		holder.imgIcon2.setImageResource(R.drawable.music);
	        		break;
	        	case 4 :
	        		// food
	        		holder.imgIcon2.setImageResource(R.drawable.food);
	        		break;
	        	case 5 :
	        		// lodging
	        		holder.imgIcon2.setImageResource(R.drawable.lodge);
	        		break;
	        	case 6 :
	        		// schedule
	        		holder.imgIcon2.setImageResource(R.drawable.calendar);
	        		break;
	        	case 7 :
	        		// shopping
	        		holder.imgIcon2.setImageResource(R.drawable.shop);
	        		break;
	        	case 8 :
	        		// sports
	        		holder.imgIcon2.setImageResource(R.drawable.sports);
	        		break;	        	
	        	default:
	        		// blank
	        		holder.imgIcon2.setVisibility(View.GONE);
	        		ckb.setVisibility(View.GONE);
	        		break;
	        } 
    	} 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    	}
        return row;
    }
}
