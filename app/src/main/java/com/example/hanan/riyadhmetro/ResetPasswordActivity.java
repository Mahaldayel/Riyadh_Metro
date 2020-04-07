package com.example.hanan.riyadhmetro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.hanan.riyadhmetro.SigninActivity.isEmailValid;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputEmail;

    private Button btnReset, btnBack;

    private FirebaseAuth auth;

    private ProgressBar progressBar;
    private TextView textViewSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        inputEmail = findViewById(R.id.email);

        btnReset = findViewById(R.id.btn_reset_password);

        textViewSignin = findViewById(R.id.textViewSignIn);

        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        textViewSignin.setOnClickListener(this);
        btnReset.setOnClickListener(this);


    }
        @Override
        public void onClick(View view) {
            if(view == btnReset){
                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "please enter your registered Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isEmailValid(email)) {
                    Toast.makeText(getApplication(), "please enter valid Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(email)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    goToSingin();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "This Email is not registered", Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
            if(view == textViewSignin){

                    Context context = ResetPasswordActivity.this;
                    Class singinClass = SigninActivity.class;
                    Intent intent = new Intent(context,singinClass);
                    startActivity(intent);

                } }


    /**/
    private void goToSingin() {

        Context context = ResetPasswordActivity.this;
        Class singinClass = SigninActivity.class;
        Intent intent = new Intent(context,singinClass);
        startActivity(intent);

    }

}