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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import android.database.Cursor;
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

	private static final String TAG = "DatabaseHelper";
	private static final String DATABASE_NAME = "schulte_plus.db";
	private static final int DATABASE_VERSION = getVersionCode();

	private Dao<AdminNote, Integer> adminNoteDao = null;
	private Dao<Achievement, Integer> achievementDao = null;
	private Dao<Turn, Integer> turnDao = null;
	private Dao<UserHelper, Integer> userHelperDao = null;
	private Dao<ExResult, Integer> exResultDao = null;
	private Dao<ExResult, Integer> exResultSssrDao = null;

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
			TableUtils.createTable(connectionSource, Achievement.class);
			TableUtils.createTable(connectionSource, ExResult.class);
			TableUtils.createTable(connectionSource, Turn.class);
			TableUtils.createTable(connectionSource, UserHelper.class);
			TableUtils.createTable(connectionSource, AdminNote.class);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to create datbases", e);
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

		Log.d(TAG, "onUpgrade: STARTED");
		sqliteDatabase.beginTransaction();

		try {
/*			TableUtils.dropTable(connectionSource, Achievement.class, true);
			TableUtils.dropTable(connectionSource, ExResult.class, true);
			TableUtils.dropTable(connectionSource, Turn.class, true);
			TableUtils.dropTable(connectionSource, UserHelper.class, true);
			TableUtils.dropTable(connectionSource, AdminNote.class, true);
			*/ // this was for wipe update of structure and clear data

			// Temporary tables for keep data
			sqliteDatabase.execSQL("ALTER TABLE Achievement RENAME TO tmp_Achievement;");
			sqliteDatabase.execSQL("ALTER TABLE ExResult RENAME TO tmp_ExResult;");
			sqliteDatabase.execSQL("ALTER TABLE Turn RENAME TO tmp_Turn;");
			sqliteDatabase.execSQL("ALTER TABLE UserHelper RENAME TO tmp_UserHelper;");
			sqliteDatabase.execSQL("ALTER TABLE AdminNote RENAME TO tmp_AdminNote;");

			// New tables
			onCreate(sqliteDatabase, connectionSource);

			// get new bulk data
//			sqliteDatabase.execSQL("INSERT INTO Achievement SELECT * FROM tmp_Achievement;");
			sqliteDatabase.execSQL(generateInsertQuery(sqliteDatabase, "tmp_Achievement", "Achievement"));
//			sqliteDatabase.execSQL("INSERT INTO ExResult SELECT * FROM tmp_ExResult;");
			sqliteDatabase.execSQL(generateInsertQuery(sqliteDatabase, "tmp_ExResult", "ExResult"));
//			sqliteDatabase.execSQL("INSERT INTO Turn SELECT * FROM tmp_Turn;");
			sqliteDatabase.execSQL(generateInsertQuery(sqliteDatabase, "tmp_Turn", "Turn"));
//			sqliteDatabase.execSQL("INSERT INTO UserHelper SELECT * FROM tmp_UserHelper;");
			sqliteDatabase.execSQL(generateInsertQuery(sqliteDatabase, "tmp_UserHelper", "UserHelper"));
//			sqliteDatabase.execSQL("INSERT INTO AdminNote SELECT * FROM tmp_AdminNote;");
			sqliteDatabase.execSQL(generateInsertQuery(sqliteDatabase, "tmp_AdminNote", "AdminNote"));

			// Remove the Temporary tables
			sqliteDatabase.execSQL("DROP TABLE tmp_Achievement;");
			sqliteDatabase.execSQL("DROP TABLE tmp_ExResult;");
			sqliteDatabase.execSQL("DROP TABLE tmp_Turn;");
			sqliteDatabase.execSQL("DROP TABLE tmp_UserHelper;");
			sqliteDatabase.execSQL("DROP TABLE tmp_AdminNote;");

			// For DEBUG ONLY decrease version 103
//			sqliteDatabase.execSQL("PRAGMA user_version = 103;");

			sqliteDatabase.setTransactionSuccessful();
			Log.d(TAG, "onUpgrade: TRY IS DONE");
		} catch (android.database.SQLException e) {
			Log.e(TAG, "CATCH Unable to upgrade database from version " +
							oldVer + " to new " + newVer, e);
		} finally {
			sqliteDatabase.endTransaction();
			sqliteDatabase.execSQL("PRAGMA user_version = " + oldVer + ";"); // Set back DB version
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

	public Dao<AdminNote, Integer> getAdminNoteDao() throws SQLException {
		if (adminNoteDao == null) {
			adminNoteDao = getDao(AdminNote.class);
		}
		return adminNoteDao;
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
	public Dao<ExResultSssr, Integer> getExResultSssrDao() throws SQLException {
		return (Dao<ExResultSssr, Integer>) getDao(ExResultSssr.class);
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
			turnDao = null;
			userHelperDao = null;
			adminNoteDao = null;
			exResultDao = null;
			// and finally the DatabaseHelper itself
			helper = null;
		}
	}

	/**
	 * Provides SQL-query with generated field-list of oldTable
	 * @param db
	 * @param oldTable
	 * @param newTable
	 */
	public String generateInsertQuery(SQLiteDatabase db, String oldTable, String newTable) {
		Cursor cursor = db.rawQuery("PRAGMA table_info(" + oldTable + ");", null);

		StringBuilder columnList = new StringBuilder();
		while (cursor.moveToNext()) {
			int columnIndex = cursor.getColumnIndex("name");
			String columnName = cursor.getString(columnIndex);

			// Skip fields for deletion
/*			if (Arrays.asList("flo04", "flo05", "flo06", "flo07").contains(columnName)) {
				continue;
			}*/

			// Make an addition
			if (columnList.length() > 0) {
				columnList.append(", ");
			}
			columnList.append(columnName);
		}
		cursor.close();

		return "INSERT INTO " + newTable + " (" + columnList.toString() + ") SELECT " + columnList.toString() + " FROM " + oldTable + ";";
	}
}