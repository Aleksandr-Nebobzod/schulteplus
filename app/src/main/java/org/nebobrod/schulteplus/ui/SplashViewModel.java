/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;


import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.getVersionCode;
import static org.nebobrod.schulteplus.Utils.intStringHash;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.NetworkConnectivity;
import org.nebobrod.schulteplus.data.AdminNote;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.data.UserHelper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.internal.LifecycleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashViewModel extends ViewModel {
	private static final String TAG = "SplashViewModel";
	private MutableLiveData<SplashState> splashState = new MutableLiveData<>();
	private MutableLiveData<InitialCheck> checkResult = new MutableLiveData<>();
	private MutableLiveData<UserHelper> userHelper = new MutableLiveData<>();
	private final ExecutorService executorService = Executors.newFixedThreadPool(4);
	AppExecutors appExecutors = new AppExecutors();

	public enum SplashState {
		START,
		APP_CHECK,
		USER_CHECK,
		NETWORK_CHECK,
		DATA_CHECK,
		FINISH,
		ERROR
	}

	public enum CheckType {
		APP,
		USER,
		NETWORK,
		DATA
	}

	public enum CheckResult {
		OK,
		WARN,
		ERROR
	}

	public static class InitialCheck {
		private final CheckType type;
		private final CheckResult result;
		private final String message;

		public InitialCheck(CheckType type, CheckResult result, String message) {
			this.type = type;
			this.result = result;
			this.message = message;
		}

		public CheckType getType() {
			return type;
		}

		public CheckResult getResult() {
			return result;
		}

		public String getMessage() {
			return message;
		}
	}

	public LiveData<SplashState> getSplashState() {
		return splashState;
	}

	public LiveData<InitialCheck> getCheckResult() {
		return checkResult;
	}

	public void postCheckResult(InitialCheck initialCheck) {
		this.checkResult.postValue(initialCheck);
	}

	public UserHelper getUserHelper() {
		return userHelper.getValue();
	}

	public void startSplashProcess() {
		Log.d(TAG, "startSplashProcess: ");
		splashState.setValue(SplashState.START);
		executorService.execute(this::checkApp);
		executorService.execute(this::checkUser);
		executorService.execute(this::checkNetwork);
		executorService.execute(this::checkData);
		// appExecutors.mainThread().execute(this::waitForAllChecks);
	}

	private void checkApp() {
		android.util.Log.d(TAG, "checkApp: ");
		splashState.postValue(SplashState.APP_CHECK);
		final CheckResult[] result = {CheckResult.ERROR};
		final String[] message = new String[1];

		new DataOrmRepo<>(AdminNote.class).getListByField("uak", DataRepository.WhereCond.EQ, "0").addOnCompleteListener(new OnCompleteListener<List<AdminNote>>() {
			@Override
			public void onComplete(@NonNull Task<List<AdminNote>> task) {
				if (task.isSuccessful()) {
					List<AdminNote> list = task.getResult();
					int verDeprecated = list.get(0).getVerDeprecated();
					int verDeprecating = list.get(0).getVerDeprecating();
					int verAppLatest = list.get(0).getVerAppLatest();
					if (getVersionCode() <= verDeprecated) {
						// no-op result ERROR
					} else if (getVersionCode() <= verDeprecating) {
						// warning but OK:
						result[0] = CheckResult.WARN;
						String msgTemplate = getRes().getString(R.string.msg_app_deprecating);
						message[0] = String.format(msgTemplate, verDeprecated, verAppLatest);
					} else {
						// version is OK:
						result[0] = CheckResult.OK;
					};

					// Update the necessary AdminNotes:
					long latestLocalTS = list.get(0).getTimeStamp();
					new DataRepos<>(AdminNote.class).fetchAdminNotes(latestLocalTS);

				} else {
					// no version records found -- considered as OK
					result[0] = CheckResult.OK;
					message[0] = "";

					// Update the all AdminNotes:
					new DataRepos<>(AdminNote.class).fetchAdminNotes(0L);
				}
			}
		});

		checkResult.postValue(new InitialCheck(CheckType.APP, result[0], message[0]));
	}

	private void checkUser() {
		android.util.Log.d(TAG, "checkUser: ");
		splashState.postValue(SplashState.USER_CHECK);
		final CheckResult[] result = {CheckResult.ERROR};
		final String[] message = new String[1];

		FirebaseAuth fbAuth = FirebaseAuth.getInstance();
		FirebaseUser user = fbAuth.getCurrentUser();
		if (user == null) {
			// No user detected so don't call UserFbData
			/* no-op */
		} else {
			String strMessage;
			String email = user.getEmail();

			new DataOrmRepo<>(UserHelper.class).read("" + intStringHash(user.getUid()))
					.addOnSuccessListener(new OnSuccessListener<UserHelper>() {
						@Override
						public void onSuccess(UserHelper userHelper) {
							if (userHelper.isVerified()) {
								result[0] = CheckResult.OK;
							} else if (user.isEmailVerified()) {
								result[0] = CheckResult.OK;
								userHelper.setVerified(true);
								new DataOrmRepo<>(UserHelper.class).create(userHelper);
							} else {
								result[0] = CheckResult.WARN;
							}
							message[0] = email + " " + getRes().getString(R.string.msg_user_logged_in);
						}
					});
		}
		checkResult.postValue(new InitialCheck(CheckType.USER, result[0], message[0]));
	}

	private void checkNetwork() {
		android.util.Log.d(TAG, "checkNetwork: ");
		splashState.postValue(SplashState.NETWORK_CHECK);
		final CheckResult[] result = {CheckResult.ERROR};
		final String[] message = new String[1];

		NetworkConnectivity networkConnectivity = new NetworkConnectivity(appExecutors, getAppContext());
		networkConnectivity.checkInternetConnection((isConnected) -> {
			if (isConnected) {
				result[0] = CheckResult.OK;
			}

		}, null);
		checkResult.postValue(new InitialCheck(CheckType.NETWORK, result[0], message[0]));
	}

	private void checkData() {
		android.util.Log.d(TAG, "checkData: ");
		splashState.postValue(SplashState.DATA_CHECK);
		final CheckResult[] result = {CheckResult.ERROR};
		final String[] message = new String[1];
		// Simulate data check
		result[0] = CheckResult.OK;
		checkResult.postValue(new InitialCheck(CheckType.DATA, result[0], message[0]));
	}

	/*private void waitForAllChecks() {
		Log.d(TAG, "waitForAllChecks: ");
		try {
			// Wait for all checks to complete
			CountDownLatch latch = new CountDownLatch(4);
			Observer<InitialCheck> observer = new Observer<InitialCheck>() {
				@Override
				public void onChanged(InitialCheck initialCheck) {
					android.util.Log.d(TAG, "waitForAllChecks, onChanged initialCheck: " + initialCheck.toString());
					latch.countDown();
					if (latch.getCount() == 0) {
						checkResult.removeObserver(this);
						splashState.postValue(SplashState.FINISH);
					}
				}
			};
			checkResult.observeForever(observer);
			latch.await();
		} catch (InterruptedException e) {
			//Thread.currentThread().interrupt();
			splashState.postValue(SplashState.ERROR);
		}
	}*/

}
