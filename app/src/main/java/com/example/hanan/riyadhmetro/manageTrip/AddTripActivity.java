package com.example.hanan.riyadhmetro.manageTrip;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TRIP;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_NUMBER_OF_SEATS_FIELD;
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

public class AddTripActivity  extends AppCompatActivity implements View.OnClickListener {

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final private Random rng = new SecureRandom();
    private static final long MINIMUM_TIME_MILLISECONDS = 900000;


    private EditText arrivalT,leavingT, GateNumber,date,Bseats,Aseats;
    private TextInputLayout arrivingLabel ;
    private Spinner arrivalD, leavingD;
    private Button Add;
    private FirebaseFirestore db;
    private String arrivalDe ;
    private String leavingDe ;
    private String arrivalTi ;
    private String GateNum;
    private String  dateStr ;
    private StringBuilder Tripcode ;
    private String leavingTi ;
    private Spinner metro;
    private ArrayList<String> mMetroIds;
    private Map<String,Object> mMetroSeats ;
    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);


        initElement();
    }


    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();

        GateNumber = findViewById(R.id.GateNumber);
        date = findViewById(R.id.date);
        arrivingLabel = findViewById(R.id.ArrivingLabel);
        metro = findViewById(R.id.spinnerMetro);

        arrivalD = findViewById(R.id.spinnerArrivingDestination);
        leavingD = findViewById(R.id.spinnerLeavingDestination);

        displaySpinner(arrivalD);
        displaySpinner(leavingD);


        mMetroIds = new ArrayList<>();
        mMetroSeats = new HashMap<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        Add = findViewById(R.id.Add);
        Add.setOnClickListener(this);

        showPickerDate();

        arrivalT = findViewById(R.id.arrivalT);
        leavingT = findViewById(R.id.leavingT);
        showPickerTime(leavingT);
        showPickerTime(arrivalT);

        getMonitorEmailList();

    }

    /**/
    private void displaySpinner(Spinner spinner){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.riyadh_stations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

                for (DocumentSnapshot document : task.getResult()) {

                    Map<String, Object> metro = document.getData();
                    String email = metro.get(METRO_METRO_ID_FIELD).toString();
                    String seats = metro.get(METRO_NUMBER_OF_SEATS_FIELD).toString();
                    mMetroIds.add(email);
                    mMetroSeats.put(email,seats);

                }
                displaySpinnerForMetroId(metro);
                metro.setSelection(0);
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

    @Override
    public void onClick(View view) {
        if(view == Add){
            AddTrip();
        }
    }

    /**/
    private void AddTrip(){

        Map<String,String> trip = getDataFromInput();

        if(trip != null) {
            db.collection(COLLECTION_TRIP).add(trip)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            addTripSuccefully();
                            goToViewTrip();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            addTripUnsuccefully(e);
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
        dateStr = date.getText().toString();
        Tripcode = randomUUID(16,4,'a');
        leavingTi = leavingT.getText().toString();

        if(checkEmptyInput(arrivalTi, GateNum,  dateStr, leavingTi,arrivalD,leavingD))
        {

            AddingTOdb(trip);
            return trip;
        }

        return null;
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

        if(TextUtils.isEmpty(dateStr)){
            date.setError("Please enter Trip Date");
            //stopping the function execution further
            notEmpty = false;        }

        else if(!isValidDate(dateStr)){            //firstName is empty
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

        if((!TextUtils.isEmpty(arrivalTi)&&!TextUtils.isEmpty(leavingTi)&&!TextUtils.isEmpty(dateStr) )){
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
        trip.put(TRIP_AVAILABLE_SEATS, (String) mMetroSeats.get(getMetroID()));
        trip.put(TRIP_BOOKED_SEATS,"0");
        trip.put(TRIP_GATE_NUMBER, GateNum);
        trip.put(TRIP_DATE,  dateStr);
        trip.put(TRIP_LEAVING_DESTINATION, leavingDe);
        trip.put(TRIP_LEAVING_TIME, leavingTi);
        trip.put(TRIP_TRIP_CODE, String.valueOf(Tripcode));
        trip.put(TRIP_METRO_ID,metro.getSelectedItem().toString());

    }


    /**/
    private String getMetroID(){

        return metro.getSelectedItem().toString();
    }



    /**/
    private void addTripUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(AddTripActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void addTripSuccefully(){

        Toast.makeText(AddTripActivity.this,"The Trip has been added succefully!",Toast.LENGTH_SHORT).show();

    }


    /**/
    private void goToViewTrip() {

        Context context = AddTripActivity.this;
        Class viewTripsClass = TripListViewActivity.class;
        Intent intent = new Intent(context,viewTripsClass);
        startActivity(intent);
    }




    char randomChar(){
        return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
    }


    public StringBuilder randomUUID(int length,int spacing,char spacerChar){
        StringBuilder sb = new StringBuilder();
        int spacer = 0;
        while(length > 0){
            if(spacer == spacing){
                sb.append(spacerChar);
                spacer = 0;
            }
            length--;
            spacer++;
            sb.append(randomChar());
        }
        return sb;
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
