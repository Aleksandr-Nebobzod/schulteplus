package org.nebobrod.schulteplus.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.nebobrod.schulteplus.R;

public class DashboardViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public DashboardViewModel() {
		mText = new MutableLiveData<>();
	}

	public LiveData<String> getText() {
		return mText;
	}
}