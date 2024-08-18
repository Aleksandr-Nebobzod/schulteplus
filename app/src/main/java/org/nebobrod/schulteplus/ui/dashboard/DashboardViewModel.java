/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.dashboard;

import static org.nebobrod.schulteplus.Utils.localDateOfTimeStamp;
import static org.nebobrod.schulteplus.Utils.timeStampPlusDays;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.Identifiable;
import org.nebobrod.schulteplus.data.fbservices.ConditionEntry;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;
import org.nebobrod.schulteplus.ui.SpCalendarView;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class DashboardViewModel<TEntity extends Identifiable<String>> extends ViewModel {
	private static final String TAG = "DashboardViewModel";

	// State data
	private final MutableLiveData<List<ExResult>> exCalendarLiveData = new MutableLiveData<>();
	private final MutableLiveData<List<SpCalendarView.DayData>> exContributionsLiveData = new MutableLiveData<>();
	private final MutableLiveData<Integer> daysLD = new MutableLiveData<>();
	private final MutableLiveData<Integer> psyCoinsLD = new MutableLiveData<>();

	// detailed list
	private final MutableLiveData<String> dashboardKey = new MutableLiveData<>();
	private final MutableLiveData<String> dashboardFilter = new MutableLiveData<>();
	private final MutableLiveData<List<Achievement>> achievLD = new MutableLiveData<>();
	private final MutableLiveData<List<TEntity>> exResultLD = new MutableLiveData<>();
	// private final MutableLiveData<List<Achievement>> achievementsLiveData = new MutableLiveData<>();


	public LiveData<List<ExResult>> getExCalendarLiveData() {
		return exCalendarLiveData;
	}

	public LiveData<List<SpCalendarView.DayData>> getExContributionsLiveData() {
		return exContributionsLiveData;
	}

	public MutableLiveData<Integer> getDaysLD() {
		return daysLD;
	}

	public MutableLiveData<Integer> getPsyCoinsLD() {
		return psyCoinsLD;
	}

	/**
	 * Getting exercise Calendar data for page 00State Fragment
	 */
	public void fetchExCalendar() {
		long ninetyDaysAgoTimestamp = timeStampPlusDays(-90);
//		ninetyDaysAgoTimestamp = 1723115774; // 2024-08-08T14:16:14"

		// Get all exResult records not older than 90 days for today
		new DataFirestoreRepo<ExResult>(ExResult.class).getListByField(
						new ConditionEntry(
								ExResult.TIMESTAMP_FIELD_NAME,
								DataRepository.WhereCond.GE,
								ninetyDaysAgoTimestamp),
						new ConditionEntry(
								ExResult.UID_FIELD_NAME,
								DataRepository.WhereCond.EQ,
								ExerciseRunner.getUserHelper().getUid()))
				.addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						List<ExResult> results = task.getResult();

						// Safety
						if (results == null) {
							results = new ArrayList<>();
						}
						// Put all data
						exCalendarLiveData.postValue(task.getResult());

						// Proceed calculations for Calendar and Stata
						List<SpCalendarView.DayData> dataList = new ArrayList<>();
						Integer psyCoins = 0;
						Set<LocalDate> uniqueDaysTrained = new HashSet<>();
						for (ExResult res : results) {
							dataList.add(new SpCalendarView.DayData(
									localDateOfTimeStamp(res.getTimeStamp()),
									res.getPsycoins()));
							uniqueDaysTrained.add(localDateOfTimeStamp(res.getTimeStamp()));
							psyCoins += res.getPsycoins();
						}
						// Put Calendar and Stata
						exContributionsLiveData.postValue(dataList);
						daysLD.postValue(uniqueDaysTrained.size());
						psyCoinsLD.postValue(psyCoins);
					} else {
						Log.w(TAG, "onError: ", task.getException());
					}
				});
	}

	public MutableLiveData<List<Achievement>> getAchievLD() {
		return achievLD;
	}

	/**
	 * Getting Achievements data for page 01
	 */
	public void fetchAchievements() {

		String _filter = Objects.requireNonNull(dashboardFilter.getValue());
		if (_filter.equals(Utils.getRes().getString(R.string.lbl_datasource_local))) {
			List<Achievement> result = (new DataOrmRepo(Achievement.class)).getListLimited(Achievement.class, dashboardKey.getValue());
//			Log.d(TAG, "fetchResultsLimited: " + results);
			achievLD.postValue(result);
		} else if (_filter.equals(Utils.getRes().getString(R.string.lbl_datasource_www))) {

			new DataFirestoreRepo<>(Achievement.class).getListByField()
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							achievLD.postValue(task.getResult());
						} else {
							Log.w(TAG, "fetchAchievements onError: ", task.getException());
						}
					});
		}
	}

	/**
	 * Getting ExResults data for page 02
	 */
	public void fetchExResults() {
		Class<? extends Identifiable<String>> clazz;
		if (dashboardKey.getValue().equals("gcb_achievements")) {
			clazz = Achievement.class;
		} else {
			clazz = ExResult.class;
		}

		// Apply to Class<TEntity>
		@SuppressWarnings("unchecked")
		Class<TEntity> entityClass = (Class<TEntity>) clazz;


		String _filter = Objects.requireNonNull(dashboardFilter.getValue());
		if (_filter.equals(Utils.getRes().getString(R.string.lbl_datasource_local))) {
			List<TEntity> result = (new DataOrmRepo(clazz)).getListLimited(clazz, dashboardKey.getValue());
//			Log.d(TAG, "fetchResultsLimited: " + results);
			exResultLD.postValue(result);
		} else if (_filter.equals(Utils.getRes().getString(R.string.lbl_datasource_www))) {
/*			new DataFirestoreRepo<TEntity>(entityClass).getListLimited(new DataRepository.RepoCallback<List<TEntity>>() {
				@Override
				public void onSuccess(List<TEntity> result) {
					resultsLiveData.postValue(result);
				}

				@Override
				public void onError(Exception e) {
					Log.w(TAG, "onError: ", e);
				}
			});*/ // that was first try with no filter

			new DataFirestoreRepo<TEntity>(entityClass).getListByField(
					new ConditionEntry("exType", DataRepository.WhereCond.EQ, dashboardKey.getValue()))
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							exResultLD.postValue(task.getResult());
						} else {
							Log.w(TAG, "onError: ", task.getException());
						}
					});
		}
	}


	// Getter for LiveData with dataset
	public LiveData<List<TEntity>> getExResultLD() {
		return exResultLD;
	}

	// Other getters & setters
	public LiveData<String> getKey() {
		return dashboardKey;
	}

	public void setKey(String s) {
		dashboardKey.setValue(s);
		Log.d(TAG, "setFilter: " + s);
	}

	public LiveData<String> getFilter() {
		return dashboardFilter;
	}
	public void setFilter(String s) {
		dashboardFilter.setValue(s);
		Log.d(TAG, "setFilter: " + s);
	}
}