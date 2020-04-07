package com.example.hanan.riyadhmetro;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.utility.DateDialogUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.example.hanan.riyadhmetro.DatabaseName.User_WELLAT;
import static com.example.hanan.riyadhmetro.SigninActivity.isEmailValid;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;

    private static final String NAME = "name";
    private static final String BIRTH = "Birth";
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "Password";
    private static final String NATIONAL_ID = "Nationalid";

    private static final int ValidPasswordSize = 8;
    private static final int ValidNationalIdSize = 10;



    private EditText editTextFirst;
    private EditText editTextBirth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRepeat;
    private EditText editTextNationalid;
    private CheckBox checkBox;

    private HashMap<String,Object> userInput;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db ;
    //private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        initElement();

    }

    /**/
    private void initElement(){

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();


        user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        buttonRegister = findViewById(R.id.buttonSignup);

        editTextFirst = findViewById(R.id.editTextName);
        editTextBirth = findViewById(R.id.editTextBirthDate);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordRepeat = findViewById(R.id.editTextRepeatPassword);
        editTextNationalid = findViewById(R.id.editTextNationalId);
        checkBox = findViewById(R.id.checkBox);

        textViewSignin = findViewById(R.id.textViewSignin);
        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);

        userInput = new HashMap<>();
        showPickerDate();

    }

    /**/
    private void showPickerDate() {

        editTextBirth.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialogUtility dialog= new DateDialogUtility(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });


    }


    private void displayNotUniqeEmail() {

        editTextEmail.setError("Email is already chosen");
        progressDialog.dismiss();
    }

    /**/
    public void isUniqueEmail() {

        progressDialog.show();

        String email = editTextEmail.getText().toString();
        db.collection("User").whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult() != null){

                            getDate(task);
                        }
                    }
                });

    }


    private  void getDate( Task<QuerySnapshot> task) {

        int size ;

        size = task.getResult().size();


        if(size == 0){
            registerUser();

        }
        else
        {
            displayNotUniqeEmail();
        }
    }



    /**/
    private boolean checkOfEmtiyInput(HashMap<String,Object> userInput) {

        boolean isValid = true;

        /*
         * NAME
         * */
        if(TextUtils.isEmpty(userInput.get(NAME).toString())){
            //firstName is empty
            editTextFirst.setError("Please enter user name");
            //stopping the function execution further
            isValid = false;
        }
        else
        if(!isVaildName(userInput.get(NAME).toString())){
            //firstName is empty
            editTextFirst.setError("name should only contain letters");
            //stopping the function execution further
            isValid = false;
        }


        /*
         * Birth date
         * */
        if(TextUtils.isEmpty(userInput.get(BIRTH).toString())){
            //firstName is empty
            editTextBirth.setError("Please enter your Birth date");
            //stopping the function execution further
            isValid = false;
        }


        if(!isValidDate(userInput.get(BIRTH).toString())){            //firstName is empty
            editTextBirth.setError("Please enter valid date");
            //stopping the function execution further
            isValid = false;
        }

        /*
         * Email
         * */
        if(TextUtils.isEmpty(userInput.get(EMAIL).toString())){
            //Email is empty
            editTextEmail.setError("Please enter Email");
            //stopping the function execution further
            isValid = false;
        }

       else if(!isEmailValid(userInput.get(EMAIL).toString())){

            editTextEmail.setError("Please enter valid Email");
            //stopping the function execution further
            isValid = false;

        }


        /*
         * NATIONAL_ID
         * */
        if(TextUtils.isEmpty(userInput.get(NATIONAL_ID).toString())){
            //Nationalid is empty
            editTextNationalid.setError("Please enter National ID");
            //stopping the function execution further
            isValid = false;
        }
        else if(userInput.get(NATIONAL_ID).toString().length() < ValidNationalIdSize){
            //Password is empty
            editTextNationalid.setError("Please enter valid National ID");
            //stopping the function execution further
            isValid = false;
        }
        else if(userInput.get(NATIONAL_ID).toString().length() > ValidNationalIdSize){
            //Password is empty
            editTextNationalid.setError("Please enter valid National ID");
            //stopping the function execution further
            isValid = false;
        }


        /*
         * PASSWORD
         * */
        if(TextUtils.isEmpty(userInput.get(PASSWORD).toString())){
            //Password is empty
            editTextPassword.setError("Please enter Password");
            //stopping the function execution further
            isValid = false;
        }

       else if(userInput.get(PASSWORD).toString().length() < ValidPasswordSize){
            //Password is empty
            editTextPassword.setError("The Password is too short");
            //stopping the function execution further
            isValid = false;
        }
       if(!userInput.get(PASSWORD).toString().equals(editTextPasswordRepeat.getText().toString())){

            editTextPasswordRepeat.setError("The Password not match");
            isValid = false;


        }

        /**/
        if(!checkBox.isChecked()){

            checkBox.setError("You Should agree");
            isValid = false;

        }

        return isValid;
    }

    /**/
    private void registerUser(){


        HashMap<String,Object> userInput = getInputFromUser();



        if(checkOfEmtiyInput(userInput) && checkBox.isChecked() ){

            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            SingUp(userInput);


        }else {

            progressDialog.dismiss();
        }

    }
    /**/
    private void SingUp(final HashMap<String,Object> userInput){

        firebaseAuth.createUserWithEmailAndPassword(userInput.get(EMAIL).toString(), userInput.get(PASSWORD).toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            SingUpSuccessfully(userInput.get(PASSWORD).toString(),getApplicationContext());
                        } else {

                            SingUpUnsuccessfully(task);
                        }
                    }
                });
    }

    /**/
    private void SingUpSuccessfully(String email,Context context){
        Toast.makeText(SignupActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
        SigninActivity.checkTypeOfSinginOrSingin(email,context);
        SaveUserInfo(userInput);
        goToHome();

        progressDialog.dismiss();

    }

    /**/
    private void SingUpUnsuccessfully(Task<AuthResult> task){
        Toast.makeText(SignupActivity.this, "Could not register,please try again", Toast.LENGTH_SHORT).show();
        editTextEmail.setError(String.valueOf(task.getException().getMessage()));
        progressDialog.dismiss();

    }
    /**/
    private void SaveUserInfo(HashMap<String,Object> userInput){

        String emil = userInput.get(EMAIL).toString();
        String collectionName = "User";

        db.collection(collectionName).add(userInput).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            public void onSuccess(DocumentReference documentReference) {
                SaveSuccessfully();
            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                SaveUnsuccessfully(e);
            }
        });

    }
    /**/
    private void SaveSuccessfully(){
//        Toast.makeText(SingupActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();


    }

    /**/
    private void goToHome() {

        Context context = SignupActivity.this;
        Class homeClass = TripListViewActivity.class;
        Intent intent = new Intent(context,homeClass);
        startActivity(intent);

    }

    /**/
    private void SaveUnsuccessfully(Exception e){

        String error = e.getMessage();
        Toast.makeText(SignupActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
    }

    /**/
    @Override
    public void onClick(View view) {

        if(view == buttonRegister){
            isUniqueEmail();

        }

        if(view == textViewSignin){
            goToSingin();
        }


    }


    /**/
    private void goToSingin() {

        Context context = SignupActivity.this;
        Class singinClass = SigninActivity.class;
        Intent intent = new Intent(context,singinClass);
        startActivity(intent);

    }

    /**/
    public HashMap<String,Object> getInputFromUser() {


        String name = editTextFirst.getText().toString().trim();
        String birth = editTextBirth.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim().toLowerCase();
        String password = editTextPassword.getText().toString().trim();
        String nationalId = editTextNationalid.getText().toString().trim();

        userInput.put(NAME,name);
        userInput.put(BIRTH,birth);
        userInput.put(EMAIL,email);
        userInput.put(PASSWORD,password);
        userInput.put(NATIONAL_ID,nationalId);
        userInput.put(User_WELLAT,"0");

        return userInput;
    }

    /**/
    private boolean isVaildName(String name){

        return (name.toString().matches("[a-zA-Z ]+"));

    }
    /**/
    public boolean isValidDate(String date)
    {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/m/yyyy");

        Date testDate ;


        try {
            testDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return false;
        }

        if (!simpleDateFormat.format(testDate).equals(date)) {
            return false;
        }


        return true;

    }

}