/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.nebobrod.schulteplus.R;

public class TapTargetViewWr {
	private TapTarget tapTarget;
	private Context context;

	public TapTargetViewWr(Activity activity, View targetView, String title, String description) {
		this.context = activity;
		this.tapTarget = TapTarget.forView(targetView, title, description);

		setDefaultSettings();
	}

	public TapTargetViewWr(Fragment fragment, View targetView, String title, String description) {
		this.context = fragment.getContext();
		this.tapTarget = TapTarget.forView(targetView, title, description);

		setDefaultSettings();
	}

	/** Default settings */
	private void setDefaultSettings() {
		this.tapTarget
				.outerCircleColor(R.color.light_grey_2_blue)	// Specify a color for the outer circle
				.outerCircleAlpha(0.96f)            			// Specify the alpha amount for the outer circle
				.targetCircleColor(R.color.light_blue_A400)		// Specify a color for the target circle
				.titleTextSize(20)							// Specify the size (in sp) of the title text
				.titleTextColor(R.color.light_grey_D)	// Specify the color of the title text
				.descriptionTextSize(14)					// Specify the size (in sp) of the description text
				.descriptionTextColor(R.color.light_grey_A_yellow)		// Specify the color of the description text
				//.textColor(R.color.light_grey_2_blue) 		// Specify a color for both the title and description text
				//.textTypeface(Typeface.MONOSPACE)				// Specify a typeface for the text
				.dimColor(R.color.black)						// If set, will dim behind the view with 30% opacity of the given color
				.drawShadow(true)								// Whether to draw a drop shadow or not
				.cancelable(false)						// Whether tapping outside the outer circle dismisses the view
				.tintTarget(true)								// Whether to tint the target view's color
				.transparentTarget(true)						// Specify whether the target is transparent (displays the content underneath)
				//.icon(ContextCompat.getDrawable(this, R.drawable.ic_circles_rb))                     // Specify a custom drawable to draw as the target
				.targetRadius(60);								// Specify the target radius (in dp);

	}

	public TapTarget getTapTarget() {
		return tapTarget;
	}

	public void show() {
		TapTargetView.showFor((Activity) context, tapTarget);
	}
}

/*
		TapTargetViewWr cTTView = new TapTargetViewWr(this,
				findViewById(R.id.fabLaunch),
				getString(R.string.hint_main_fab_title),
				getString(R.string.hint_main_fab_desc));
		cTTView.show();*/ // One call example
