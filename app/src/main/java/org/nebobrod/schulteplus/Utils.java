package org.nebobrod.schulteplus;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public final class Utils extends Application {
	private static final String TAG = "Utils";
//	private static Utils mInstance;
	private static Context context;
	private static Resources res;


	/**
	 * Private constructor to prevent instantiation
	 */
//	private Utils (){}

	@Override
	public void onCreate() {
		super.onCreate();
//		mInstance = this;
		Utils.context = getApplicationContext();
		res = getResources();
	}

/*	public static App getInstance() {
		return mInstance;
	}*/

	public static Resources getRes() {
		return res;
	}

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
	 * @param s -- String to format
	 * @return is hugged with tag
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

	public static  long timeStamp(){
		return (long) (Instant.now().getEpochSecond());
	}

	public static  String timeStampFormattedLocal(long ts) {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"); // use correct format ('S' for milliseconds)
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),  ZoneId.systemDefault()).toString()  ;
	}
	public static  String timeStampLocal(long ts) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),  ZoneId.systemDefault()).format(formatter)  ;
	}
	public static  String timeStampDateLocal(long ts) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd"); // use correct format ('S' for milliseconds)
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),  ZoneId.systemDefault()).format(formatter)  ;
	}
	public static  String timeStampTimeLocal(long ts) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // use correct format ('S' for milliseconds)
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),  ZoneId.systemDefault()).format(formatter)  ;
	}
	public static  String timeStampFormattedShortUtc(long ts) {

//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"); // use correct format ('S' for milliseconds)
//		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneId.systemDefault()).toString(); // toString(); ... + " " + ZoneId.systemDefault().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
		return Instant.ofEpochSecond(ts).toString(); // TODO: 20.12.2023 is it UTC? 
	}



	public static  long transactID(){
		return UUID.randomUUID().timestamp();
	}

	public static int getVersionCode() {

		return intFromString(getRes().getString(R.string.app_version_num));

	}

/*	public static  android.content.res.Resources getRes() {
//		return MainActivity.getInstance().getResources();
		return Resources.getSystem();
	}*/

	public static String getRandomName(){
		String[] namesArray;

		namesArray = getRes().getStringArray(R.array.four_letters_nouns);

		Random r = new Random();

			int j = r.nextInt(namesArray.length);
			return namesArray[j];
	}


	public static ProgressBar progressBar;
	public static android.app.AlertDialog alertDialog;

	public static void progressBarShow(Activity activity){
		if (progressBar == null) {
//			progressBarCreate (activity);
			alertDialog = newProgressBar_AlertDialog(activity, "Wait", "loading...");
		}
//		progressBar.setIndeterminate(true);
		progressBar.setVisibility(View.VISIBLE);
		alertDialog.show();
	}

	public static void progressBarHide(Activity activity){
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
			alertDialog.hide();
		}
	}

	private static void progressBarCreate (Activity activity){
		RelativeLayout layout = new RelativeLayout(activity);
		progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleLarge); // progressBarStyleSmall , progressBarStyleLarge , progressBarStyleHorizontal
		progressBar.setIndeterminate(true);
		progressBar.setIndeterminateTintList(ColorStateList.valueOf(getRes().getColor(R.color.light_grey_6)));
//		progressBar.setAlpha(0.2f);
		progressBar.setBackgroundColor(getRes().getColor(R.color.transparent));
		progressBar.setBackgroundDrawable(getRes().getDrawable(R.drawable.bg_login02));
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.addView(progressBar,params);

		activity.setContentView(layout);
	}

	private static android.app.AlertDialog newProgressBar_AlertDialog(
			Context context, String title, String message)
	{
		progressBar =
				new ProgressBar(
						context,
						null,
						android.R.attr.progressBarStyleHorizontal);

		progressBar.setLayoutParams(
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));

		progressBar.setIndeterminate(true);

		final LinearLayout container =
				new LinearLayout(context);

		container.addView(progressBar);

		int padding =
				getDialogPadding(context);

		container.setPadding(
				padding, (message == null ? padding : 0), padding, 0);

		android.app.AlertDialog.Builder builder =
				new android.app.AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert).
						setTitle(title).
						setMessage(message)/*.
						setView(container)*/;

		return builder.create();
	}

	private static int getDialogPadding(Context context)
	{
		int[] sizeAttr = new int[] { android.R.attr.dialogPreferredPadding };
		TypedArray a = context.obtainStyledAttributes((new TypedValue()).data, sizeAttr);
		int size = a.getDimensionPixelSize(0, -1);
		a.recycle();

		return size;
	}

	public static String getDeviceId(Context context) {

		String id = Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);
		return id;
	}

	/**
	 * provides feedbacks on clicks or other view-actions
	 * @param view from which it runs
	 * @param sound true to play
	 * @param haptic true to vibrate
	 */
	public static void feedbacks(View view, boolean haptic, boolean sound) {

		if (haptic) view.performHapticFeedback(
				HapticFeedbackConstants.VIRTUAL_KEY,
				HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
		);
		if (sound) view.playSoundEffect(android.view.SoundEffectConstants.CLICK);

	}

	public static void showSnackBar(Activity activity, String message) {
//	public static void showSnackBar(String message) {
//		View view = getView(); // --
		View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
		Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE) // LENGTH_LONG
				.show();
	}

	public static void showSnackBarConfirmation(Activity activity, String message, @Nullable View.OnClickListener listener) {
		View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);

		Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);
		if (listener==null) { // It seems that this code is redundant
			listener = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					; //  nothing
				}
			};
		}

		snackbar.setAction(getRes().getString(R.string.lbl_ok), listener);

				View snackbarView = snackbar.getView();
		TextView tv= (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
		tv.setMaxLines(7);

		snackbar.show();
	}

	public static void resultDialog(Context context, String s, @Nullable DialogInterface.OnClickListener okListener, @Nullable DialogInterface.OnClickListener cancelListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		final FrameLayout frameView = new FrameLayout(context);
		builder.setView(frameView);
		final AlertDialog alertDialog = builder.create();

		//alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		alertDialog.getWindow().setDimAmount(0.5F);

		LayoutInflater inflater = alertDialog.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.activity_schulte_result_df, frameView);
		TextView txtTitle, txtMessage;
		Button btnOk, btnCancel;

		txtTitle = dialoglayout.findViewById(R.id.txtTitle);
		txtMessage = dialoglayout.findViewById(R.id.txtMessage);
		btnCancel = dialoglayout.findViewById(R.id.btnCancel);
		btnOk = dialoglayout.findViewById(R.id.btnOK);

		txtTitle.setText(R.string.title_result);
		txtMessage.setText(Html.fromHtml(s));

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getRes().getText(R.string.lbl_ok), okListener);
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getRes().getText(R.string.lbl_no), cancelListener);

		alertDialog.show();
	}


	//Current Android version data
	public static String currentVersion(){
		double release=Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)","$1"));
		String codeName="Unsupported";//below Jelly Bean
		if(release >= 4.1 && release < 4.4) codeName = "Jelly Bean";
		else if(release < 5)   codeName="Kit Kat";
		else if(release < 6)   codeName="Lollipop";
		else if(release < 7)   codeName="Marshmallow";
		else if(release < 8)   codeName="Nougat";
		else if(release < 9)   codeName="Oreo";
		else if(release < 10)  codeName="Pie";
		else if(release >= 10) codeName="Android "+((int)release);//since API 29 no more candy code names
		return codeName+" v"+release+", API Level: "+Build.VERSION.SDK_INT;
	}

	public static void animThrob(View view, @Nullable Color color) {
/*		view.animate().scaleX(2).setDuration(500).setStartDelay(0);
		view.animate().scaleX(1).setDuration(1000).setStartDelay(600);
		view.animate().scaleYBy(3F).scaleXBy(3F).setDuration(200).setStartDelay(0);
		view.animate()
				.scaleX(1F)
				.scaleXBy(0.75F)
				.scaleY(1F)
				.scaleYBy(0.75F)
				.setDuration(750)
				.setStartDelay(0);*/
//		ColorFilter colorFilter = view.getBackground().getColorFilter();
		ColorStateList colorBgBefore = view.getBackgroundTintList();
//		Drawable img = null; // keep as it was before anim
//		ColorFilter colorFilter = null;
//		final View tvBefore = view;

		if (color != null) {
//			view.setBackgroundTintList(color);
//			view.setBackgroundColor(getRes().getColor(R.color.light_grey_A_red));
//			((TextView)view).setTextColor(color.toArgb());
//			view.getBackground().setColorFilter(Color.parseColor("#ff8800");
//			drblCurrent.setTint(Color.RED);
//			drblCurrent.setTintList(ColorStateList.valueOf(getRes().getColor(R.color.light_grey_A_red)));
//			view.setBackground(drblCurrent);
//			view.getBackground().setColorFilter(Color.parseColor("#ff8800"), PorterDuff.Mode.SRC_ATOP);
//			view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.LIGHTEN);
//			view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

//			view.getBackground().setColorFilter(color.toArgb(), PorterDuff.Mode.SRC_IN);

		}

		AnimationSet aSet = new AnimationSet(true);

		ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation1.setDuration(200);
		aSet.addAnimation(scaleAnimation1);
		view.startAnimation(aSet);
		view.setBackgroundTintList(colorBgBefore);

/*		if (null != colorFilter) {
			img.setColorFilter(colorFilter);
			final Drawable img1 = img;
			view.postDelayed(new Runnable() {
				@Override
				public void run() {
					view = tvBefore;
				}
			}, 1000L);
		}*/
//		if (color != null)  view = tvBefore;
	}


	public static Context getAppContext() {
		return Utils.context;
	}

	// TODO: 28.02.2024 check author
	public static int getStringId(String stringId) {
		return getAppContext().getResources().getIdentifier(stringId, "string", getAppContext().getPackageName());
	}
	
	public static String getString(String stringId) {
		int sid = getStringId(stringId);
		if (sid > 0) {
			return getAppContext().getResources().getString(sid);
		} else {
			return "-";
		}
	}

	/**
	 * Puts into FirebaseCrashlytics
	 * @param message log-record
	 */
	public static void logFbCrash(String message) {
		FirebaseCrashlytics.getInstance().log(message);
	}

	/**
	 * Puts into FirebaseCrashlytics
	 * @param uid user identifier
	 */
	public static void userFbCrash(String uid) {
		FirebaseCrashlytics.getInstance().setUserId(uid);
	}

	/**
	 * Puts into FirebaseCrashlytics
	 * @param e recorded Exception
	 */
	public static void exceptionFbCrash(Exception e) {
		FirebaseCrashlytics.getInstance().recordException(e);
	}
}
