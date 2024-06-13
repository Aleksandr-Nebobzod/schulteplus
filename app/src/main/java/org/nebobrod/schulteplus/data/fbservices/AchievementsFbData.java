/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;
/*

import android.text.Spanned;
import org.nebobrod.schulteplus.common.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static org.nebobrod.schulteplus.common.Const.*;

*/
/** Achievements datasource in Realtime Firebase Database *//*

public class AchievementsFbData
{
	private static final String TAG = "AchievementsFbData";
	private static final String DB_PATH = "achievements";
	static FirebaseDatabase fbDatabase;
	static DatabaseReference fbReference;
	private static AchievementsHelper achieve = achieveReturn();

	public interface DashboardCallback {
		void onCallback(ArrayList<Spanned> list);
	}

	private  static void init () {
		fbDatabase = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app");
		//fbDatabase.setLogLevel(Logger.Level.INFO); // comment if no debugging... but it works strange

		fbReference = fbDatabase.getReference(DB_PATH);
	}

	public static AchievementsHelper achieveReturn(){
		return (achieve != null ? achieve : new AchievementsHelper("TFKBiTdd7OVYUaplfzDHrXSCixr1", "nebobzod", 1, "05.11.2023","First record", "","!") );
	}

	public static void achievePut(OnCompleteListener<Void> onCompleteListener, String uid, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark){
		achieve.uid = uid;
		achieve.name = name;
		achieve.timeStamp = timeStamp;
		achieve.dateTime = dateTime;
		achieve.recordText = recordText;
		achieve.recordValue = recordValue;
		achieve.specialMark = specialMark;

		init();
		fbReference.child(("" + timeStamp)).setValue(achieve).addOnCompleteListener(onCompleteListener);
	}

	public static void  basicQueryValueListener(final DashboardCallback dashboardCallback, ArrayList<Spanned> list) {
		init();
		Query freshAchievementsQuery = fbReference.limitToLast((int) QUERY_COMMON_LIMIT)
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
				Log.w(TAG +" onCancelled: ", databaseError.toException().toString());
			}

		});
		// [END basic_query_value_listener]
	}
}
*/
