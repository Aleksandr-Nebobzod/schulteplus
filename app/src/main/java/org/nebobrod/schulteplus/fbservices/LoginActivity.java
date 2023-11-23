package org.nebobrod.schulteplus.fbservices;


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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.nebobrod.schulteplus.MainActivity;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

public class LoginActivity extends AppCompatActivity implements UserFbData.UserHelperCallback {
	private static final String TAG = "Login";

	private static final String DB_PATH = "users";

	FirebaseAuth fbAuth;
	FirebaseUser user;

	EditText etEmail, etName, etPassword;
	MaterialButton btGoOn;
	TextView tvGoOff;

	ImageView btUnwrapExtra, btWrapExtra;
	LinearLayout llExtras;
	TextView tvResendVerEmail, tvResetPassword;
	boolean nameExists = true;
	boolean signInPressed = false; // this is to prevent Instant Verification of FB


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
		user = fbAuth.getCurrentUser();

/*		if (user != null) {
			isLoginAuthorized(user);
			finish();
		}*/

		// Started with credentials?...
		if (getIntent() != null & getIntent().hasExtra("email"))
		{
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

		btGoOn.setOnClickListener(view -> {
			String email = etEmail.getText().toString().trim();
			String name = etName.getText().toString().trim();
			String password = etPassword.getText().toString().trim();

			// Check name & pass, extract user data from db,
			if (!validateEmail() | !validateName() | !validatePassword()){
				// do nothing (input errors are handled in validation functions
			} else {
				signInPressed = true;

				fbAuth.signInWithEmailAndPassword(email, password)
					.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
						@Override
						public void onSuccess(AuthResult authResult) {

							isLoginAuthorized(authResult.getUser());
						}
					}).addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							String s = email + " " + getString(R.string.msg_user_login_failed) + e.getMessage();
							Log.d(TAG, s);
							Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
						}
					});
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

	}

	private void isLoginAuthorized(FirebaseUser user) {
		String strMessage, email = user.getEmail();

		UserFbData.isExist(LoginActivity.this::onCallback, email.replace(".", "_"));

//		finish();
	}

	@Override
	public void onCallback(UserHelper fbDbUser)
	{
			if (fbDbUser != null) runMainActivity(fbDbUser);
			finish();
		/*if(signInPressed) {
		} else {
			for (int i = 0; (i < 10 & nameExists); i++ ){
				if (fbDbUser == null) {
					nameExists = false;
					break;
				} else {
					String mName = Utils.getRandomName();
					etName.setText(mName);
					UserFbData.isNameExist(this, mName);
				}
			}
		}*/


	}

/*	private UserFbData.UserCallback loginCallback = new UserFbData.UserCallback()
	{
		@Override
		public void onCallback(UserHelper fbDbUser) { Log.d(TAG, "onCallback: " + fbDbUser); }
	};*/


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

	private boolean validateName()
	{
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

	private void runMainActivity(UserHelper user)
	{
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
		finish();
	}



/*	@Override
	public void onSuccess(Object o)
	{
		Log.d(TAG, "onSuccess: " + o.toString());
//		mTaskListener();

	}*/
}