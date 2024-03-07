package org.nebobrod.schulteplus.ui.dashboard;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.DatabaseHelper;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.OrmRepo;
import org.nebobrod.schulteplus.fbservices.AppExecutors;

import java.util.List;

public class DashboardViewModel extends ViewModel {
	private static final String TAG = "DashboardViewModel";

	private final MutableLiveData<String> dashboardKey = new MutableLiveData<>("gcb_achievements");
	private final MutableLiveData<List<? extends ExResult>> resultsLiveData = new MutableLiveData<>();

	// Getting data from DB
	public void fetchResultsLimited(Class<? extends ExResult> clazz) {
		AppExecutors appExecutors = new AppExecutors();
		appExecutors.getDiskIO().execute(() -> {
			Log.d(TAG, "fetchResultsLimited, is MainLooper1?: " + (Looper.myLooper() == Looper.getMainLooper()));
			Log.d(TAG, "fetchResultsLimited, is MainLooper2?: " + (Looper.getMainLooper().getThread() == Thread.currentThread()));
			List<? extends ExResult> results = (new OrmRepo()).getResultsLimited(clazz, dashboardKey.getValue());
//			Log.d(TAG, "fetchResultsLimited: " + results);
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
		Toast.makeText(Utils.getAppContext(), s, Toast.LENGTH_SHORT).show();
	}
}