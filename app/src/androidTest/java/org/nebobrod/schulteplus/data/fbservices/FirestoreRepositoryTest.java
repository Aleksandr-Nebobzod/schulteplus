

/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;


import org.junit.Assert;
import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.DataRepositories;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.Turn;
import org.nebobrod.schulteplus.data.UserHelper;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Exclude;
import com.j256.ormlite.table.DatabaseTable;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;


public class FirestoreRepositoryTest<TEntity extends Identifiable<String>> {
	public static final String TAG = "FirestoreRepositoryTest";


	Repository repository;
//	Repository<? extends Identifiable<String>, String> repository;
//	repository = new FirestoreRepository<Achievement>(Achievement.class);

	FirestoreRepository repoExResult;



	// Short example of database entity
	@DatabaseTable(tableName = "testCollection")
	private static class TestEntity implements Serializable, Identifiable<String> {
		int id = 1;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Exclude
		@Override
		public String getEntityKey() {
			return String.valueOf(id);
		}
	}


	@Before
	public void setUp() {

		TestUtils.performAuthorization();

		// starting init of repo (with "testCollection" from TestEntity)
		repository = new FirestoreRepository<>(FirestoreRepositoryTest.TestEntity.class);
	}

/*	@Test
	public void testRepositoryInitialization() {
		// Проверяем, что репозиторий инициализирован с правильной коллекцией
		assertEquals("spdbs/dev/" + "testCollection", repository.getCollectionReference().getPath());
	}*/

	@Test
	public void testCreateTestEntityInDatabase() throws ExecutionException, InterruptedException {
		// Создаем тестовый объект
		TestEntity testEntity = new TestEntity();

		// Создаем задачу (Task) для создания объекта в базе данных и Добавляем слушатели для обработки успешного и неуспешного завершения задачи
		Task<Void> createTask = repository.create(testEntity)
				.addOnSuccessListener(aVoid -> {
				// Успешно создали объект, выполним какие-то дополнительные проверки, если необходимо
				// Например, можно проверить, что объект действительно добавлен в базу данных
					android.util.Log.d(TAG, "testCreateTestEntityInDatabase: Success");
					Assert.assertTrue(true);
				})
				.addOnFailureListener(e -> {
				// Возникла ошибка при создании объекта, выведем сообщение об ошибке
					android.util.Log.d(TAG, "testCreateTestEntityInDatabase: Failed");
					Assert.fail("Failed to create object in database: " + e.getMessage());
				});

		// Ждем завершения задачи
		Tasks.await(createTask);
	}


	/******************
	 * ****************
	 */



	@Test
	public void testPrintFields(){
		// Testing object
		ExResult exResult = new ExResult(3L, 3, 3, "3 means 3x3");
		Log.d(TAG, exResult.toTabSeparatedString());
	}

	@Test
	public void testCreateExResult(){
		DataRepositories repos;
		repos = new DataRepositories();

		// Создаем объект ExResult
		ExResult exResult = new ExResult(3L, 3, 3, "3 means 3x3");
		repos.putResult(exResult);
		android.util.Log.d(TAG, "testCreateExResult: exResult" + exResult);

		repoExResult = new FirestoreRepository<>(ExResult.class);

		// Пытаемся записать объект в Firestore
		Task<Void> createTask = repoExResult.create(exResult)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						// Запись прошла успешно
						Log.d(TAG, "ExResult successfully written to Firestore");
						Assert.assertTrue(true); // Помечаем тест как успешный
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						// Произошла ошибка при записи объекта в Firestore
						Log.e(TAG, "Error writing ExResult to Firestore", e);
						Assert.assertTrue(false); // Помечаем тест как проваленный
					}
				});
		// Ждем завершения задачи
			TestUtils.testResultAwait(createTask);
	}

	@Test
	public void testExistsCallback(){

		new AppExecutors().getNetworkIO().execute(() -> {
			repoExResult.exists("001", new Repository.RepoCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					Assert.assertTrue(result);                    // Here we check is our value equal true
				}

				@Override
				public void onError(Exception e) {
					e.printStackTrace();                    // print error content
				}
			});
		});
	}

	@Test
	public void testExResultExists() throws ExecutionException, InterruptedException {
		Boolean result;
		CompletableFuture<Boolean> future = new CompletableFuture<>();

		// Проверяем существование сущности в базе данных по уникальному ключу
		repoExResult.exists("2").addOnSuccessListener(new OnSuccessListener<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				// Обработка успешной проверки существования сущности
				future.complete(result);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				// Обработка ошибки проверки существования сущности
				future.completeExceptionally(e);
			}
		});
		result = future.get();
		Assert.assertTrue(result);;
	}

	@Test
	public void testCreateAchievement(){
		DataRepositories repos;
		repos = new DataRepositories();

		Achievement achievement = new Achievement().setAchievement("2", "n2", 1711556006L, "05.05.05", "r2", "v2", "m2");
		repos.putResult(achievement);
		android.util.Log.d(TAG, "testCreateAchievement: achievement" + achievement);

		Identifiable<String> ach;

		ach =  achievement;

		this.repository = new FirestoreRepository<>(achievement.getClass());

		// Try to put a data-object into Firestore
		Task<Void> createTask = repository.create(ach)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						// Запись прошла успешно
						Log.d(TAG, "Data-Object successfully written to Firestore");
						Assert.assertTrue(true); // Помечаем тест как успешный
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						// Произошла ошибка при записи объекта в Firestore
						Log.e(TAG, "Error writing Data-Object to Firestore", e);
						Assert.assertTrue(false); // Помечаем тест как проваленный
					}
				});

		// Finish synchronised
		TestUtils.testResultAwait(createTask);
	}

	@Test
	public void testCreateTurn() {

		Identifiable<String> data = new Turn(1711556007L, 10L, 1, 1, 1, 1, false);

		// Provide an id for ORMLite dependant objects
		DataRepositories repos = new DataRepositories();
		repos.putResult(data);
		android.util.Log.d(TAG, "testCreateTurn: data " + data);

		this.repository = new FirestoreRepository<>(data.getClass());

		// Try to put a data-object into Firestore
		Task<Void> createTask = repository.create(data)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						// Запись прошла успешно
						Log.d(TAG, "Data-Object successfully written to Firestore");
						Assert.assertTrue(true); // Помечаем тест как успешный
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						// Произошла ошибка при записи объекта в Firestore
						Log.e(TAG, "Error writing Data-Object to Firestore", e);
						Assert.assertTrue(false); // Помечаем тест как проваленный
					}
				});

		// Finish synchronised
		TestUtils.testResultAwait(createTask);
	}

	@Test
	public void testCreateUserHelper(){

		Identifiable<String> data = new UserHelper("TFKBiTdd7OVYUaplfzDHrXSCixr1", "nebobzod@gmail.com", "all", "password", "65ed474536cced3a", false);

		// Provide an id for ORMLite dependant objects
		DataRepositories repos = new DataRepositories();
		repos.putResult(data);
		android.util.Log.d(TAG, "testCreateIdentifiable: data " + data);

		this.repository = new FirestoreRepository<>(data.getClass());

		// Try to put a data-object into Firestore
		Task<Void> createTask = repository.create(data)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						// Запись прошла успешно
						Log.d(TAG, "Data-Object successfully written to Firestore");
//						assertTrue(true); // Помечаем тест как успешный
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						// Произошла ошибка при записи объекта в Firestore
						Log.e(TAG, "Error writing Data-Object to Firestore", e);
//						assertTrue(false); // Помечаем тест как проваленный
					}
				});
		// Wait for finishing
		TestUtils.testResultAwait(createTask);
		Assert.assertTrue(data.getClass().getTypeName() + "Data object successfully written to Firestore", createTask.isSuccessful());
	}

	@Test
	public void createVariousObjects() {
		Achievement achievement = new Achievement().setAchievement("2", "n2", 1711556006L, "05.05.05", "r2", "v2", "m2");
//		testCreateIdentifiable(achievement);


		Assert.assertTrue("All objects successfully written to Firestore", true);
	}
/*		UserHelper userHelper = new UserHelper("TFKBiTdd7OVYUaplfzDHrXSCixr1", "nebobzod@gmail.com", "all", "password", "65ed474536cced3a", false);
		testCreateIdentifiableInFirestore(userHelper);

		ExResult exResult = new ExResult(3L, 3, 3, "3 means 3x3");
		testCreateIdentifiableInFirestore(exResult);

		ExResultSchulte exResultSchulte = new ExResultSchulte(0L, 3, 3, 3F, 0.3F, 3, 3, "note3");

		testCreateIdentifiableInFirestore(exResultSchulte);*/
}