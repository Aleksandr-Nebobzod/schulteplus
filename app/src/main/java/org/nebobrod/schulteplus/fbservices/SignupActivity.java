package org.nebobrod.schulteplus.fbservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

public class SignupActivity extends AppCompatActivity implements UserFbData.UserCallback {
	private static final String TAG = "Signup";
	private static final String NAME_REG_EXP = "^[a-z][[a-z]![0-9]]{3,14}$";

	private FirebaseAuth auth;

	EditText etEmail, etName, etPassword;
	MaterialButton btGoOn;
	TextView tvGoOff;

	UserFbData fbDbUser;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_signup);

		auth = FirebaseAuth.getInstance();

		etEmail = findViewById(R.id.et_email);
		etName = findViewById(R.id.et_name);
		etPassword = findViewById(R.id.et_pass);
		btGoOn = findViewById(R.id.bt_go_on);
		tvGoOff = findViewById(R.id.tv_go_off);

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

					auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> taskAddUser) {
							if (taskAddUser.isSuccessful()) {
								resMessage[0] = name + " " + getString(R.string.msg_user_signed_up);
								auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
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
								FirebaseUser user = auth.getCurrentUser();
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

		{ // asynchronous calling
			if (UserFbData.isExist(signupCallback, val)) {
				etName.setError(getString(R.string.msg_username_exists));
				return false;
			}
		}

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

	@Override
	public void onCallback(UserHelper value) { }

	private UserFbData.UserCallback signupCallback = new UserFbData.UserCallback()
	{
		@Override
		public void onCallback(UserHelper fbDbUser)
		{
			Log.d(TAG, "onCallback: " + fbDbUser);
		}
	};
}