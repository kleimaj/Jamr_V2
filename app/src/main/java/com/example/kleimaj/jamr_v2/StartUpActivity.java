package com.example.kleimaj.jamr_v2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartUpActivity extends AppCompatActivity {

    private EditText email,password;
    private String emailString,passwordString;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        setContentView(R.layout.activity_start_up);

        mAuth = FirebaseAuth.getInstance(); //must initialize firebase auth
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) { //must initialize authstatelistener
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //current logged in user, or null
                if (user != null){
                    //user is already logged in
                    //TODO: create intent for swipe screen
                    Toast.makeText(StartUpActivity.this, "signed-in",Toast.LENGTH_SHORT).show();
                    //Intent myIntent = new Intent(StartUpActivity.this, MainActivity.class);
                    //startActivity(myIntent);
                    //finish();
                    //return;

                }
            }
        };

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailString = email.getText().toString();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordString = password.getText().toString();
            }
        });
    }

    public void SignInClick(View view){
        if (emailString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(StartUpActivity.this, "Unfinished Sign-in Fields",Toast.LENGTH_SHORT).show();
        }
        else {
            //TODO: sign user in
            mAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(StartUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){ //firebase login failed
                        Toast.makeText(StartUpActivity.this, "Sign-in Success",Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(StartUpActivity.this, MainActivity.class);
                        startActivity(myIntent);
                        finish();
                        return;
                    }
                    else {
                        Toast.makeText(StartUpActivity.this, "Sign-in Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void RegisterClick(View view){
        Intent myIntent = new Intent(view.getContext(),Registration.class);
        startActivity(myIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
