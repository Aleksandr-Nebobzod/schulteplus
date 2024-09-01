/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.sssr;

import static org.nebobrod.schulteplus.Utils.localDateOfTimeStamp;
import static org.nebobrod.schulteplus.Utils.timeStampPlusDays;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_R1;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.ExResultSssr;
import org.nebobrod.schulteplus.data.fbservices.ConditionEntry;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SssrViewModel extends ViewModel {
	private final String TAG = this.getClass().getSimpleName();
	public static final int DAYS_BEFORE = 31;

	private DataFirestoreRepo<ExResultSssr> repo = new DataFirestoreRepo<>(ExResultSssr.class);
	private DataRepos<ExResultSssr> repos = new DataRepos<>(ExResultSssr.class);
	private final MutableLiveData<List<ExResultSssr>> exercisesLD = new MutableLiveData<>();
	private final MutableLiveData<Map<LocalDate, ExResultSssr>> exercisesMapLD = new MutableLiveData<>();

	public MutableLiveData<Map<LocalDate, ExResultSssr>> getExercisesMapLD() {
		return exercisesMapLD;
	}

	/** Getting Sssr exercise data and fill a mont-before dates set 	 */
	public void fetchExResults() {
		long monthAgoTimestamp = timeStampPlusDays( - 1 *  DAYS_BEFORE);

		// Get all exResult records not older than 90 days for today
		repo.getListByField(
				new ConditionEntry(
						ExResult.TIMESTAMP_START_FIELD_NAME,
						DataRepository.WhereCond.GE,
						monthAgoTimestamp),
				new ConditionEntry(
						ExResult.UID_FIELD_NAME,
						DataRepository.WhereCond.EQ,
						ExerciseRunner.getUserHelper().getUid()),
				new ConditionEntry(
						ExResult.EXTYPE_FIELD_NAME,
						DataRepository.WhereCond.EQ,
						KEY_PRF_EX_R1))
				.addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						List<ExResultSssr> results = task.getResult();

						// Safety
						if (results == null) {
							results = new ArrayList<>();
						}
						// Put all data
						exercisesLD.postValue(results);

						// Proceed calculations for Calendar and Stata
						exercisesMapLD.postValue(getMonthResultMap(results));
					} else {
						Log.w(TAG, "onError: ", task.getException());
					}
				});
	}

	private Map<LocalDate, ExResultSssr> getMonthResultMap(List<ExResultSssr> results) {
		Map<LocalDate, ExResultSssr> dateMap = new HashMap<>();
		LocalDate date;
		for (ExResultSssr result : results) {
			date = localDateOfTimeStamp(result.getTimeStampStart());
			dateMap.put(date, result.extractPack());
		}
		for (int i = 0; i < DAYS_BEFORE; i++) {
			date = LocalDate.now().minusDays(i);
			if (dateMap.get(date) == null) {
				dateMap.put(date, new ExResultSssr(date, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, ""));
			}
		}
		return dateMap;
	}

	public void saveRecord(ExResultSssr exResult) {
		Task<Void> _task = repos.create(exResult).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					Map<LocalDate, ExResultSssr> _map = getExercisesMapLD().getValue();
					_map.put(localDateOfTimeStamp(exResult.getTimeStampStart()), exResult);
					getExercisesMapLD().postValue(_map);
				}
			}
		});
	}
}
