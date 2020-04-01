package com.wisekrakr.david.lonesome.app.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.wisekrakr.david.lonesome.R;
import com.wisekrakr.david.lonesome.app.actions.AuthActions;
import com.wisekrakr.david.lonesome.app.activities.navigation.DashboardActivity;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseContext;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseRunner;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "RegisterActivity";

    // views
    private EditText emailText, passwordText;
    private Button signUpBtn;
    private TextView alreadyMember;

    // progressbar to display while registering user
    private ProgressBar progressBar;

    //actions
    private AuthActions authActions;

    //FirebaseRunner has all firebase handlers
    private FirebaseRunner firebaseRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //actionbar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init firebase authentication
        firebaseRunner = new FirebaseRunner();
        firebaseRunner.initFirebaseAssistant();

        //init views
        emailText = findViewById(R.id.email_input);
        passwordText = findViewById(R.id.password_input);
        signUpBtn = findViewById(R.id.signUp_btn);
        alreadyMember = findViewById(R.id.alreadyMember_text);

        progressBar = findViewById(R.id.progressbar);

        //init actions
        authActions = new AuthActions();

        //handle sign up button click
        signUpClick();

        //handle login textview clicklistener
        alreadyMemberClick();

    }

    private void signUpClick(){
        signUpBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and focus on email edittext
                    emailText.setError("Invalid Email");
                    emailText.setFocusable(true);
                }
                else if(password.length()<8){
                    //set error and focus on password edittext
                    passwordText.setError("Password length must be greater than 7 characters");
                    passwordText.setFocusable(true);
                }else{
                    registerUser(email, password);
                    progressBar.setVisibility(View.VISIBLE); // To show the ProgressBar
                }
            }
        });
    }

    private void alreadyMemberClick(){
        alreadyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser(String email, String password) {
        //when email and password are valid register the user and show progress bar
        progressBar.setVisibility(View.VISIBLE);

        firebaseRunner.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);

                            // Sign in success, dismiss progressbar and start register activity
                            FirebaseUser user = firebaseRunner.getFirebaseAuth().getCurrentUser();

                            //handle database entry for users
                            authActions.dbCreateUser(firebaseRunner);

                            //registering user successful, start profile activity
                            Toast.makeText(RegisterActivity.this, "Registered \n" +user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error, dismiss progress bar and get and show the error message
                progressBar.setVisibility(View.INVISIBLE); // To hide the ProgressBar
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activity
        return super.onSupportNavigateUp();
    }
}
