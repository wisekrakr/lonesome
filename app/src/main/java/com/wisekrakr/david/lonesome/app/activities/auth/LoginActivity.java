package com.wisekrakr.david.lonesome.app.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.wisekrakr.david.lonesome.R;
import com.wisekrakr.david.lonesome.app.actions.AuthActions;
import com.wisekrakr.david.lonesome.app.activities.navigation.DashboardActivity;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseContext;
import com.wisekrakr.david.lonesome.client.firebase.FirebaseRunner;
import com.wisekrakr.david.lonesome.utils.CheckUserStatus;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    // views
    private EditText emailText, passwordText;
    private Button signInBtn;
    private TextView notYetMember, recoverPasswordText;
    private SignInButton signInGoogleBtn;

    // progressbar to display while user login
    private ProgressBar progressBar;
    private AuthActions authActions;

    //FirebaseRunner has all firebase handlers
    private FirebaseRunner firebaseRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //actionbar and title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login to your account");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //init firebase
        firebaseRunner = new FirebaseRunner();
        firebaseRunner.initFirebaseAssistant();

        //init views
        emailText = findViewById(R.id.email_input);
        passwordText = findViewById(R.id.password_input);
        recoverPasswordText = findViewById(R.id.recover_password_text);
        signInBtn = findViewById(R.id.signIn_btn);
        signInGoogleBtn = findViewById(R.id.google_signIn_btn);
        notYetMember = findViewById(R.id.notYetMember_text);

        progressBar = findViewById(R.id.progressbar);

        //init actions
        authActions = new AuthActions();

        //handle sign in button click
        signInClick();

        //handle google sign in button click
        signInGoogleClick();

        //handle register textview clicklistener
        notYetMemberClick();

        //handle recover password textview clicklistener
        recoverPasswordClick();
    }


    private void signInClick(){
        signInBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("click");
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and focus on email edittext
                    emailText.setError("Invalid Email");
                    emailText.setFocusable(true);
                }
                else if(password.length()<8){
                    //set error and focus to password edittext
                    passwordText.setError("Password length must be greater than 7 characters");
                    passwordText.setFocusable(true);
                }else{
                    loginUser(email, password);
                    progressBar.setVisibility(View.VISIBLE); // To show the ProgressBar
                }
            }
        });

    }

    /**
     * Handles signing into google without firebase authentication
     */
    private void signInGoogleClick() {
        signInGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //google login process
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
    }

    private void loginUser(String email, String password) {
        //when email and password are valid, login the user and show progress bar
        progressBar.setVisibility(View.VISIBLE);

        firebaseRunner.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);

                            // Sign in success, dismiss progressbar and start login activity
                            FirebaseUser user = firebaseRunner.getFirebaseAuth().getCurrentUser();

                            //user is logged in, start profile activity
                            Toast.makeText(LoginActivity.this, "Logged in \n" +user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error, dismiss progress bar and get and show the error message
                progressBar.setVisibility(View.INVISIBLE); // To hide the ProgressBar
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void notYetMemberClick(){
        notYetMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void recoverPasswordClick(){
        recoverPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Recover Password");

                //set linear layout
                LinearLayout linearLayout = new LinearLayout(LoginActivity.this);

                //view to set in dialog
                final EditText emailText = new EditText(LoginActivity.this);
                emailText.setHint("Email");
                emailText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                //sets a min width for the email Text View
                emailText.setMinEms(16);

                //add email to linear layout
                linearLayout.addView(emailText);
                linearLayout.setPadding(10,10,10,10);

                //add linear layout to builder and set view
                builder.setView(linearLayout);

                //recover button
                builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //input email
                        String email = emailText.getText().toString().trim();
                        startRecoveryProcess(email);
                    }
                });

                //cancel button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });

                //show dialog
                builder.create().show();
            }
        });
    }

    /**
     * Starts the recovery process for a password
     *
     * @param email String
     */
    private void startRecoveryProcess(String email) {
        //when email is being send, show progressbar
        progressBar.setVisibility(View.VISIBLE);



        firebaseRunner.getFirebaseAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){

                            Toast.makeText(LoginActivity.this, "It worked! Email sent", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "Oh no, sending email failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                //show error message
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseRunner.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseRunner.getFirebaseAuth().getCurrentUser();

                            //if user signs in for the first time, get info from google account when logging in with google
                            if( CheckUserStatus.firstSignInWithGoogle(task)){
                                //handle database entry for users
                                authActions.dbCreateUser(firebaseRunner); //todo here is the null exception from profile when logging in with google
                            }

                            //show user email when logged in
                            Toast.makeText(LoginActivity.this, "Logged in \n" +user.getEmail(), Toast.LENGTH_SHORT).show();

                            //user is logged in with google account, start profile activity
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login Failed" , Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //show error message
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
