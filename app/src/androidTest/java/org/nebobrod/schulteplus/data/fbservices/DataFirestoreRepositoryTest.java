/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.os.Looper;

import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.Identifiable;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.data.Turn;
import org.nebobrod.schulteplus.data.UserHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.Exclude;
import com.j256.ormlite.table.DatabaseTable;

import org.mockito.Mockito;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class DataFirestoreRepositoryTest<TEntity extends Identifiable<String>> {
	public static final String TAG = "FirestoreRepositoryTest";


	DataRepository repository;
//	Repository<? extends Identifiable<String>, String> repository;
//	repository = new FirestoreRepository<Achievement>(Achievement.class);

	DataFirestoreRepo fsRepo;



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

		@Override
		public long getTimeStamp() {
			return 0;
		}
	}


	@Before
	public void setUp() {

		TestUtils.performAuthorization();

		// starting init of repo (with "testCollection" from TestEntity)
		repository = new DataFirestoreRepo<>(DataFirestoreRepositoryTest.TestEntity.class);
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
		ExResult exResult = new ExResult(3L, 3L, 3, 3, "3 means 3x3");
		Log.d(TAG, exResult.toTabSeparatedString());
	}

	@Test
	public void testCreateExResult(){
		DataRepos repos;
		repos = new DataRepos(ExResult.class);

		// Создаем объект ExResult
		ExResult exResult = new ExResult(3L,3L, 3, 3, "3 means 3x3");
		repos.put(exResult);
		android.util.Log.d(TAG, "testCreateExResult: exResult" + exResult);

		fsRepo = new DataFirestoreRepo<>(ExResult.class);

		// Пытаемся записать объект в Firestore
		Task<Void> createTask = fsRepo.create(exResult)
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
			fsRepo.exists("001", new DataRepository.RepoCallback<Boolean>() {
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
		fsRepo.exists("2").addOnSuccessListener(new OnSuccessListener<Boolean>() {
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
		DataRepos repos;
		repos = new DataRepos(Achievement.class);

		Achievement achievement = new Achievement().set("uid2", "uak2", "n2", 1711556006L, "05.05.05", "r2", "v2", "m2");
		repos.put(achievement);
		android.util.Log.d(TAG, "testCreateAchievement: achievement" + achievement);

		Identifiable<String> ach;

		ach =  achievement;

		this.repository = new DataFirestoreRepo<>(achievement.getClass());

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
	public void testGetListAchievement() {
		new DataFirestoreRepo<>(Achievement.class).getListLimited(new DataRepository.RepoCallback<List<Achievement>>() {
			@Override
			public void onSuccess(List<Achievement> result) {
				android.util.Log.d(TAG, "onSuccess testGetListAchievement: " + result);
			}

			@Override
			public void onError(Exception e) {
				android.util.Log.e(TAG, "onError testGetListAchievement: " + e.getMessage(), e);
			}
		});
	}

	@Test
	public void testCreateTurn() {

		// Provide mocking id from parent-object to ORMLite dependant objects
		ExResult exResult = new ExResult(3L, 3L, 3, 3, "3 means 3x3");

		DataRepos mockRepos = Mockito.mock(DataRepos.class);

		mockRepos.put(exResult);
		exResult.setId(-1000001); //like if it generated id

		Identifiable<String> data = new Turn(exResult, 1711556007L, 10L, 1, 1, 1, 1, false);


		((Turn)data).setExResult(exResult);

		// Provide an id for ORMLite dependant objects
		DataRepos repos = new DataRepos(data.getClass());
		repos.put(data);
		android.util.Log.d(TAG, "testCreateTurn: data " + data);

		this.repository = new DataFirestoreRepo<>(data.getClass());

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
						Log.e(TAG, "Error writing Data-Object to Firestore", e);
						Assert.assertTrue(false); // Помечаем тест как проваленный
					}
				});

		// Finish synchronised
		TestUtils.testResultAwait(createTask);
	}

	@Test
	public void testCreateUserHelper(){

		Identifiable<String> data = new UserHelper("TFKBiTdd7OVYUaplfzDHrXSCixr1", "nebobzod@gmail.com", "all", "password", "65ed474536cced3a", "65ed474536cced3a", false);

		// Provide an id for ORMLite dependant objects
		DataRepos repos = new DataRepos(data.getClass());
		repos.create(data);


		android.util.Log.d(TAG, "testCreateIdentifiable: data " + data);

		this.repository = new DataFirestoreRepo<>(data.getClass());

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
	public void testGetListLimited() throws ExecutionException, InterruptedException {
		List<Identifiable<String>> data = new ArrayList<>();
		CompletableFuture<List> future = new CompletableFuture<>();

		this.fsRepo = new DataFirestoreRepo<>(Turn.class);

		fsRepo.getListLimited(new DataRepository.RepoCallback<List>() {
			@Override
			public void onSuccess(List result) {
				future.complete(result);
			}

			@Override
			public void onError(Exception e) {
				android.util.Log.w(TAG, "onError in: getListLimited", e);
				future.completeExceptionally(e);
			}
		});

		data = future.get();
		assertFalse(data.isEmpty());
	}

	@Test
	public void z_testReadBackGround() {
		this.fsRepo = new DataFirestoreRepo<>(Turn.class);
		final Task<TEntity> task;

		Executor bgRunner = new AppExecutors().getNetworkIO();
		task = fsRepo.read("10").addOnSuccessListener(bgRunner, new OnSuccessListener() {
			@Override
			public void onSuccess(Object o) {
				android.util.Log.d(TAG, "onSuccess: " + o);
			}
		});

		bgRunner.execute(new Runnable() {
			@Override
			public void run() {
				// wait for finishing the task and
				try {
					Tasks.await(task);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Confirm success
		assertTrue(task.isSuccessful());
	}

	@Test
	public void testReadBackGround() {
		this.fsRepo = new DataFirestoreRepo<>(Turn.class);
		this.fsRepo = new DataFirestoreRepo<>(UserHelper.class);
		String docNum = "-1906027091.007uaked47";

		Log.d(TAG,  " test starts at: main " + Looper.getMainLooper().isCurrentThread() + Thread.currentThread());

		final Task<TEntity> task = fsRepo.read(docNum).addOnSuccessListener( new OnSuccessListener() {
			@Override
			public void onSuccess(Object o) {
				Log.d(TAG," OnSuccessListener Object o: " + o.toString());
				Log.d(TAG," OnSuccessListener main " + Looper.getMainLooper().isCurrentThread() + Thread.currentThread());
			}
		});

		// wait for finishing the task and
		try {
			Tasks.await(task);
			Log.d(TAG,  " after Wait: main " + Looper.getMainLooper().isCurrentThread() + Thread.currentThread().toString());
			assertNotNull("Must be not null", task.getResult().getEntityKey());
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

/*		final Task<TEntity> task = fsRepo.read("110").continueWithTask(bgRunner, new Continuation<DocumentSnapshot, TEntity>() {
			@Override
			public TEntity then(@NonNull Task task) throws Exception {
				android.util.Log.d(TAG, "then: " + task.getResult());;
				return null;
			}
		});*/ // Continuation doesn't work (exception)

/*		final Task<TEntity> task = fsRepo.read("110").addOnCompleteListener(bgRunner, new OnCompleteListener<TEntity>() {
			@Override
			public void onComplete(@NonNull Task<TEntity> task) {
				if (task.isSuccessful()) {
					TEntity result = task.getResult();
					new AppExecutors().mainThread().execute(new Runnable() {
						@Override
						public void run() {
							android.util.Log.d(TAG, Thread.currentThread() + " onSuccess: " + result);
						}
					});


					// Confirm success
					assertTrue(true);
				} else {
					Exception e = task.getException();
					android.util.Log.e(TAG, "Error reading document", e);
					// Confirm failure
					assertFalse(true);
				}
			}
		});*/ // OnComplete doesn't work (lies)
	}

	@Test
	public void getListByField() {
		this.fsRepo = new DataFirestoreRepo<>(UserHelper.class);
		String field = "id";
		int value = -990303179; 	// "-990303179.65ed474536cced3a"
		Task<?> _task;

		_task = fsRepo.getListByField(field, value).addOnSuccessListener(o -> {
			Log.d(TAG, "getListByField: " + o);
			assertTrue(true);
		}).addOnFailureListener(e -> {
			Log.d(TAG, "getListByField: " + e.getLocalizedMessage());
			assertTrue(false);
		});


		TestUtils.testResultAwait(_task);
	}

	@Test
	public void printListByField() {
		this.fsRepo = new DataFirestoreRepo<>(UserHelper.class);
		String field = "id";
		String value = "-990303179"; 	// "-990303179.65ed474536cced3a"
		Task<?> _task;

		_task = fsRepo.printListByField().addOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(@NonNull Task task) {
				assertFalse(false);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				assertTrue(false);
			}
		});

		TestUtils.testResultAwait(_task);
	}


	@Test
	public void unpersoniliseTest() {
//		this.fsRepo = new DataFirestoreRepo<>(UserHelper.class);
		this.fsRepo = new DataFirestoreRepo<>(Achievement.class);
		String _uid = "MhBXs9apwObZc0wFz8zesERLdIB2";
		String _name = "upDated";
		Task<?> _task;

		_task = fsRepo.unpersonilise(_uid, _name).addOnSuccessListener(o -> {
			Log.d(TAG, "unpersoniliseTest: " + o);
			assertTrue(true);
		}).addOnFailureListener(e -> {
			Log.d(TAG, "unpersoniliseTest: " + e.getLocalizedMessage());
			fail();
		});

		TestUtils.testResultAwait(_task);
	}

}