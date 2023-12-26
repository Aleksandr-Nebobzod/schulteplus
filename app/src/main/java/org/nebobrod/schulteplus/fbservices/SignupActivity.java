package org.nebobrod.schulteplus.fbservices;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.MainActivity;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import static org.nebobrod.schulteplus.Const.NAME_REG_EXP;

import java.util.Arrays;
import java.util.List;

public class SignupActivity extends AppCompatActivity implements UserFbData.UserHelperCallback {
	private static final String TAG = "Signup";

	private FirebaseAuth fbAuth;

	EditText etEmail, etName, etPassword;
	MaterialButton btGoOn;
	TextView tvGoOff, tvAlternativeReg, tvContinueAnonymously, tvContinueUnregistered;

	ImageView btUnwrapExtra, btWrapExtra;
	LinearLayout llExtras;


	UserFbData fbDbUser;
	FirebaseAuth.AuthStateListener mAuthListener;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_signup);

		fbAuth = FirebaseAuth.getInstance();

		etEmail = findViewById(R.id.et_email);
		etName = findViewById(R.id.et_name);
		etPassword = findViewById(R.id.et_pass);
		btGoOn = findViewById(R.id.bt_go_on);
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
		btGoOn.setOnClickListener(new View.OnClickListener()
		{
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

								// Create the fbDB copy of User
								FirebaseUser user = fbAuth.getCurrentUser();
								fbDbUser = new UserFbData();
								fbDbUser.addUser(email, name, password, user.getUid(), Utils.getDeviceId(SignupActivity.this) , false);
//								updateUI(user);

								Toast.makeText(SignupActivity.this, resMessage[0], Toast.LENGTH_SHORT).show();

								// fill with extras to avoid retyping on Login
								Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//								intent.putExtra("user", user); // null username
								intent.putExtra("email", email);
								intent.putExtra("name", name);
								intent.putExtra("password", password);
								intent.putExtra("uid", user.getUid());
								startActivity(intent);
								finish();

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

		// This listener updates UserDisplayName in case of success authentication
		mAuthListener = new FirebaseAuth.AuthStateListener()
		{
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
		tvGoOff.setOnClickListener(new View.OnClickListener()
		{
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

		btUnwrapExtra.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				llExtras.setVisibility(View.VISIBLE);
				btUnwrapExtra.setVisibility(View.INVISIBLE);
			}
		});

		btWrapExtra.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				btUnwrapExtra.setVisibility(View.VISIBLE);
				llExtras.setVisibility(View.GONE);
			}
		});

		tvContinueAnonymously.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
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
				}, val);
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
		if (val.isEmpty()) {
			etPassword.setError(getString(R.string.msg_password_empty));
			return false;
		} else {
			etPassword.setError(null);
			return true;
		}
	}

	public void signInAnonymously() {
//		signInPressed = true;
		// [START sign_in_anonymously]
		fbAuth.signInAnonymously()
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

								fbDbUser.addUser(_name +  "@email.com", _name, "password", user.getUid(), _device, false);
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
							Log.w(TAG, getString(R.string.msg_signin_anonymously_failure), task.getException());
							Toast.makeText(SignupActivity.this, getString(R.string.msg_signin_anonymously_failure),
									Toast.LENGTH_LONG).show();
						}
					}

				});
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
	public void onStop()
	{
		super.onStop();
		if(mAuthListener != null){
			fbAuth.removeAuthStateListener(mAuthListener);
		}
	}

	@Override
	public void onCallback(UserHelper value) { }

	private UserFbData.UserHelperCallback signupCallback = new UserFbData.UserHelperCallback()
	{
		@Override
		public void onCallback(UserHelper fbDbUser)
		{
			Log.d(TAG, "onCallback: " + fbDbUser);
			runMainActivity(fbDbUser);
		}
	};
}