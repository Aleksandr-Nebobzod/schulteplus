/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.ui.schultesettings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceViewHolder;

import org.nebobrod.schulteplus.R;

public class DrawerPreference extends androidx.preference.Preference {
	SurfaceView surfaceView;

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

	public SurfaceView getSurfaceView() {
		return surfaceView;
	}

	@Override
	public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
		surfaceView = (SurfaceView) holder.findViewById(R.id.surface_view);
		super.onBindViewHolder(holder);
	}

}
