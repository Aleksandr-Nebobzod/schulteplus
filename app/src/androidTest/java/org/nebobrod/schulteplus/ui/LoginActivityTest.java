/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.data.UserHelper;
import org.nebobrod.schulteplus.data.fbservices.TestUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
	public static final String TAG = "LoginActivityTest";
	DataRepository repository;

	@Rule
	public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);


	@Before
	public void setUp() throws Exception {
		TestUtils.performAuthorization();
	}

	@Test
	public void ensureViewModelStore() {
	}

	@Test
	public void onBackPressed() {
	}

	@Test
	public void getLatestUserHelperTest() throws InterruptedException {
		int id = -990303179;

		CountDownLatch latch = new CountDownLatch(1);
		final UserHelper[] result = {null};

		// Get activity
		activityScenarioRule.getScenario().onActivity(activity -> {
			new Thread(() -> {
				/*result[0] = activity.getLatestUserHelper(id);*/ // remove from Activity
				latch.countDown();
			}).start();
		});

		// Wait
		latch.await(10, TimeUnit.SECONDS);

		assertNotNull( "UserHelper should not be null", result[0]);
		System.out.println(result[0]);
	}

}