package org.nebobrod.schulteplus.fbservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.nebobrod.schulteplus.MainActivity;
import org.nebobrod.schulteplus.R;

public class LoginActivity extends AppCompatActivity {
	private static final String TAG = "Login";
	private static final String DB_PATH = "users";

	EditText etName, etPassword;
	MaterialButton btGoOn;
	TextView tvGoOff;
	FirebaseDatabase fbDatabase;
	DatabaseReference fbReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_login);

		etName = findViewById(R.id.et_name);
		etPassword = findViewById(R.id.et_pass);
		btGoOn = findViewById(R.id.bt_go_on);
		tvGoOff = findViewById(R.id.tv_go_off);

		btGoOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (!validateName() | !validatePassword()){
					// do nothing (input errors are handled in validation functions
				} else {
					checkUserInFirebase();
				}
			}
		});

		tvGoOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "Go off proceeded for registration" );

				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
			}
		});
	}

	private boolean validateName(){
		String val = etName.getText().toString();
		if (val.isEmpty()) {
			etName.setError(getString(R.string.msg_username_empty));
			return false;
		} else {
			etName.setError(null);
			return true;
		}
	}

	private boolean validatePassword(){
		String val = etPassword.getText().toString();
		if (val.isEmpty()) {
			etPassword.setError(getString(R.string.msg_password_empty));
			return false;
		} else {
			etPassword.setError(null);
			return true;
		}
	}

	private void checkUserInFirebase(){
		String name = etName.getText().toString().trim();
		String password = etPassword.getText().toString().trim();

		DatabaseReference reference = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app").getReference(DB_PATH);
		Query checkUserDatabase = reference.orderByChild("username").equalTo(name);

		checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists()) {
					etName.setError(null);
					String passwordFromDB = snapshot.child(name).child("password").getValue(String.class);

					if (!passwordFromDB.equals(password)) {
						etName.setError(null);

						Log.d(TAG, "Go on proceeded for user: " + name);
						Toast.makeText(LoginActivity.this, "success!", Toast.LENGTH_SHORT).show();

						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
					} else {
						etPassword.setError(getString(R.string.msg_password_wrong));
						etPassword.requestFocus();
					}
				} else {
					etName.setError(getString(R.string.msg_username_wrong));
					etName.requestFocus();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}
}