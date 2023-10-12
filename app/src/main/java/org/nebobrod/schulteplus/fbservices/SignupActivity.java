package org.nebobrod.schulteplus.fbservices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.nebobrod.schulteplus.R;

public class SignupActivity extends AppCompatActivity {
	private static final String TAG = "Signup";
	private static final String DB_PATH = "users";

	EditText etEmail, etName, etPassword;
	MaterialButton btGoOn;
	TextView tvGoOff;
	FirebaseDatabase fbDatabase;
	DatabaseReference fbReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_signup);

		etEmail = findViewById(R.id.et_email);
		etName = findViewById(R.id.et_name);
		etPassword = findViewById(R.id.et_pass);
		btGoOn = findViewById(R.id.bt_go_on);
		tvGoOff = findViewById(R.id.tv_go_off);

		btGoOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fbDatabase = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app");
				fbReference = fbDatabase.getReference(DB_PATH);

				String email = etEmail.getText().toString();
				String name = etName.getText().toString();
				String password = etPassword.getText().toString();

				Helper helper = new Helper(name, email, password, false);
				fbReference.child(name).setValue(helper);

				Log.d(TAG, "Go on proceeded for user: " + name);
				Toast.makeText(SignupActivity.this, "success!", Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
				startActivity(intent);

			}
		});

		tvGoOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "Go on proceeded for user: " + etName.getText().toString());
				// TODO: 12.10.2023 send credentials with intent's extras
				Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});

	}
}