/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.schultesettings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceViewHolder;

import org.nebobrod.schulteplus.R;


/**
 * Custom Preference which allows set values by touching SurfaceView on {@link R.layout#fragment_probabilities}
 */
public class DrawerPreference extends androidx.preference.Preference {
	SurfaceView surfaceView;
	SurfaceViewCallback dsvCallback;

	/**
	 * These variety of constructors required for proper using in xml, they say
	 * @param context
	 */
	public DrawerPreference(@NonNull Context context) {
		super(context);
	}

	public DrawerPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public DrawerPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DrawerPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public interface SurfaceViewCallback {
		void onCallback(@Nullable SurfaceView sView);
	}

	public void getSurfaceView(final SurfaceViewCallback myCallback, SurfaceView sView) {
		myCallback.onCallback(sView);
		dsvCallback = myCallback;
	}
	public SurfaceView getSurfaceView() {
		return surfaceView;
	}

	@Override
	public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
		super.onBindViewHolder(holder);
		surfaceView = (SurfaceView) holder.findViewById(R.id.surface_view);
		if (dsvCallback != null) dsvCallback.onCallback(surfaceView);
	}
}
