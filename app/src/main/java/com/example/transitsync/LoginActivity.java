package com.example.transitsync;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button loginButton;
    private Button signupBtn;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signupBtn = findViewById(R.id.signup_link);

        // Set up login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Set up signup link
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the SignUpActivity
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    // Method to handle user login
    private void login() {
        final String emailId = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(emailId) || !Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            password.setError("Password cannot be empty");
            password.requestFocus();
        } else {
            // Firebase Authentication to log in the user
            mAuth.signInWithEmailAndPassword(emailId, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // If authentication is successful, check if the user exists in the database
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        checkIfUserExistsInDatabase(userId);
                    } else {
                        // Handle failed login
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Method to check if the authenticated user exists in the Firebase Realtime Database
    private void checkIfUserExistsInDatabase(final String userId) {
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists in the database, proceed to MainActivity
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish(); // Close the current activity
                } else {
                    // User doesn't exist in the database
                    Toast.makeText(LoginActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                    mAuth.signOut(); // Sign out the user since the database check failed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @noinspection deprecation
     */
    @Override
    public void onBackPressed() {
        // Exit the app instead of going back to the launcher
        super.onBackPressed();
        finishAffinity(); // Close all activities and exit the app
    }
}
