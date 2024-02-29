/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Const.QUERY_COMMON_LIMIT;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.data.DatabaseHelper.getHelper;

import android.database.SQLException;
import android.media.MediaPlayer;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.nebobrod.schulteplus.Const;
import org.nebobrod.schulteplus.fbservices.AppExecutors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** Provides common CRUD methods working on local SchultePlus SQLite DB by ORMLite
 **/
public class OrmRepo implements DataRepository {

//	private static final String TAG = getClass().getSimpleName();
	private static final String TAG = OrmRepo.class.getSimpleName();

	private static final AppExecutors appExecutors = new AppExecutors();

	private final DatabaseHelper helper;

	public OrmRepo(DatabaseHelper helper) {
		this.helper = helper;
	}

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
	 * Puts into a DataRepository
	 *
	 * @param result ExResult's child classes
	 */
	@Override
	public<T> void putResult(T result) {
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
	public<T> List<T> getResultsLimited(Class<T> clazz) {



		return null;
	}


	public interface OrmGetCallback<R> {
		void onComplete(R result);
	}

	public static synchronized void achievePut(String uid, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark) {
		Achievement achievement = new Achievement();
		achievement.setAchievement( uid,  name,  timeStamp,  dateTime,  recordText,  recordValue,  specialMark);
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
				Log.e(TAG, "achieveGet25: " + e.getMessage());
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
		Log.i(OrmRepo.class.getName(), "Show list again");
		try {
			Dao<Achievement, Integer> dao = getHelper().getAchievementDao();
			QueryBuilder<Achievement, Integer> builder = dao.queryBuilder();
			builder.orderBy(Achievement.DATE_FIELD_NAME, false).limit(QUERY_COMMON_LIMIT);
			return (ArrayList) dao.query(builder.prepare());
		} catch (Exception e) {
			Log.i(OrmRepo.class.getName(), e.getMessage());
			return null;
		}
	}



}
