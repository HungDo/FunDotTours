package com.fundots.static_vals;

public class PublicStaticValues 
{
	public static final String DEV_ID_PREF = "dev_id";
	public static final String APP_ID_PREF = "app_id";
	public static final String APP_ID_PING = 
		"http://www.fundottours.com/index.php?option=com_aidots&sTask=stats&task=aa&partner_id=1&tour_id=1&dot_id=1&type=something&app_user_id=the_id&device_id=the_serial";
		//"http://www.tourthedots.com/index.php?option=com_aidots&sTask=stats&task=aa&partner_id=1&tour_id=1&dot_id=1&type=something&app_user_id=the_id&device_id=the_serial";
	public static final String FEATURED = 
		"http://www.tourthedots.com/index.php?option=com_aidots&sTask=featured&task=getlist";
	
	public static boolean isDoneDownloading = false;
	public static boolean isCurrentData = false;
}
