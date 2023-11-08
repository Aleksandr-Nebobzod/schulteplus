/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

import static org.nebobrod.schulteplus.Utils.timeStamp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
/**
 * Realtime Database's copy of FirebaseUser in Authentication db
 * */
public final class UserFbData {
	private static final String TAG = "UserData";
	private static final String DB_PATH = "users";
	static FirebaseDatabase fbDatabase;
	static DatabaseReference fbReference;
	static UserHelper userHelper;
	static String tmpMessage = "";

	public static UserHelper getFbUserHelper() {
		return userHelper;
	}

	public interface UserCallback {
		void onCallback(@Nullable UserHelper fbDbUser);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("hello from test");
		//println(Log.DEBUG, "main: run!", "main: run!");
		//Log.d(TAG, "main: run!");
	}

	private  static void init () {
		fbDatabase = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app");
		//fbDatabase.setLogLevel(Logger.Level.INFO); // comment if no debugging... but it works strange

		fbReference = fbDatabase.getReference(DB_PATH);
	}

	void addUser(String email, String name, String password, String uid, String deviceId, boolean verified){
		fbDatabase = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app");
		fbReference = fbDatabase.getReference(DB_PATH);

		userHelper = new UserHelper(email, name, password, uid, deviceId, verified);
		// Firebase Database paths must not contain '.', '#', '$', '[', or ']', so email:
		fbReference.child(email.replace(".", "_")).setValue(userHelper);
	}

	public interface NameFreeCallback {
		void onCallback(boolean isFree);
	}
	public static void isNameFree(NameFreeCallback callback, String name) // To check anonymous sign in
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


	public static boolean z_getUserFromFirebase(final UserCallback myCallback, String name)
	{
		init();
/*		fbReference.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				fbUserHelper = dataSnapshot.getValue(FbUserHelper.class);
//				assert user != null;
//				fbUserHelper = user;
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					FbUserHelper user = snapshot.getValue(FbUserHelper.class);
					assert user != null;
//					String contact_found = user.getUid();
//					mContactsFromFirebase.add(contact_found);
					fbUserHelper = user;
					System.out.println("Loaded " + fbUserHelper.toString() + " user");
				}
				myCallback.onCallback(fbUserHelper);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				throw databaseError.toException();
			}
		});*/
		if (userHelper != null) {
			return true;
		} else {
			return false;
		}
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
				Log.w(TAG,"Failed to read value.", error.toException());
			}
		});
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
		*/
		return fbUser[0].getName();
	}

	String getPassword (String name) {
/*		String passwordFromDB = snapshot.child(name).child("password").getValue(String.class);

		if (!passwordFromDB.equals(name)) {
//						etName.setError(null);
			s[0] = "Getting data proceeded for user: " + name;
			Log.d(TAG, s[0]);

		} else {
			s[0] = (Utils.getRes().getString(R.string.msg_password_wrong));
			Log.d(TAG, s[0]);
		}*/
		return "";
	};

	public static boolean z_checkUserNamePass(String name, String password){
		final boolean[] res = {false};
		DatabaseReference reference = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app").getReference(DB_PATH);
		reference.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.hasChild(name)) {

					String passwordFromDB = snapshot.child(name).child("password").getValue(String.class);
					res[0] = true;
/*					if (!passwordFromDB.equals(password)) {

						Log.d(TAG, "Go on proceeded for user: " + name);
						res[0] = true;
					} else {
						Log.d(TAG, "Wrong pass for user: " + name);
					}*/
				} else {
					Log.d(TAG, "Wrong username: " + name);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.d("TAG", error.getMessage());
			}
		});
		return res[0];
	}

	public static boolean isExist(final UserCallback userCallback, String key){
		init();
		try {
			fbReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {

				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					userHelper = dataSnapshot.getValue(UserHelper.class);
					userCallback.onCallback(userHelper);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					throw databaseError.toException();
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

	public static void isNameExist(final UserCallback myCallback, String name)
	{
		init();

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
					Log.d(TAG, "onDataChange: "+ usersSnapshot.getValue().toString());
				}
				userHelper = dataSnapshot.getValue(UserHelper.class);
				myCallback.onCallback(userHelper);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				throw databaseError.toException();
			}
		});

	}

	public static void printQuery (final UserCallback myCallback, String name) {
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

	}

	private static boolean z_isUserExists(String name)
	{
		final boolean[] res = {false};
		DatabaseReference reference = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app").getReference(DB_PATH);
		//"https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app"
		Semaphore sem = new Semaphore(0);

		reference.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.hasChild(name)) {
					sem.release();
					res[0] = true;
//					Log.e(TAG, "User with Key: " + snapshot.child(name).getKey() + "--exists!.");

				} else {
//					Log.d(TAG, "Wrong username: " + name);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
//				Log.d("TAG", error.getMessage());
			}
		});

/*		try {
//			userReference = reference.child(name);
			reference.addValueEventListener(userListener);
			res[0] = true;
			Log.d(TAG, "E.x.i.s.t.i.n.g username: " + name);
			reference.removeEventListener(userListener);
		} catch (Exception e) {
			Log.d(TAG, "F.r.e.e username: " + name + e.getMessage());
		}*/



/*		reference.addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				for (DataSnapshot item: dataSnapshot.getChildren()) {
					if (item.getKey().equals(name))
					{
						res[0] = true;
					}

				}
				if (dataSnapshot.hasChild(name)) {
					res[0] = true;
				} else {
					Log.d(TAG, "Wrong username: " + name);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.d("TAG", error.getMessage());
			}
		});*/

/*		reference.child("users").child(name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task) {
				if (task.isSuccessful()) {
					Log.e(TAG, "User with Key: " + String.valueOf(task.getResult().getKey()) + "--exists!.");
					res[0] = true;
				}
				else {
					Log.d(TAG, "Error getting data", task.getException());
				}
			}
		});*/

		try
		{
			sem.tryAcquire(5000, TimeUnit.MILLISECONDS);
//			sem.acquire();
		}
		catch (Exception ignored)
		{
		}

		reference = null;

		return res[0];
	}

/*	static ValueEventListener userListener = new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			// Get Post object and use the values to update the UI
			fbUserHelper = dataSnapshot.getValue(FbUserHelper.class);

		}

		@Override
		public void onCancelled(DatabaseError databaseError) {
			// Getting user failed, log a message
			Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
		}
	};*/

	// TODO: 30.10.2023 maybe -- this method is about to refresh users-data-structure
	private static void refreshUserDb(){
		final String DB_NEW_PATH = "users_" + timeStamp();
		DatabaseReference fbReferenceNew = fbDatabase.getReference(DB_NEW_PATH);

		init();
		Map newUserData = new HashMap();

		// gather data in loop from fbReference
		//newUserData.put(fbReference.child);

		//put data to the back-up node
		fbReferenceNew.updateChildren(newUserData);

		//delete data from DB_PATH

		// copy and enrich data to DB_PATH

	}

}
