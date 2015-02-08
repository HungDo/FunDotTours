package com.fundots.tts;

import java.util.Locale;

//Alex import org.ispeech.SpeechSynthesis;
//Alex import org.ispeech.SpeechSynthesisEvent;
//Alex import org.ispeech.error.BusyException;
//Alex import org.ispeech.error.InvalidApiKeyException;
//Alex import org.ispeech.error.NoNetworkException;

import com.fundots.deploy.SettingsActivity;
import com.fundots.deploy.TourAppActivity;

import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;

public class FunDotsTTS 
{
	private static final String VOICES [] = 
	{ 	
		"usenglishfemale"	,
		"usenglishmale"		, 
		"ukenglishfemale"	,
		"ukenglishmale"
		// iSpeech voices ^^^ 
	}; 
	public static final boolean USE_ISPEECH = false;
	private static final String TAG = "TTS";
	private TextToSpeech speaker;
	//Alex private SpeechSynthesis tts;
	
	public FunDotsTTS( final TourAppActivity act )
	{
		try
		{
			initialize( act );
		}
		catch ( Exception e )
		{
			Log.e( "TTS Engine Error", "TTS Engine Error" );
		}
	}
	
	public void initialize( TourAppActivity act )
	{
    	speaker = new TextToSpeech( act, new TextToSpeech.OnInitListener()
    	{
    		//Alex @Override
			public void onInit(int status) 
			{
				// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
				if ( status == TextToSpeech.SUCCESS )
				{
					// Set preferred language to US english.
					// Note that a language may not be available, and the result will
					// indicate this.
					
					int result = speaker.setLanguage( Locale.US );
					// Try this someday for some interesting results.
					// int result mTts.setLanguage(Locale.FRANCE);
					if ( result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED )
					{
						// Lanuage data is missing or the language is not supported.
						Log.e( "TTS", "Language is not available." );
					}
				}
				else
				{
					// Initialization failed.
					Log.e( "TTS", "Could not initialize TextToSpeech." );
				}
			}
    	});//*/
		speaker.setSpeechRate(1.1f);
    	Log.i(TAG, "TTS Successful");
	}
	
	/**
	 *  Translates text to speech either using Google's API or iSpeech's API
	 * @param text
	 */
	public void speak( String text )
	{
		//*
		if ( USE_ISPEECH )
		{
			/** Alex 
			try 
			{
				if ( text != null )
				{
					if ( text.equals("") )
					{
						//Alex tts.stop();
					}
					else
					{
						//Alex tts.speak( Html.fromHtml( text ).toString() );
					}
				}
				else
				{
					//Alex tts.stop();
				}
			} 
			catch (BusyException e)
			{
				Log.e( "TTS speak()", "Busy Request" );
			} 
			catch (NoNetworkException e) 
			{
				Log.e( "TTS speak()", "No Network" );
			}//*/
		}
		else
		{
	    	//*
	    	if ( speaker != null )
	    	{
	    		if ( text.equals("") )
	    		{
	    			speaker.stop();
	    		}
	    		else
	    		{
	    			speaker.speak(text + ".", TextToSpeech.QUEUE_FLUSH, null);
	    		}
	    	} //*/
		}
	}
	
	/**
	 * 	This closes all TTS services either Google or iSpeech
	 */
	public void close()
	{
		if ( speaker != null )
		{
			speaker.shutdown();
			speaker = null;
		}
		/** Alex 
		if ( tts != null )
		{
			tts.stop();
			tts = null;
		}
		*/
		Log.i(TAG, "Shutdown TTS");
	}
	
}
