package com.example.hanan.riyadhmetro.manageTrip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.LogoActivity;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.manageMetro.MetroListViewActivity;
import com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity;
import com.example.hanan.riyadhmetro.manageTicket.ScanTicket;
import com.example.hanan.riyadhmetro.manageUser.ViewUserAccountActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.ViewMetroMonitorActivity;
import com.example.hanan.riyadhmetro.user_wallet.WalletActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_ARRIVAL_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_ARRIVAL_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_AVAILABLE_SEATS;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_BOOKED_SEATS;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_GATE_NUMBER;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_LEAVING_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_LEAVING_TIME;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.INTENT_METRO_OR_TRIP;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.TRIP_END_STATION;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.TRIP_EXTA_NUMBER;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.TRIP_START_STATION;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;

public class ViewTripActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView mTextViewGate;
    private TextView mTextViewBookedSeats;
    private TextView mTextViewAvailableSeats;

    private TextView mTextViewLeavingPlace;
    private TextView mTextViewArrivingPlace;

    private TextView mTextViewTripDate;
    private TextView mTextViewArrivingTime;
    private TextView mTextViewLeavingTime;

    private Button mTripPathButton;

    private ProgressDialog progressDialog;
    private String mStartStation;
    private String mEndStation;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_trip);

        initElement();
        viewTrip();



    }

    /**/
    private void initElement() {

        mTextViewGate = findViewById(R.id.textViewGate);
        mTextViewBookedSeats = findViewById(R.id.textViewBookedSeats);
        mTextViewAvailableSeats = findViewById(R.id.textViewAvailableSeats);

        mTextViewLeavingPlace = findViewById(R.id.textViewLeavingPlace);
        mTextViewArrivingPlace = findViewById(R.id.textViewArrivingPlace);

        mTextViewTripDate = findViewById(R.id.tripDateText);
        mTextViewArrivingTime = findViewById(R.id.arrivingTimeText);
        mTextViewLeavingTime = findViewById(R.id.leavingTimetext);

        progressDialog = new ProgressDialog(this);

        mTripPathButton = findViewById(R.id.trip_path);
        mTripPathButton.setOnClickListener(this);

        /*bottom nav*/
        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        setNavigationMenu(navigation,this);
        navigation.setSelectedItemId(R.id.view_trip);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




    }


    /**/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.view_trip:
                    startActivity(new Intent(ViewTripActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_metro:
                    startActivity(new Intent(ViewTripActivity.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_metro_monitor:
                    startActivity(new Intent(ViewTripActivity.this,ViewMetroMonitorActivity.class));
                    return true;
                case R.id.scan_ticket:
                    startActivity(new Intent(ViewTripActivity.this,ScanTicket.class));
                    return true;
                case R.id.view_account_monitor:
                    startActivity(new Intent(ViewTripActivity.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_account_user:
                    startActivity(new Intent(ViewTripActivity.this,ViewUserAccountActivity.class));
                    return true;
                case R.id.wallet:
                    startActivity(new Intent(ViewTripActivity.this,WalletActivity.class));
                    return true;
                case R.id.view_ticket:
                    startActivity(new Intent(ViewTripActivity.this,TripListViewActivity.class));
                    return true;


            }
            return false;
        }
    };

    /**/
    private void viewTrip() {
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(TRIP_KEY_INTENT);
        Map<String, Object> trip = (Map)t;
        setData(trip);

    }

    /**/
    private void setData( Map<String, Object> trip){
        if(trip.size() >= 9 ) {

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

        }
        progressDialog.dismiss();


    }

    @Override
    public void onClick(View view) {

        if(view == mTripPathButton)
            goToTrackMetro();

    }

    /**/
    private void goToTrackMetro() {

        Class trackMetroClass = TrackMetroActivity.class;
        Context context = ViewTripActivity.this;

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


        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseAuth.signOut();
        //closing activity
        finish();
        Context context = ViewTripActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }
}
