package org.nebobrod.schulteplus;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.nebobrod.schulteplus.data.ExResult;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
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

	public static  long timeStampU(){
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

		// Ensure we have a listener
		if (listener==null) { // It seems that this code is redundant
			listener = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//  nothing
					snackbar.dismiss();
				}
			};
		}
		snackbar.setAction(getRes().getString(R.string.lbl_ok), listener);

		// Set 7 rows allowed in snackbar
		View snackbarView = snackbar.getView();
		TextView tv= (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
		tv.setMaxLines(7);

		// Show
		snackbar.show();
	}

	/**
	 * <b>"OK"</b> means Continue or restart <p> when <b>"No"</b> means stop the parent Activity and exit.
	 * @param context1 really necessary as SchulteActivity02.this
	 * @param resultLiveData ExResult (for set of key-value Pairs to fill internal result table)
	 * @param strMessage
	 * @param okListener
	 * @param cancelListener
	 */
	public static void resultDialog(Context context1,
									MutableLiveData<ExResult> resultLiveData,
									String strMessage,
									@Nullable DialogInterface.OnClickListener okListener,
									@Nullable DialogInterface.OnClickListener cancelListener) {
		AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context1);
//		, androidx.appcompat.R.style.Theme_AppCompat_Dialog
//		, androidx.appcompat.R.attr.dialogTheme);

		ExResult resultClone;
		Map<String, String> resultsMap = null;

		if (resultLiveData != null) {
			resultClone = resultLiveData.getValue();
			resultsMap = resultClone.toMap();
		} else {
			resultClone =  new ExResult();
		}


		final FrameLayout frameView = new FrameLayout(context1);
		builder.setView(frameView);
		builder.setPositiveButton(getRes().getText(R.string.lbl_ok), null);

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
		View layout = inflater.inflate(R.layout.activity_schulte_result_df, frameView);
		TextView txtTitle, txtMessage;
		TableLayout tb;
		TextView txtKey, txtValue, txtKeyNew, txtValueNew;
		TableRow tbRow, tbRowNew;
		TableLayout tbPsychometry;
		EditText etNote;
		SwitchCompat switchDataProvided;
		SeekBar sbEmotionalLevel, sbEnergyLevel;
		SeekBar.OnSeekBarChangeListener seekBarChangeListener;
		Button btnOk, btnCancel;	// These template buttons are invisible on  inflated layout
		final Button[] btnRedesign = new Button[1];

		// Initiating controls
		txtTitle = layout.findViewById(R.id.txtTitle);
		tb = layout.findViewById(R.id.table_layout);
		tbRow = layout.findViewById(R.id.table_row);
		txtKey = layout.findViewById(R.id.tv_key1);
		txtValue = layout.findViewById(R.id.tv_value1);
		txtMessage = layout.findViewById(R.id.txtMessage);
		tbPsychometry = layout.findViewById(R.id.table_psychometry);
		etNote = layout.findViewById(R.id.et_note);
		switchDataProvided = layout.findViewById(R.id.sw_data_provided);
		sbEmotionalLevel = layout.findViewById(R.id.sb_emotion);
		sbEnergyLevel = layout.findViewById(R.id.sb_energy);
		btnCancel = layout.findViewById(R.id.btnCancel);
		btnOk = layout.findViewById(R.id.btnOK);

		txtTitle.setText(R.string.title_result);
		txtMessage.setText(Html.fromHtml(strMessage));

		if (resultsMap != null) {
			// Each result pair key-value put into textviews of new row
			for (Map.Entry<String, String> entry : resultsMap.entrySet()) {
				tbRowNew = new TableRow(context);
				tbRowNew.setLayoutParams(tbRow.getLayoutParams());
				// Add textviews to new table row
				txtKeyNew = new TextView(context);
					txtKeyNew.setLayoutParams(txtKey.getLayoutParams());
					txtKeyNew.setText(entry.getKey());
					tbRowNew.addView(txtKeyNew);
				txtValueNew = new TextView(context);
					txtValueNew.setLayoutParams(txtValue.getLayoutParams());
					txtValueNew.setText(entry.getValue());
					tbRowNew.addView(txtValueNew);
				// Add new row
				tb.addView(tbRowNew);
			}
		} else {
			tbPsychometry.setVisibility(View.GONE);
		}
		// hide template row of table
		tbRow.setVisibility(View.GONE);

		// Prepare listeners
		DialogInterface.OnClickListener voidListener = (DialogInterface dialogInterface, int i) -> {
			// Means continue ex i.e. do nothing
			alertDialog.dismiss();
		};

		if (cancelListener == null) cancelListener = voidListener;
		if (okListener == null) okListener = voidListener;


//		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getRes().getText(R.string.lbl_ok), (Message) null);
//		Button btnPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getRes().getText(R.string.lbl_ok), (DialogInterface.OnClickListener) okListener);
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getRes().getText(R.string.lbl_no), (DialogInterface.OnClickListener)  cancelListener);

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				// redesign OK by template
				btnRedesign[0] = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				btnRedesign[0].setLayoutParams(btnOk.getLayoutParams());
				btnRedesign[0].setBackground(getRes().getDrawable(R.drawable.bg_button));
				btnRedesign[0].setTextAppearance(R.style.button3d);
				btnRedesign[0].setAllCaps(false);
				btnRedesign[0].setWidth(btnOk.getWidth()-10);
				// redesign Cancel by template
				btnRedesign[0] = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				btnRedesign[0].setLayoutParams(btnCancel.getLayoutParams());
				btnRedesign[0].setBackground(getRes().getDrawable(R.drawable.bg_button));
				btnRedesign[0].setTextAppearance(R.style.button3d);
				btnRedesign[0].setAllCaps(false);
				btnRedesign[0].setWidth(btnCancel.getWidth()-10);
			}
		});

		// check if notes entered manually
		etNote.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable editable) {
				if (!editable.toString().equals("")) switchDataProvided.setChecked(true);
			}

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
		});

		switchDataProvided.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (tbPsychometry.getVisibility() == View.GONE) return;

				// Update Views and LiveData
				sbEmotionalLevel.setThumbTintList(context1.getColorStateList(R.color.light_grey_A_green));
				sbEnergyLevel.setThumbTintList(context1.getColorStateList(R.color.light_grey_A_green));
				alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.lbl_no_ok);
				resultClone.setNote(etNote.getText().toString());
				resultClone.setLevelOfEmotion(sbEmotionalLevel.getProgress()-2);
				resultClone.setLevelOfEnergy(sbEnergyLevel.getProgress()-1);
				resultLiveData.setValue(resultClone);
				switchDataProvided.setChecked(false);
			}
		});

		seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				switchDataProvided.setChecked(true);
			}
			// Not used:
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		};

		sbEmotionalLevel.setOnSeekBarChangeListener(seekBarChangeListener); // Same listener
		sbEnergyLevel.setOnSeekBarChangeListener(seekBarChangeListener);

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
