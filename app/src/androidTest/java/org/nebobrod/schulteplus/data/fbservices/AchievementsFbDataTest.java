/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;
/*

import static org.junit.Assert.*;

import android.text.Spanned;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.junit.Before;
import org.junit.Test;
//import org.nebobrod.schulteplus.common.Log;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class AchievementsFbDataTest {
	public static final String TAG = "AchievementsFbDataTest";

	// Создаем CountDownLatch
	CountDownLatch latch;

	private FirebaseAuth firebaseAuth;
	AuthResult authResult;
	String authToken;

	@Before
	public void setUp() {

		// Инициализация CountDownLatch
		latch = new CountDownLatch(1);

		// Инициализация Firebase Authentication
		firebaseAuth = FirebaseAuth.getInstance();
		// firebaseAuth.useEmulator("localhost", 9099); // Для использования тестовой среды Firebase

		// Выполнение аутентификации для получения токена
		com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> authTask;
		authTask = firebaseAuth.signInWithEmailAndPassword("tester01@attentions.org", "tester01")
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							authResult = task.getResult();
*/
/*							GetTokenResult token = authResult.getUser().getIdToken(true).getResult();
							// Сохранение токена для дальнейшего использования
							authToken = token.getToken();*//*

							Log.d(TAG, "onComplete authToken: " + authToken);
							// как подождать и только отсюда начать следующее тестирование?
						} else {
							fail("can not login");
							// как закончить работу и прекратить все тесты?
							// System.exit(1);
						}

						// Уменьшаем счетчик после завершения аутентификации
						latch.countDown();
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.d(TAG, "onFailure: " + e.getMessage());
						latch.countDown();
					}
				});

		// this will wait 0 latch
		try {
			latch.await(); // Ожидаем завершения аутентификации
			Log.d(TAG, "setUp: AWAITED");
		} catch (InterruptedException e) {
			Log.d(TAG, "error setUp: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void achievePut() {

		latch  = new CountDownLatch(1);

		AchievementsFbData.achievePut(
				task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "achievePut: Successful ");
						assertTrue(true);
					} else {
						Log.d(TAG, "achievePut: NOT Successful ");
						fail();
					}
					latch.countDown();
				},
				"004", "004", 1711556004L, "04.01.01",
				"rec004", "004", "no");

		try {
			latch.await(); // Ожидаем завершения аутентификации
			Log.d(TAG, "setUp: AWAITED");
		} catch (InterruptedException e) {
			Log.d(TAG, "error setUp: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void AchievementsGet() {
		ArrayList<Spanned> nList = new ArrayList<>();

		latch  = new CountDownLatch(1);

		AchievementsFbData.basicQueryValueListener(new AchievementsFbData.DashboardCallback() {
			@Override
			public void onCallback(ArrayList<Spanned> list) {
//				Log.d(TAG, "onCallback started: ");
				int lSize = nList.size();
				Log.d(TAG, "onCallback size: " + lSize);
				Log.d(TAG, "onCallback list: " + list.toString());
				assertTrue(lSize > 0);
				latch.countDown();
			}

		}, nList);

		try {
			latch.await(); // Ожидаем завершения аутентификации
			Log.d(TAG, "setUp: AWAITED");
		} catch (InterruptedException e) {
			Log.d(TAG, "error setUp: " + e.getMessage());
			e.printStackTrace();
		}
	}
}*/
