/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.animBlink;
import static org.nebobrod.schulteplus.Utils.durationCut;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.colorMix;
import org.nebobrod.schulteplus.common.Log;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.data.ExResult;

import java.util.List;

public class ExResultCardViewAdapter extends RecyclerView.Adapter<ExResultCardViewAdapter.ExerciseViewHolder> {
	private static final String TAG = "ExResultCardViewAdapter";

	private List<ExResult> exerciseList;
	int colorNegativeEnergy;
	int colorPositiveEnergy;

	public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
		private CardView cvExResult;
		private ImageView exTypeIcon;
		private TextView exDescription;
		private TextView numValue;
		private TextView psyCoins;
		private TextView events;

		public TextView tvRating;

		public ExerciseViewHolder(View itemView) {
			super(itemView);
			cvExResult = itemView.findViewById(R.id.cv_ex_result);
			exTypeIcon = itemView.findViewById(R.id.ex_type_icon);
			exDescription = itemView.findViewById(R.id.ex_description);
			numValue = itemView.findViewById(R.id.ex_num_value);
			psyCoins = itemView.findViewById(R.id.ex_psy_coins);
			events = itemView.findViewById(R.id.ex_events);
			tvRating = itemView.findViewById(R.id.tv_rating);
		}
	}

	public ExResultCardViewAdapter(List<ExResult> exerciseList) {
		this.exerciseList = exerciseList;
	}

	@Override
	public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.cardview_exresult_list_item, parent, false);
		return new ExerciseViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ExerciseViewHolder holder, int position) {
		ExResult exResult = exerciseList.get(position);
		String[] exDesc = exResult.getExDescription().split("-");
		holder.exTypeIcon.setImageDrawable(this.getExTypeDrawable(exResult));
		holder.exDescription.setText(exResult.getExDescription());
		holder.numValue.setText(durationCut(exResult.getNumValue()));
		holder.psyCoins.setText(String.valueOf(exResult.calculatePsycoins()));
		holder.events.setText(String.valueOf(exResult.getTurns()));

		// see exDescription() in org.nebobrod.schulteplus.common.ExerciseRunner
		if (exDesc != null && "R".equals(exDesc[0])) {
			holder.tvRating.setTextColor(Color.RED);
			animBlink(holder.tvRating);
		} else {
			holder.tvRating.setTextColor(Color.TRANSPARENT);
		}

		// Update the background color based on levelOfEmotion and levelOfEnergy
		int colorOfEE = determineColorOfLevelsEE(exResult.getLevelOfEmotion(), exResult.getLevelOfEnergy());
		holder.cvExResult.setCardBackgroundColor(colorOfEE);
	}

	@Override
	public int getItemCount() {
		return exerciseList.size();
	}

	private Drawable getExTypeDrawable(ExResult exResult) {
		// Default
		Drawable dr = getRes().getDrawable(R.drawable.ic_baseline_check_box_24);
		String drName = "ic_" + exResult.getExType().substring(4);
		int drId;

		// Apply to resources by generated name
		try {
			drId = getRes().getIdentifier(drName, "drawable", getAppContext().getPackageName());
			dr = getRes().getDrawable(drId);
		} catch (Resources.NotFoundException e) {
			/*no-op*/
		}

		return dr;
	}

	/**
	 * Determine color based on levelOfEmotion and levelOfEnergy
	 */
	private int determineColorOfLevelsEE(int levelOfEmotion, int levelOfEnergy) {
		int result;
		// Set Energy color bounds
//		boolean isDarkTheme = (getRes().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
		colorNegativeEnergy = Color.parseColor("#ff333333"); // grey
		colorPositiveEnergy = Color.parseColor("#ffFFFFFF"); // White

		int emotionColor; /*= colorMix(colorNegativeEmotion, colorPositiveEmotion, (float) (levelOfEmotion + 2) / 4);*/
		switch (levelOfEmotion) {
			case -2:
				emotionColor = 0xff7777FF;	// Purple
				break;
			case -1:
				emotionColor = 0xff33AAFF;	// Blue
				break;
			case 0:
				emotionColor = 0xffAAFF77;	// Green
				break;
			case 1:
				emotionColor = 0xffFFF869;	// Yellow AAAA77
				break;
			case 2:
				emotionColor = 0xffFF7777;	// Red
				break;
			default:
				emotionColor = 0xff777777; // Safety
		}

		int energyColor = colorMix(colorNegativeEnergy, colorPositiveEnergy, (float) (levelOfEnergy + 1) / 2);

		// Final color
		result = colorMix(emotionColor, energyColor, 0.4f);
		Log.d(TAG, String.format("determineColorOfLevelsEE energyColor %s, colorMix %s", Integer.toHexString(energyColor), Integer.toHexString(result)));
		return result;
	}

}

