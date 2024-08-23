/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.nebobrod.schulteplus.R;

public class KeyValueView extends LinearLayout {

	private TextView keyTextView;
	private TextView valueTextView;

	public KeyValueView(@NonNull Context context) {
		super(context);
		init(context, null);
	}

	public KeyValueView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public KeyValueView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.view_key_value, this, true);
		keyTextView = findViewById(R.id.key_text_view);
		valueTextView = findViewById(R.id.value_text_view);

		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KeyValueView, 0, 0);
			try {
				String keyText = a.getString(R.styleable.KeyValueView_keyText);
				String valueText = a.getString(R.styleable.KeyValueView_valueText);
				int keyWidthPercent = a.getInteger(R.styleable.KeyValueView_keyWidthPercent, 70);
				int valueAlign = a.getInt(R.styleable.KeyValueView_valueAlign, 2); // 0 - left, 1 - center, 2 - right

				keyTextView.setText(keyText);
				valueTextView.setText(valueText);

				// Set key width percentage
				LayoutParams keyParams = (LayoutParams) keyTextView.getLayoutParams();
				keyParams.width = (int) (getResources().getDisplayMetrics().widthPixels * keyWidthPercent / 100.0);
				keyTextView.setLayoutParams(keyParams);

				// Set value alignment
				switch (valueAlign) {
					case 1:
						valueTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
						break;
					case 2:
						valueTextView.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
						break;
					default:
						valueTextView.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
						break;
				}
			} finally {
				a.recycle();
			}
		}
	}

	public void setKeyText(String text) {
		keyTextView.setText(text);
	}

	public void setValueText(String text) {
		valueTextView.setText(text);
	}
}
