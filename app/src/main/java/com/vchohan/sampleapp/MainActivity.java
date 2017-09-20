package com.vchohan.sampleapp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText userEmail;

    private EditText userPassword;

    private Button signInButton;

    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        //initialize the AuthStateListener instance
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        userEmail = (EditText) findViewById(R.id.user_email);
        userPassword = (EditText) findViewById(R.id.user_password);
        signInButton = (Button) findViewById(R.id.sign_in);
        signInButton.setOnClickListener(this);
        signUpButton = (Button) findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                //sign-in user
                validateUserSignIn(userEmail.getText().toString(), userPassword.getText().toString());
                break;
            case R.id.sign_up:
                //sign-up user
                createNewUserAccount(userEmail.getText().toString(), userPassword.getText().toString());
                break;
        }
    }

    private void validateUserSignIn(String email, String password) {
        Log.d(TAG, "validateUserSignIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }
                    hideProgressDialog();
                }
            });
    }

    private void createNewUserAccount(String email, String password) {
        Log.d(TAG, "validateUserSignIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    } else {
                        // Sign up success, update UI with the signed-up user's information
                        Log.d(TAG, "signUpWithEmail:success");
                        Toast.makeText(MainActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }

                    // ...
                }
            });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = userEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Required.");
            valid = false;
        } else {
            userEmail.setError(null);
        }

        String password = userPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            userPassword.setError("Required.");
            valid = false;
        } else {
            userPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        finish();

        if (user != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            userEmail.getText().clear();
            userPassword.getText().clear();
        }
    }
}
