/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;


import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.showSnackBarConfirmation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import org.nebobrod.schulteplus.common.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.UserHelper;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

public class LoginActivity extends AppCompatActivity {
	private static final String TAG = "Login";

	private static final String DB_PATH = "users";

	FirebaseAuth fbAuth;
	FirebaseUser user;

	EditText etEmail, etName, etPassword;
	MaterialButton btGoOn;
	TextView tvGoOff;

	ImageView btUnwrapExtra, btWrapExtra;
	LinearLayout llExtras;
	TextView tvResendVerEmail, tvResetPassword, tvDeleteAccount;
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
		user = fbAuth.getCurrentUser();

		// Started with credentials?...
		if (getIntent() != null & getIntent().hasExtra("email"))	{
//			fbUser = getIntent().getExtras().getParcelable("user");
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

			// Check name & pass, extract user data from db,
			if (!validateEmail() | !validateName() | !validatePassword()){
				// do nothing (input errors are handled in validation functions
			} else {
				signInPressed = true;
				progressBar.setVisibility(View.VISIBLE);
//				progressDialog = ProgressDialog.show(getApplicationContext(),
//						"Please wait...", "Retrieving data ...", true);

				// TODO: 01.05.2024   Change to userHelper
				Toast.makeText(LoginActivity.this, "todo in progress...", Toast.LENGTH_SHORT).show();
/*				UserFbData.getUserFromFirebase(new UserFbData.UserHelperCallback() {
					@Override
					public void onCallback(@Nullable UserHelper fbDbUser) {
//						Toast.makeText(LoginActivity.this, fbDbUser.toString(), Toast.LENGTH_SHORT).show();
					}
				}, email);*/

				fbAuth.signInWithEmailAndPassword(email, password)
					.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
						@Override
						public void onSuccess(AuthResult authResult) {
							// check an account for complete registration (repo copies)
							String email = authResult.getUser().getEmail();
							String uid = authResult.getUser().getUid();
							UserHelper userHelper;
							new DataFirestoreRepo<>(UserHelper.class).read(uid)
									.addOnSuccessListener(userHelper1 -> {
										runMainActivity(userHelper1);
										finish();
									})
									.addOnFailureListener(e -> {
										Toast.makeText(LoginActivity.this, getString(R.string.msg_user_data_failed), Toast.LENGTH_SHORT).show();
										Log.w(TAG, "onFailure: " + e.getMessage());
									});
						}
					}).addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							String s = email + " " + getString(R.string.msg_user_login_failed) + " - " + e.getMessage();
							Log.d(TAG, s);
							Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
						}
					});
				progressBar.setVisibility(View.INVISIBLE);
			}
		});

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

				if (!validateEmail()){
					// do nothing (input errors are handled in validation functions
				} else {
					// TODOne: 25.03.2024  can't resend Email for non-authorised users
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
					deleteUser(email, password);
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

	private boolean validateEmail()	{
		String val = etEmail.getText().toString().trim();
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
		if (val.isEmpty()) {
			etPassword.setError(getString(R.string.msg_password_empty));
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
		String _uid = firebaseUser.getUid();
		AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

		firebaseUser.reauthenticate(authCredential)
				.addOnCompleteListener(task -> firebaseUser.delete()
						.addOnCompleteListener(task1 -> {
							String _message;
							String dummyName = Utils.getRandomName();
							// Inform user
							if (task1.isSuccessful()) {
								new DataRepos(UserHelper.class).unpersonalise(_uid, dummyName);
								_message = getRes().getString(R.string.msg_delete_account_success)  + dummyName;
							} else {
								_message = getRes().getString(R.string.msg_delete_account_failed);
							};
							Log.v(TAG, _message);
							showSnackBarConfirmation(this, _message, null);
		}));
	}
}