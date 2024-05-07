/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.common.Const.QUERY_COMMON_LIMIT;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.data.DatabaseHelper.getHelper;

import android.database.SQLException;
import android.media.MediaPlayer;
import org.nebobrod.schulteplus.common.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.nebobrod.schulteplus.common.Const;
import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.AppExecutors;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** Provides common CRUD methods working on local SchultePlus SQLite DB by ORMLite
 **/
public class DataOrmRepo implements xDataRepository {
	private static final String TAG = DataOrmRepo.class.getSimpleName();

	private static final AppExecutors appExecutors = new AppExecutors();
	private final DatabaseHelper helper;

	/**
	 * easy constructor
	 */
	public DataOrmRepo() {
		this.helper = DatabaseHelper.getHelper();
	}

	/**
	 * Get Dao-object <p> (like {@link DatabaseHelper#getExResultDao()}) for:
	 * @param className defined as a String
	 */
	public<T> Dao<T, Integer>  getAnyDao(String className) {
		Dao<T, Integer> dao;

		try {
			// getting method name like "getExResultBasicsDao"
			String methodName = "get" + className + "Dao";
			// getting method
			Method daoMethod = helper.getClass().getMethod(methodName);

			// Calling method to get Dao of object
			dao = (Dao<T, Integer>) daoMethod.invoke(helper);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return dao;
	}

	/**
	 * Put into a DataRepository
	 * @param result ExResult's child classes
	 */
	@Override
	public<T> void create(T result) {
		Dao<T, Integer> dao = getAnyDao(result.getClass().getSimpleName());

		try {
			// put the object into DB:
			dao.create(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets from a DataRepository <p>
	 * number of rows as defined in: {@link Const#QUERY_COMMON_LIMIT}
	 */
	@Override
	public<T> List<T> getListLimited(Class<T> clazz, String exType) {
		try {
			// get name of data-class
			String className = clazz.getSimpleName();
			// get dao-method name
			String methodName = "get" + className + "Dao";
			// get dao-method by name
			Method daoMethod = helper.getClass().getMethod(methodName);

			// call method to get Dao of clazz
			Dao<T, Integer> dao = (Dao<T, Integer>) daoMethod.invoke(helper);

			// Prepare the Query
			QueryBuilder<T, Integer> qb = dao.queryBuilder();
			Where where = qb.where();
			Field field;
			String fieldName;

			// uid field must be filtered
			field = clazz.getDeclaredField("UID_FIELD_NAME");
			fieldName = (String) field.get(null);
			where.eq(fieldName, ExerciseRunner.getUserHelper().getUid());

			// and
			where.and();
			// exType field must be filtered
			field = clazz.getDeclaredField("EXTYPE_FIELD_NAME");
			fieldName = (String) field.get(null);
			where.eq(fieldName, exType);

//			PreparedQuery<T> preparedQuery = qb.prepare();
//			return dao.queryBuilder().limit(QUERY_COMMON_LIMIT).query();

			// timeStamp field must be ordered
			field = clazz.getDeclaredField("TIMESTAMP_FIELD_NAME");
			fieldName = (String) field.get(null);

			// return result of query
			return qb.limit(QUERY_COMMON_LIMIT).orderBy(fieldName, true).query();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Clean user personal data (after account removal)
	 *
	 * @param uid                user id
	 * @param unpersonalisedName new dummy name for keep ExResults' history
	 */
	@Override
	public void unpersonalise(String uid, String unpersonalisedName) {
		; // not need for local data
	}


	public interface OrmGetCallback<R> {
		void onComplete(R result);
	}

	public static synchronized void achievePut(String uid, String uak, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark) {
		Achievement achievement = new Achievement();
		achievement.setAchievement(uid,  uak, name,  timeStamp,  dateTime,  recordText,  recordValue,  specialMark);
		try {
			DatabaseHelper helper = new DatabaseHelper();
			Dao<Achievement, Integer> dao = helper.getAchievementDao();
			dao.create(achievement);
		} catch (SQLException | java.sql.SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * return result of query to local db in background thread. play sound on success.
	 * @param callback for main thread
	 * @param <R> (check the reason of this)
	 */
	public static <R> void achieveGet25(OrmGetCallback<R> callback) {
		final ArrayList[] arrayList = {null};

		appExecutors.getDiskIO().execute(() -> {
			final R result;
			try {
				Log.d(TAG, "query to local db within: " + Thread.currentThread());
//				result = callable.call();
				Dao<Achievement, Integer> dao = getHelper().getAchievementDao();
				QueryBuilder<Achievement, Integer> builder = dao.queryBuilder();
				builder.orderBy(Achievement.DATE_FIELD_NAME, false).limit(QUERY_COMMON_LIMIT);
				result = (R) dao.query(builder.prepare());

				MediaPlayer mp = MediaPlayer.create(getAppContext(), org.nebobrod.schulteplus.R.raw.slow_whoop_bubble_pop);
				mp.start();
				mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mPlayer) {
						mPlayer.release();
					}
				});
			} catch (Exception e) {
				Log.e(TAG, "achieveGet25: ", e);
				throw new RuntimeException(e);
			}
			appExecutors.mainThread().execute(() -> {
				callback.onComplete(result);
			});
		});
	}

/*
	private static ArrayList z_achieveGet25() {
		final ArrayList[] arrayList = {null};
		appExecutors.getNetworkIO().execute(() -> {


		AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {

			@Override
			protected Void doInBackground(Integer... integers) {
				try {

				} catch (Exception e) {
					Log.i(OrmUtils.class.getName(), e.getMessage());
				}
				return  Void();
			}

			@Override
			protected void onPostExecute(Void aVoid) {
//				updateScreenValue();

			}
		};

		asyncTask.execute();
		return arrayList[0];
	}
*/

	public static ArrayList getAchievementList(){
		Log.i(DataOrmRepo.class.getName(), "Show list again");
		try {
			Dao<Achievement, Integer> dao = getHelper().getAchievementDao();
			QueryBuilder<Achievement, Integer> builder = dao.queryBuilder();
			builder.orderBy(Achievement.DATE_FIELD_NAME, false).limit(QUERY_COMMON_LIMIT);
			return (ArrayList) dao.query(builder.prepare());
		} catch (Exception e) {
			Log.i(DataOrmRepo.class.getName(), e.getMessage());
			return null;
		}
	}
}
