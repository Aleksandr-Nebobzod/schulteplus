package org.nebobrod.schulteplus.ui.home;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.nebobrod.schulteplus.R;

public class HomeViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public HomeViewModel() {
		mText = new MutableLiveData<>();
	}

	public LiveData<String> getText() {
		return mText;
	}
}