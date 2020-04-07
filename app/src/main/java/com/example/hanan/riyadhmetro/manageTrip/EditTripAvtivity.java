package com.example.hanan.riyadhmetro.manageTrip;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.utility.DateDialogUtility;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.utility.TimeDialogUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TRIP;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_ARRIVAL_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_ARRIVAL_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_AVAILABLE_SEATS;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_BOOKED_SEATS;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_GATE_NUMBER;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_LEAVING_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_LEAVING_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_METRO_ID;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_TRIP_CODE;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;

public class EditTripAvtivity extends AppCompatActivity implements View.OnClickListener{

    private EditText arrivalT,leavingT, GateNumber,date;
    private Spinner arrivalD, leavingD;
    private Button Add;
    private FirebaseFirestore db;
    private String arrivalDe ;
    private String leavingDe ;
    private String arrivalTi ;
    private String GateNum;
    private String Date ;
    private String mTripCode ;
    private String leavingTi ;
    private TextInputLayout arrivingLabel ;

    private TextView title;
    private ProgressDialog progressDialog;
    private Spinner metro;
    private ArrayList<String> mMetroIds;
    private String mMetroId;

    private static final long MINIMUM_TIME_MILLISECONDS = 900000;

    private String mTripId;
    private Map<String, Object> mTrip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        initElement();
        changeAddButtonToUpdate();
        getDateFromIntent();
    }

    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();

        arrivalD = findViewById(R.id.spinnerArrivingDestination);
        leavingD = findViewById(R.id.spinnerLeavingDestination);
        arrivalT = findViewById(R.id.arrivalT);
        leavingT = findViewById(R.id.leavingT);
        arrivingLabel = findViewById(R.id.ArrivingLabel);
        GateNumber = findViewById(R.id.GateNumber);
        date = findViewById(R.id.date);
        title = findViewById(R.id.title);
        metro = findViewById(R.id.spinnerMetro);


        mMetroIds = new ArrayList<>();


        Add = findViewById(R.id.Add);
        Add.setOnClickListener(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        showPickerDate();
        showPickerTime(leavingT);
        showPickerTime(arrivalT);

        getMonitorEmailList();


    }

    /**/
    private void showPickerDate() {

        date.setOnFocusChangeListener(new View.OnFocusChangeListener(){
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
    private void showPickerTime(EditText time) {

        TimeDialogUtility fromTime = new TimeDialogUtility(time, this);

    }


    /**/
    private void getMonitorEmailList(){

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        db.collection(COLLECTION_METRO).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                int index = 0;
                int count = 0;

                for (DocumentSnapshot document : task.getResult()) {

                    Map<String, Object> metro = document.getData();
                    String metroId = metro.get(METRO_METRO_ID_FIELD).toString();
                    mMetroIds.add(metroId);
                    if(mMetroId.equals(metroId))
                        index = count;
                    count ++;
                }
                displaySpinnerForMetroId(metro);
                metro.setSelection(index);
            }
        });
    }

    /**/
    private void displaySpinnerForMetroId(final Spinner spinner) {


        // Initializing an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,mMetroIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                metro.setSelection(i,true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressDialog.dismiss();

    }




    /**/
    private void changeAddButtonToUpdate(){

        Add.setText("Update");
        title.setText("Edit Trip");
    }


    /**/
    private void getDateFromIntent() {

        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(TRIP_KEY_INTENT);
        mTrip = (Map)t;
        mMetroId = mTrip.get(TRIP_METRO_ID).toString();
        mTripCode = mTrip.get(TRIP_TRIP_CODE).toString();
        getTripId();
        setDateOnfields(mTrip);

    }

    /**/
    private void getTripId(){


        db.collection(COLLECTION_TRIP).whereEqualTo(TRIP_TRIP_CODE, mTripCode).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()) {

                            mTripId = document.getId();

                        }
                    }
                });
    }


    /**/
    private void setDateOnfields( Map<String, Object> trip){

        leavingT.setText(trip.get(TRIP_LEAVING_TIME).toString());
        arrivalT.setText(trip.get(TRIP_ARRIVAL_TIME).toString());

        setSpinnerValue(arrivalD,trip.get(TRIP_ARRIVAL_DESTINATION).toString());
        setSpinnerValue(leavingD,trip.get(TRIP_LEAVING_DESTINATION).toString());

        date.setText(trip.get(TRIP_DATE).toString());
        GateNumber.setText(trip.get(TRIP_GATE_NUMBER).toString());

    }


    /**/
    private void setSpinnerValue(Spinner spinner, String stringValue){

        displaySpinner(spinner);
        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = myAdap.getPosition(stringValue);

        spinner.setSelection(spinnerPosition);
    }


    /**/
    private void displaySpinner(Spinner spinner){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.riyadh_stations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }




    /**/
    @Override
    public void onClick(View view) {

        if(view == Add){
            UpdateTrip();
        }
    }


    /**/
    private void UpdateTrip(){

        Map<String,String> trip = getDataFromInput();
        if(trip.size() != 0) {
            progressDialog.show();
            db.collection(COLLECTION_TRIP).document(mTripId).set(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    UpdateTripSuccefully();
                    goToViewTrip();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    UpdateTripUnsuccefully(e);
                }
            });
        }

    }

    /**/

    private Map<String, String> getDataFromInput(){

        Map<String,String> trip = new HashMap<>();

        arrivalDe = arrivalD.getSelectedItem().toString();
        leavingDe = leavingD.getSelectedItem().toString();
        arrivalTi = arrivalT.getText().toString();
        GateNum = GateNumber.getText().toString();
        Date = date.getText().toString();

        leavingTi = leavingT.getText().toString();

        setSpinnerValue(arrivalD,arrivalDe);
        setSpinnerValue(leavingD,leavingDe);


        if(checkEmptyInput(arrivalTi, GateNum, Date, leavingTi,arrivalD,leavingD))
        {

            AddingTOdb(trip);
        }

        return trip;
    }


    /**/
    private boolean checkEmptyInput(String arrivalTi,String GateNum,String dateSTR,String leavingTi,Spinner arrivalPlace,Spinner leavingPlace){

        boolean notEmpty = true;


        if(TextUtils.isEmpty(arrivalTi)){
            arrivalT.setError("Please enter Arriving Time");
            //stopping the function execution further
            notEmpty = false;        }
        else if(!isValidTime(arrivalTi)){            //firstName is empty
            arrivalT.setError("Please enter valid time");
            //stopping the function execution further
            notEmpty = false;
        }

        if(TextUtils.isEmpty(GateNum)){
            GateNumber.setError("Please enter Gate Number");
            //stopping the function execution further
            notEmpty = false;        }

        if(TextUtils.isEmpty(dateSTR)){
            date.setError("Please enter Trip Date");
            //stopping the function execution further
            notEmpty = false;        }

        else if(!isValidDate(dateSTR)){            //firstName is empty
            date.setError("Please enter valid date");
            //stopping the function execution further
            notEmpty = false;
        }


        if(TextUtils.isEmpty(leavingTi)){
            leavingT.setError("Please enter Leaving Time");
            //stopping the function execution further
            notEmpty = false;        }
        else if(!isValidTime(leavingTi)){            //firstName is empty
            leavingT.setError("Please enter valid time");
            //stopping the function execution further
            notEmpty = false;
        }

        if((!TextUtils.isEmpty(arrivalTi)&&!TextUtils.isEmpty(leavingTi)&&!TextUtils.isEmpty(dateSTR) )){
            if(!isMoreThenMinimumValue(dateSTR,leavingTi,arrivalTi)){
                arrivalT.setError("The Minimum between leaving time and arrival time should be at least 15 min ");
                notEmpty = false;
            }
        }

        if(!isDifferentStation(arrivalPlace,leavingPlace)){
            arrivingLabel.setError("The stations should be different ");

            notEmpty = false;
        }


        return notEmpty ;

    }
    /**/
    /**/
    public boolean isValidDate(String date)
    {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/m/yyyy");

        java.util.Date testDate ;


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
    public boolean isValidTime(String date)
    {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:m");

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
    private boolean isMoreThenMinimumValue(String date, String leaving,String arrival){

        Date leavingDate = convertTime(date,leaving);
        Date arrivalDate = convertTime(date,arrival);

        if(leavingDate != null) {
            long leaveMLS = leavingDate.getTime();
            long minimumArrivalTimeMLS = leaveMLS + MINIMUM_TIME_MILLISECONDS;

            Date minimumArrivalTime = new Date(minimumArrivalTimeMLS);

            if (arrivalDate.before(minimumArrivalTime))
                return false;
        }
        return true;
    }

    /**/
    private boolean isDifferentStation(Spinner arrivalPlace,Spinner leavingPlace){

        String arrivalPlaceStr = arrivalPlace.getSelectedItem().toString();
        String leavingPlaceStr = leavingPlace.getSelectedItem().toString();

        if(arrivalPlaceStr.equals(leavingPlaceStr))
            return false;
        return true;
    }


    /**/
    public void AddingTOdb(Map<String, String> trip){

        trip.put(TRIP_ARRIVAL_DESTINATION, arrivalDe);
        trip.put(TRIP_ARRIVAL_TIME, arrivalTi);
        trip.put(TRIP_AVAILABLE_SEATS,String.valueOf(mTrip.get("Available seats")));
        trip.put(TRIP_BOOKED_SEATS,String.valueOf(mTrip.get("Booked seats")));
        trip.put(TRIP_GATE_NUMBER, GateNum);
        trip.put(TRIP_DATE, Date);
        trip.put(TRIP_LEAVING_DESTINATION, leavingDe);
        trip.put(TRIP_LEAVING_TIME, leavingTi);
        trip.put(TRIP_TRIP_CODE, String.valueOf(mTripCode));
        trip.put(TRIP_METRO_ID,metro.getSelectedItem().toString());

    }



    /**/
    private void UpdateTripSuccefully(){
        Toast.makeText(EditTripAvtivity.this,"The Trip has been Updated succefully!",Toast.LENGTH_SHORT).show();

    }


    /**/
    private void UpdateTripUnsuccefully( Exception e){
        String error = e.getMessage();
        Toast.makeText(EditTripAvtivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void goToViewTrip() {
        Context context = EditTripAvtivity.this;
        Class viewTripsClass = TripListViewActivity.class;
        Intent intent = new Intent(context,viewTripsClass);
        startActivity(intent);

    }


    /**/
    private  Date convertTime(String dateStr,String timeStr){

        Date date = null;

        String sDate = dateStr+" "+timeStr ;

        try {
            date = new SimpleDateFormat("d/m/yyyy HH:mm").parse(sDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }



}
