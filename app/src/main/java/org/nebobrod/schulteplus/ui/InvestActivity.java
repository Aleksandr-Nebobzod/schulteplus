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

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.google.android.material.button.MaterialButton;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.databinding.ActivityInvestBinding;
import org.nebobrod.schulteplus.databinding.ActivitySssrBinding;

public class InvestActivity extends AppCompatActivity {
	public static final String TAG = "InvestActivity";
	private ActivityInvestBinding binding;

	WebView wvContent;
	MaterialButton btnCancel, btnOk;

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
//				Button okButton = findViewById(R.id.ok_button);
				btnOk.setEnabled(allAnswered);
			});
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityInvestBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		// get exerciseId from the intent
		String exerciseId = getIntent().getStringExtra("exercise_id");

		// create html-path
		String htmlFilePath = "file:///android_asset/ru/test/" + exerciseId + ".html";

		// WebView settings
		wvContent = binding.wvContent;
		wvContent.getSettings().setJavaScriptEnabled(true);
		wvContent.getSettings().setDomStorageEnabled(true);
		wvContent.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface");

		// loading HTML
		wvContent.loadUrl(htmlFilePath);

		// Buttons
		btnCancel = binding.btnCancel;
		btnCancel.setOnClickListener(view -> finish());

		btnOk = binding.btnOk;
		btnOk.setEnabled(false);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				wvContent.evaluateJavascript("checkAllResults();", new ValueCallback<String>() {
					@Override
					public void onReceiveValue(String value) {
						// get checkAllResults from js
						boolean allCorrect = Boolean.parseBoolean(value);
						Log.d("Result", "answers as summary: " + allCorrect);

						if (allCorrect) {
							showSnackBarConfirmation(InvestActivity.this, getRes().getString(R.string.msg_tests_passed), null);
						} else {
							showSnackBarConfirmation(InvestActivity.this, getRes().getString(R.string.msg_tests_failed), null);
						}
					}
				});

			}
		});
	}
}
