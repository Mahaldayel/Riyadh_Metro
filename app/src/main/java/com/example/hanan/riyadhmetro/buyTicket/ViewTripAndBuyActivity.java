package com.example.hanan.riyadhmetro.buyTicket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.LogoActivity;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity;
import com.example.hanan.riyadhmetro.manageTicket.ViewTicketActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TICKET;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TRIP;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_USER;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ARRIVAL_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ARRIVAL_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_GATE_NUMBER;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ID;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_LEAVING_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_LEAVING_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_METRO_ID;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_TRIP_CODE;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_USER_EMAIL;
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
import static com.example.hanan.riyadhmetro.DatabaseName.User_EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.User_WELLAT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.INTENT_METRO_OR_TRIP;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.TRIP_END_STATION;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.TRIP_EXTA_NUMBER;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.TRIP_START_STATION;
import static com.example.hanan.riyadhmetro.manageTicket.TicketListAdpater.Ticket_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;

public class ViewTripAndBuyActivity extends AppCompatActivity implements View.OnClickListener {

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static final private String Ticket ="Ticket";
    final private Random rng = new SecureRandom();
    private int mInteger;
    private TextView mTextViewGate;
    private TextView mTextViewBookedSeats;
    private TextView mTextViewPrice;
    private TextView mTextViewLeavingPlace;
    private TextView mTextViewArrivingPlace;
    private TextView mTextViewAvailableSeats;
    private TextView mTextViewTripDate;
    private TextView mTextViewArrivingTime;
    private TextView mTextViewLeavingTime;
    private ProgressDialog progressDialog;
    private Button mBuyCardButton;
    private Button mBuyWalletButton;
    private Button mBuyAdminButton;
    private FirebaseFirestore db ;
    private Map<String, Object> trip;
    private String mTripId;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private String mTripCode ;
    private static final double TICKET_PRICE = 20;
    private String ticketCode;
    private Map<String,Object> mTicket;

    private Button mTripPathButton;

    private String mStartStation;
    private String mEndStation;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_ticket);

        initElement();
        hideBuyButton();
        getTripForIntent();
        getTripId();
        getUserId();
        checkOfpayment();

    }


    /**/
    private void initElement() {
        db = FirebaseFirestore.getInstance();
        mInteger = 1;
        mTextViewGate = findViewById(R.id.textViewGate);
        mTextViewBookedSeats = findViewById(R.id.textViewBookedSeats);
        mTextViewAvailableSeats = findViewById(R.id.textViewAvailableSeats);
        mTextViewPrice = findViewById(R.id.price);

        mTextViewLeavingPlace = findViewById(R.id.textViewLeavingPlace);
        mTextViewArrivingPlace = findViewById(R.id.textViewArrivingPlace);

        mTextViewTripDate = findViewById(R.id.tripDateText);
        mTextViewArrivingTime = findViewById(R.id.arrivingTimeText);
        mTextViewLeavingTime = findViewById(R.id.leavingTimetext);

        mBuyCardButton = findViewById(R.id.buyCardButton);
        mBuyWalletButton = findViewById(R.id.buyWalletButton);
        mBuyAdminButton = findViewById(R.id.buyAdminButton);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mBuyCardButton.setOnClickListener(this);
        mBuyWalletButton.setOnClickListener(this);
        mBuyAdminButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        mTripPathButton = findViewById(R.id.trip_path);
        mTripPathButton.setOnClickListener(this);

        mTicket = new HashMap<>();


    }



    /**/
    private void hideBuyButton() {
        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY) {

            mBuyAdminButton.setVisibility(View.GONE);

        }
        else if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY){
            mBuyWalletButton.setVisibility(View.GONE);
            mBuyCardButton.setVisibility(View.GONE);

        }



    }

    /**/
    private  void getTripForIntent(){
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(TRIP_KEY_INTENT);
        trip = (Map)t;
        mTripCode = trip.get(TRIP_TRIP_CODE).toString();

        displayVeiwTrip(trip);

    }
    /**/

    private void displayVeiwTrip( Map<String, Object> trip){

        mTextViewLeavingTime.setText(trip.get(TRIP_LEAVING_TIME).toString());
        mTextViewArrivingTime.setText(trip.get(TRIP_ARRIVAL_TIME).toString());

        mTextViewArrivingPlace.setText(trip.get(TRIP_ARRIVAL_DESTINATION).toString());
        mTextViewLeavingPlace.setText(trip.get(TRIP_LEAVING_DESTINATION).toString());
        mTextViewTripDate.setText(trip.get(TRIP_DATE).toString());

        mTextViewAvailableSeats.setText(trip.get(TRIP_AVAILABLE_SEATS).toString());
        mTextViewBookedSeats.setText(trip.get(TRIP_BOOKED_SEATS).toString());
        mTextViewGate.setText(trip.get(TRIP_GATE_NUMBER).toString());

        mStartStation = trip.get(TRIP_LEAVING_DESTINATION).toString();
        mEndStation = trip.get(TRIP_ARRIVAL_DESTINATION).toString();

        progressDialog.dismiss();


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
    private void getUserId(){

        String email =  FirebaseAuth.getInstance().getCurrentUser().getEmail();


        if(email != null) {
            db.collection(COLLECTION_USER)
                    .whereEqualTo(User_EMAIL_FIELD, email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (DocumentSnapshot document : task.getResult()) {

                                mUserId = document.getId();

                            }
                        }
                    });
        }

    }

    /**/
    private void checkOfpayment(){

        Intent intent = getIntent();
        boolean isPaymentCorrectly = intent.getBooleanExtra("isPaymentCorrectly",false);
        int mInteger = intent.getIntExtra("mInteger",0);
        mTripId = intent.getStringExtra(ID_KEY_INTENT);

        if(isPaymentCorrectly){
            progressDialog.show();
            addTickets(mInteger);
            updateSeats(mInteger);


        }else {
            display(1);
        }
    }

    /**/
    private void addTickets(int mInteger){


        for(int i = 0 ; i <  mInteger ; i++){

            if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY){
                Map<String,Object> ticket = craeteTicket();
                addTicket(ticket);
            }
            if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY)
                craeteTicket();
        }

    }

    /**/
    private Map<String,Object> craeteTicket() {



        String email = mFirebaseAuth.getCurrentUser().getEmail();


        StringBuilder ticketCodeBulder = randomUUID(16, 4, 'a');
        ticketCode = String.valueOf(ticketCodeBulder);
        if(ticketCode == null)
            ticketCode = "not formatted";
        mTicket.put(TICKET_ID,ticketCode);
        mTicket.put(TICKET_LEAVING_TIME,trip.get(TRIP_LEAVING_TIME));
        mTicket.put(TICKET_TRIP_CODE,trip.get(TRIP_TRIP_CODE));
        mTicket.put(TICKET_ARRIVAL_TIME,trip.get(TRIP_ARRIVAL_TIME));
        mTicket.put(TICKET_ARRIVAL_DESTINATION,trip.get(TRIP_ARRIVAL_DESTINATION));
        mTicket.put(TICKET_DATE,trip.get(TRIP_DATE));
        mTicket.put(TICKET_LEAVING_DESTINATION,trip.get(TRIP_LEAVING_DESTINATION));
        mTicket.put(TICKET_GATE_NUMBER,trip.get(TRIP_GATE_NUMBER));
        mTicket.put(TICKET_USER_EMAIL,email);
        mTicket.put(TICKET_METRO_ID,trip.get(TRIP_METRO_ID));
        CreateTicketID(mTicket);

        return mTicket;

    }


    private void CreateTicketID(Map<String, Object> ticket) {

        Map<String, Object> tickete = new HashMap<String, Object>();

        if(ticket != null){
            ticketCode = ticket.get(TICKET_ID).toString();
        }

        tickete.put(TICKET_ID,ticketCode);
        db.collection(Ticket).document(ticketCode).set(tickete);
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
    private void addTicket(Map<String, Object> ticket) {

        db.collection(COLLECTION_TICKET).add(ticket)

                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        buyTicketUnsuccefully(e);

                    }
                });
    }


    /**/
    private void goToViewTicket() {
        Intent intent = new Intent(this, ViewTicketActivity.class);// Send intent to asmaa
        intent.putExtra(Ticket_KEY_INTENT, (HashMap)mTicket);
        startActivity(intent);
        progressDialog.dismiss();

    }

    /**/
    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number);
        displayInteger.setText(" " + number);
    }


    public void increaseInteger(View view) {
        if(mInteger < 9)
            mInteger = mInteger + 1;
        mTextViewPrice.setText(String.valueOf(mInteger*20));
        display(mInteger);
    }
    public void decreaseInteger(View view) {
        if(mInteger > 1){
            mInteger = mInteger - 1;
            int decm = mInteger;
            mTextViewPrice.setText(String.valueOf(decm*20));
            display(mInteger);}}




    /**/
    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.buyCardButton:
                checkAvailableTickets();
                break;
            case R.id.buyWalletButton:
                buyByWallet();
                break;
            case R.id.buyAdminButton:
                checkAvailableTickets();
                break;
            case R.id.trip_path:
                goToTrackMetro();
                break;

        }


    }

    /**/
    private void confirmBuyWallet(final double newValueOfWellat){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation message");
        builder.setMessage(
                "The money will be excluded from your wallet, are you sure you want to continue?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateWellat(newValueOfWellat);

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**/
    private void buyByWallet(){

        int available_seats = Integer.parseInt(trip.get(TRIP_AVAILABLE_SEATS).toString());

        if(available_seats >= mInteger  ){
            progressDialog.show();

            if(mUserId != null) {
                db.collection(COLLECTION_USER).document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        getDate(task);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        buyTicketUnsuccefully(e);
                    }
                });

            }
        } else {

            displayUnavailableSeats();
        }

    }

    private  void getDate(Task<DocumentSnapshot> task) {

        double wellat = Double.parseDouble(task.getResult().get(User_WELLAT).toString());
        double newValueOfWellat = wellat - (TICKET_PRICE * mInteger);

        confirmBuyWallet(newValueOfWellat);



    }
    /**/

    private void updateWellat(double wellat) {


        db.collection(COLLECTION_USER).document(mUserId).update(User_WELLAT,wellat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addTickets(mInteger);
                updateSeats(mInteger);
            }
        });

    }



    /**/
    public void checkAvailableTickets(){

        int available_seats = Integer.parseInt(trip.get(TRIP_AVAILABLE_SEATS).toString());

        if(available_seats >= mInteger  ){

            if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY){
                goToPaymenet();
            }
            else if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY) {
                addTickets(mInteger);
                updateSeats(mInteger);

            }
        } else {

            displayUnavailableSeats();
        }
    }

    /**/
    private void goToPaymenet() {


        Class buymenetClass = BuyClass.class;
        Context context = ViewTripAndBuyActivity.this;
        int price = Integer.parseInt(mTextViewPrice.getText().toString());
        Intent intent = new Intent(context,buymenetClass);
        intent.putExtra("price",price);
        intent.putExtra(TRIP_KEY_INTENT, (HashMap)trip);
        intent.putExtra(ID_KEY_INTENT,mTripId);
        intent.putExtra("mInteger",mInteger);
        startActivity(intent);
    }



    /**/
    private void updateSeats( int numberOfBookedSeats) {

        progressDialog.show();

        if(trip != null) {

            trip.put(TRIP_AVAILABLE_SEATS, Integer.parseInt(trip.get(TRIP_AVAILABLE_SEATS).toString()) - numberOfBookedSeats);
            trip.put(TRIP_BOOKED_SEATS, Integer.parseInt(trip.get(TRIP_BOOKED_SEATS).toString()) + numberOfBookedSeats);

            if(mTripId != null) {

                db.collection(COLLECTION_TRIP).document(mTripId).set(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        buyTicketSuccefully();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        buyTicketUnsuccefully(e);
                    }
                });

            }

        }
    }

    /**/
    private void buyTicketUnsuccefully( Exception e){

        progressDialog.dismiss();

        String error = e.getMessage();
        Toast.makeText(ViewTripAndBuyActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }



    private void buyTicketSuccefully(){
        goToViewTicket();

    }



    /**/
    private void displayUnavailableSeats() {
        Toast.makeText(ViewTripAndBuyActivity.this,"There are not enough seats",Toast.LENGTH_SHORT);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("No Available seats");
        builder.setMessage("Unfortunately all tickets have been sold out :( Sorry!");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**/
    private void goToTrackMetro() {

        Class trackMetroClass = TrackMetroActivity.class;
        Context context = ViewTripAndBuyActivity.this;

        Intent intent = new Intent(context,trackMetroClass);
        intent.putExtra(TRIP_START_STATION,mStartStation);
        intent.putExtra(TRIP_END_STATION,mEndStation);
        intent.putExtra(INTENT_METRO_OR_TRIP,TRIP_EXTA_NUMBER);
        startActivity(intent);
    }



    /*Sign out*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                singout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**/
    private void singout() {

        mFirebaseAuth.signOut();
        //closing activity
        finish();
        Context context = ViewTripAndBuyActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }




}
