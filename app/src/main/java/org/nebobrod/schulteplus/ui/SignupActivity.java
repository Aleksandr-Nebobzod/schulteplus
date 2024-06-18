/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import org.nebobrod.schulteplus.common.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.data.AdminNote;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.UserHelper;

import static org.nebobrod.schulteplus.Utils.currentOsVersion;
import static org.nebobrod.schulteplus.Utils.generateUuidInt;
import static org.nebobrod.schulteplus.Utils.getVersionCode;
import static org.nebobrod.schulteplus.common.Const.NAME_REG_EXP;
import static org.nebobrod.schulteplus.common.Const.PASSWORD_REG_EXP;

import java.util.Arrays;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
	private static final String TAG = "Signup";
	EditText etEmail, etName, etPassword;
	MaterialButton btGoOn;
	CheckBox cbAgreed;
	TextView tvPolicy, tvAgreement, tvGoOff, tvAlternativeReg, tvContinueAnonymously, tvContinueUnregistered;
	ImageView btUnwrapExtra, btWrapExtra;
	LinearLayout llExtras;
	DataRepository fsRepo;
	UserHelper userHelper;
	FirebaseAuth.AuthStateListener mAuthListener;
	private FirebaseAuth fbAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_signup);

		fbAuth = FirebaseAuth.getInstance();

		etEmail = findViewById(R.id.et_email);
		etName = findViewById(R.id.et_name);
		etPassword = findViewById(R.id.et_pass);
		btGoOn = findViewById(R.id.bt_go_on);
		cbAgreed = findViewById(R.id.cb_agreed);
		tvPolicy = findViewById(R.id.tv_policy);
		tvAgreement = findViewById(R.id.tv_agreement);
		tvGoOff = findViewById(R.id.tv_go_off);

		btUnwrapExtra = findViewById(R.id.bt_unwrap_extra);
		btWrapExtra = findViewById(R.id.bt_wrap_extra);
		llExtras = findViewById(R.id.ll_extras);
		tvAlternativeReg = findViewById(R.id.tv_alternative_reg);
		tvContinueAnonymously = findViewById(R.id.tv_continue_anonymously);
		tvContinueUnregistered = findViewById(R.id.tv_continue_unregistered);


		if (false == ExerciseRunner.isOnline()){
			etEmail.setEnabled(false);
			etName.setEnabled(false);
			etPassword.setEnabled(false);
			btGoOn.setEnabled(false);
			tvAlternativeReg.setEnabled(false);
			tvContinueAnonymously.setEnabled(false);
//			tvGoOff & tvContinueUnregistered -- active only
			tvContinueUnregistered.setTextColor(getColor(R.color.purple_500));
		}

		// Choose authentication providers for an alternative registration
		List<AuthUI.IdpConfig> providers = Arrays.asList(
				new AuthUI.IdpConfig.EmailBuilder().build());
		/*
				new AuthUI.IdpConfig.PhoneBuilder().build(), // -- we can do it later
				new AuthUI.IdpConfig.GoogleBuilder().build(),
				new AuthUI.IdpConfig.FacebookBuilder().build(),
				new AuthUI.IdpConfig.TwitterBuilder().build()
		);
		*/

		// Create and launch sign-in intent for an alternative registration

		Intent signInIntent = AuthUI.getInstance()
				.createSignInIntentBuilder()
				.setAvailableProviders(providers)
				.setTheme(R.style.GreyTheme)
				.build();

		ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
				new FirebaseAuthUIActivityResultContract(),
				(result) -> {
					// Handle the FirebaseAuthUIAuthenticationResult
					// ...
					Log.d(TAG, "AuthUI launcher: " + result);
					Toast.makeText(SignupActivity.this, "AuthUI launcher: " + result, Toast.LENGTH_LONG).show();
				});


		// Go Register
		btGoOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String[] resMessage = {""};
				String email = etEmail.getText().toString().trim();
				String name = etName.getText().toString().trim();
				String password = etPassword.getText().toString().trim();

				if (!validateEmail() | !validateName() | !validatePassword()){
					// do nothing (input errors are handled in validation functions
				} else	{

					fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> taskAddUser) {
							if (taskAddUser.isSuccessful()) {
								resMessage[0] = name + " " + getString(R.string.msg_user_signed_up);
								fbAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
									@Override
									public void onComplete(@NonNull Task<Void> taskVerifSent) {
										if (taskVerifSent.isSuccessful()) {
											resMessage[0] += " " + getString(R.string.msg_user_verif_sent);
										} else {
											resMessage[0] += " " + getString(R.string.msg_user_verif_not_sent);
										}
									}
								});

								Log.d(TAG, resMessage[0]);

								// Create the repositories copy of the new UserHelper
								FirebaseUser fbUser = fbAuth.getCurrentUser();
								userHelper = new UserHelper(fbUser.getUid(), email, name, password, Utils.getDevId() , Utils.generateUak(),  false);
								DataRepos repos;
								repos = new DataRepos<>(UserHelper.class);
								repos.create(userHelper);		// Since it's a new user
																// no need to check other records in central repo

								Toast.makeText(SignupActivity.this, resMessage[0], Toast.LENGTH_SHORT).show();

								// registration record
								AdminNote firstAdminNote = new AdminNote(generateUuidInt(), userHelper.getUak(), userHelper.getUid(), "SignUp", "Android: " + currentOsVersion(), "", userHelper.getTimeStamp(), getVersionCode(), 0, 0, userHelper.getTimeStamp());
								repos = new DataRepos<>(AdminNote.class);
								repos.create(firstAdminNote);

								runMainActivity(userHelper);
							} else { // In case of unsuccessful registration

								if (taskAddUser.getException().getMessage().contains("A network error")){
									resMessage[0] = name + " " + getString(R.string.msg_user_network_failed);
								} else {
									resMessage[0] = name + " " + getString(R.string.msg_user_signed_up_failed) + taskAddUser.getException().getMessage();
								}
								Log.d(TAG, resMessage[0]);
								Toast.makeText(SignupActivity.this, resMessage[0], Toast.LENGTH_LONG).show();
							}
						}
					});
				}
			}
		});

		// User acceptance of Policy
		cbAgreed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					btGoOn.setEnabled(true);
					tvAlternativeReg.setEnabled(true);
				} else {
					btGoOn.setEnabled(false);
					tvAlternativeReg.setEnabled(false);
				}
			}
		});
		tvPolicy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					Utils.displayHtmlAlertDialog(SignupActivity.this, R.string.str_about_user_data_policy_html_source);
				} catch (Exception e) {
					Log.e(TAG, "user_data_policy dialog opening", e);
				}
			}
		});
		tvAgreement.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					Utils.displayHtmlAlertDialog(SignupActivity.this, R.string.str_about_user_agreement_html_source);
				} catch (Exception e) {
					Log.e(TAG, "user_data_policy dialog opening", e);
				}
			}
		});

		// This listener updates UserDisplayName in case of success authentication
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				String mName = etName.getText().toString().trim();

				if (user == null) return;

				if(user!=null & validateName()){
					// update name in authDB
					UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
							.setDisplayName(mName).build();

					user.updateProfile(profileUpdates)
							.addOnCompleteListener(new OnCompleteListener<Void>() { // this is just for fun
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										Log.d(TAG, "User profile updated with Name: " + mName);
									}
								}
							});
				} else {
//					firebaseAuth.signOut();
				}
			}
		};

		// Goto LoginActivity
		tvGoOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "Go on proceeded for user: " + etName.getText().toString().trim());
				// fill with extras to avoid retyping on Login
				Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
				intent.putExtra("email", etEmail.getText().toString().trim());
				intent.putExtra("name", etName.getText().toString().trim());
				intent.putExtra("password", etPassword.getText().toString().trim());
				intent.putExtra("uid", ""); // user is still not authenticated, do that on LoginActivity
				startActivity(intent);
				finish();
			}
		});

		btUnwrapExtra.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				llExtras.setVisibility(View.VISIBLE);
				btUnwrapExtra.setVisibility(View.INVISIBLE);
			}
		});

		btWrapExtra.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				btUnwrapExtra.setVisibility(View.VISIBLE);
				llExtras.setVisibility(View.GONE);
			}
		});

		tvContinueAnonymously.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				// TODO: 21.04.2024 remove toast & update this & signInAnonymously() function with repository approach in Firestore
				Toast.makeText(SignupActivity.this, "TODO anonymous signup", Toast.LENGTH_LONG).show();
/*
				String val = Utils.getRandomName();
				etName.setText(val);

				if (validateName() == false)  return;

				UserFbData.isNameFree(new UserFbData.NameFreeCallback() {
					@Override
					public void onCallback(boolean isFree) {
						if (isFree) {
							signInAnonymously();
						} else {
							String mName = Utils.getRandomName();

							Toast.makeText(SignupActivity.this, val + " is occupied, try with new name, i.e.: " +mName, Toast.LENGTH_SHORT).show();
							etName.postDelayed(new Runnable() {
								@Override
								public void run() {
									etName.setText(mName);
								}
							}, 1000);
						}
					}
				}, val);*/
			}
		});

		tvContinueUnregistered.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(SignupActivity.this, MainActivity.class));
			}
		});

		tvAlternativeReg.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				signInLauncher.launch(signInIntent);
			}
		});

	}

	private boolean validateEmail()
	{
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

	private boolean validateName ()
	{
		String val = etName.getText().toString().trim();

		if (!val.matches(NAME_REG_EXP)) {
			etName.setError(getString(R.string.msg_username_rules));
			return false;
		}
		// etName.setError(getString(R.string.msg_username_exists));

		etName.setError(null);
		return true;
	}

	private boolean validatePassword()
	{
		String val = etPassword.getText().toString().trim();
		if (!val.matches(PASSWORD_REG_EXP)) {
			etPassword.setError(getString(R.string.msg_password_rules));
			return false;
		} else {
			etPassword.setError(null);
			return true;
		}
	}

	public void signInAnonymously() {
//		signInPressed = true;
		// [START sign_in_anonymously]

		// TODO: 21.04.2024 remove toast & update this & signInAnonymously() function with repository approach in Firestore
		Toast.makeText(SignupActivity.this, "TODO anonymous signup", Toast.LENGTH_LONG).show();
/*		fbAuth.signInAnonymously()
				.addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							FirebaseUser user = fbAuth.getCurrentUser();

							//SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

							Log.d(TAG, getString(R.string.msg_signin_anonymously_success) +" UID: "+ user.getUid());

							if (task.getResult().getAdditionalUserInfo().isNewUser()) {
								// name either from screen as generated before
								UserFbData fbDbUser = new UserFbData();
								String _name = etName.getText().toString().trim();
								String _device = Utils.getDeviceId(SignupActivity.this);

								fbDbUser.addUser(user.getUid(), _name +  "@email.com", _name, "password", _device, false);
								Log.d(TAG, "onComplete signIn Anon: " + fbDbUser);
//							Toast.makeText(LoginActivity.this, _name +  "@email.com" + getString(R.string.msg_signin_anonymously_credentials), Toast.LENGTH_LONG).show();
								Snackbar.make(tvContinueAnonymously,
												_name +  "@email.com" + getString(R.string.msg_signin_anonymously_credentials), Snackbar.LENGTH_INDEFINITE)
										.setAction("✓", null).show();

								runMainActivity(UserFbData.getFbUserHelper());
							}
							else {
								UserFbData.getByUid(signupCallback, user.getUid());
							}

						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG + getString(R.string.msg_signin_anonymously_failure), task.getException().toString());
							Toast.makeText(SignupActivity.this, getString(R.string.msg_signin_anonymously_failure),
									Toast.LENGTH_LONG).show();
						}
					}

				});*/
		// [END sign_in_anonymously]

	}

	private void runMainActivity(UserHelper user)
	{
		Intent intent = new Intent(SignupActivity.this, MainActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
		finish();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		fbAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			fbAuth.removeAuthStateListener(mAuthListener);
		}
	}
}