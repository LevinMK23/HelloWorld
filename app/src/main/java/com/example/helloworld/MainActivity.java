package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText userName, userSurname, email, password;
    private Button reg, auth;
    private FirebaseAuth authService;
    private DatabaseReference ref;

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
            String nameValue = userName.getText().toString();
            String surnameValue = userSurname.getText().toString();
            authService.createUserWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String id = authService.getCurrentUser().getUid();
                            Log.d("user_id", id);
                            HashMap<String, String> values = new HashMap<>();
                            values.put("email", emailValue);
                            values.put("name", nameValue);
                            values.put("surname", surnameValue);
                            values.put("userId", id);
                            ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
                            ref.setValue(values).addOnCompleteListener(task1 -> {
                                if (!task1.isSuccessful()) {
                                    Log.d("firebase", "record_fail");
                                }
                            });
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
                                ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                                        intent.putExtra("userId", user.getUserId());
                                        startActivity(intent);
                                        finish();
                                        if (user != null) {
                                            Log.d("users_info", user.toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                Log.d("userId", user.getUid());
                            }
                        } else {
                            Log.d("userId", "User not found");
                        }
                    });
        });
    }
}
