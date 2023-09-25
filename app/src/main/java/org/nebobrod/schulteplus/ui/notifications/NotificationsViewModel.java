package org.nebobrod.schulteplus.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.nebobrod.schulteplus.R;

public class NotificationsViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public NotificationsViewModel() {
		mText = new MutableLiveData<>();
	}

	public LiveData<String> getText() {
		return mText;
	}
}