package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText userName, userSurname, email, password;
    private Button reg, auth;
    private FirebaseAuth authService;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = authService.getCurrentUser();
        if (user != null) {
            Log.d("userId", user.getUid());
        } else {
            Log.d("userId", "not auth");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = findViewById(R.id.userName);
        userSurname = findViewById(R.id.userSurname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        reg = findViewById(R.id.reg);
        auth = findViewById(R.id.auth);
        authService = FirebaseAuth.getInstance();
        reg.setOnClickListener(view -> {
            String emailValue = email.getText().toString();
            String passwordValue = password.getText().toString();
            authService.createUserWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "REGISTER ERROR!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        auth.setOnClickListener(view -> {
            String emailValue = email.getText().toString();
            String passwordValue = password.getText().toString();
            authService.signInWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = authService.getCurrentUser();
                            if (user != null) {
                                Log.d("userId", user.getUid());
                            }
                        } else {
                            Log.d("userId", "User not found");
                        }
                    });
        });
    }
}
