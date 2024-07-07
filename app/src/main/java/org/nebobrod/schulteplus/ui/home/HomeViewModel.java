/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.home;


import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.getVersionCode;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.SnackBarManager;
import org.nebobrod.schulteplus.data.AdminNote;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.ui.SplashViewModel;

import java.util.List;

public class HomeViewModel extends ViewModel {
	private static final String TAG = "HomeViewModel";

	private final MutableLiveData<String> mText = new MutableLiveData<>();
	private final MutableLiveData<String> mNewsIndicator = new MutableLiveData<>();
	private final MutableLiveData<List<AdminNote>> mAdminNotes = new MutableLiveData<>();

	public LiveData<String> getText() {
		return mText;
	}

	public MutableLiveData<String> getmNewsIndicator() {
		return mNewsIndicator;
	}

	public MutableLiveData<List<AdminNote>> getmAdminNotes() {
		return mAdminNotes;
	}

	public void init() {

		// Get only unconfirmed Local Notes (they came during Splash)
		new DataOrmRepo<>(AdminNote.class).getListByField("timeStampConfirmed", DataRepository.WhereCond.EQ, "0").addOnCompleteListener(new OnCompleteListener<List<AdminNote>>() {
			@Override
			public void onComplete(@NonNull Task<List<AdminNote>> task) {
				if (task.isSuccessful()) {
					List<AdminNote> list = task.getResult();

					Log.d(TAG, "getListByField onComplete: " + list.size());

					// remove non-server and non-personal Notes
					for (int i = list.size() - 1; i >= 0; i--) {
						if (!list.get(i).getUak().equals("0")
								|| !(list.get(i).getUidAddress().equals(ExerciseRunner.GetUid())
									|| list.get(i).getUidAddress().equals("All")
									|| list.get(i).getUidAddress().equals(""))) {
							list.remove(i);
						}
					}
					Log.d(TAG, "getListByField after removal non-admin: " + list.size());

					// Update LiveData
					new Handler(Looper.getMainLooper()).post(() -> {
						if (list.size() != 0) {
							mAdminNotes.setValue(list);
							mNewsIndicator.setValue(getRes().getString(R.string.msg_news_reminder_yes));
						} else {
							mAdminNotes.setValue(null);
							mNewsIndicator.setValue(getRes().getString(R.string.msg_news_reminder_no));
						}
					});
				} else {
					Log.d(TAG, "getListByField onComplete: EMPTY");
					new Handler(Looper.getMainLooper()).post(() -> {
						mAdminNotes.setValue(null);
						mNewsIndicator.setValue(getRes().getString(R.string.msg_news_reminder_no));
					});
				}
			}
		});
	}
}