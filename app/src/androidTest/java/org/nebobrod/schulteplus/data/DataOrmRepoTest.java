/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.junit.Assert.assertTrue;
import static org.nebobrod.schulteplus.Utils.currentVersion;
import static org.nebobrod.schulteplus.Utils.getVersionCode;
import static org.nebobrod.schulteplus.Utils.intFromString;
import static org.nebobrod.schulteplus.Utils.timeStampU;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.junit.Test;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.fbservices.TestUtils;

public class DataOrmRepoTest<TEntity extends Identifiable<String>>  {
	public static final String TAG = "DataOrmRepoTest";
	
	@Test
	public void emptyTest() {
		assertTrue(true);
	}


	@Test
	public void testExist() {
		Identifiable<String> data;
		DataOrmRepo repo;

		String pr = "3";
//		data = new UserHelper(pr + "TFKBiTdd", pr + "@gmail.com", pr + "name", pr + "pass", pr + "device3a", pr + "uaked47", false);
		data = new Achievement().set(pr + "uid", pr + "uak", pr + "nam", 1711556007L, "05.05.05", pr + "r", pr + "v", pr + "m");

		repo = new DataOrmRepo<>(data.getClass());

		Task<Boolean> _task = repo.exists(pr).addOnSuccessListener(new OnSuccessListener<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				Log.d(TAG, "onSuccess: ");
				assertTrue("Success", result);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "onFailure: ");
				assertTrue("unSuccess", false);
			}
		});

		TestUtils.testResultAwait(_task);
		Log.d(TAG, "test finished");
	}

	@Test
	public void testCreate() {
		Identifiable<String> data;
		DataOrmRepo repo;

		String pr = "008";
//		data = new UserHelper(pr + "TFKBiTdd", pr + "@gmail.com", pr + "name", pr + "pass", pr + "device3a", pr + "uaked47", false);
//		data = new Achievement().setAchievement(pr + "uid", pr + "uak", pr + "nam", 1711556007L, "05.05.05", pr + "r", pr + "v", pr + "m");
		data = new AdminNote(intFromString(pr), pr + "uaked47", pr + "TFKBiTdd", "SignUp", "Android: " + currentVersion(), "", timeStampU(), getVersionCode(), 0, 0, timeStampU());


		repo = new DataOrmRepo<>(data.getClass());

		Task<Void> _task = repo.create(data).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void unused) {
				Log.d(TAG, "onSuccess: ");
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.d(TAG, "onFailure: ");
			}
		});

		TestUtils.testResultAwait(_task);

		assertTrue("Success", true);
	}


	@Test
	public void read() {
		Identifiable<String> dataIn;
		Identifiable<String> dataOut;
		DataOrmRepo repo;

		String pr = "006";
		dataIn = new UserHelper(pr + "TFKBiTdd", pr + "@gmail.com", pr + "name", pr + "pass", pr + "device3a", pr + "uaked47", false);
//		data = new Achievement().setAchievement(pr + "uid", pr + "uak", pr + "nam", 1711556007L, "05.05.05", pr + "r", pr + "v", pr + "m");

		repo = new DataOrmRepo<>(dataIn.getClass());

		pr = ((UserHelper) dataIn).getId() + "";


		Task<TEntity> _task = repo.read(pr).addOnSuccessListener(new OnSuccessListener() {
			@Override
			public void onSuccess(Object o) {
				TEntity entity = (TEntity) o;
				Log.d(TAG, "onSuccess: " + entity.toString());
				assertTrue("Success", true);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.e(TAG, "onFailure: ", e);
				assertTrue("unSuccess", false);
			}
		});

		TestUtils.testResultAwait(_task);
		Log.d(TAG, "test finished");
	}
}
