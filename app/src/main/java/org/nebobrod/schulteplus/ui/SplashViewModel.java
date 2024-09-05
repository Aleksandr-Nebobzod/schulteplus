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
import static org.nebobrod.schulteplus.Utils.timeStampU;

import android.os.Handler;
import android.os.Looper;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class SplashViewModel extends ViewModel {
	private static final String TAG = "SplashViewModel";
	private static final long SPLASH_STEP_TIME = 300;
	private static final long TEST_TIME_ALLOWED = 8000L; // 8 sec is maximum splash time

	private MutableLiveData<SplashState> splashState = new MutableLiveData<>();
	private MutableLiveData<InitialCheck> checkResult = new MutableLiveData<>();
	private MutableLiveData<UserHelper> userHelperLD = new MutableLiveData<>();
	private Handler mainHandler = new Handler(Looper.getMainLooper());
	@Inject
	NetworkConnectivity networkConnectivity;
	@Inject
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
		DATA,
		TIME
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

		@Override
		public String toString() {
			return "InitialCheck{" +
					"type=" + type +
					", result=" + result +
					", message='" + message + '\'' +
					'}';
		}
	}

	/***********getters and setters**********/
	public LiveData<SplashState> getSplashState() {
		return splashState;
	}

	public LiveData<InitialCheck> getCheckResult() {
		return checkResult;
	}

	public void postCheckResult(InitialCheck initialCheck) {
		this.checkResult.postValue(initialCheck);
	}

	public MutableLiveData<UserHelper> getUserHelperLD() {
		return userHelperLD;
	}

	/**
	 * This method will be called when this ViewModel is no longer used and will be destroyed.
	 * <p>
	 * It is useful when ViewModel observes some data and you need to clear this subscription to
	 * prevent a leak of this ViewModel.
	 */
	@Override
	protected void onCleared() {
		// Cancel ongoing tasks, close connections, etc.
		if (appExecutors != null) {
			appExecutors = null;
		}
		Log.v(TAG, "ViewModel cleared");
		super.onCleared();
	}

	/** initializing */
	public void startSplashProcess() {
		Log.v(TAG, "startSplashProcess: ");
		splashState.setValue(SplashState.START);
		//checkData();
		//checkTime();
		mainHandler.postDelayed(() -> appExecutors.getDiskIO().execute(this::checkApp), 0 * SPLASH_STEP_TIME);
		mainHandler.postDelayed(() -> appExecutors.getDiskIO().execute(this::checkUser), 1 * SPLASH_STEP_TIME);
		mainHandler.postDelayed(() -> appExecutors.getDiskIO().execute(this::checkNetwork), 2 * SPLASH_STEP_TIME);
		mainHandler.postDelayed(() -> appExecutors.getDiskIO().execute(this::checkData), 5 * SPLASH_STEP_TIME);
		mainHandler.postDelayed(() -> appExecutors.getDiskIO().execute(this::checkTime), 7 * SPLASH_STEP_TIME);
/*		appExecutors.getNetworkIO().execute(this::checkApp);
		appExecutors.getNetworkIO().execute(this::checkUser);
		appExecutors.getNetworkIO().execute(this::checkNetwork);
		appExecutors.getDiskIO().execute(this::checkData);
		appExecutors.getDiskIO().execute(this::checkTime);*/
/*		executorService.execute(this::checkApp);
		executorService.execute(this::checkUser);
		executorService.execute(this::checkNetwork);
		executorService.execute(this::checkData);
		executorService.execute(this::checkTime);*/
		// appExecutors.mainThread().execute(this::waitForAllChecks);
	}

	/******* run Checks by Type *******/
	private void checkApp() {
		Log.d(TAG, "checkApp: ");
		splashState.postValue(SplashState.APP_CHECK);
		checkResult.postValue(new InitialCheck(CheckType.APP, CheckResult.OK, ""));
		final CheckResult[] result = {CheckResult.ERROR};
		final String[] message = new String[1];

		// Notes from the Server
		new DataOrmRepo<>(AdminNote.class).getListByField("uak", DataRepository.WhereCond.EQ, "0").addOnCompleteListener(new OnCompleteListener<List<AdminNote>>() {
			@Override
			public void onComplete(@NonNull Task<List<AdminNote>> task) {
				if (task.isSuccessful()) {
					List<AdminNote> list = task.getResult();
					int verDeprecated = 0;
					int verDeprecating = 0;
					int verAppLatest = 0;
					int i = 0;
					for (; i < list.size(); i++) {
						verDeprecated = list.get(i).getVerDeprecated();
						verDeprecating = list.get(i).getVerDeprecating();
						verAppLatest = list.get(i).getVerAppLatest();
						// skip non-version-content
						if (0 != verDeprecated * verDeprecating * verAppLatest) {
							break;
						}
					}
					if (getVersionCode() <= verDeprecated) {
						// result ERROR
						result[0] = CheckResult.ERROR;
						String msgTemplate = getRes().getString(R.string.msg_app_deprecated);
						message[0] = String.format(msgTemplate, verDeprecated, verAppLatest);
						//checkResult.postValue(new InitialCheck(CheckType.APP, result[0], message[0]));
					} else if (getVersionCode() <= verDeprecating) {
						// warning but OK:
						result[0] = CheckResult.WARN;
						String msgTemplate = getRes().getString(R.string.msg_app_deprecating);
						message[0] = String.format(msgTemplate, verDeprecated, verAppLatest);
						//checkResult.postValue(new InitialCheck(CheckType.APP, result[0], message[0]));
					} else {
						// version is OK:
						result[0] = CheckResult.OK;
						message[0] = "Version check is OK";
					}
					new Handler(Looper.getMainLooper()).post(() -> {
						checkResult.setValue(new InitialCheck(CheckType.APP, result[0], message[0]));
					});

					// Update the necessary AdminNotes:
					long latestLocalTS = list.get(0).getTimeStamp();
					new DataRepos<>(AdminNote.class).fetchAdminNotes(latestLocalTS);

				} else {
					// no version records found -- considered as OK
					result[0] = CheckResult.OK;
					message[0] = "";
					new Handler(Looper.getMainLooper()).post(() -> {
						checkResult.setValue(new InitialCheck(CheckType.APP, result[0], message[0]));
					});

					// Update the all AdminNotes:
					new DataRepos<>(AdminNote.class).fetchAdminNotes(0L);
				}
			}
		});

		// And try to run google app-update

	}

	private void checkUser() {
		Log.d(TAG, "checkUser: ");
		splashState.postValue(SplashState.USER_CHECK);
		final CheckResult[] result = {CheckResult.ERROR};
		final String[] message = new String[1];

		FirebaseAuth fbAuth = FirebaseAuth.getInstance();
		FirebaseUser user = fbAuth.getCurrentUser();
		if (user == null) {
			// No user detected so just skip UserHelper
			checkResult.postValue(new InitialCheck(CheckType.USER, result[0], message[0]));
		} else {
			String strMessage;
			String email = user.getEmail();

			new DataOrmRepo<>(UserHelper.class).read("" + intStringHash(user.getUid()))
					.addOnSuccessListener(new OnSuccessListener<UserHelper>() {
						@Override
						public void onSuccess(UserHelper userHelper) {

							if (userHelper.isVerified()) {
								result[0] = CheckResult.OK;
								message[0] = email + " " + getRes().getString(R.string.msg_user_logged_in);
								//checkResult.postValue(new InitialCheck(CheckType.USER, result[0], message[0]));
							} else if (user.isEmailVerified()) {
								result[0] = CheckResult.OK;
								message[0] = email + " " + getRes().getString(R.string.msg_user_logged_in);
								//checkResult.postValue(new InitialCheck(CheckType.USER, result[0], message[0]));
								// update local user record
								userHelper.setVerified(true);
								userHelper.setTimeStamp(timeStampU());
								new DataOrmRepo<>(UserHelper.class).create(userHelper);
							} else {
								result[0] = CheckResult.WARN;
								//checkResult.postValue(new InitialCheck(CheckType.USER, result[0], message[0]));
							}
							new Handler(Looper.getMainLooper()).post(() -> {
								userHelperLD.setValue(userHelper);
								checkResult.setValue(new InitialCheck(CheckType.USER, result[0], message[0]));
							});
						}
					});
		}
	}

	private void checkNetwork() {
		Log.d(TAG, "checkNetwork: ");
		splashState.postValue(SplashState.NETWORK_CHECK);
		final CheckResult[] result = {CheckResult.ERROR};
		final String[] message = new String[1];

		networkConnectivity = new NetworkConnectivity(appExecutors, getAppContext());
		networkConnectivity.checkInternetConnection((isConnected) -> {
			if (isConnected) {
				result[0] = CheckResult.OK;
				new Handler(Looper.getMainLooper()).post(() -> {
					checkResult.setValue(new InitialCheck(CheckType.NETWORK, result[0], message[0]));
				});
			} else {
				new Handler(Looper.getMainLooper()).post(() -> {
					checkResult.setValue(new InitialCheck(CheckType.NETWORK, result[0], message[0]));
				});
			}
		}, "http://attplus.in/schulte/ru/attention_schulte_plus_info_ru.html");
	}

	private void checkData() {
		Log.d(TAG, "checkData: ");
		splashState.postValue(SplashState.DATA_CHECK);
		final CheckResult[] result = {CheckResult.OK};
		final String[] message = new String[1];
		// Simulate data check
//		checkResult.postValue(new InitialCheck(CheckType.DATA, CheckResult.OK, ""));
		new Handler(Looper.getMainLooper()).post(() -> {
			checkResult.setValue(new InitialCheck(CheckType.DATA, CheckResult.OK, ""));
		});
	}

	private void checkTime() {
		Log.d(TAG, "checkTime: ");
		long endTime = System.currentTimeMillis() + TEST_TIME_ALLOWED;

		// initial set flag to OK (to not prevent other tests)
//		checkResult.postValue(new InitialCheck(CheckType.TIME, CheckResult.OK, ""));
		new Handler(Looper.getMainLooper()).post(() -> {
			checkResult.setValue(new InitialCheck(CheckType.TIME, CheckResult.OK, ""));
		});

		// wait for set flag to WARN
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					Log.i(TAG, "checkTime + 3sec: " + timeStampU());
					wait(3000);
				} catch (Exception e) {
					Log.e(TAG, "checkTime.wait: ", e);
				}
			}
		}
		Log.d(TAG, "BTP time's up in: " + Thread.currentThread());
		new Handler(Looper.getMainLooper()).post(() -> {
			checkResult.setValue(new InitialCheck(CheckType.TIME, CheckResult.WARN, ""));
		});
	}
}
