package org.nebobrod.schulteplus.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.nebobrod.schulteplus.data.DatabaseHelper;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.OrmRepo;
import org.nebobrod.schulteplus.fbservices.AppExecutors;

import java.util.List;

public class DashboardViewModel extends ViewModel {

	private final MutableLiveData<String> dashboardKey = new MutableLiveData<>();
	private final MutableLiveData<List<? extends ExResult>> resultsLiveData = new MutableLiveData<>();

	// Getting data from DB
	public void fetchResultsLimited(Class<? extends ExResult> clazz) {
		AppExecutors appExecutors = new AppExecutors();
		appExecutors.getDiskIO().execute(() -> {
			List<? extends ExResult> results = (new OrmRepo(DatabaseHelper.getHelper())).getResultsLimited(clazz);
			resultsLiveData.postValue(results);
		});
	}

	// Getter for LiveData with ExResult
	public LiveData<List<? extends ExResult>> getResultsLiveData() {
		return resultsLiveData;
	}


	public LiveData<String> getKey() {
		return dashboardKey;
	}
	public void setKey(String s) {
		dashboardKey.setValue(s);
	}
}