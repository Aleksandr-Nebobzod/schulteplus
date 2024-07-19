/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.text.HtmlCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.common.hash.Hashing;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.nebobrod.schulteplus.common.Log;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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
	 * @return its numeric value
	 */
	public static int intFromString(String s) {
		int num = 0;
		try
		{
			if(s != null)
				num = Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			Log.e(TAG, "intFromString: ", e);
		}
		return num;
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

	/** UTC timestamp for central DB comparability */
	public static  long timeStampU(){
		return Instant.now().getEpochSecond();
	}

	public static  String timeStampFormattedLocal(long ts) {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"); // use correct format ('S' for milliseconds)
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),  ZoneId.systemDefault()).toString()  ;
	}
	public static  String timeStampLocal(long ts) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),  ZoneId.systemDefault()).format(formatter);
	}
	public static  String timeStampToDateLocal(long ts) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd"); // use correct format ('S' for milliseconds)
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),  ZoneId.systemDefault()).format(formatter);
	}
	public static  String timeStampToTimeLocal(long ts) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); // use correct format ('ss' fro seconds 'S' for milliseconds)
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts),  ZoneId.systemDefault()).format(formatter)  ;
	}
	public static  String timeStampFormattedShortUtc(long ts) {

//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"); // use correct format ('S' for milliseconds)
//		return LocalDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneId.systemDefault()).toString(); // toString(); ... + " " + ZoneId.systemDefault().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
		return Instant.ofEpochSecond(ts).toString(); // TODO: 20.12.2023 is it UTC? 
	}

	/**
	 * @return String H:M:S.ms
	 */
	public static String duration (long millis) {
		int s = (int) (millis / 1000);
		return String.format(Locale.ENGLISH, "%d:%02d:%02d.%03d", s / 3600, (s % 3600) / 60, (s % 60), millis % 1000);
	}

	/**
	 * @return String H:M:S
	 */
	public static String durationCut (long millis) {
		int s = (int) (millis / 1000);
		return String.format(Locale.ENGLISH, "%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
	}
	/**
	 * @return String 9999
	 */
	public static String durationMinutes (long millis) {
		int s = (int) (millis / 1000 / 60);
		return String.format(Locale.ENGLISH, "%d", s );
	}

	/**
	 * @return Timestamp value of universally unique identifier (UUID)
	 */
	public static  long transactID(){
		return UUID.randomUUID().timestamp();
	}

	/**
	 * @return Make an int value of universally unique identifier (UUID)
	 */
	public static  int generateUuidInt(){
		return UUID.randomUUID().hashCode();
	}

	/**
	 * @return String value of universally unique identifier (UUID)
	 */
	public static  String generateUuidString(){
		return UUID.randomUUID().toString();
	}

	/**
	 * @return String value of Hex string of int from UUID
	 */
	public static  String generateUak(){
		String strUuid = UUID.randomUUID().toString();
		int intUuid = intStringHash(strUuid);
		return Long.toString(intUuid & 0xFFFFFFFFL, 16);
	}

	/**
	 * @return integer value SHA1-hashcode of String
	 */
	public static  int intStringHash(String transform){
		return Hashing.sha1().hashString(transform, Charset.defaultCharset()).asInt();
	}

	/** Takes the app version from < gradleResValues.xml */
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
			Context context, String title, String message) {
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

	private static int getDialogPadding(Context context) {
		int[] sizeAttr = new int[] { android.R.attr.dialogPreferredPadding };
		TypedArray a = context.obtainStyledAttributes((new TypedValue()).data, sizeAttr);
		int size = a.getDimensionPixelSize(0, -1);
		a.recycle();

		return size;
	}

	/** Device ID */
	public static String getDevId() {
		return Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);
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

	public static void showSnackBar(String message) {

		try {
			View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
			Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE) // LENGTH_LONG
					.show();
		} catch (Exception e) {
			Log.e(TAG, "showSnackBar: " + e.getLocalizedMessage(), e);
		}
	}

	public static void showSnackBarConfirmation(Activity activity, String message, @Nullable View.OnClickListener okListener) {
		View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
		if (rootView == null) {
			Log.w(TAG, "showSnackBarConfirmation: Root view is null");
			return;
		}
		if (!rootView.isShown()) {
			Log.w("showSnackBarConfirmation", "Root view is not shown");
			return;
		}

		// Toast.makeText(activity, "Before Snackbar", Toast.LENGTH_SHORT).show();

		Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);

		// Ensure we have a listener
		if (okListener==null) { // It seems that this code is redundant
			okListener = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//  nothing
					snackbar.dismiss();
				}
			};
		}
		snackbar.setAction(getRes().getString(R.string.lbl_ok), okListener);

		// Set 7 rows allowed in snackbar
		View snackbarView = snackbar.getView();
		TextView tv= (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
		tv.setMaxLines(7);

		// Show
		snackbar.show();
		Log.d(TAG + " showSnackBarConfirmation", "Snackbar shown");
	}

	public static void showSnackBarConfirmation2(final Activity activity, final String message, @Nullable View.OnClickListener listener) {
		final View rootView = activity.findViewById(android.R.id.content);

		if (rootView == null) {
			Log.w(TAG, "showSnackBarConfirmation: Root view is null");
			return;
		}

		if (!rootView.isShown()) {
			Log.w(TAG, "showSnackBarConfirmation: Root view is not shown");
			return;
		}

		// Make listener final to access it inside inner class
		final View.OnClickListener finalListener = listener != null ? listener : new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// nothing
			}
		};

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);

				snackbar.setAction(activity.getString(R.string.lbl_ok), finalListener);

				View snackbarView = snackbar.getView();
				TextView tv = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
				tv.setMaxLines(7);

				snackbar.show();
				Log.d(TAG, "showSnackBarConfirmation: Snackbar shown");
			}
		}, 1000); // Delay of 1 second
	}


	/** Current Android version data */
	public static String currentOsVersion(){
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
		return codeName + " v" + release + ", API Level: " + Build.VERSION.SDK_INT;
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

		// TODO: 06.03.2024 tried to prevent clipping in gridView but it doesn't help
		scaleAnimation1.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				animation.setZAdjustment(Animation.ZORDER_TOP);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				animation.setZAdjustment(Animation.ZORDER_BOTTOM);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}
		});

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

	/**
	 * Rearrange Z value of the
	 * @param view chosen element in the viewGroup
	 * @param onTop if true (or sent back if false)
	 */
	public static void setViewZOrder(ViewGroup viewGroup, View view, boolean onTop) {
		int count = viewGroup.getChildCount();
		TextView child;

		for(int i = 0; i < count; i++) {
			child = (TextView) viewGroup.getChildAt(i);
			child.setZ(0.5F);
//			Log.d(TAG, i + ". animLayerTop: " + child.getText() +", Z: "+child.getZ());
		}
		view.setZ(onTop ? 1.0F : 0.0F);
		viewGroup.invalidate();
		// TODO: 06.03.2024 the first element still uncontrollable
	}

	/**
	 * Visualise appearance with animation
	 * @param myView  a previously invisible view.
	 * @link <a href="https://developer.android.com/develop/ui/views/animations/reveal-or-hide-view#Reveal">Create a circular reveal animation</a>
	 */
	public static void animVisiblate(View myView){
		// Check whether the runtime version is at least Android 5.0.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// Get the center for the clipping circle.
			int cx = myView.getWidth() / 2;
			int cy = myView.getHeight() / 2;

			// Get the final radius for the clipping circle.
			float finalRadius = (float) Math.hypot(cx, cy);

			// Create the animator for this view. The start radius is 0.
			Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius);

			// Make the view visible and start the animation.
			myView.setVisibility(View.VISIBLE);
			anim.start();
		} else {
			// Set the view to invisible without a circular reveal animation below
			// Android 5.0.
			myView.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Visualise disappearance with animation
	 * @param myView  a previously invisible view.
	 * @link <a href="https://developer.android.com/develop/ui/views/animations/reveal-or-hide-view#Reveal">Create a circular reveal animation</a>
	 */
	public static void animInvisiblate(View myView){
		// Check whether the runtime version is at least Android 5.0.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// Get the center for the clipping circle.
			int cx = myView.getWidth() / 2;
			int cy = myView.getHeight() / 2;

			// Get the initial radius for the clipping circle.
			float initialRadius = (float) Math.hypot(cx, cy);

			// Create the animation. The final radius is 0.
			Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0f);

			// Make the view invisible when the animation is done.
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					myView.setVisibility(View.INVISIBLE);
				}
			});

			// Start the animation.
			anim.start();
		} else {
			// Set the view to visible without a circular reveal animation below Android
			// 5.0.
			myView.setVisibility(View.VISIBLE);
		}
	}


	public static Context getAppContext() {
		return Utils.context;
	}

	public static Activity getActivity(Context context) {
		if (context == null) {
			return null;
		} else if (context instanceof ContextWrapper) {
			if (context instanceof Activity) {
				return (Activity) context;
			} else {
				return getActivity(((ContextWrapper) context).getBaseContext());
			}
		}

		return null;
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
	public static void setFbCrashlyticsUser(String uid) {
		FirebaseCrashlytics.getInstance().setUserId(uid);
	}

	/**
	 * Puts into FirebaseCrashlytics
	 * @param e recorded Exception
	 */
	public static void exceptionFbCrash(Exception e) {
		FirebaseCrashlytics.getInstance().recordException(e);
	}

	/**
	 * Modifies
	 * @param input List of an Object
	 * @param groupFieldName grouping by Date of TIMESTAMP_FIELD_NAME
	 * @return marked layoutFlag-field with G or H
	 */
	public static <T> List<T> markupListAsGroupedBy(List<T> input, String groupFieldName) {
		final String LAYOUT_FLAG_FIELD_NAME = "layoutFlag";
		final String LAYOUT_HEADER_FLAG = "H";
		final String LAYOUT_GROUP_FLAG = "G";

		// data source checks
		if (input == null) return input;
		if (input.size() <= 0) return input;

		// Define List-item's Class
		T item = input.get(0);
		Class<?> itemClass = item.getClass();
		Field groupField, layoutFlagField;

		// Check presence of a groupField and char layoutFlag field
		try {
			groupField = itemClass.getDeclaredField(groupFieldName);
			groupField.setAccessible(true); // Make the field accessible
			layoutFlagField = itemClass.getDeclaredField(LAYOUT_FLAG_FIELD_NAME);
			layoutFlagField.setAccessible(true); // Make the field accessible
		} catch (NoSuchFieldException e) {
			FirebaseCrashlytics.getInstance().recordException(e);
			e.printStackTrace();
			return input;
		}

		// Handle input collection sorting it by groupFieldName
		input.sort((o1, o2) -> {
			try {
//				Comparable fieldValue1 = (Comparable) groupField.get(o1);
//				Comparable fieldValue2 = (Comparable) groupField.get(o2);
//				return fieldValue1.compareTo(fieldValue2);
				String date1 = timeStampToDateLocal((Long) groupField.get(o1));
				String date2 = timeStampToDateLocal((Long) groupField.get(o2));
				return date2.compareTo(date1); // reverse sorting
			} catch (IllegalAccessException | NullPointerException e) {
				e.printStackTrace();
				return 0;
			}
		});

		// Manage Date grouping
		String previousValue = "";
		String currentValue;
		for (int i = 0; i < input.size(); i++) {
			try {
				currentValue = timeStampToDateLocal((Long) groupField.get(input.get(i)));
				if (!String.valueOf(currentValue).equals(previousValue)) {
					layoutFlagField.set(input.get(i), LAYOUT_GROUP_FLAG);
					previousValue = String.valueOf(currentValue);
				}
			} catch (IllegalAccessException | NullPointerException e) {
				e.printStackTrace();
			}
		}

		// Manage header
		try {
			layoutFlagField.set(input.get(0), LAYOUT_HEADER_FLAG);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return input;
	}

	/**
	 * @return screen wideness category from 1.small to 5.extra large<p> (0 and 9 are non-real)
	 */
	public static int getScreenFactor() {
		WindowManager windowManager = (WindowManager) getAppContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getRealMetrics(displayMetrics);

		float screenWidthPx = displayMetrics.widthPixels;
		float screenDensity = displayMetrics.density;

		// Screen width
		float screenWidthInches = screenWidthPx / (screenDensity * 160);

		// Screen width category
		int screenCategory;
		if (screenWidthInches >= 6.0F) {
			// Очень большие устройства (Extra Large)
			screenCategory = 5;
		} else if (screenWidthInches >= 4.0F) {
			// Большие устройства (Large)
			screenCategory = 4;
		} else if (screenWidthInches >= 3.2F) {
			// Средние устройства+ (Medium+)
			screenCategory = 3;
		} else if (screenWidthInches >= 2.0F) {
			// Средние устройства (Medium)
			screenCategory = 2;
		} else if (screenWidthInches >= 1.5F) {
			// Малые устройства (Small)
			screenCategory = 1;
		} else {
			// Сомнительные устройства (Doubtful)
			screenCategory = 0;
		}

		// Эмулированные устройства (Emulated)
		if (isEmulator()) {
			// screenCategory = 9;
		}
		return screenCategory;
	}

	/**
	 * Check if not physical device runs the App (and exclude cheating data from Statistics)
	 * @return
	 */
	public static boolean isEmulator() {
		return Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MANUFACTURER.contains("Genymotion")
				|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| "google_sdk".equals(Build.PRODUCT);
	}

	/**
	 * Gets a field from the project's BuildConfig. This is useful when, for example, flavors
	 * are used at the project level to set custom fields.
	 * @param fieldName     The name of the field-to-access
	 * @return              The value of the field, or {@code null} if the field is not found.
	 */
	public static Object getBuildConfigValue(String fieldName) {
		try {
			Class<?> clazz = Class.forName(context.getPackageName() + ".BuildConfig");
			Field field = clazz.getField(fieldName);
			return field.get(null);
		} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Shows content of html-file
	 * @param htmlSourceName string that keeps name of html-file for multi language purpose (like R.string.str_about_license_html_source)
	 */
	public static void displayHtmlAlertDialog(Context context1, @StringRes int htmlSourceName) {
		if (context1 instanceof Activity) {
			Activity activity = (Activity) context1;
			if (activity.isFinishing() || activity.isDestroyed()) {
				Log.w(TAG, "displayHtmlAlertDialog: Activity is not valid for displaying dialog.");
				return;
			}
		}

		// WebView and scale
		WebView view = (WebView) LayoutInflater.from(context1).inflate(R.layout.dialog_one_webview, null);
/*		WebSettings webSettings = view.getSettings();
		webSettings.setTextZoom(200);*/


		String fileName = getRes().getString(htmlSourceName);

		view.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.i("Utils.displayHtmlAlertDialog", "Page loaded successfully: " + url);

				Activity activity = (Activity) context1;
				if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
					Log.w("Utils.displayHtmlAlertDialog#onPageFinished", "Activity lost: " + context1);
					return;
				}

				view.getSettings().setTextZoom(170);
/*				androidx.appcompat.app.AlertDialog alertDialog =
						new AlertDialog.Builder(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
								.setView(view)
								.setPositiveButton(android.R.string.ok, null)
								.show();*/ // -- This variant shows smaller window

				// This one is fullscreen window
				Dialog dialog = new Dialog(context1, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
				dialog.setContentView(view);
				dialog.show();
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				super.onReceivedError(view, request, error);
				Log.w("Utils", "Error loading page: " + error.getDescription());
			}

			@Override
			public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
				super.onReceivedHttpError(view, request, errorResponse);
				Log.w("Utils", "HTTP error loading page: " + errorResponse.getStatusCode());
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				String url = request.getUrl().toString();
				if (url.startsWith("mailto:")) {
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse(url));
					if (intent.resolveActivity(context1.getPackageManager()) != null) {
						context1.startActivity(intent);
					} else {
						/* no-op */
					}
					return true;
				} else if (url.startsWith("http://") || url.startsWith("https://")) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					if (intent.resolveActivity(context1.getPackageManager()) != null) {
						context1.startActivity(intent);
					} else {
						/* no-op */
					}
					return true;
				} else {
					// if nor a "mailto:", neither "http://" or "https://"... WebView
					Log.w("Utils shouldOverrideUrlLoading", "strange URL: " + url);
					return false;
				}
			}
		});

		// To prevent IllegalStateException: The specified child already has a parent
/*		ViewParent parent = view.getParent();
		if (parent instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) parent;
			viewGroup.removeView(view);
		}*/

		view.loadUrl("file:///android_asset/" + fileName);
	}

	/**
	 * <b>"OK"</b> usually means Skip <p> when <b>"No"</b> means an Action.
	 * @param strMessage Output below results and feedback elements
	 */
	public static void confirmationDialog(Context newContext,
										  String title,
										  String strMessage,
										  @Nullable DialogInterface.OnClickListener okListener,
										  @Nullable DialogInterface.OnClickListener cancelListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(newContext, R.style.alertDialogTheme); 	// androidx.appcompat.Theme_AppCompat_Dialog_Alert

		final FrameLayout frameView = new FrameLayout(newContext);
		builder.setView(frameView);
		builder.setPositiveButton(Utils.getRes().getText(R.string.lbl_ok), null);

		final AlertDialog alertDialog = builder.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		alertDialog.getWindow().setDimAmount(0.2F);

		// Put the dialog layout to bottom of the screen
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
//		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		// Variables
		LayoutInflater inflater = alertDialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_result_feedback, frameView);
		TextView txtTitle, txtMessage;
		TableRow tbRow;
		TableLayout tbPsychometry;
		EditText etNote;
		Button btnOk, btnCancel;	// These template buttons are invisible on  inflated layout
		final Button[] btnRedesign = new Button[1];

		// Initiating controls
		txtTitle = layout.findViewById(R.id.txtTitle);
		tbRow = layout.findViewById(R.id.table_row);
		txtMessage = layout.findViewById(R.id.txtMessage);
		tbPsychometry = layout.findViewById(R.id.table_psychometry);
		etNote = layout.findViewById(R.id.et_note);
		btnCancel = layout.findViewById(R.id.btnCancel);
		btnOk = layout.findViewById(R.id.btnOK);

		txtTitle.setText(title);
		txtMessage.setText(HtmlCompat.fromHtml(strMessage, HtmlCompat.FROM_HTML_MODE_LEGACY));


		// hide template row of table
		tbRow.setVisibility(View.GONE);
		tbPsychometry.setVisibility(View.GONE);
		etNote.setVisibility(View.GONE);

		// Prepare listeners
		DialogInterface.OnClickListener voidListener = (DialogInterface dialogInterface, int i) -> {
			// Means continue ex i.e. do nothing
			alertDialog.dismiss();
		};

		if (cancelListener == null) cancelListener = voidListener;
		if (okListener == null) okListener = voidListener;

		// Set the buttons
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, Utils.getRes().getText(R.string.lbl_ok), (DialogInterface.OnClickListener) okListener);
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, Utils.getRes().getText(R.string.lbl_no), (DialogInterface.OnClickListener)  cancelListener);

		// Copy design from templates
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				// redesign OK by template
				btnRedesign[0] = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				btnRedesign[0].setLayoutParams(btnOk.getLayoutParams());
//				btnRedesign[0].setBackground(Utils.getRes().getDrawable(R.drawable.bg_button, newContext.getTheme()));
//				btnRedesign[0].setTextAppearance(R.style.button3d);
				btnRedesign[0].setBackgroundColor(getRes().getColor(R.color.light_grey_8, null));
				btnRedesign[0].setAllCaps(false);
				btnRedesign[0].setWidth(btnOk.getWidth()-10);
				// redesign Cancel by template
				btnRedesign[0] = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				btnRedesign[0].setLayoutParams(btnCancel.getLayoutParams());
//				btnRedesign[0].setBackground(AppCompatResources.getDrawable(newContext, R.drawable.bg_button));
//				btnRedesign[0].setTextAppearance(R.style.button3d);
				btnRedesign[0].setBackgroundColor(getRes().getColor(R.color.light_grey_8, null));
				btnRedesign[0].setAllCaps(false);
				btnRedesign[0].setWidth(btnCancel.getWidth()-10);
			}
		});
		alertDialog.show();
	}

	/** For using with any class */
	public static boolean hasFieldName(Class<?> clazz, String fieldName) {
		try {
			return (null != clazz.getDeclaredField(fieldName));
		} catch (NoSuchFieldException e) {
			return false;
		}
	}


	/**
	 * @param activity
	 * @return a rectangle of right-top corner of the screen (settings menu)
	 */
	public static Rect getTopRightCornerRect(Activity activity) {
		// Get screen size
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);

		// Размеры и координаты верхнего правого угла
		int screenWidth = displayMetrics.widthPixels;
		int statusBarHeight = getStatusBarHeight(activity);
		int rectSize = 100; // размер прямоугольника, например, 100 пикселей

		int left = screenWidth - rectSize;
		int top = statusBarHeight;
		int right = screenWidth;
		int bottom = statusBarHeight + rectSize;

		return new Rect(left, top, right, bottom);
	}

	private static int getStatusBarHeight(Activity activity) {
		int result = 0;
		int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = activity.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * Show dialog, disappearing after 3 sec and after that execute runnable
	 * @param context1
	 */
	public static void countdownDialog(Context context1, Runnable runnable) {
		AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context1);

		final FrameLayout frameView = new FrameLayout(context1);
		builder.setView(frameView);

		final AlertDialog alertDialog = builder.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		alertDialog.getWindow().setDimAmount(0.4F);

		// Put the dialog layout to center of the screen
		Window window = alertDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.CENTER;
		window.setAttributes(wlp);
		window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);


		// Variables
		LayoutInflater inflater = alertDialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_countdown, frameView);
		TextView counter;

		counter = layout.findViewById(R.id.tv_counter);

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
/*				final long startTime = System.currentTimeMillis();

				final Handler handler = new Handler();
				final Timer timer = new Timer();

				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						long currentTime = System.currentTimeMillis();
						long remainingTime = 4000 - (currentTime - startTime);
						int roundedTime = (int) Math.ceil(remainingTime / 1000);
						String newCounterText = String.valueOf(roundedTime);

						if (!counter.getText().toString().equals(newCounterText)) {
							counter.setText(newCounterText);
						}

						if (remainingTime == 0) {
							timer.cancel();
							alertDialog.dismiss();
							runnable.run();
						}
					}
				}, 0, 200); // Execute every 200 milliseconds (0.2 seconds)*/
				new Thread(new Runnable() {
					@Override
					public void run() {
						long timeLeft = 3900;
						long finishTime = System.currentTimeMillis() + timeLeft;

						while (timeLeft > 0) { // 4 seconds
							try {
								Thread.sleep(200); // 0.2 seconds
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							timeLeft = finishTime - System.currentTimeMillis();
							final long secondsElapsed = timeLeft / 1000;

							// Update the counter on the UI thread
							if (secondsElapsed != Integer.parseInt((String) counter.getText())) {
								((Activity) context1).runOnUiThread(new Runnable() {
									@Override
									public void run() {
										counter.setText(String.valueOf(secondsElapsed));
										animThrob(counter, null);
									}
								});
							}
						}

						// Dismiss the dialog and execute the runnable
						((Activity) context1).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								alertDialog.dismiss();
								runnable.run();
							}
						});
					}
				}).start();

			}
		});

		alertDialog.show();
	}

	/**
	 * Makes array of colors equally spread between start and end
	 * @param startColor
	 * @param endColor
	 * @param steps
	 * @return
	 */
	public static int[] interpolateColors(int startColor, int endColor, int steps) {
		int[] colors = new int[steps];

		float[] startRGB = new float[3];
		float[] endRGB = new float[3];

		Color.colorToHSV(startColor, startRGB);
		Color.colorToHSV(endColor, endRGB);

		for (int i = 0; i < steps; i++) {
			float ratio = (float) i / (steps - 1);
			int red = (int) ((Color.red(endColor) - Color.red(startColor)) * ratio + Color.red(startColor));
			int green = (int) ((Color.green(endColor) - Color.green(startColor)) * ratio + Color.green(startColor));
			int blue = (int) ((Color.blue(endColor) - Color.blue(startColor)) * ratio + Color.blue(startColor));
			colors[i] = Color.rgb(red, green, blue);
		}

		return colors;
	}
	public static String stringRepeat(String str, int times) {
		StringBuilder builder = new StringBuilder(times * str.length());
		for (int i = 0; i < times; i++) {
			builder.append(str);
		}
		return builder.toString();
	}

	/**
	 * THREE methods to get math brightness and contrast (Google requirements by W3C for WCAG 2.0)
	 * @param color1 as 0x88A0FF
	 * @param color2 as 0x7C160E
	 * @return
	 */
	public static double contrastRatio(int color1, int color2) {
		double luminance1 = relativeLuminance(color1);
		double luminance2 = relativeLuminance(color2);
		double brighter = Math.max(luminance1, luminance2);
		double darker = Math.min(luminance1, luminance2);
		double ratio = (brighter + 0.05) / (darker + 0.05);
		System.out.printf("Contrast Ratio: %.2f%n", ratio);
		return ratio;
	}

	/**
	 * by W3C for WCAG 2.0
	 * @param color
	 * @return
	 */
	public static double relativeLuminance(int color) {
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		return 0.2126 * channelLuminance(r) +
				0.7152 * channelLuminance(g) +
				0.0722 * channelLuminance(b);
	}

	/**
	 * @param channel one color 0-255
	 * @return Luminance by W3C for WCAG 2.0
	 */
	public static double channelLuminance(int channel) {
		double channelNormalized = channel / 255.0;
		return channelNormalized <= 0.03928
				? channelNormalized / 12.92
				: Math.pow((channelNormalized + 0.055) / 1.055, 2.4);
	}


}
