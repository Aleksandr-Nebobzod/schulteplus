/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Set of utilities for testing
 */
public class TestUtils {
	public static final String TAG = "TestUtils";

	static CountDownLatch latch;

	protected static void performAuthorization() {
		// CountDownLatch for asynchronous responses
		latch = new CountDownLatch(1);

		//  Firebase Authentication
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		// firebaseAuth.useEmulator("localhost", 9099); // for a local Firebase environment

		// Run Authentication
		com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> authTask =
				firebaseAuth.signInWithEmailAndPassword("tester01@attentions.org", "tester01")
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
/*							AuthResult authResult = task.getResult();
							GetTokenResult token = authResult.getUser().getIdToken(true).getResult();
							// Сохранение токена для дальнейшего использования
							authToken = token.getToken();*/
							Log.d(TAG, "onComplete isSuccessful");
						} else {
							fail("can not login");
							// как закончить работу и прекратить все тесты?
							// System.exit(1);
						}

						// как подождать и только отсюда начать следующее тестирование? -- см. try ниже
						// Уменьшаем счетчик после завершения аутентификации
						latch.countDown();
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						android.util.Log.d(TAG, "onFailure: " + e.getMessage());
						latch.countDown();
					}
				});

		// this will wait 0 latch
		try {
			latch.await(); // Waiting for zero latch (trigger)
			android.util.Log.d(TAG, "setUp: AWAITED");
		} catch (InterruptedException e) {
			android.util.Log.d(TAG, "error setUp: " + e.getMessage());
			e.printStackTrace();
		}
	}


	@Test
	public static void testResultAwait(Task<Void> task) {

		// wait for finishing the task and
		try {
			Tasks.await(task);
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// Confirm success
		assertTrue(task.isSuccessful());
	}

	public static byte[] serialize(Serializable object) throws Exception {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		objectStream.writeObject(object);
		objectStream.close();
		return byteStream.toByteArray();
	}

	public static Serializable deserialize(byte[] bytes) throws Exception {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objectStream = new ObjectInputStream(byteStream);
		Serializable object = (Serializable) objectStream.readObject();
		objectStream.close();
		return object;
	}

	public void sendLogMessage(final String message) {
		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
			@Override
			public void run() {
				android.util.Log.d(TAG, message);
			}
		});
	}
}
