/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.dashboard;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.Identifiable;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Objects;


public class DashboardViewModel<TEntity extends Identifiable<String>> extends ViewModel {
	private static final String TAG = "DashboardViewModel";

	private final MutableLiveData<String> dashboardKey = new MutableLiveData<>();
	private final MutableLiveData<String> dashboardFilter = new MutableLiveData<>();
	//private final MutableLiveData<List<? extends ExResult>> resultsLiveData = new MutableLiveData<>();
	private final MutableLiveData<List<TEntity>> resultsLiveData = new MutableLiveData<>();
	// private final MutableLiveData<List<Achievement>> achievementsLiveData = new MutableLiveData<>();


	// Getting data from DB
	public void fetchLimitedData() {
/*		AppExecutors appExecutors = new AppExecutors();
		appExecutors.getDiskIO().execute(() -> {
		});*/

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
			resultsLiveData.postValue(result);
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

			new DataFirestoreRepo<TEntity>(entityClass).getListByField("exType", dashboardKey.getValue())
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							resultsLiveData.postValue(task.getResult());
						} else {
							Log.w(TAG, "onError: ", task.getException());
						}
					});
		}
	}


	// Getter for LiveData with dataset
	public LiveData<List<TEntity>> getResultsLiveData() {
		return resultsLiveData;
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