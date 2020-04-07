package com.example.hanan.riyadhmetro.mangeMetroMonitor;

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

import com.example.hanan.riyadhmetro.utility.DateDialogUtility;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_BIRTH_DATE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_NAME_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_NATIONAL_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_PASSWORD_FIELD;
import static com.example.hanan.riyadhmetro.SigninActivity.isEmailValid;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater.METRO_MONITOR_KEY_INTENT;

public class EditMetroMonitorActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonupdate;


    private static final int ValidPasswordSize = 8;
    private static final int ValidNationalIdSize = 10;



    private EditText editTextName;
    private EditText editTextBirth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextNationalid;
    private CheckBox checkBox;
    private EditText editTextPasswordRepeat;

    private TextView textViewTitle;

    private HashMap<String,Object> userInput;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db ;
    private Map<String, Object> mMetroMonitor;
    private DatabaseReference databaseReference;
    private String mId;


    private String mMetroMonitorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        initElement();
        hidePassword();
        getDateFromIntent();
        displayEditView();

    }
    /**/
    private void initElement(){

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();


        user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        buttonupdate = findViewById(R.id.buttonSignup);

        editTextName = findViewById(R.id.editTextName);
        editTextBirth = findViewById(R.id.editTextBirthDate);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNationalid = findViewById(R.id.editTextNationalId);
        editTextPasswordRepeat = findViewById(R.id.editTextRepeatPassword);

        textViewTitle = findViewById(R.id.textViewTitle);
        checkBox = findViewById(R.id.checkBox);

        textViewSignin = findViewById(R.id.textViewSignin);
        buttonupdate.setOnClickListener(this);

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

    /**/
    private void hidePassword(){

        editTextPassword.setVisibility(View.GONE);
        editTextPasswordRepeat.setVisibility(View.GONE);

    }

    /**/
    private void getDateFromIntent() {

        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(METRO_MONITOR_KEY_INTENT);
        mMetroMonitor = (Map)t;
        mId = intent.getStringExtra(ID_KEY_INTENT);
        setDateOnfields(mMetroMonitor);
        getMointorId(mMetroMonitor.get(METRO_MONITOR_EMAIL_FIELD).toString());

    }
    /**/
    private void getMointorId(String email){


        if(email != null) {
            db.collection(COLLECTION_METRO_MONITOR).whereEqualTo(METRO_MONITOR_EMAIL_FIELD, email).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (DocumentSnapshot document : task.getResult()) {

                                mMetroMonitorId = document.getId();

                            }
                        }
                    });
        }

    }

    /**/
    private void setDateOnfields( Map<String, Object> metroMonitor) {

        if (metroMonitor != null) {
            editTextName.setText(metroMonitor.get(METRO_MONITOR_NAME_FIELD).toString());
            editTextEmail.setText(metroMonitor.get(METRO_MONITOR_EMAIL_FIELD).toString());
            editTextPassword.setText(metroMonitor.get(METRO_MONITOR_PASSWORD_FIELD).toString());

            editTextBirth.setText(metroMonitor.get(METRO_MONITOR_BIRTH_DATE_FIELD).toString());
            editTextNationalid.setText(metroMonitor.get(METRO_MONITOR_NATIONAL_ID_FIELD).toString());
        }
    }
        /**/
    private void displayEditView(){

        editTextEmail.setFocusableInTouchMode(false);
        checkBox.setVisibility(View.GONE);
        textViewSignin.setVisibility(View.GONE);
        buttonupdate.setText("Update");
        textViewTitle.setText("Update Account Information");
    }

    /**/
    @Override
    public void onClick(View view) {

        if(view == buttonupdate){
            UpdateMetroMonitorInfo();

        }
    }

    /**/
    private void UpdateMetroMonitorInfo(){

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        HashMap<String,Object> userInput = getInputFromUser();




        if(checkOfEmtiyInput(userInput) ){

            edit(userInput);


        }else {

            progressDialog.dismiss();

        }

    }
    /**/
    public HashMap<String,Object> getInputFromUser() {


        String name = editTextName.getText().toString().trim();
        String birth = editTextBirth.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nationalId = editTextNationalid.getText().toString().trim();

        userInput.put(METRO_MONITOR_NAME_FIELD,name);
        userInput.put(METRO_MONITOR_BIRTH_DATE_FIELD,birth);
        userInput.put(METRO_MONITOR_EMAIL_FIELD,email);
        userInput.put(METRO_MONITOR_PASSWORD_FIELD,password);
        userInput.put(METRO_MONITOR_NATIONAL_ID_FIELD,nationalId);

        return userInput;
    }

    /**/
    private boolean checkOfEmtiyInput(HashMap<String,Object> userInput) {

        boolean isValid = true;

        /*
         * NAME
         * */
        if(TextUtils.isEmpty(userInput.get(METRO_MONITOR_NAME_FIELD).toString())){
            //firstName is empty
            editTextName.setError("Please enter The Name");
            //stopping the function execution further
            isValid = false;
        }


        if(!isVaildName(userInput.get(METRO_MONITOR_NAME_FIELD).toString())){
            //firstName is empty
            editTextName.setError("Please enter valid  Name");
            //stopping the function execution further
            isValid = false;
        }


        /*
         * Birth date
         * */
        if(TextUtils.isEmpty(userInput.get(METRO_MONITOR_BIRTH_DATE_FIELD).toString())){
            //firstName is empty
            editTextBirth.setError("Please enter your Birth date");
            //stopping the function execution further
            isValid = false;
        }


        if(!isValidDate(userInput.get(METRO_MONITOR_BIRTH_DATE_FIELD).toString())){            //firstName is empty
            editTextBirth.setError("Please enter valid date");
            //stopping the function execution further
            isValid = false;
        }

        /*
         * Email
         * */
        if(TextUtils.isEmpty(userInput.get(METRO_MONITOR_EMAIL_FIELD).toString())){
            //Email is empty
            editTextEmail.setError("Please enter Email");
            //stopping the function execution further
            isValid = false;
        }

        if(!isEmailValid(userInput.get(METRO_MONITOR_EMAIL_FIELD).toString())){

            editTextEmail.setError("Please enter valid Email");
            //stopping the function execution further
            isValid = false;

        }


        /*
         * NATIONAL_ID
         * */
        if(TextUtils.isEmpty(userInput.get(METRO_MONITOR_NATIONAL_ID_FIELD).toString())){
            //Nationalid is empty
            editTextNationalid.setError("Please enter National ID");
            //stopping the function execution further
            isValid = false;
        }
        if(userInput.get(METRO_MONITOR_NATIONAL_ID_FIELD).toString().length() < ValidNationalIdSize){
            //Password is empty
            editTextNationalid.setError("Please enter valid National ID");
            //stopping the function execution further
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
    private boolean isVaildName(String name){

        return !(name.matches(".*\\d+.*"));

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



    /**/
    private void edit(HashMap<String,Object> userInput){

        String emil = userInput.get(METRO_MONITOR_EMAIL_FIELD).toString();
        String collectionName = COLLECTION_METRO_MONITOR;

        mMetroMonitor = userInput;
        if(mMetroMonitorId != null) {
            db.collection(collectionName).document(mMetroMonitorId).set(userInput).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    updateSuccessfully();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    updateUnsuccessfully(e);
                }
            });
        }else{

            progressDialog.dismiss();
        }
    }
    /**/
    private void updateSuccessfully(){
        Toast.makeText(EditMetroMonitorActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY )
            goToViewAcount();
        else
            goToViewMonitorList();

    }

    /**/
    private void goToViewAcount() {

        Context context = EditMetroMonitorActivity.this;
        Class ViewMetroMonitorClass = ViewMetroMonitorActivity.class;
        Intent intent = new Intent(context,ViewMetroMonitorClass);

        if(mMetroMonitor != null){
            intent.putExtra(METRO_MONITOR_KEY_INTENT,(HashMap) mMetroMonitor );
            intent.putExtra(MetroMonitorListAdpater.ID_KEY_INTENT,mId);

            startActivity(intent);
        }
    }

    private void goToViewMonitorList() {

        Context context = EditMetroMonitorActivity.this;
        Class MonitortsClass = MetroMonitorListViewActivity.class;
        Intent intent = new Intent(context,MonitortsClass);
        startActivity(intent);
        progressDialog.dismiss();

    }

    /**/
    private void updateUnsuccessfully(Exception e){

        String error = e.getMessage();
        Toast.makeText(EditMetroMonitorActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }





}
