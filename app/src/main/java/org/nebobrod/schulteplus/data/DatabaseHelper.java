/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.getVersionCode;


import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import android.database.sqlite.SQLiteDatabase;
import org.nebobrod.schulteplus.common.Log;


import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.nebobrod.schulteplus.R;


/**
 * Database helper which creates and upgrades the database and provides the DAOs for the app.
 *
 * @author kevingalligan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	/************************************************
	 * Suggested Copy/Paste code. Everything from here to the done block.
	 ************************************************/

	private static final String DATABASE_NAME = "schulte_plus.db";

	private static final int DATABASE_VERSION = getVersionCode();

//	private Dao<ClickGroup, Integer> groupDao = null;
//	private Dao<ClickCount, Integer> clickDao = null;
	private Dao<Achievement, Integer> achievementDao = null;
	private Dao<ExResult, Integer> exResultDao = null;
	private Dao<Turn, Integer> turnDao = null;
	private Dao<UserHelper, Integer> userHelperDao = null;

	private static final AtomicInteger usageCounter = new AtomicInteger(0);

	// we do this so there is only one helper
	private static DatabaseHelper helper = null;

	public DatabaseHelper() {
		super(getAppContext(), DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/************************************************
	 * Suggested Copy/Paste Done
	 ************************************************/

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
//			TableUtils.createTable(connectionSource, ClickGroup.class);
//			TableUtils.createTable(connectionSource, ClickCount.class);
			TableUtils.createTable(connectionSource, Achievement.class);
			TableUtils.createTable(connectionSource, ExResult.class);
			TableUtils.createTable(connectionSource, Turn.class);
			TableUtils.createTable(connectionSource, UserHelper.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
		}
	}

	/**
	 * Get the helper, possibly constructing it if necessary. For each call to this method,
	 * there should be 1 and only 1 call to {@link #close()}.
	 */
	public static synchronized DatabaseHelper getHelper() {
		if (helper == null) {
			helper = new DatabaseHelper();
		}
		usageCounter.incrementAndGet();
		return helper;
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
//			TableUtils.dropTable(connectionSource, ClickGroup.class, true);
//			TableUtils.dropTable(connectionSource, ClickCount.class, true);
			TableUtils.dropTable(connectionSource, Achievement.class, true);
			TableUtils.dropTable(connectionSource, ExResult.class, true);
			TableUtils.dropTable(connectionSource, Turn.class, true);
			TableUtils.dropTable(connectionSource, UserHelper.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(
					DatabaseHelper.class.getName(),
					"Unable to upgrade database from version " + oldVer + " to new " + newVer,
					e);
		}
	}

	/*
	public Dao<ClickGroup, Integer> getGroupDao() throws SQLException {
		if (groupDao == null) {
			groupDao = getDao(ClickGroup.class);
		}
		return groupDao;
	}

	public Dao<ClickCount, Integer> getClickDao() throws SQLException {
		if (clickDao == null) {
			clickDao = getDao(ClickCount.class);
		}
		return clickDao;
	}*/ // Left as an example:

	public Dao<Achievement, Integer> getAchievementDao() throws SQLException {
		if (achievementDao == null) {
			achievementDao = getDao(Achievement.class);
		}
		return achievementDao;
	}

	public Dao<ExResult, Integer> getExResultDao() throws SQLException {
		if (exResultDao == null) {
			exResultDao = getDao(ExResult.class);
		}
		return exResultDao;
	}
	public Dao<ExResultBasics, Integer> getExResultBasicsDao() throws SQLException {
		return (Dao<ExResultBasics, Integer>) getDao(ExResultBasics.class);
	}
	public Dao<ExResultSchulte, Integer> getExResultSchulteDao() throws SQLException {
		return (Dao<ExResultSchulte, Integer>) getDao(ExResultSchulte.class);
	}

	public Dao<Turn, Integer> getTurnDao() throws SQLException {
		if (turnDao == null) {
			turnDao = getDao(Turn.class);
		}
		return turnDao;
	}
	public Dao<UserHelper, Integer> getUserHelperDao() throws SQLException {
		if (userHelperDao == null) {
			userHelperDao = getDao(UserHelper.class);
		}
		return userHelperDao;
	}


	/**
	 * Close the database connections and clear any cached DAOs. For each call to {@link #getHelper()}, there
	 * should be 1 and only 1 call to this method. If there were 3 calls to {@link #getHelper()} then on the 3rd
	 * call to this method, the helper and the underlying database connections will be closed.
	 */
	@Override
	public void close() {
		if (usageCounter.decrementAndGet() == 0) {
			super.close();
//			groupDao = null;
//			clickDao = null;
			achievementDao = null;
			exResultDao = null;
			turnDao = null;
			userHelperDao = null;
			// and finally the DatabaseHelper itself
			helper = null;
		}
	}
}