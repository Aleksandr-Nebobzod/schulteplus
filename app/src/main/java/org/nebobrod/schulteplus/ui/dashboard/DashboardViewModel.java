/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.dashboard;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.common.AppExecutors;

import java.util.List;

public class DashboardViewModel extends ViewModel {
	private static final String TAG = "DashboardViewModel";

	private final MutableLiveData<String> dashboardKey = new MutableLiveData<>();
	private final MutableLiveData<String> dashboardFilter = new MutableLiveData<>();
	private final MutableLiveData<List<? extends ExResult>> resultsLiveData = new MutableLiveData<>();

	// Getting data from DB
	public void fetchResultsLimited(Class<? extends ExResult> clazz) {
		AppExecutors appExecutors = new AppExecutors();
		appExecutors.getDiskIO().execute(() -> {
//			Log.d(TAG, "fetchResultsLimited, is MainLooper1?: " + (Looper.myLooper() == Looper.getMainLooper()));
//			Log.d(TAG, "fetchResultsLimited, is MainLooper2?: " + (Looper.getMainLooper().getThread() == Thread.currentThread()));
			List<? extends ExResult> results = (new DataOrmRepo()).getListLimited(clazz, dashboardKey.getValue());
//			Log.d(TAG, "fetchResultsLimited: " + results);
			resultsLiveData.postValue(results);
		});
	}

	// Getter for LiveData with ExResult
	public LiveData<List<? extends ExResult>> getResultsLiveData() {
		return resultsLiveData;
	}

	// Other getters & setters
	public LiveData<String> getKey() {
		return dashboardKey;
	}
	public void setKey(String s) {
		dashboardKey.setValue(s);
		Toast.makeText(Utils.getAppContext(), s, Toast.LENGTH_SHORT).show();
	}

	public LiveData<String> getFilter() {
		return dashboardFilter;
	}
	public void setFilter(String s) {
		dashboardFilter.setValue(s);
		Toast.makeText(Utils.getAppContext(), s, Toast.LENGTH_SHORT).show();
	}
}