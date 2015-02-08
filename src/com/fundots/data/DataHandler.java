package com.fundots.data;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fundots.static_vals.PublicStaticValues;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * v1 - 
 * v2 - pre 12/1/12
 * v3 - 12/1/12 
 * Changes - more fields
 */
public class DataHandler extends SQLiteOpenHelper
{ 
	public static final String[] EVENT_TYPES = 
	{ 	"general",
		"primary",
		"tour",
		"event",
		"recreation", 
	};
	//
	private LinkedList <DataItem> latlonData = new LinkedList<DataItem> ();
	// ------------------------ Database variables --------------------------------
	private final static String DB_PATH 	= "/data/data/com.fundots.deploy/databases/";
	private final static String DB_NAME 	= "fundottours.db";
	private final static int VERSION 		= 1; 
	private final static String SITE_PATH 	= 
		 "http://www.tourthedots.com/index.php?option=com_aidots&sTask=xml"; 
		// current backend - test
//		 "http://www.fundottours.com/index.php?option=com_aidots&sTask=xml"; 
		// old backend - deployed 
	//
	private static final String[] RECORD = 
	{
		"partnername",         "toursite",             "id",                
		"toursiteid",          "locationname",         "address", // RECORD[5]                
		"city",                "state",                "zipcode",                
		"country",             "langitude", /*10*/     "latitude", /*11*/                
/*12*/	"typeofactivitys",	   "dotcategory", /*13*/   "superdot",                
		"subdots",             "basictour",            "moretour",                
		"weblink1",            "webkey1",              "weblink2",                
		"webkey2",             "weblink3",             "webkey3",                
		"audiotourinfo1",      "audioserver1",         "audiotourinfo2",                
		"audioserver2",        "picser1",              "picnamer1",                
		"picdesc1",            "picser2",              "picnamer2",                
		"picdesc2",            "picser3",              "picnamer3",                
		"picdesc3",            "videoser1",            "videonamer1",                
		"videodesc1",          "videoser2",            "videonamer2",                
		"videodesc2",          "videoser3",            "videonamer3",                
		"videodesc3",          "coupon",               "status"
	};
	
    private String createTableQuery = 
      "( 'partnername' TEXT, "+
    	"'toursite' TEXT, "+
    	"'id' INTEGER, "+
    	"'toursiteid' INTEGER, "+
    	"'locationname' TEXT, "+
    	"'address' TEXT NOT NULL, "+
    	"'city' TEXT NOT NULL, "+
    	"'state' TEXT NOT NULL, "+
    	"'zipcode' TEXT, "+
    	"'country' TEXT NOT NULL, "+
    	"'langitude' NUMERIC NOT NULL, "+
    	"'latitude' NUMERIC NOT NULL, "+
    	"'typeofactivitys' TEXT NOT NULL, "+
    	"'dotcategory' TEXT, "+
    	"'superdot' TEXT, "+
    	"'subdots' TEXT, "+
    	"'basictour' TEXT, "+
    	"'moretour' TEXT, "+
    	"'weblink1' TEXT, "+
    	"'webkey1' TEXT, "+
    	"'weblink2' TEXT, "+
    	"'webkey2' TEXT, "+
    	"'weblink3' TEXT, "+
    	"'webkey3' TEXT, "+
    	"'audiotourinfo1' TEXT, "+
    	"'audioserver1' TEXT, "+
    	"'audiotourinfo2' TEXT, "+
    	"'audioserver2' TEXT, "+
    	"'picser1' TEXT, "+
    	"'picnamer1' TEXT, "+
    	"'picdesc1' TEXT, "+
    	"'picser2' TEXT, "+
    	"'picnamer2' TEXT, "+
    	"'picdesc2' TEXT, "+
    	"'picser3' TEXT, "+
    	"'picnamer3' TEXT, "+
    	"'picdesc3' TEXT, "+
    	"'videoser1' TEXT, "+
    	"'videonamer1' TEXT, "+
    	"'videodesc1' TEXT, "+
    	"'videoser2' TEXT, "+
    	"'videonamer2' TEXT, "+
    	"'videodesc2' TEXT, "+
    	"'videoser3' TEXT, "+
    	"'videonamer3' TEXT, "+
    	"'videodesc3' TEXT, "+
    	"'coupon' TEXT, "+
    	"'status' INTEGER )"; 
    
	private Context context;
	private SQLiteDatabase db;
	
	private final String TABLES = "SELECT * FROM sqlite_master WHERE type='table' AND name !='android_metadata'";
	
	public DataHandler( Context context )
	{
		super( context, DB_NAME, null, VERSION );
		this.context = context;
		try
		{
			createDatabase();
	 		openDatabase();
	 	}
		catch (Exception e)
		{
			int len = e.getStackTrace().length;
			for ( int x = 0; x < len; x++ )
			{
				Log.e( "DataHandler()", e.getStackTrace()[x].toString() );
			}
		}
	}
	
	public void add( DataItem data )
	{
		latlonData.add(data);
	}
	
	public DataItem get( int index )
	{
		return latlonData.get(index);
	}
	
	public int getSize()
	{
		return latlonData.size(); 
	}
	
	public LinkedList <DataItem> getData()
	{
		return latlonData;
	}
	
	/**
	 * DEPRECIATED
	 * Gets a list of dots given an array of Integer ids
	 * The array of Integer ids is fixed but accounts for any sized array
	 * @param ids
	 * @return
	 */
	public LinkedList <DataItem> filter (int[] ids)
	{
		LinkedList<DataItem> list = new LinkedList<DataItem>();
//		for( DataItem item: latlonData )
//		{
//			int i;
//			for ( i = 0; i < ids.length; i++ )
//			{
//				if ( ids[i] == -1 ) { continue; }
//				else if( item.id == ids[i] )
//				{
//					list.add(item);
//				}
//			}
//		}
		for ( int i = 0; i < ids.length; i++ )
		{
			if ( ids[i] == -1 ) { continue; }
			else
			{
				for( DataItem item: latlonData )
				{
					if( Integer.parseInt( item.variables[2].trim() ) == ids[i] && !list.contains((DataItem)item) )
					{
						Log.i("ID", item.id +"");
						list.add(item);
					}
				}
			}
		}
		
		return list;
	}
	
	public LinkedList <DataItem> filter (String type)
	{
		return filter(type, false, null);
	}
	
	// filters out all the filtered overlays
	public LinkedList <DataItem> filter( String typeDot , boolean findTour, String tourName)
	{
		//type dot is the type of dot itself, whether its General, Public or whatever
		//findTour is whether or not we're looking for a specific tour default is false
		//tourName is the tour site we're looking at.
		LinkedList<DataItem> list = new LinkedList<DataItem>();
		Iterator <DataItem> i = latlonData.iterator();
		DataItem data = null;
		while( i.hasNext() )
		{
			data = i.next();
			if(findTour == true)
			{
				if(data.variables[12].equals(typeDot) && data.variables[1].equals(tourName))
				{
					list.add(data);
				}
			}
			else
			{
				if (data.variables[12].equals(typeDot) )
				{
					list.add(data);
				}
			}
		}
		return list;
	}
	
	public LinkedList <DataItem> filter( String type, String partner, String state, String city )
	{
		LinkedList<DataItem> list = new LinkedList<DataItem>();
		Iterator <DataItem> i = latlonData.iterator();
		DataItem data = null;
		while( i.hasNext() )
		{
			data = i.next();
			// check for duplicate primary dots ignore the rest
			boolean duplicate = false;
			for ( DataItem di : list )
			{
				if ( data.variables[1].equals(di.variables[1]) ) // tour site name comparison
				{
					duplicate = true;
					break;
				}
			}
			// skip duplicate
			if ( duplicate == true )
			{
				continue;
			}
			
//			if ( (type != null && data.variables[12].equals(type)) || (partner == null) ) // type
			if ( (type != null && data.variables[13].equals(type)) || (partner == null) ) // category
			{
				if ( partner != null && data.variables[0].equals(partner) )
				{
					if ( state != null && data.variables[7].equals(state) )
					{
						if ( city != null && data.variables[6].equals(city) ) { list.add(data); }
						else if ( city == null ) { list.add(data); }
					}
					else if ( state == null )
					{
						if ( city != null && data.variables[6].equals(city) ) { list.add(data); }
						else if ( city == null ) { list.add(data); }
					}
				}
				else if ( partner == null )
				{
					if ( state != null && data.variables[7].equals(state) )
					{
						if ( city != null && data.variables[6].equals(city) ) { list.add(data); }
						else if ( city == null ) { list.add(data); }
					}
					else if ( state == null )
					{
						if ( city != null && data.variables[6].equals(city) ) { list.add(data); }
						else if ( city == null ) { list.add(data); }
					}
				}
			}
		}
		return list;
	}
	
	// -----------------------------------------------------------------------
	//							Database code
	// -----------------------------------------------------------------------
	
	/**
	 *  Deletes all data in the table
	 */
	public void deleteTable( String table ) 
	{
//		Log.d( "DataHandler - deleteTable()", "Deleting "+table+" ... ");
		try
		{
			db.execSQL("DROP TABLE IF EXISTS " + table + ";" );
//			Log.d( "DataHandler - deleteTable()", "... Successful");
		}
		catch (SQLException sqle)
		{
			Log.d( "DataHandler - deleteTable()", table + " - " + sqle.getMessage() );
		}
	}
	
	// ----------------------- static vars for deleteAll() -----------------------
	private static Cursor tables;
	private static Cursor cursor;	
	// ---------------------------------------------------------------------------
	
	public void deleteAll()
	{
		try
		{
			Log.d( "deleteAll()", "Deleting all tables ..." );
//			Cursor tables = db.rawQuery( TABLES, null);
			tables = db.rawQuery( TABLES, null);
			if ( tables != null && tables.moveToFirst() )
	    	{
				String str = "";
	    		do
	    		{
	    			str = tables.getString(1);
	    			deleteTable( str );
	//    			Log.d("Deleting Table", str );
	    		}
	    		while ( tables.moveToNext() );
	    	}
			
			if (tables != null && !tables.isClosed())
	    	{
	    		tables.close();
	    	}
			Log.d( "deleteAll()", "... Finished" );
		}
		catch ( Exception e )
		{
			int len = e.getStackTrace().length;
			for ( int x = 0; x < len; x++ )
			{
				Log.e( "deleteAll()", e.getStackTrace()[x].toString() );
			}
		}
	}
	
	public void populateFromDeviceDB()
	{
		String [] record = null;
		try
		{
			Log.d( "POPULATE FROM DEVICE" , "Start...");
			tables = db.rawQuery( TABLES, null );
	    	if ( tables != null && tables.moveToFirst() ) 
	    	{
	    		do
	    		{
	    			if ( tables.getString(1).length() != 0 )
	    			{
		    			query = "SELECT * FROM " + tables.getString( 1 ).replaceAll("[, ]", "") + ";" ;
//		    			Log.d( "populate from device" , query + " :: " + tables.getString( 1 ) );   			
		    			try
		    			{
		    				cursor = db.rawQuery( query , null );
			    			if ( cursor.moveToFirst() )
			    			{
			    				do
			    				{
			    					record = new String[ cursor.getColumnCount() ];
	//		    					Log.i("POPULATE", cursor.getColumnCount()+"");
			    					if ( cursor.getColumnCount() == record.length )
			    					{
			    						int i = 0;
			    						int len = record.length;
				    					for ( int x = 0; x < len; x++ )
				    					{
	//			    						System.out.println( i + " :: " + cursor.getString(i) );
				    						record[x] = cursor.getString(i);
				    						i++;
				    					}
				    					DataItem item = new DataItem( record );
//				    					Log.i( "Record", item.variables[4] );
				    					this.latlonData.add( item );
			    					}
			    				}
			    				while ( cursor.moveToNext() );
			    			}
			    			else
			    			{
			    				Log.e("POPULATE FROM DEVICE", "Query Failed: " + query);
			    			}
		    			}
		    			catch( Exception e ) 
		    			{
		    				Log.e("POPULATE FROM DEVICE", "Failure: " + query );
		    				if ( cursor != null && !cursor.isClosed() ) 
		    				{
		    					cursor.close();
		    				}
		    				int len;
		    				if ( record != null)
		    				{
		    					len = record.length;
			    				for ( int x = 0; x < len; x++ )
			    				{
			    					Log.e( "Record", x + " :: " + record[x]);
			    				}
		    				}
		    				
		    				len = e.getStackTrace().length;
		    				for ( int x = 0; x < len; x++ )
		    				{
		    					Log.e( "Record", e.getStackTrace()[x].toString() );
		    				}
		    			}
		    			if ( cursor != null && !cursor.isClosed() ) 
		    			{
		    				cursor.close();
		    			}
	    			}
	    		}
	    		while( tables.moveToNext() );
	    	}
	    	Log.d( "POPULATE FROM DEVICE" , "...Finish");
		}
		catch ( Exception e )
		{
			int len = e.getStackTrace().length;
			for ( int x = 0; x < len; x++ )
			{
				Log.e( "ERROR POPULATING FROM DEVICE", e.getStackTrace()[x].toString() );
			}

			if (tables != null && !tables.isClosed())
	    	{
	    		tables.close();
	    	}
			// log error
			// send message to email about error
		}
		if ( tables != null && !tables.isClosed() )
    	{
    		tables.close();
    	}	
	}
	
	// ----------------------- static vars for updateFromServer -----------------------
	
	private static NodeList funds;
	private static String [] record;
	private static String city_state = "", toursite = "", partner = "", id = "";
	// --------------------------------------------------------------------------------
	/**
	 * To be implemented in part 3
	 */
	public void updateFromServer()
	{
		// counts the number of records
		int count = 0;
		try
		{
			Log.d( "UPDATE FROM SERVER" , "Start");
			
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        NodeList root = null;
	        DataItem tmp = null;
	        try 
	        {
	        	URL url = new URL( SITE_PATH );
	            URLConnection conn = url.openConnection();
	            // Using factory get an instance of document builder
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            Document dom;
	            // parse using builder to get DOM representation of the XML file
	            dom = db.parse( conn.getInputStream() );
	            // String file = "src/new.xml";
	            // System.out.println( file );
	            // dom = db.parse( new File( file ));
	            root = dom.getElementsByTagName("FundData");
			}
			catch(ParserConfigurationException pce) 
			{
			    Log.e("FROM SERVER", "PCE" + pce.getMessage() );
			}
			catch(SAXException se)  
			{
				Log.e("FROM SERVER", "SAX: " + se.getMessage() );
			}
			catch(IOException ioe) 
			{
				Log.e("FROM SERVER", "IO: "+ ioe.getMessage() );
			}
			// System.out.println( root.getLength() + " " + root.item(0).getNodeName());
			if ( root != null )
			{
				int len = root.getLength();
				String [] fields = null;
				for ( int x = 0; x < len; x++ )
				{
//					NodeList 
					funds = root.item(x).getChildNodes();
//					Log.i("UPDATE FROM SERVER", funds.getLength()+"" );
					record = new String[ funds.getLength() ];  
					
					// 44 fields - old fields before 12/1/12
					
					// Hung: new fields in database - after 12/1/12
//					record = new String[ 48 ];  
					
					fields = new String[ record.length ];
					// i is the number of fields or attributes for each record
					int i = 0;					 
//					if ( Integer.parseInt( funds.item(funds.getLength() - 1).getTextContent()) == 1)
					if ( Integer.parseInt( funds.item( 47 ).getTextContent() ) == 1 )
					{
						for (int y = 0; y < funds.getLength(); y++) 
						{
							/*
							if (funds.item(y).getNodeName().equals("partnername"))
							{
								partner = funds.item(y).getTextContent();
							}
							if (funds.item(y).getNodeName().equals("toursite"))
							{
								toursite = funds.item(y).getTextContent();
							} //*/
							if (!funds.item(y).getNodeName().equals("#text") 
//								&& !funds.item(y).getNodeName().equals("partnername")
//								&& !funds.item(y).getNodeName().equals("toursite")
//								&& !funds.item(y).getNodeName().equals("toursiteid")
//								&& !funds.item(y).getNodeName().equals("id")
								) 
							{
								//*
								if (funds.item(y).getNodeName().equals("city")) 
								{
									city_state = funds.item(y).getTextContent().replaceAll("[,]", "").replaceAll("[.]", "").replaceAll(" ", "").trim();
								}
								if (funds.item(y).getNodeName().equals("state")) 
								{
									city_state += "_" + funds.item(y).getTextContent().replaceAll("[,]", "").replaceAll("[.]", "").replaceAll(" ", "").trim();
								}//*/
//								if ( funds.item(y).getNodeName().equals("id") )
//								{
//									id = funds.item(y).getTextContent();
//									continue;
//								}
								
								
								if (funds.item(y).getTextContent().equals("Novalue") || 
									funds.item(y).getTextContent().trim().equals("") ||
									funds.item(y).getTextContent() == null ) 
								{
									if ( i == 5 || i == 12 ) // incase these values are null
									{
										record[i] = " ";
									}
									else
									{
										record[i] = null;
									}
//									Log.i( "Null" ,  "Record " + i + " null");
								}
								else
								{
									// handle weird symbols/char
									if (funds.item(y).getNodeName().equals( RECORD[5] ) ) 
									{
										record[i] = funds.item(y).getTextContent().replaceAll("[,]", "");
									}
									// handle city and state with weird symbols/characters
									else if ( funds.item(y).getNodeName().equals("city") || funds.item(y).getNodeName().equals("state") )
									{
										record[i] = funds.item(y).getTextContent().replaceAll("[,. ]", "");
									}
									// no weird symbols except: \n and "
									else
									{
										record[i] = funds.item(y).getTextContent().replaceAll("\n", " ").replaceAll("\"", "'");
									}
								}
//								Log.i( "Update Info", (i) + ": " + funds.item(y).getNodeName() + " : " + record[i]);
								i++;
							}
						}
//						record[i] = funds.item( funds.getLength()-1 ).getTextContent();
						city_state = city_state.toLowerCase().replaceAll(" ", "_");
//						Log.i("UPDATE", city_state);
						tmp = new DataItem( record );
//						Log.d( "Location name", tmp.variables[4]);
						// add to db for future retrieval
						insertRecord( city_state, tmp );
						// add to list
						this.latlonData.add( tmp );
						
						count++;
					}
				}
			}
			Log.d( "UPDATE FROM SERVER" , "Finish");
		}
		catch ( Exception e )
		{
			int len = e.getStackTrace().length;
			for ( int x = 0; x < len; x++ )
			{
				Log.e( "updateFromServer()", e.getStackTrace()[x].toString() );
			}
		}
		finally
		{
			Log.i( "updateFromServer()" , "Number of records :: " + count );
			PublicStaticValues.isCurrentData = true;
		}
	}
    
    /**
     * 
     * @param tableName
     */
    public boolean createTable( String tableName )
    {
    	boolean tableExists = false;
    	cursor = db.rawQuery("SELECT * FROM sqlite_master", null);
    	if ( cursor != null && cursor.moveToFirst() )
    	{
    		do
    		{
    			if ( cursor.getString(0).toLowerCase().equals( tableName.toLowerCase() ) )
    			{
    				tableExists = true;
    			}
    		}
    		while( cursor.moveToNext() );
    	}
    	if (cursor != null && !cursor.isClosed())
    	{
    		cursor.close();
    	}
    	if ( tableExists == false )
    	{
    		try
    		{
	    		db.execSQL( "CREATE TABLE '"+tableName.toLowerCase()+"'" + createTableQuery );
	    		return true;
//	    		Log.d("DatabHandler - createTable()", "creating " + tableName);
    		}
    		catch (SQLException sqle)
    		{
    			// commented out for easier debug
    			Log.d("DatabHandler - createTable()", "SQL error in creating table" );
    		}
    	}
    	else
    	{
    		Log.d("DatabHandler - createTable()", tableName + " exists");
    	}
    	return false;
    }
    
    // ------------------------ static vars for insertRecord -------------------------
    private static String query = "", update = "";
    private static ContentValues values; 
    // -------------------------------------------------------------------------------    
    /**
     * 
     * @param table
     * @param latlon
     * @param address
     * @param loc_name
     * @param comments
     * @param url
     * @param path
     */
    public void insertRecord( String table, DataItem record ) 
    {	
    	this.createTable(table);
    	query = 
    		"SELECT " +
	    		RECORD[0]  + ", " + RECORD[1]  + ", " + RECORD[2]  + ", " +
	    		RECORD[3]  + ", " + RECORD[4]  + ", " + RECORD[5]  + ", " +
	    		RECORD[6]  + ", " + RECORD[7]  + ", " + RECORD[8]  + ", " +
	    		RECORD[9]  + ", " + RECORD[10] + ", " + RECORD[11] + ", " +
	    		RECORD[12] + ", " + RECORD[13] + ", " + RECORD[14] + ", " +
	    		RECORD[15] + ", " + RECORD[16] + ", " + RECORD[17] + ", " +
	    		RECORD[18] + ", " + RECORD[19] + ", " + RECORD[20] + ", " +
	    		RECORD[21] + ", " + RECORD[22] + ", " + RECORD[23] + ", " +
	    		RECORD[24] + ", " + RECORD[25] + ", " + RECORD[26] + ", " +
	    		RECORD[27] + ", " + RECORD[28] + ", " + RECORD[29] + ", " +
	    		RECORD[30] + ", " + RECORD[31] + ", " + RECORD[32] + ", " +
	    		RECORD[33] + ", " + RECORD[34] + ", " + RECORD[35] + ", " +
	    		RECORD[36] + ", " + RECORD[37] + ", " + RECORD[38] + ", " +
	    		RECORD[39] + ", " + RECORD[40] + ", " + RECORD[41] + ", " +
	    		RECORD[42] + ", " + RECORD[43] + ", " + RECORD[44] + ", " +
	    		RECORD[45] + ", " + RECORD[46] + ", " + RECORD[47]	
		    	//
		    + " FROM " +
				//
		    	table 		
		    	//
		    + " WHERE " +	
		    	RECORD[0]	+ " = \"" + record.variables[4]		+ "\" AND " +
		    	RECORD[5] 	+ " = \"" + record.variables[5]		+ "\" AND " +
		    	RECORD[11]	+ " = \"" + record.lat				+ "\" AND " +
		    	RECORD[10]	+ " = \"" + record.lon				+ "\" AND " +
		    	RECORD[12]	+ " = \"" + record.variables[12]	+ "\";" ;
    	
//    	Log.d( "Query InsertRecord", query );
    	
    	cursor = db.rawQuery( query, null);
    	
    	values = new ContentValues();
    	
    	for ( int i = 0; i < RECORD.length; i++ )
    	{
    		values.put( RECORD[i], record.variables[i] );
    	}
    	
    	// check if the record is already in the database
    	if ( cursor.getCount() == 0 )
    	{
	    	try
	    	{
	    		if ( db.insertOrThrow( table, null, values ) == -1 )
	    		{	
	    			Log.d( "DataHandler - insertRecord()", "insert error" );
	    		}
	    	}
	    	catch ( SQLException sqle )
	    	{
	    		Log.e( "DataHandler - insertRecord()", sqle.getMessage() );
	    	}
    	}
    	else
    	{
    		update = 
    			"UPDATE "+ table + " " +
				"SET " + 	
					RECORD[4]  + " = \"" + record.variables[4]  + "\" , " + // name
					RECORD[12] + " = \"" + record.variables[12] + "\" , " + // type
					
					RECORD[16] + " = \"" + record.variables[16] + "\" , " + // basic 
					RECORD[17] + " = \"" + record.variables[17] + "\" , " + // more
					
					RECORD[18] + " = \"" + record.variables[18] + "\" , " + // web links
					RECORD[19] + " = \"" + record.variables[19] + "\" , " +
					RECORD[20] + " = \"" + record.variables[20] + "\" , " +
					RECORD[21] + " = \"" + record.variables[21] + "\" , " +
					RECORD[22] + " = \"" + record.variables[22] + "\" , " +
					RECORD[23] + " = \"" + record.variables[23] + "\" , " +
					
					RECORD[24] + " = \"" + record.variables[24] + "\" , " + // audio
					RECORD[25] + " = \"" + record.variables[25] + "\" , " +
					RECORD[26] + " = \"" + record.variables[26] + "\" , " +
					RECORD[27] + " = \"" + record.variables[27] + "\" , " +
					
					RECORD[28] + " = \"" + record.variables[28] + "\" , " + // pics
					RECORD[29] + " = \"" + record.variables[29] + "\" , " +
					RECORD[30] + " = \"" + record.variables[30] + "\" , " +
					RECORD[31] + " = \"" + record.variables[31] + "\" , " +
					RECORD[32] + " = \"" + record.variables[32] + "\" , " +
					RECORD[33] + " = \"" + record.variables[33] + "\" , " +
					RECORD[34] + " = \"" + record.variables[34] + "\" , " +
					RECORD[35] + " = \"" + record.variables[35] + "\" , " +
					RECORD[36] + " = \"" + record.variables[36] + "\" , " +
					
					RECORD[37] + " = \"" + record.variables[37] + "\" , " + // video
					RECORD[38] + " = \"" + record.variables[38] + "\" , " +
					RECORD[39] + " = \"" + record.variables[39] + "\" , " +
					RECORD[40] + " = \"" + record.variables[40] + "\" , " +
					RECORD[41] + " = \"" + record.variables[41] + "\" , " +
					RECORD[42] + " = \"" + record.variables[42] + "\" , " +
					RECORD[43] + " = \"" + record.variables[43] + "\" , " +
					RECORD[44] + " = \"" + record.variables[44] + "\" , " +
					RECORD[45] + " = \"" + record.variables[45] + "\" , " +
					
					RECORD[46] + " = \"" + record.variables[46] + "\" , " + // coupon
					RECORD[47] + " = \"" + record.variables[47] + "\" "     // status
					
				+ "WHERE " + 	
					RECORD[5] 	+ " = \"" + record.variables[5]	+ "\"";
    		
    		db.execSQL( update );
    	}
    	cursor.close();
    }
	
	public void createDatabase() 
	{
		boolean dbExist = checkDatabase();
		try
		{
	//		Log.d("Database", "Create database");
			if (!dbExist)
			{
				getWritableDatabase();
				
					copyDatabase();
				
				if ( db != null && db.isOpen() )
				{
					db.close();
				}
			}
		}
		catch ( Exception e )
		{
			int len = e.getStackTrace().length;
			for ( int x = 0; x < len; x++ )
			{
				Log.e( "createDatabase()", e.getStackTrace()[x].toString() );
			}
		}
	}
	
	/**
	 *  Checks if the database is open
	 * @return
	 */
	public boolean isOpen()
	{
		if (db != null)
		{
			return db.isOpen();
		}
		else
		{
			return false;
		}
	}
	
	/**
	 *  Credit: http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
	 *  
	 *  Checks if the database exists
	 * @return
	 */
	private boolean checkDatabase()
	{ 
		SQLiteDatabase db = null;
		try
		{
			db = SQLiteDatabase.openDatabase( DB_PATH.concat(DB_NAME) , null, SQLiteDatabase.OPEN_READWRITE);
		}
		catch (SQLiteException e)
		{
			int len = e.getStackTrace().length;
			for ( int x = 0; x < len; x++ )
			{
				Log.e( "checkDatabase()", e.getStackTrace()[x].toString() );
			}
		}
		if (db != null)
		{
			db.close();
		}
		return (db != null);
	}
	
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDatabase() throws IOException
    {
//    	Log.d("Database", "copyDatabase()");
    	//Open your local db as the input stream
    	InputStream myInput = context.getAssets().open( DB_NAME );
    	
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream( DB_PATH + DB_NAME );
    	
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer)) > 0 ) 
    	{
    		myOutput.write(buffer, 0, length);
    	}
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
	
	/**
	 *  Opens up a database with the specified path and database
	 * @throws SQLException
	 */
	public void openDatabase() throws SQLException
	{
    	//Open the database
//		Log.d("Database", "Open Database");
    	db = SQLiteDatabase.openOrCreateDatabase((DB_PATH + DB_NAME), null);
    }
	
	/**
	 * Closes the database being used 
	 */
	public void closeDatabase()
	{
//		Log.d("Database", "Close database if it is open");
		if ( db.isOpen() )
		{
			db.close();
			Log.i( "DataHandler", "Closing Database Access ... "); 
		}
		if ( this.isOpen() )
		{
			this.close();
		}
	}
	
	// ------------------------------- NOT USED ------------------------------------
	
	@Override
	public void onCreate(SQLiteDatabase db) { } 

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
	
	
	
}
