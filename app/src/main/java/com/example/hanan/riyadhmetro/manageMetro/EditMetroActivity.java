package com.example.hanan.riyadhmetro.manageMetro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO;

import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_STATION;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_STATUS_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_NUMBER_OF_SEATS_FIELD;
import static com.example.hanan.riyadhmetro.manageMetro.MetroListAdpater.Metro_KEY_INTENT;

public class EditMetroActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText metroId, seatsNumber;
    private Spinner metroStatus;
    private Button Add;
    private FirebaseFirestore db;
    private String metroID,seatNumber,stutus,loctionStr;
    private TextView title;
    private Spinner loction;
    private LinearLayout loctionLayout;
    private ProgressDialog progressDialog;


    private String mMetroIdStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_metro);

        initElement();
        changeAddButtonToUpdate();
        getDateFromIntent();
        getMetroId();
        displayEdit();
    }


    /**/
    private void initElement() {
        db = FirebaseFirestore.getInstance();
        metroStatus = findViewById(R.id.spinnerMetroStatus);
        metroId = findViewById(R.id.metroId);
        seatsNumber = findViewById(R.id.seatsNumber);

        loction = findViewById(R.id.spinnerMetroStation);
        loctionLayout = findViewById(R.id.setLocutionLayout);


        title = findViewById(R.id.title);

        Add = findViewById(R.id.Add);
        Add.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

    }



    /**/
    private void changeAddButtonToUpdate(){

        Add.setText("Update");
        title.setText("Update metro information");
    }


    /**/
    private void getDateFromIntent() {

        Intent intent = getIntent();
        HashMap<String, String> m= (HashMap<String, String>)intent.getSerializableExtra(Metro_KEY_INTENT);
        Map<String, Object> metro = (Map)m;
        setDateOnfields(metro);
    }

    /**/
    private void setDateOnfields( Map<String, Object> metro){


        metroId.setText(metro.get(METRO_METRO_ID_FIELD).toString());
        seatsNumber.setText(metro.get(METRO_NUMBER_OF_SEATS_FIELD).toString());

        setSpinnerValue(metroStatus,metro.get(METRO_METRO_STATUS_FIELD).toString(),R.array.metro_status);
        setSpinnerValue(loction,metro.get(METRO_METRO_STATION).toString(),R.array.riyadh_stations);

        metroID = metro.get(METRO_METRO_ID_FIELD).toString();


    }

    /**/
    private void setSpinnerValue(Spinner spinner, String stringValue,int array){

        displaySpinner(spinner,array);
        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = myAdap.getPosition(stringValue);

        spinner.setSelection(spinnerPosition);
    }



    /**/

    private void displaySpinner(Spinner spinner,int array) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**/
    private void displayEdit(){

        metroId.setFocusableInTouchMode(false);
        seatsNumber.setFocusableInTouchMode(false);
        loctionLayout.setVisibility(View.VISIBLE);

    }


    /**/
    @Override
    public void onClick(View view) {

        if(view == Add){
            progressDialog.show();
            UpdateMetro();

        }
    }


    /**/
    private void getMetroId(){

        db.collection(COLLECTION_METRO).whereEqualTo("metro_id", metroID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()) {

                            mMetroIdStr = document.getId();

                        }
                    }
                });
    }

    /**/
    private void UpdateMetro(){


        Map<String,String> metro = getDataFromInput();

        if(mMetroIdStr != null) {
            db.collection(COLLECTION_METRO).document(mMetroIdStr).set(metro).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    UpdateMetroSuccefully();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    UpdateMetroUnsuccefully(e);
                }
            });
        }
    }

    /**/
    private Map<String, String> getDataFromInput(){

        Map<String,String> metro = new HashMap<>();

        stutus = metroStatus.getSelectedItem().toString();
        metroID = metroId.getText().toString();
        seatNumber = seatsNumber.getText().toString();
        loctionStr = loction.getSelectedItem().toString();



        setSpinnerValue(metroStatus,stutus,R.array.metro_status);
        setSpinnerValue(loction,loctionStr,R.array.riyadh_stations);


        if(checkEmptyInput(metroID, seatNumber ))
        {

            AddingTOdb(metro);
        }

        return metro;
    }


    /**/
    private boolean checkEmptyInput (String metroID,String seatNumber) {

        boolean notEmpty = true;


        if (TextUtils.isEmpty(metroID)) {
            metroId.setError("Please enter Metro ID");
            //stopping the function execution further
            notEmpty = false;
        }

        if (TextUtils.isEmpty(seatNumber)) {
            seatsNumber.setError("Please enter seats Number");
            //stopping the function execution further
            notEmpty = false;
        }

        return notEmpty;

    }

    /**/
    public void AddingTOdb(Map<String, String> metro){


        metro.put(METRO_METRO_ID_FIELD, metroID);
        metro.put(METRO_NUMBER_OF_SEATS_FIELD, seatNumber);
        metro.put(METRO_METRO_STATUS_FIELD, stutus);
        metro.put(METRO_METRO_STATION,loctionStr);

    }


    /**/
    private void UpdateMetroUnsuccefully( Exception e){
        progressDialog.dismiss();
        String error = e.getMessage();
        Toast.makeText(EditMetroActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void UpdateMetroSuccefully(){
        progressDialog.dismiss();
        goToViewMetro();
        Toast.makeText(EditMetroActivity.this,"The Metro has been Updated succefully!",Toast.LENGTH_SHORT).show();

    }

    private void goToViewMetro() {

        Context context = EditMetroActivity.this;
        Class viewMetroClass = MetroListViewActivity.class;

        Intent intent = new Intent(context, viewMetroClass);
        startActivity(intent);
    }





}

