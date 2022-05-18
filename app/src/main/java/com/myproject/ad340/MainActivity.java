package com.myproject.ad340;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sharedPref;
    Button logB;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button movies = findViewById(R.id.movies);
        Button cities = findViewById(R.id.cities);
        Button parks = findViewById(R.id.parks);
        Button music = findViewById(R.id.music);
        Button traffic = findViewById(R.id.traffic);
        Button food = findViewById(R.id.map);


        cities.setOnClickListener(this);
        parks.setOnClickListener(this);
        music.setOnClickListener(this);
        food.setOnClickListener(this);
        movies.setOnClickListener(this);
        traffic.setOnClickListener(this);

        usernameField = findViewById(R.id.userName);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        logB = findViewById(R.id.login_btn);

        sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        usernameField.setText(getEntry("userName"));
        emailField.setText(getEntry("email"));
        passwordField.setText(getEntry("password"));

        logB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    public String getEntry(String key) {
        return sharedPref.getString(key, "");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cities:
                Toast.makeText(this, "Cities", Toast.LENGTH_SHORT).show();
                break;
            case R.id.parks:
                Toast.makeText(this, "Parks", Toast.LENGTH_SHORT).show();
                break;
            case R.id.music:
                Toast.makeText(this, "Music", Toast.LENGTH_SHORT).show();
                break;
            case R.id.map:
                 openMapsPage();
                break;
            case R.id.movies:
                 openMoviesPage();
                break;
            case R.id.traffic:
                openTrafficPage();
                break;
        }
    }
    public void openTrafficPage(){
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }
    public void openMoviesPage(){
        Intent intent = new Intent(MainActivity.this, MoviesActivity.class);
        startActivity(intent)  ;
    }
    public void openMapsPage(){
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }




    private boolean checkEntry(String userName, String email, String password) {
        boolean checked = true;
        if (TextUtils.isEmpty(userName)) {
            usernameField.setError("Required");
            checked = false;
        } else {
            usernameField.setError(null);
        }
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required");
            checked = false;
        } else {
            emailField.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required");
            checked = false;
        } else {
            passwordField.setError(null);
        }
        return checked;
    }

    public void signIn() {
        Log.d("FIREBASE", "signIn");
        // 1 - validate display name, email, and password entries
        String userName = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        //Dr5ah2QjhUwhs4se
        //Dr5ah2qjhUwhs4se
        Log.d("password", passwordField.getText().toString());

        if (!checkEntry(userName, email, password)) {
            return;
        }
        // 2 - save valid entries to shared preferences
        saveEntry("userName", userName);
        saveEntry("email", email);
        saveEntry("password", password);

        // 3 - firebase sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FIREBASE", "signIn:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // update profile. displayname is the value entered in UI
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    .build();
                            assert user != null;
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("FIREBASE", "User profile updated.");
                                                // Go to FirebaseActivity
                                                startActivity(new Intent(MainActivity.this, FirebaseActivity.class));
                                            }
                                        }
                                    });
                        } else {
                            Log.d("FIREBASE", "sign-in failed");
                            Toast.makeText(MainActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.d("FIREBASE", "FirebaseAuthInvalidCredentialsException");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                Log.d("FIREBASE", "ERROR_USER_NOT_FOUND");
                            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                                Log.d("FIREBASE", "ERROR_USER_DISABLED");
                            } else {
                                Log.d("FIREBASE", "OTHER_ERROR");
                            }
                        }

                    }
                });

    }

    public void saveEntry(String key, String message) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, message);
        editor.commit();
    }

}