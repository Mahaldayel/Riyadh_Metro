package com.example.hanan.riyadhmetro.manageMetro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_STATION;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_STATUS_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_NUMBER_OF_SEATS_FIELD;


public class addMetro  extends AppCompatActivity implements View.OnClickListener {


    private EditText metroId, seatsNumber;
    private Spinner metroStatus;
    private Button Add;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    private String metroID,seatNumber,stutus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_metro);


        initElement();

    }


    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();
        metroStatus = findViewById(R.id.spinnerMetroStatus);
        metroId = findViewById(R.id.metroId);
        seatsNumber = findViewById(R.id.seatsNumber);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        displaySpinner(metroStatus,R.array.metro_status);


        Add = findViewById(R.id.Add);
        Add.setOnClickListener(this);

    }


    /**/

    private void displaySpinner(Spinner spinner,int array) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**/
    @Override
    public void onClick(View view) {
        if(view == Add){
            isUniqueId();
        }
    }
    /**/
    public void isUniqueId() {

        progressDialog.show();

        metroID = metroId.getText().toString();
        db.collection(COLLECTION_METRO).whereEqualTo(METRO_METRO_ID_FIELD, metroID)
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

    /**/
    private  void getDate( Task<QuerySnapshot> task) {

        int size ;

        size = task.getResult().size();


        if(size == 0){
            AddMetro();

        }
        else
        {
            displayNotUniqeId();
        }
    }


    /**/
    private void AddMetro(){


        Map<String,String> metro = getDataFromInput();


        if(metro != null) {
            // ff.collection
            db.collection(COLLECTION_METRO).add(metro)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            addMetroSuccefully();
                            goToViewMetro();
                            progressDialog.dismiss();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            addMetroUnsuccefully(e);
                            progressDialog.dismiss();

                        }
                    });
        }
    }



    private Map<String, String> getDataFromInput(){

        Map<String,String> metro=new HashMap<>();

        stutus = metroStatus.getSelectedItem().toString();
        metroID = metroId.getText().toString();
        seatNumber = seatsNumber.getText().toString();

        if(checkEmptyInput( metroID, seatNumber))
        {

            AddingTOdb(metro);
            return metro;

        }

        return null;
    }

    /**/
    private boolean checkEmptyInput( String metroID, String seatNumber){

        boolean notEmpty = true;

        if(TextUtils.isEmpty(metroID)){
            metroId.setError("Please enter Metro ID");
            //stopping the function execution further
            notEmpty = false;        }

        if(TextUtils.isEmpty(seatNumber)){
            seatsNumber.setError("Please enter seats Number");
            //stopping the function execution further
            notEmpty = false;        }


        progressDialog.dismiss();

        return notEmpty ;

    }


    /**/
    public void AddingTOdb(Map<String, String> metro){

        metro.put(METRO_METRO_ID_FIELD, metroID);
        metro.put(METRO_METRO_STATION,"Olaya Station");
        metro.put(METRO_NUMBER_OF_SEATS_FIELD, seatNumber);
        metro.put(METRO_METRO_STATUS_FIELD, stutus);

    }



    /**/
    private void addMetroUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(addMetro.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }
    /**/
    private void addMetroSuccefully(){

        Toast.makeText(addMetro.this,"The Metro has been added succefully!",Toast.LENGTH_SHORT).show();

    }

    private void displayNotUniqeId() {

        metroId.setError("Metro ID must be unique");
        progressDialog.dismiss();
    }



    /**/
    private void goToViewMetro() {

        Context context = addMetro.this;
        Class viewMetroClass = MetroListViewActivity.class;
        Intent intent = new Intent(context,viewMetroClass);
        startActivity(intent);
    }






}

