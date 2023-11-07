package org.nebobrod.schulteplus.fbservices;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.nebobrod.schulteplus.MainActivity;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements UserFbData.UserCallback {
	private static final String TAG = "Login";
	private static final String NAME_REG_EXP = "^[a-z][[a-z]![0-9]]{3,14}$";
	private static final String DB_PATH = "users";

	FirebaseAuth fbAuth;
	FirebaseUser fbUser;

	EditText etEmail, etName, etPassword;
	MaterialButton btGoOn;
	TextView tvGoOff;

	ImageView btUnwrapExtra, btWrapExtra;
	LinearLayout llExtras;
	TextView tvResendVerEmail, tvResetPassword, tvAlternativeReg, tvContinueUnregistered;
	boolean nameExists = true;
	boolean signInPressed = false; // this is to prevent Instant Verification of FB

	FirebaseAuth.AuthStateListener mAuthListener;
//	OnSuccessListener mTaskListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_login);

		etEmail = findViewById(R.id.et_email);
		etName = findViewById(R.id.et_name);
		etPassword = findViewById(R.id.et_pass);
		btGoOn = findViewById(R.id.bt_go_on);
		tvGoOff = findViewById(R.id.tv_go_off);

		fbAuth = FirebaseAuth.getInstance();
		if (getIntent() != null & getIntent().hasExtra("email"))
		{
//			fbUser = getIntent().getExtras().getParcelable("user");
			etEmail.setText(getIntent().getExtras().getString("email",""));
			etName.setText(getIntent().getExtras().getString("name", ""));
			etPassword.setText(getIntent().getExtras().getString("password", ""));
		}
		{
			String mName = "nebobzod"; // This name is definitely busy
//			etName.setText(mName);
			nameExists = true;
			UserFbData.isNameExist(this::onCallback, mName);
		}

		btUnwrapExtra = findViewById(R.id.bt_unwrap_extra);
		btWrapExtra = findViewById(R.id.bt_wrap_extra);
		llExtras = findViewById(R.id.ll_extras);
		tvResendVerEmail = findViewById(R.id.tv_resend_verification_email);
		tvResetPassword = findViewById(R.id.tv_reset_password);
		tvAlternativeReg = findViewById(R.id.tv_alternative_reg);
		tvContinueUnregistered = findViewById(R.id.tv_continue_unregistered);

		// Choose authentication providers
		List<AuthUI.IdpConfig> providers = Arrays.asList(
				new AuthUI.IdpConfig.EmailBuilder().build());
//				new AuthUI.IdpConfig.PhoneBuilder().build(), // -- we can do it later
//				new AuthUI.IdpConfig.GoogleBuilder().build(),
//				new AuthUI.IdpConfig.FacebookBuilder().build(),
//				new AuthUI.IdpConfig.TwitterBuilder().build()
//		);

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
					Toast.makeText(LoginActivity.this, "AuthUI launcher: " + result, Toast.LENGTH_LONG).show();
				});

		// This should listen events of authentication
		/*mTaskListener = new OnSuccessListener() {
			@Override
			public void onSuccess(Object o) {
				Log.d(TAG, "onSuccess: " + o.toString());
			}
		};*/

		// This listener updates UserDisplayName in case of success authentication
		mAuthListener = new FirebaseAuth.AuthStateListener()
		{
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				String mName = "null"; // etName.getText().toString().trim();

				if (user == null) return;

				if (signInPressed) {
					// ensuring Name for anonymous user
					if (user.isAnonymous()) {

					}
				} else {
					user = null; // Here we can clean autologin (default applied token from previous session)
				}

				if(user!=null){
					// update name in authDB
					UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
							.setDisplayName(mName).build();

					user.updateProfile(profileUpdates)

/*							.addOnSuccessListener(new OnSuccessListener<Void>() {
								@Override
								public void onSuccess(Void unused) {

								}
							})*/
							.addOnCompleteListener(new OnCompleteListener<Void>() { // this is just for fun
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										Log.d(TAG, "User profile updated. ");
									}
								}
							});
				} else {
					firebaseAuth.signOut();
				}
			}
		};

		btGoOn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				String email = etEmail.getText().toString().trim();
				String name = etName.getText().toString().trim();
				String password = etPassword.getText().toString().trim();

				// Check name & pass, extract user data from db,
				if (!validateName() | !validatePassword()){
					// do nothing (input errors are handled in validation functions
				} else {
					signInPressed = true;

					fbAuth.signInWithEmailAndPassword(email, password)
						.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
							@Override
							public void onSuccess(AuthResult authResult) {
								FirebaseUser user = fbAuth.getCurrentUser();
								if (user.isEmailVerified()){
									String s = name + " " + getString(R.string.msg_user_logged_in);
									Log.d(TAG, s);
								} else {
									String s = name + ", " + getString(R.string.msg_user_unverified);
									Log.d(TAG, s);
//									showSnackbar(btGoOn, s, getString(R.string.lbl_go_on) );
								}

								UserFbData.isExist(LoginActivity.this::onCallback, email.replace(".", "_"));

//									finish();
							}
						}).addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								String s = name + " " + getString(R.string.msg_user_login_failed) + e.getMessage();
								Log.d(TAG, s);
								Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
							}
						});
				}
			}
		});

		tvGoOff.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				Log.d(TAG, "Go off proceeded for registration" );

				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
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

		tvContinueUnregistered.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {

				signInAnonymously();
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

	private boolean validateName()
	{
		String val = etName.getText().toString().trim();

/*		if (!val.matches(NAME_REG_EXP)) {
			etName.setError(getString(R.string.msg_username_rules));
			return false;
		}
		if (!UserFbData.isExist(this, val)) {
			etName.setError(getString(R.string.msg_username_doesnt_exists));
			return false;
		}
*/
		etName.setError(null);
		return true;

	}

	@Override
	public void onCallback(UserHelper fbDbUser)
	{
		if(signInPressed) {
			if (fbDbUser != null) runMainActivity(fbDbUser);
		} else {
			for (int i = 0; (i < 10 & nameExists); i++ ){
				if (fbDbUser == null) {
					nameExists = false;
					break;
				} else {
					String mName = Utils.getRandomName();
					etName.setText(mName);
					UserFbData.isNameExist(this::onCallback, mName);
				}
			}
		}


	}

/*	private UserFbData.UserCallback loginCallback = new UserFbData.UserCallback()
	{
		@Override
		public void onCallback(UserHelper fbDbUser) { Log.d(TAG, "onCallback: " + fbDbUser); }
	};*/

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

	private void signInAnonymously()
	{
		if (nameExists) {
			Toast.makeText(this, "Try later with another name", Toast.LENGTH_SHORT).show();
			String mName = Utils.getRandomName();
			etName.setText(mName);
			UserFbData.isNameExist(this::onCallback, mName);
			return;
		}

		signInPressed = true;

/*		if (UserFbData.isExist(this::onCallback, mName)) {
			Toast.makeText(LoginActivity.this, getString(R.string.msg_cant_ensure_username), Toast.LENGTH_SHORT).show();
			return;
		}*/
//		printQuery(loginCallback, mName);

		// [START sign_in_anonymously]
		fbAuth.signInAnonymously()
/*				.addOnSuccessListener(new OnSuccessListener<AuthResult>()
				{
					@Override
					public void onSuccess(AuthResult authResult) {
						Log.d(TAG, "onSuccess: from Anonymous ");
					}
				})*/
				.addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							FirebaseUser user = fbAuth.getCurrentUser();
							Log.d(TAG, getString(R.string.msg_signin_anonymously_success) +" UID: "+ user.getUid());
							// name either from screen as generated before
							String _name = etName.getText().toString().trim();
							String _device = Utils.getDeviceId(LoginActivity.this);

							UserFbData fbDbUser = new UserFbData();
							fbDbUser.addUser(_name +  "@email.com", _name, "password", user.getUid(), _device, false);
							Log.d(TAG, "onComplete signIn Anon: " + fbDbUser);
							runMainActivity(UserFbData.getFbUserHelper());
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, getString(R.string.msg_signin_anonymously_failure), task.getException());
							Toast.makeText(LoginActivity.this, getString(R.string.msg_signin_anonymously_failure),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
		// [END sign_in_anonymously]

	}

	private void runMainActivity(UserHelper user)
	{
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
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


/*	@Override
	public void onSuccess(Object o)
	{
		Log.d(TAG, "onSuccess: " + o.toString());
//		mTaskListener();

	}*/
}