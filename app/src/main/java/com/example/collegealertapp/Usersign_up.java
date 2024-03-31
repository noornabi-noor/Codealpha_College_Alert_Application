package com.example.collegealertapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Usersign_up extends AppCompatActivity {

    EditText emailEditText, passwordEditText, usernameEditText;
    TextView sign_up, already_signed;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    CheckBox showPasswordCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersign_up);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views by their IDs
        emailEditText = findViewById(R.id.emailEt);
        passwordEditText = findViewById(R.id.passwordEt);
        usernameEditText = findViewById(R.id.username);
        sign_up = findViewById(R.id.sign_up);
        progressBar = findViewById(R.id.progressBar);
        already_signed = findViewById(R.id.already_signed);
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);


        already_signed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the current admin user
                mAuth.signOut();

                // Redirect to Adminlog_in class
                Intent myIntent = new Intent(Usersign_up.this, Userlog_in.class);
                startActivity(myIntent);
                finish(); // Finish the current activity to prevent going back to it without logging in
            }
        });

        // Set click listener for the signup button
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input values from EditText fields
                progressBar.setVisibility(View.VISIBLE);

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();

                // Check if any field is empty
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Usersign_up.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check password constraints
                if (!isValidPassword(password)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Usersign_up.this, "Password must be at least 6 characters long and contain at least one special character", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new admin account using Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Usersign_up.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Signup successful, show a success message
                                    Toast.makeText(Usersign_up.this, "User account created successfully", Toast.LENGTH_SHORT).show();
                                    // Redirect to another activity or perform additional actions
                                    Intent myIntent=new Intent(Usersign_up.this, MainActivity.class);
                                    startActivity(myIntent);
                                } else {
                                    // Signup failed, show an error message
                                    Toast.makeText(Usersign_up.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Set listener for the showPasswordCheckbox
        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Show or hide the password based on checkbox state
                if (isChecked) {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    // Method to validate password
    private boolean isValidPassword(String password) {
        // Password must be at least 6 characters long and contain at least one special character
        return password.length() >= 6 && password.matches(".*[!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?].*");
    }
}