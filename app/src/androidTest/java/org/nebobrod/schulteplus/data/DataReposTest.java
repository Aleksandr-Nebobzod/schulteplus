/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.junit.Test;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.fbservices.TestUtils;

public class DataReposTest {
	public static final String TAG = "DataReposTest";

	@Test
	public void create() {
		Identifiable<String> data;
		DataRepos repos;

		String pr = "009";
		String uid = pr + "T1K1i1d171V1U1p1f1D1r1S1i1r1"; // "TFKBiTdd7OVYUaplfzDHrXSCixr1"
//		data = new UserHelper(uid, pr + "@gmail.com", pr + "name", pr + "pass", pr + "device3a", pr + "uaked47", false);
		data = new Achievement().set(pr + "uid", pr + "uak", pr + "nam", 1711556007L, "05.05.05", pr + "r", pr + "v", pr + "m");

		// Provide component Repository
		repos = new DataRepos(data.getClass());

		Task<Void> _task = repos.create(data).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void unused) {
				Log.d(TAG, "onSuccess: ");
				System.out.println(data.toString());
				assertTrue("Success", true);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "onFailure: ");
				assertTrue("NOT success with " + e.getLocalizedMessage(), false);
			}
		});

		TestUtils.testResultAwait(_task);
		Log.d(TAG, "create test finished: ");
	}

	//	1138605908.2uaked47
	@Test
	public void read() {

		String key = "-1906027091.007uaked47";

		Identifiable<String> localData;
		Identifiable<String> centralData;
		DataRepos<UserHelper> repos = new DataRepos<>(UserHelper.class);

		Task<UserHelper> _task = repos.read(key).addOnSuccessListener(new OnSuccessListener<UserHelper>() {
			@Override
			public void onSuccess(UserHelper userHelper) {
				System.out.println(userHelper);
				assertTrue("Success", true);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "onFailure: ");
				assertTrue("Read NOT success with " + e.getLocalizedMessage(), false);
			}
		});

		TestUtils.testResultAwait(_task);
		Log.d(TAG, "read test finished: ");
	}
}