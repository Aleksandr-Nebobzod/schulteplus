/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static org.nebobrod.schulteplus.Utils.animThrob;
import static org.nebobrod.schulteplus.Utils.getFirstDayOfWeek;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.interpolateColors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class SpCalendarView extends CalendarView {
	private static final String TAG = "SpCalendarView";
	private static final int CONTRIBUTION_LEVELS = 3;
	private static List<DayData> data;
	private int maxContribution;
//	private int lowColor = 	0x0100F0DD;
//	private int lowColor = 	0x01005C55;
//	private int highColor = 0xFF00F0DD;
//	private int lowColor = 	0xAAFFD5;
//	private int lowColor = 	0xD3DFD9;
	private int lowColor = 	0x01FFFFFF;
	private int highColor = 0x00F0DD;
	private int[] colors;
	private int colorBG;
	private TextView daySelected;
	private OnDateClickListener dateClickListener;

	public interface OnDateClickListener {
		void onDateClicked(LocalDate date);
	}

	public SpCalendarView(Context context) {
		super(context);
		init(context);
	}

	public SpCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SpCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	/** Data structure for marks on a DayViewContainer */
	public static class DayData {
		private final LocalDate localDate;
		private final int contribution;

		public DayData(LocalDate localDate, int contribution) {
			this.localDate = localDate;
			this.contribution = contribution;
		}
	}

	/** Date of Month cell container which acts as a view holder  */
	private class DayViewContainer extends ViewContainer {

		private final TextView v;
		private final MaterialCardView bg;

		public DayViewContainer(View view) {
			super(view);
			v = view.findViewById(R.id.calendar_day_text);
			bg = view.findViewById(R.id.calendar_day_bg);
			bg.setCardElevation(0);
		}

		public void bind(CalendarDay calendarDay) {
			if (calendarDay != null) {
				v.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

				// bold Current date
				if (calendarDay.getDate().equals(LocalDate.now())) {
					daySelected = v;
					daySelected.setTypeface(null, Typeface.BOLD);
//					daySelected.setBackground(getRes().getDrawable(R.drawable.ic_border, null));
				}

				// Red Weekends
				DayOfWeek dayOfWeek = calendarDay.getDate().getDayOfWeek();
				if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
					v.setTextColor(Color.RED);
				}

				// inDates and outDates are grayed
				if (calendarDay.getPosition() != DayPosition.MonthDate) {
					v.setTextColor(Color.GRAY);
				}

				// Colorized background if contributed
				if (maxContribution > 0) {
					int level = getDayContribution(calendarDay, data);
					if (level > 0) {
						bg.setCardBackgroundColor(colors[level]);
//						v.setBackgroundColor(colors[level]);
//						Log.d(TAG, calendarDay.getDate() + "bind level: " + level + " with bind color: " + Integer.toHexString(colors[level]));
					} else {
						bg.setCardBackgroundColor(colorBG);
//						v.setBackgroundColor(colorBG);
					}
				}

				// link Day click
				bg.setOnClickListener(v -> {
					if (dateClickListener != null ) {	// && calendarDay.getPosition() == DayPosition.MonthDate (limitation of current month)
						dateClickListener.onDateClicked(calendarDay.getDate());
						animThrob(v, null);
						if (!v.equals(daySelected)) {
							daySelected.setTypeface(null, Typeface.NORMAL);
//							daySelected.setBackground(null);
							daySelected = v.findViewById(R.id.calendar_day_text);
							daySelected.setTypeface(null, Typeface.BOLD);
//							daySelected.setBackground(getRes().getDrawable(R.drawable.ic_border, null));
						}
//						animFlip(v);
					}
				});

			} else {
				v.setText("");
			}
		}
	}

	/** NOT USED Day of week cell container which acts as a view holder for each  */
	private static class HeaderWeekDayViewContainer extends ViewContainer {

		private final TextView v;

		public HeaderWeekDayViewContainer(View view) {
			super(view);
			v = view.findViewById(R.id.calendar_day_text);
		}

		public void bind(CalendarDay calendarDay) {
			if (calendarDay != null) {
				v.setText(String.valueOf(calendarDay.getDate().getDayOfWeek()));
			} else {
				v.setText("");
			}
		}
	}

	/** Month name cell container which acts as a view holder */
	private static class HeaderMonthViewContainer extends ViewContainer {

		private final TextView v;
		private final MaterialCardView bg;

		public HeaderMonthViewContainer(View view) {
			super(view);
			v = view.findViewById(R.id.calendar_day_text);
			bg = view.findViewById(R.id.calendar_day_bg);
		}

		public void bind(CalendarMonth calendarMonth) {
			if (calendarMonth != null) {
				String monthYear = getRes().getString(R.string.lbl_exercises) + " "
						+ calendarMonth.getYearMonth().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())
						+ " '" +("" + calendarMonth.getYearMonth().getYear()).substring(2);
				v.setText(monthYear);
				v.setTypeface(null, Typeface.BOLD);
				bg.setCardBackgroundColor(Color.parseColor("#00000000")); // Transparent
				bg.setBackground(null);
				bg.setStrokeColor(0); 												// Transparent
				bg.setCardElevation(0F);
			} else {
				v.setText("");
			}
		}
	}

	private void init(Context context) {
		setupCalendar();
		colorBG = ContextCompat.getColor(context, R.color.transparent);
	}

	// Search data of Calendar for brightest colour
	private static int findMaxContribution(@Nullable List<DayData> data) {
		// Start checks
		if (data == null || data.isEmpty()) {
			return 0;
		}

		int maxContribution = 0; // we do not need Integer.MIN_VALUE here

		// Looking though
		for (DayData dayData : data) {
			if (dayData.contribution > maxContribution) {
				maxContribution = dayData.contribution;
			}
		}

		return maxContribution;
	}

	// define value of a day contribution [0-5]
	private int getDayContribution(CalendarDay calendarDay, @Nullable List<DayData> data) {
		// safety
		if (data == null || data.isEmpty()) {
			return 0;
		}

		int result;

		// define range base (and check minimal)
		int base = Math.max(this.maxContribution / CONTRIBUTION_LEVELS, 1);
		for (DayData dayData : data) {
			if (calendarDay.getDate().equals(dayData.localDate)) {
				result =  Math.min((dayData.contribution + base - 1) / base, CONTRIBUTION_LEVELS);
				// Log.d(TAG, "Contrib: " + calendarDay.getDate() + String.format(" max: %s so current %s gives %s ", maxContribution, dayData.contribution, result));
				return result;
			}
		}
		return 0;
	}

	/**
	 * Default setup
	 */
	public void setupCalendar() {
		setupCalendar(null, lowColor, highColor);
	}

	/**
	 * Setup for reflecting contribution
	 * @param data List<DayData>
	 * @param lowColor low contribution color
	 * @param highColor high contribution color
	 */
	public void setupCalendar(@Nullable List<DayData> data, int lowColor, int highColor) {

		// Today is default
		LocalDate today = LocalDate.now();
		// if both colors 0 then use default
		if (lowColor != 0 || highColor != 0) {
			this.lowColor = lowColor;
			this.highColor = highColor;
		}
		this.colors = interpolateColors(this.lowColor, this.highColor, CONTRIBUTION_LEVELS + 1);
		this.colors[0] = 0; // transparent for any

		SpCalendarView.data = data;
		this.maxContribution = findMaxContribution(data);

		// 2 months before
		LocalDate startDate = today.minusMonths(1);
		YearMonth startYearMonth = YearMonth.of(startDate.getYear(), startDate.getMonth());

		// 1 month after
		LocalDate endDate = today.plusMonths(1);
		YearMonth endYearMonth = YearMonth.of(endDate.getYear(), endDate.getMonth());

		this.setDateRange(startYearMonth,endYearMonth);

		this.setDayBinder(new MonthDayBinder<DayViewContainer>() {
			@NonNull
			@Override
			public DayViewContainer create(@NonNull View view) {
				return new DayViewContainer(view);
			}

			@Override
			public void bind(@NonNull DayViewContainer dayViewContainer, CalendarDay calendarDay) {
				dayViewContainer.bind(calendarDay);
			}
		});

		this.setMonthHeaderBinder(new MonthHeaderFooterBinder<HeaderMonthViewContainer>() {
			@NonNull
			@Override
			public HeaderMonthViewContainer create(@NonNull View view) {
				return new HeaderMonthViewContainer(view);
			}

			@Override
			public void bind(@NonNull HeaderMonthViewContainer container, CalendarMonth calendarMonth) {
				container.bind(calendarMonth);
			}
		});
	}

	/**
	 * Only refresh contributions changed
	 * @param data List<DayData>
	 */
	public void updateCalendar(List<DayData> data) {
		SpCalendarView.data = data;
		this.maxContribution = findMaxContribution(data);

//		this.getAdapter().notifyDataSetChanged(); // Update only for changed
		for (DayData date : data) {
			this.notifyDateChanged(date.localDate);
		}
	}

	public void setDateRange(YearMonth startMonth, YearMonth endMonth) {
		this.setup(startMonth, endMonth, getFirstDayOfWeek());
	}

	public void setOnDateClickListener(OnDateClickListener listener) {
		this.dateClickListener = listener;
	}
}

