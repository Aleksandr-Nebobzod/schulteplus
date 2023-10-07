package org.nebobrod.schulteplus;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class Utils {
	private static final String TAG = "Utils";

	/**
	 * Private constructor to prevent instantiation
	 */
	private Utils (){}

	/**
	 * transforms "" and null into 0
	 * @param s String
	 * @return
	 */
	public static int intFromString(String s) {
		int num=0;
		try
		{
			if(s != null)
				num = Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			num =  0;
		}
		return num;
	}

	/**
	 * @return current time as a String
	 */
	public static String getTimeStampNow() {
		Time time = new Time();
		time.setToNow();
		return time.format3339(false);
	}

	/**
	 * @param s -- is hugged with tag
	 * @return
	 */
	public static String bHtml(String s){ return "<b>" + s + "</b>";}
	public static String iHtml(String s){ return "<i>" + s + "</i>";}
	public static String uHtml(String s){ return "<u>" + s + "</u>";}
	public static String ttHtml(String s){ return "<tt>" + s + "</tt>";}
	public static String liHtml(String s){ return "<li>" + s + "</li>";}
	public static String cHtml(String s){ return "<font color='#FFAAAA'>" + s + "</font>";}
	/**
	 * Returns a CharSequence that applies a background color i.e.0xFF8B008B to the
	 * concatenation of the specified CharSequence objects.
	 */
	public static CharSequence c1Html(String s, int color ) {
		SpannableString sString = new SpannableString(s);
		sString.setSpan(new ForegroundColorSpan(color), 1, sString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		Log.d(TAG, "cHtml: " + sString);
		return sString.toString();
	}
	// Method overloading: in case of color missed it set to Red
	public static CharSequence c1Html(String s) {
		return c1Html(s,  0xFFFF0000);

	}
	public static String pHtml(){ return "<br>";}
	public static String tHtml(){ return "\u0009";}

	public static void openWebPage(String url, Context context) {

		Uri webpage = Uri.parse(url);

		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			webpage = Uri.parse("http://" + url);
		}

		Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
		if (intent.resolveActivity(context.getPackageManager()) != null) {
			context.startActivity(intent);
		}
	}

	public static final long timeStamp(){
		return (long) (Instant.now().getEpochSecond());
	}

	public static final String timeStampFormatted (long ts) {
		// use correct format ('S' for milliseconds)
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneId.systemDefault()).toString() + " " + ZoneId.systemDefault().toString();
	}



	public static  long transactID(){
		return UUID.randomUUID().timestamp();
	}

	public static int getVersionCode() {

		return intFromString(getRes().getString(R.string.app_version_num));

	}

	public static  android.content.res.Resources getRes() {
		return MainActivity.getInstance().getResources();
	}

}
