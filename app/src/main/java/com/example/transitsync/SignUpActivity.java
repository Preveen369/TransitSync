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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText name, email, password;
    private Button signupButton, loginBtn;
    private DatabaseReference mDatabase; // Reference to the Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        name = findViewById(R.id.name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginBtn = findViewById(R.id.login_link);

        // Set up sign-up button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        // Set up login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    // Method to handle user sign-up
    private void signup() {

        final String fullName = name.getText().toString().trim();
        final String emailId = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(fullName)) {
            name.setError("Full name cannot be empty");
            name.requestFocus();
        } else if (TextUtils.isEmpty(emailId) || !Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
        } else if (TextUtils.isEmpty(userPassword) || userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
        } else {
            // Firebase Authentication to create a new user
            mAuth.createUserWithEmailAndPassword(emailId, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // If account creation is successful, store user details in the Firebase Database
                        Toast.makeText(SignUpActivity.this, "Account registered successfully", Toast.LENGTH_SHORT).show();

                        // Get the unique user ID from Firebase Authentication
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        // Create a new User object and store it in the database
                        User user = new User(fullName, emailId);
                        mDatabase.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Redirect to the main activity after successful registration
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish(); // Close the current activity
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // Handle failed account creation
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(SignUpActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // User class to represent user data in the Firebase Realtime Database
    public static class User {
        public String fullName;
        public String emailId;

        // Default constructor required for Firebase Database operations
        public User() {
        }

        // Constructor to set user details
        public User(String fullName, String emailId) {
            this.fullName = fullName;
            this.emailId = emailId;
        }
    }
}
