package com.example.hanan.riyadhmetro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SigninActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String IS_ADMIN_KEY = "isAdmin";

    public static final String ADMIN = "Admin.RiyadhMetro";
    public static final String METRO = "Metro.RiyadhMetro";
    public static final String TYPE_OF_SINGUP = "typeOfSingUp";


    public static final int USER_ID = 1;
    public static final int ADMIN_ID = 2;
    public static final int METRO_ID = 3;


    //defining views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private TextView textViewReset;


    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        initElement();

        checkIfSingin();
    }


    /**/
    private void checkIfSingin(){

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), TripListViewActivity.class));
        }



    }

    /**/
    private void initElement(){
        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn =  findViewById(R.id.buttonSignin);
        textViewSignup = findViewById(R.id.textViewSignUp);
        textViewReset = findViewById(R.id.textViewReset);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        textViewReset.setOnClickListener(this);
    }

    /**/
    private boolean checkEmptyInput(String email, String password){

        boolean notEmpty = true;

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Please enter email");
            notEmpty = false;
        }
        else if(!isEmailValid(email)){
            editTextEmail.setError("Please enter email correctly");
            notEmpty = false;

        }

        if(TextUtils.isEmpty(password)){
            editTextPassword.setError("Please enter password");
            notEmpty = false;
        }

        return notEmpty ;

    }




    /**/
    private void userSingin(){

        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        if(checkEmptyInput(email,password))
            singin(email,password);


    }

    /**/
    private void singin(final String email, String password) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful()){
                            singinSuccessfully(email,getApplicationContext());

                        }
                        else {

                            singinUnsuccessfully(task);

                        }

                    }
                });

    }
    /**/
    public static void checkTypeOfSinginOrSingin(String email,Context context){

        email = email.toLowerCase();

        if(email.contains(ADMIN.toLowerCase())){
            PreferencesUtility.setAuthority(context, PreferencesUtility.ADMIN_AUTHORITY);
        }else if (email.contains(METRO.toLowerCase())) {

            PreferencesUtility.setAuthority(context, PreferencesUtility.MONITOR_AUTHORITY);

        }else {

            PreferencesUtility.setAuthority(context, PreferencesUtility.USER_AUTHORITY);

        }

    }


    /**/
    private void singinSuccessfully(String email,Context context){

        finish();
        Intent intent = new Intent(getApplicationContext(), TripListViewActivity.class);
        checkTypeOfSinginOrSingin(email,context);
        startActivity(intent);

    }

    /**/
    private void singinUnsuccessfully(Task<AuthResult> task){

        String errorMessage = String.valueOf(task.getException().getMessage());
        String passwordErrorMessage = "The email or the password is invalid";

        if(errorMessage.contains("password"))
            editTextEmail.setError(passwordErrorMessage);
        else if(errorMessage.contains("no user record"))
            editTextEmail.setError(passwordErrorMessage);
        else
            editTextEmail.setError(errorMessage);


    }

    /**/
    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userSingin();
        }

        if(view == textViewSignup){

            goToSingup();
        }
        if(view == textViewReset){

            goToReset();
        }


    }

    /**/
    private void goToSingup() {

        Context context = SigninActivity.this;
        Class singupClass = SignupActivity.class;

        Intent intent = new Intent(context,singupClass);
        startActivity(intent);

    }

    /**/
    private void goToReset() {

        Context context = SigninActivity.this;
        Class ResetClass = ResetPasswordActivity.class;

        Intent intent = new Intent(context,ResetClass);
        startActivity(intent);

    }

    /**/
    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}