package com.example.collegealertapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Userlog_in extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CheckBox showPasswordCheckbox;
    private FirebaseAuth firebaseAuth;
    TextView LogIn,not_signed,forgotPasswordTv;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlog_in);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            // User is already logged in, proceed to AdminPage
            startActivity(new Intent(Userlog_in.this, MainActivity.class));
            finish(); // Finish the current activity to prevent user from going back to login page
        }

        emailEditText = findViewById(R.id.emailEt);
        passwordEditText = findViewById(R.id.passwordEt);
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);
        LogIn = findViewById(R.id.LogIn);
        not_signed=findViewById(R.id.not_signed);
        forgotPasswordTv=findViewById(R.id.forgotPasswordTv);
        progressBar=findViewById(R.id.progressBar);

        not_signed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(Userlog_in.this, Usersign_up.class);
                startActivity(myIntent);
            }
        });

        forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle password visibility
                if (isChecked) {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                loginUser(email, password);
            }
        });
    }



    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Admin login successful
                            Toast.makeText(Userlog_in.this, "Login successful", Toast.LENGTH_SHORT).show();
                            // Proceed to admin dashboard or other activity
                            Intent myIntent=new Intent(Userlog_in.this, MainActivity.class);
                            startActivity(myIntent);
                        } else {
                            // Admin login failed
                            Toast.makeText(Userlog_in.this, "Login failed. Please check your credentials"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //send link for forget password
    private void showForgotPasswordDialog() {
        EditText resetEmail = new EditText(this);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link.");
        passwordResetDialog.setView(resetEmail);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mail = resetEmail.getText().toString().trim();
                sendPasswordResetEmail(mail);
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close the Dialog
            }
        });

        passwordResetDialog.create().show();
    }
    private void sendPasswordResetEmail(String email) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        // Hide progress bar on success
                        progressBar.setVisibility(View.GONE);
                        Utils.toast(Userlog_in.this, "Reset Link Sent To Your Email.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Hide progress bar on failure
                        progressBar.setVisibility(View.GONE);
                        Utils.toast(Userlog_in.this, "Error! Reset Link is Not Sent" + e.getMessage());
                    }
                });
    }
}