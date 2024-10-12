package com.example.transitsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileDetails extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // UI Components
    private TextView itemText1, itemDescription;
    private ImageView userImage;
    private RelativeLayout homeButton;
    private RelativeLayout logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        // Initialize Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize views from the layout
        itemText1 = findViewById(R.id.item_text_1);
        itemDescription = findViewById(R.id.email_description);

        userImage = findViewById(R.id.user_image);
        ImageView logoutIcon = findViewById(R.id.logout_icon);

        homeButton = findViewById(R.id.home_btn);
        logOutButton = findViewById(R.id.logout_btn);


        // Set default profile image
        userImage.setImageResource(R.drawable.baseline_supervised_user_circle_24);

        // Set icon colors dynamically
        logoutIcon.setColorFilter(ContextCompat.getColor(this, R.color.black));

        // Load user profile information
        loadUserProfile();

        // Set click listener for home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });

        // Set click listener for logout button
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    private void loadUserProfile() {
        // Get the currently logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Get the user's unique ID
            String userId = currentUser.getUid();

            // Fetch user data from Firebase Realtime Database
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve user profile data
                        String email = dataSnapshot.child("emailId").getValue(String.class);
                        String name = dataSnapshot.child("fullName").getValue(String.class);

                        // Set profile information dynamically
                        itemText1.setText(name != null ? name : "N/A");
                        itemDescription.setText("Email: " + (email != null ? email : "N/A"));
                    } else {
                        Toast.makeText(ProfileDetails.this, "Failed to load profile information.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Toast.makeText(ProfileDetails.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If no user is logged in, show an error
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToHome() {
        // Redirect to the MainActivity screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        // Sign out the user from Firebase
        mAuth.signOut();

        // Show a toast message
        Toast.makeText(ProfileDetails.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity
        Intent intent = new Intent(ProfileDetails.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        startActivity(intent);
        finish(); // Finish the ProfileActivity
    }
}