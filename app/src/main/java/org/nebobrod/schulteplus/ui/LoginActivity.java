/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;


import static org.nebobrod.schulteplus.Utils.confirmationDialog;
import static org.nebobrod.schulteplus.Utils.currentOsVersion;
import static org.nebobrod.schulteplus.Utils.generateUuidInt;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.getVersionCode;
import static org.nebobrod.schulteplus.Utils.intStringHash;
import static org.nebobrod.schulteplus.Utils.showSnackBarConfirmation;
import static org.nebobrod.schulteplus.common.Const.PASSWORD_REG_EXP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.nebobrod.schulteplus.common.AppExecutors;
import org.nebobrod.schulteplus.common.Log;

import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.AdminNote;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.UserHelper;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
	private static final String TAG = "Login";

	FirebaseAuth fbAuth;
	FirebaseUser fbUser;

	EditText etEmail, etName, etPassword;
	MaterialButton btGoOn;
	TextView tvGoOff;

	ImageView btUnwrapExtra, btWrapExtra;
	LinearLayout llExtras;
	TextView tvResendVerEmail, tvResetPassword, tvDeleteAccount;
	boolean tvResendVerEmailFlag = false;
	ProgressBar progressBar;
	boolean signInPressed = false; // this is to prevent Instant Verification of FirebaseAuth


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_login);

		etEmail = findViewById(R.id.et_email);
		etName = findViewById(R.id.et_name);
		etPassword = findViewById(R.id.et_pass);
		btGoOn = findViewById(R.id.bt_go_on);
		tvGoOff = findViewById(R.id.tv_go_off);
		progressBar = findViewById(R.id.progress_bar);

		fbAuth = FirebaseAuth.getInstance();
		fbUser = fbAuth.getCurrentUser();

		// Started with credentials?...
		if (getIntent() != null & getIntent().hasExtra("email"))	{
			etEmail.setText(getIntent().getExtras().getString("email",""));
			etName.setText(getIntent().getExtras().getString("name", ""));
			etPassword.setText(getIntent().getExtras().getString("password", ""));
		}

		btUnwrapExtra = findViewById(R.id.bt_unwrap_extra);
		btWrapExtra = findViewById(R.id.bt_wrap_extra);
		llExtras = findViewById(R.id.ll_extras);
		tvResendVerEmail = findViewById(R.id.tv_resend_verification_email);
		tvResetPassword = findViewById(R.id.tv_reset_password);
		tvDeleteAccount = findViewById(R.id.tv_delete_account);

		btGoOn.setOnClickListener(view -> {
			String email = etEmail.getText().toString().trim();
			String password = etPassword.getText().toString().trim();
			final String[] resMessage = {""};

			// Check name & pass, extract user data from db,
			if (!validateEmail() | !validateName() | !validatePassword()){
				// do nothing (input errors are handled in validation functions
			} else {
				signInPressed = true;
				progressBar.setVisibility(View.VISIBLE);
//				progressDialog = ProgressDialog.show(getApplicationContext(),
//						"Please wait...", "Retrieving data ...", true);

				// TODOne: 01.05.2024   Change UserFbData to userHelper

				fbAuth.signInWithEmailAndPassword(email, password)
					.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
						@Override
						public void onSuccess(AuthResult authResult) {

							fbUser = authResult.getUser();
							if (tvResendVerEmailFlag) {
								fbUser.sendEmailVerification()
										.addOnCompleteListener(new OnCompleteListener<Void>() {
											@Override
											public void onComplete(@NonNull Task<Void> taskVerifSent) {
												if (taskVerifSent.isSuccessful()) {
													resMessage[0] += " " + getString(R.string.msg_user_verif_sent);
												} else {
													resMessage[0] += " " + getString(R.string.msg_user_verif_not_sent);
												}
												Toast.makeText(LoginActivity.this, resMessage[0], Toast.LENGTH_LONG).show();
											}
										});
							}
							// check the freshest account for correct login (repo copies)
							String uid = Objects.requireNonNull(fbUser).getUid();
							DataRepos<UserHelper> repos = new DataRepos<>(UserHelper.class);
							repos.getLatestUserHelper(intStringHash(uid))
									.addOnCompleteListener(task -> {
										if (task.isSuccessful()) {
											runMainActivity(task.getResult());
										} else {
											if (task.getException().getCause() instanceof RuntimeException){

												//No actual user record in any repository!
												Toast.makeText(LoginActivity.this, getString(R.string.msg_user_data_renewed), Toast.LENGTH_LONG).show();
												UserHelper userHelper = new UserHelper(fbUser.getUid(), email, "new", password, Utils.getDevId() , Utils.generateUak(),  fbUser.isEmailVerified());

												// Make Note about a new device LogIn
												new DataRepos<>(AdminNote.class).create(
														new AdminNote(generateUuidInt(), userHelper.getUak(), userHelper.getUid(), "LogIn", "Android: " + currentOsVersion(), "", userHelper.getTimeStamp(), getVersionCode(), 0, 0, userHelper.getTimeStamp())
												);
												repos.create(userHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
													@Override
													public void onComplete(@NonNull Task<Void> task) {
														runMainActivity(userHelper);
													}
												});
											} else {
												Toast.makeText(LoginActivity.this, getString(R.string.err_unknown), Toast.LENGTH_SHORT).show();
											}
										}
									});
						}
					}).addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							String s = email + " " + getString(R.string.msg_user_login_failed) + " - " + e.getLocalizedMessage();
							Log.w(TAG, s);
							Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
						}
					});
				progressBar.setVisibility(View.INVISIBLE);
			}
		});

		// Unregistered login with Demo account
		if ("support@attplus.in".equals(etEmail.getText().toString())) {
			lockForDemo();
			btGoOn.performClick();
		}

		tvGoOff.setOnClickListener(new View.OnClickListener()	{
			@Override
			public void onClick(View view) {
				Log.d(TAG, "Go off proceeded for registration" );

				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
			}
		});

		btUnwrapExtra.setOnClickListener(new View.OnClickListener()	{
			@Override
			public void onClick(View view) {
				llExtras.setVisibility(View.VISIBLE);
				btUnwrapExtra.setVisibility(View.INVISIBLE);
			}
		});

		tvResendVerEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String email = etEmail.getText().toString().trim();

				if (!validateEmail() | !validatePassword()){
					// do nothing (input errors are handled in validation functions
				} else {
					tvResendVerEmailFlag = true;
					btGoOn.performClick();
				}
			}
		});
		tvResetPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String email = etEmail.getText().toString().trim();

				if (!validateEmail()){
					// do nothing (input errors are handled in validation functions
				} else {
					fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								String _message = getRes().getString(R.string.msg_password_reset_email_sent);
								showSnackBarConfirmation(LoginActivity.this, _message, null);
								Log.v(TAG, _message);
							}
							else {
								showSnackBarConfirmation(LoginActivity.this, task.getException().getLocalizedMessage(), null);
							}
						}
					});
				}
			}
		});

		tvDeleteAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String email = etEmail.getText().toString().trim();
				String password = etPassword.getText().toString().trim();

				if (!validateEmail()  | !validatePassword()){
					// do nothing (input errors are handled in validation functions
				} else {
					confirmationDialog( LoginActivity.this,
							getString(R.string.str_attention),
							getString(R.string.str_delete_caution),
							(dialogInterface, i) -> { /* OK no-op*/ },
							(dialogInterface, i) -> {deleteUser(email, password);}
					);
				}
			}
		});

		btWrapExtra.setOnClickListener(new View.OnClickListener()	{
			@Override
			public void onClick(View view) {
				btUnwrapExtra.setVisibility(View.VISIBLE);
				llExtras.setVisibility(View.GONE);
			}
		});
	}

	private void lockForDemo() {
		btUnwrapExtra.setEnabled(false);
		etEmail.setEnabled(false);
		etPassword.setEnabled(false);
		tvResetPassword.setEnabled(false);
		tvDeleteAccount.setEnabled(false);
	}


	private boolean validateEmail()	{
		String val = etEmail.getText().toString().trim();

		if ("support@attplus.in".equals(val)) {
			lockForDemo();
		}

		if (val.isEmpty()) {
			etEmail.setError(getString(R.string.msg_email_empty));
			return false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
			etEmail.setError(getString(R.string.msg_email_pattern));
			return false;
		} else {
			etEmail.setError(null);
			return true;
		}
	}

	private boolean validateName()	{
		/*String val = etName.getText().toString().trim();

		if (!val.matches(NAME_REG_EXP)) {
			etName.setError(getString(R.string.msg_username_rules));
			return false;
		}
*//*		if (!UserFbData.isExist(this, val)) {
			etName.setError(getString(R.string.msg_username_doesnt_exists));
			return false;
		}
*//*
		etName.setError(null);*/
		return true;

	}

	private boolean validatePassword()	{
		String val = etPassword.getText().toString().trim();
		if (!val.matches(PASSWORD_REG_EXP)) {
			etPassword.setError(getString(R.string.msg_password_rules));
			return false;
		} else {
			etPassword.setError(null);
			return true;
		}
	}


	private void runMainActivity(UserHelper user)	{
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
		finish();
	}

	private void deleteUser(String email, String password) {
		FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		if (firebaseUser == null) {
			showSnackBarConfirmation(this, getRes().getString(R.string.msg_user_must_login), null);
			return;
		}
		String _uid = firebaseUser.getUid();
		String dummyName = Utils.getRandomName();

		// Replace userdata in central repository
		DataFirestoreRepo<Achievement> achRepo = new DataFirestoreRepo<>(Achievement.class);
		Task<Void> taskAch = achRepo.unpersonilise(_uid, dummyName).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					Log.i(TAG, "onComplete: Achievements rewritten");
				} else {
					Log.w(TAG, "onComplete: Achievements rewriting error " + task.getException().getLocalizedMessage());
				}
			}
		});

		DataFirestoreRepo<ExResult> exResRepo = new DataFirestoreRepo<>(ExResult.class);
		Task<Void> taskExRes = exResRepo.unpersonilise(_uid, dummyName).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					Log.i(TAG, "onComplete: ExResult rewritten");
				} else {
					Log.w(TAG, "onComplete: ExResult rewriting error " + task.getException().getLocalizedMessage());
				}
			}
		});

		DataFirestoreRepo<UserHelper> userRepo = new DataFirestoreRepo<>(UserHelper.class);
		Task<Void> taskUser = userRepo.unpersonilise(_uid, dummyName).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) {
					Log.i(TAG, "onComplete: UserHelper rewritten");
				} else {
					Log.w(TAG, "onComplete: UserHelper rewriting error " + task.getException().getLocalizedMessage());
				}
			}
		});

		Tasks.whenAll(taskAch, taskExRes, taskUser).continueWith(new Continuation<Void, Object>() {
			@Override
			public Object then(@NonNull Task<Void> taskAll) throws Exception {
				// Remove authentication record
				AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
				firebaseUser.reauthenticate(authCredential)
						.addOnCompleteListener(task -> firebaseUser.delete()
								.addOnCompleteListener(task1 -> {
									String _message;

									// Inform user
									if (task1.isSuccessful()) {
										new DataRepos(UserHelper.class).unpersonalise(_uid, dummyName);
										_message = getRes().getString(R.string.msg_delete_account_success)  + " " + dummyName;
									} else {
										_message = getRes().getString(R.string.msg_delete_account_failed);
									};
									Log.v(TAG, _message);
									AppExecutors _appEx = new AppExecutors();
									Executor mainThread = _appEx.mainThread();
									mainThread.execute(() -> showSnackBarConfirmation(LoginActivity.this, _message, null));
								}));
				return null;
			}
		});
	}
}