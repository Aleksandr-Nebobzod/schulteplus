/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.data.DatabaseHelper.getHelper;

import android.database.SQLException;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.fbservices.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/** Provides common methods working with local SchultePlus DB SQLite by ORMLite
 **/
public class OrmUtils implements DataRepository {

//	private static final String TAG = getClass().getSimpleName();
	private static final String TAG = OrmUtils.class.getSimpleName();

	private static final AppExecutors appExecutors = new AppExecutors();

	@Override
	public synchronized void exResultPut(ExResult exResult) {
	}

	@Override
	public List<ExResult> exResultGet25() {
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
				builder.orderBy(Achievement.DATE_FIELD_NAME, false).limit(25L);
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
		Log.i(OrmUtils.class.getName(), "Show list again");
		try {
			Dao<Achievement, Integer> dao = getHelper().getAchievementDao();
			QueryBuilder<Achievement, Integer> builder = dao.queryBuilder();
			builder.orderBy(Achievement.DATE_FIELD_NAME, false).limit(25L);
			return (ArrayList) dao.query(builder.prepare());
		} catch (Exception e) {
			Log.i(OrmUtils.class.getName(), e.getMessage());
			return null;
		}
	}



}
