/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.showSnackBarConfirmation;
import static org.nebobrod.schulteplus.Utils.timeStampFormattedLocal;
import static org.nebobrod.schulteplus.Utils.timeStampU;
import static org.nebobrod.schulteplus.common.ExerciseRunner.timeStamp;
import static org.nebobrod.schulteplus.common.ExerciseRunner.uak;
import static org.nebobrod.schulteplus.common.ExerciseRunner.uid;
import static org.nebobrod.schulteplus.common.ExerciseRunner.userName;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.data.ExType;
import org.nebobrod.schulteplus.databinding.ActivityInvestBinding;
import org.nebobrod.schulteplus.databinding.ActivitySssrBinding;

public class InvestActivity extends AppCompatActivity {
	public static final String TAG = "InvestActivity";
	private ActivityInvestBinding binding;

	ExType exType;
	String htmlFilePath;
	String psyPrice = "";
	String psyBudget = "";
	WebView wvContent;
	private boolean isPageLoaded = false;
	MaterialButton btnCancel, btnOk;
	Achievement achievement;

	// JavaScript interaction-class
	public class WebAppInterface {
		Context mContext;

		WebAppInterface(Context c) {
			mContext = c;
		}

		// get data from JavaScript
		@JavascriptInterface
		public void onAllQuestionsAnswered(boolean allAnswered) {
			runOnUiThread(() -> {
				// enabling button by allAnswered-js-variable
				btnOk.setEnabled(allAnswered);
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityInvestBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		// WebView settings
		wvContent = binding.wvContent;
		wvContent.getSettings().setJavaScriptEnabled(true);
		wvContent.getSettings().setDomStorageEnabled(true);
		wvContent.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface");

		wvContent.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// Ok loaded
				isPageLoaded = true;
				// Send psy-coins to html
				if (url.contains("purchase.html")) {
					wvContent.evaluateJavascript("setPsyData('" + psyPrice + "', '" + psyBudget + "');", null);
				}
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				super.onReceivedError(view, request, error);

				// set empty page
				view.loadUrl("about:blank");
				Log.w(TAG, "setWebViewClient: " + "onReceivedError " + error);
				showSnackBarConfirmation(InvestActivity.this,
						getRes().getString(R.string.err_no_data),
						view1 -> finish());
			}

			// if external link clicked
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				String url = request.getUrl().toString();
				if (url.startsWith("http://") || url.startsWith("https://")) {
					// Open external browser
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					view.getContext().startActivity(intent);
					return true;
				}
				return false; // show link in WebView
			}

		});

		// get exerciseId from the intent
		String exerciseId = getIntent().getStringExtra("exercise_id");

		// Verify Achieved for loading html
		exType = ExerciseRunner.getExTypes().get(exerciseId);
		exType.refreshAchieved().addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void unused) {
				switch (exType.getRequiredAchievement()) {
					// Define html-path and Data
					case ExType.ACHIEVE_CERTIFIED:
						htmlFilePath = getRes().getString(R.string.str_test_html_source) + exerciseId + ".html";
						break;
					case ExType.ACHIEVE_PURCHASED:
						htmlFilePath = getRes().getString(R.string.str_test_html_source) + "purchase.html";
						psyPrice = String.valueOf(exType.getPrice() / 100);
						psyBudget = String.valueOf(ExerciseRunner.getUserHelper().getPsyCoins() / 100);
						break;
					default:
						Log.w(TAG, "refreshAchieved: " + "onSuccess NO case");
						finish();
				}
				// loading HTML
				wvContent.loadUrl(htmlFilePath);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.w(TAG, "refreshAchieved: " + "onFailure " + exerciseId);
				showSnackBarConfirmation(InvestActivity.this, getRes().getString(R.string.err_no_data), view1 -> finish());
			}
		});

		// Buttons
		btnCancel = binding.btnCancel;
		btnCancel.setOnClickListener(view -> finish());

		btnOk = binding.btnOk;
		btnOk.setEnabled(false);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isPageLoaded) {
					showSnackBarConfirmation(InvestActivity.this, getRes().getString(R.string.err_no_data), null);
					Log.w(TAG, "OK onClick: " + "html error " + htmlFilePath);
					return;
				}

				wvContent.evaluateJavascript("checkAllResults();", new ValueCallback<String>() {
					@Override
					public void onReceiveValue(String value) {
						// get checkAllResults from js
						boolean allCorrect = Boolean.parseBoolean(value);
						Log.d("Result", "answers as summary: " + allCorrect);

						if (allCorrect) {
							ExerciseRunner.setTimeStamp(timeStampU());
							achievement = new Achievement();

							if (htmlFilePath.contains("purchase.html")) {
								if (ExerciseRunner.psycoins < exType.getPrice() ) {
									// No psy-coins
									showSnackBarConfirmation(InvestActivity.this,
											getRes().getString(R.string.msg_no_psycoins),
											view1 -> finish());
								} else {
									// Investment passed
									achievement.set(uid, uak, userName, timeStamp,
											timeStampFormattedLocal(timeStamp),
											exType.getId(),
											Utils.getRes().getString(R.string.msg_tests_passed),
											psyPrice,
											exType.getRequiredAchievement());
									new DataOrmRepo<>(Achievement.class).create(achievement);
									ExerciseRunner.psycoins -= exType.getPrice();
								}
							} else {
								// Quiz passed
								achievement.set(uid, uak, userName, timeStamp,
										timeStampFormattedLocal(timeStamp),
										exType.getId(),
										Utils.getRes().getString(R.string.msg_tests_passed),
										"1",
										exType.getRequiredAchievement());
								new DataOrmRepo<>(Achievement.class).create(achievement);
							}
							ExerciseRunner.updateUserHelper();
							showSnackBarConfirmation(InvestActivity.this,
									getRes().getString(R.string.msg_psycoins_invested),
									view1 -> finish());
						} else {
							showSnackBarConfirmation(InvestActivity.this,
									getRes().getString(R.string.msg_tests_failed),
									view1 -> finish());
						}
					}
				});
			}
		});
	}
}
