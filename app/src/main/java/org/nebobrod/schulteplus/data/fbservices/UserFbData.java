/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;

/*
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.UserHelper;
*/

/**
 * Realtime Database's copy of FirebaseUser from Authentication db
 * */
/*

public final class UserFbData {
	private static final String TAG = "UserFbData";
	public static final String DB_URL = "https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app";
	private static final String DB_PATH = "users";
	static FirebaseDatabase fbDatabase;
	static DatabaseReference fbReference;
	static UserHelper userHelper;
	static String tmpMessage = "";

	public static UserHelper getFbUserHelper() {
		return userHelper;
	}

	public interface UserHelperCallback {
		void onCallback(@Nullable UserHelper fbDbUser);
	}

	private  static void init () {
		fbDatabase = FirebaseDatabase.getInstance(DB_URL);
		//fbDatabase.setLogLevel(Logger.Level.INFO); // comment if no debugging... but it works strange

		fbReference = fbDatabase.getReference(DB_PATH);
	}

	public void addUser(
			String uid, String email, String name, String password, String deviceId, boolean verified){
		init ();

		userHelper = new UserHelper(uid, email, name, password, deviceId, verified);
		// Firebase Database paths must not contain '.', '#', '$', '[', or ']', so email:
		fbReference.child(email.replace(".", "_")).setValue(userHelper);
	}

	public interface NameFreeCallback {
		void onCallback(boolean isFree);
	}

	*/
/**
	 * To check anonymous sign in
	 * @param callback
	 * @param name
	 *//*

	public static void isNameFree(NameFreeCallback callback, String name)
	{
//		FirebaseDatabase.getInstance().getReference().child("users").orderByChild("name").equalTo(name).addValueEventListener(new ValueEventListener() {
		init();
		fbReference.orderByChild("name").equalTo(name).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					callback.onCallback(false);
					Log.d(TAG, "This username already exists: " + name);
					// ("");
				} else {
					callback.onCallback(true);
					Log.d(TAG, "true, there is free space for: " + name);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				throw databaseError.toException(); // never ignore errors
			}
		});
	}

	public static void getUserFromFirebase(final UserHelperCallback myCallback, String email)
	{
		init();
		fbReference.child(email.replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				userHelper = dataSnapshot.getValue(UserHelper.class);

*/
/*				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					UserHelper user = snapshot.getValue(UserHelper.class);
					assert user != null;
					String contact_found = user.getUid();
					mContactsFromFirebase.add(contact_found);
					userHelper = user;
					System.out.println("Loaded " + userHelper.toString() + " user");
				}*//*

				myCallback.onCallback(userHelper);
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				myCallback.onCallback(null);
//				throw databaseError.toException();
				System.err.println("getUserFromFirebase: " + databaseError.getMessage());
			}
		});
	}

	String getData (String name) {
		DatabaseReference reference = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app").getReference(DB_PATH);
//		fbUserHelper = (FbUserHelper) fbReference.child("name").get();
		final UserHelper[] fbUser = new UserHelper[1];

		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				GenericTypeIndicator<UserHelper> t = new GenericTypeIndicator<UserHelper>() {};
				fbUser[0] = snapshot.getValue(t);
				Log.d(TAG, "onDataChange: OK for: " + fbUser[0]);

			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				// Failed to read value
				Log.w(TAG + " Failed to read value.", error.toException().toString());
			}
		});
*/
/*
		final String[] s = new String[1]; // AS just required: Cannot assign a value to final variable 's'
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.hasChild(name)) {

				} else {
					s[0] = (Utils.getRes().getString(R.string.msg_username_wrong));
					Log.d(TAG, s[0]);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
		*//*

		return fbUser[0].getName();
	}

	String getPassword (String name) {
*/
/*		String passwordFromDB = snapshot.child(name).child("password").getValue(String.class);

		if (!passwordFromDB.equals(name)) {
//						etName.setError(null);
			s[0] = "Getting data proceeded for user: " + name;
			Log.d(TAG, s[0]);

		} else {
			s[0] = (Utils.getRes().getString(R.string.msg_password_wrong));
			Log.d(TAG, s[0]);
		}*//*

		return "";
	};

	public static boolean z_checkUserNamePass(String name, String password){
		final boolean[] res = {false};
*/
/*		DatabaseReference reference = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app").getReference(DB_PATH);
		reference.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.hasChild(name)) {

					String passwordFromDB = snapshot.child(name).child("password").getValue(String.class);
					res[0] = true;
					if (!passwordFromDB.equals(password)) {

						Log.d(TAG, "Go on proceeded for user: " + name);
						res[0] = true;
					} else {
						Log.d(TAG, "Wrong pass for user: " + name);
					}
				} else {
					Log.d(TAG, "Wrong username: " + name);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.d("TAG", error.getMessage());
			}
		});*//*

		return res[0];
	}

	public static void getByUid(final UserHelperCallback userHelperCallback, String uid)
	{
		init();
//		Query myQuery = fbReference.orderByValue().equalTo(uid, "uid") ;
//		Query myQuery = fbReference.equalTo(uid, "uid") ;
//		Query myQuery = fbReference.orderByChild("uid") ; // THIS WORKED with dataset ( many records)
//		Query myQuery = fbReference.orderByChild("uid").equalTo(uid, "uid") ; // Doesn't work ((
		Query myQuery = fbReference.orderByChild("uid").equalTo(uid) ; //

		myQuery.addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				userHelper = null;
				for(DataSnapshot usersSnapshot: dataSnapshot.getChildren())
				{ // this is a redundant cycle but it works...
					Log.d(TAG, "getByUid.onDataChange: "+ usersSnapshot.getValue().toString());
					userHelper = usersSnapshot.getValue(UserHelper.class);
				}

				userHelperCallback.onCallback(userHelper);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

				userHelperCallback.onCallback(null);
//				throw databaseError.toException();
				System.err.println("getByUid: " + databaseError.getMessage());
			}
		});
	}
	public static boolean isExist(final UserHelperCallback userHelperCallback, String key)	{
		init();
		try {
			fbReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					userHelper = dataSnapshot.getValue(UserHelper.class);
					Log.d(TAG, "onDataChange: "  + Thread.currentThread());
					userHelperCallback.onCallback(userHelper);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					userHelperCallback.onCallback(null);
//					throw databaseError.toException();
					System.err.println("isExist: " + databaseError.getMessage());
				}
			});
		} catch (Exception e) {
			Log.d(TAG, "isExist: error" + e.getMessage());
			return false;
		}


		if (userHelper != null) {
			return true;
		} else {
			return false;
		}
	}

	public static void isNameExist(final UserHelperCallback myCallback, String name)
	{
*/
/*		init();

//		fbReference.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
//		Query myQuery = fbReference.orderByValue().equalTo(name);
//		Query myQuery = fbReference.orderByChild("name") ;
//		Query myQuery = fbReference.orderByChild("name").equalTo(name) ;
		Query myQuery = fbReference.orderByValue().equalTo(name, "name") ;

		myQuery.addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				userHelper = null;
				for(DataSnapshot usersSnapshot: dataSnapshot.getChildren())
				{
					Log.d(TAG, "isNameExist:onDataChange: "+ usersSnapshot.getValue().toString());
				}
				userHelper = dataSnapshot.getValue(UserHelper.class);
				myCallback.onCallback(userHelper);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				throw databaseError.toException();
			}
		});*//*


	}

	public static void printQuery (final UserHelperCallback myCallback, String name) {
*/
/*
		DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
		DatabaseReference userRef = rootRef.child(DB_PATH);
		ValueEventListener eventListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for(DataSnapshot ds : dataSnapshot.getChildren()) {
					String s = ds.child("name").getValue(String.class);
					Log.d("TAG", s);
					if (name.equals(s)){
						userHelper = ds.getValue(UserHelper.class);
						myCallback.onCallback(userHelper);
					}

					for(DataSnapshot dSnapshot : ds.getChildren()) {
						String ss = dSnapshot.child("name").getValue(String.class);
						Log.d("TAG", ss);
					}
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		};
		userRef.addListenerForSingleValueEvent(eventListener);

*//*

	}

*/
/*	static ValueEventListener userListener = new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			// Get Post object and use the values to update the UI
			userHelper = dataSnapshot.getValue(UserHelper.class);

		}

		@Override
		public void onCancelled(DatabaseError databaseError) {
			// Getting user failed, log a message
			Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
		}
	};*//*


	// TODO: 30.10.2023 maybe -- this method is about to refresh users-data-structure
	private static void refreshUserDb(){
*/
/*		final String DB_NEW_PATH = "users_" + timeStamp();
		DatabaseReference fbReferenceNew = fbDatabase.getReference(DB_NEW_PATH);

		init();
		Map newUserData = new HashMap();

		// gather data in loop from fbReference
		//newUserData.put(fbReference.child);

		//put data to the back-up node
		fbReferenceNew.updateChildren(newUserData);

		//delete data from DB_PATH

		// copy and enrich data to DB_PATH*//*


	}

}
*/
