/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

import android.text.Spanned;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static org.nebobrod.schulteplus.Const.*;

/** Achievements datasource in Realtime Database Firestore */
public class AchievementsFbData
{
	private static final String TAG = "UserData";
	private static final String DB_PATH = "achievements";
	static FirebaseDatabase fbDatabase;
	static DatabaseReference fbReference;
	private static AchievementsHelper achieve = achieveReturn();

	public interface DashboardCallback {
		void onCallback(ArrayList<Spanned> list);
	}

	private  static void init () {
		FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app");
		//fbDatabase.setLogLevel(Logger.Level.INFO); // comment if no debugging... but it works strange

		fbReference = fbDatabase.getReference(DB_PATH);
	}

	public static AchievementsHelper achieveReturn(){
		return (achieve != null ? achieve : new AchievementsHelper("TFKBiTdd7OVYUaplfzDHrXSCixr1", "nebobzod", 1, "05.11.2023","First record", "","!") );
	}

	public static void achievePut(String uid, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark){
		achieve.uid = uid;
		achieve.name = name;
		achieve.timeStamp = timeStamp;
		achieve.dateTime = dateTime;
		achieve.recordText = recordText;
		achieve.recordValue = recordValue;
		achieve.specialMark = specialMark;

		init();
		fbReference.child(("" + timeStamp)).setValue(achieve);
	}

	public static void getData() {
		init();

		// calling add value event listener method
		// for getting the values from database.
		fbReference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				// this method is call to get the realtime
				// updates in the data.
				// this method is called when the data is
				// changed in our Firebase console.
				// below line is for getting the data from
				// snapshot of our database.
				AchievementsHelper value = snapshot.getValue(AchievementsHelper.class);
//				 value = (FbAchievementsHelper) snapshot.getValue();

				// after getting the value we are setting
				// our value to our text view in below line.
				Log.d(TAG, "onDataChange: " + value);

			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				// calling on cancelled method when we receive
				// any error or we are not able to get the data.
				Log.d(TAG, "onCancelled: " + "Fail to get data.");
//				Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void  basicQueryValueListener(final DashboardCallback dashboardCallback, ArrayList<Spanned> list) {
		init();
		Query freshAchievementsQuery = fbReference.limitToLast(QUERY_COMMON_LIMIT)
				.orderByChild("timeStamp");

		// [START basic_query_value_listener]
		// My last records by ...
		freshAchievementsQuery.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				list.clear();
				long i = dataSnapshot.getChildrenCount();
				for (DataSnapshot ds: dataSnapshot.getChildren()) {
					AchievementsHelper achieve = ds.getValue(AchievementsHelper.class);
					Spanned achieveStr = achieve.toSpanned();
					list.add(achieveStr);
					i--;
				}
				Collections.reverse(list);
				dashboardCallback.onCallback(list);

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				// Getting Post failed, log a message
				Log.w(TAG, "onCancelled: ", databaseError.toException());
				// ...
			}
		});

		// [END basic_query_value_listener]

	}


}
