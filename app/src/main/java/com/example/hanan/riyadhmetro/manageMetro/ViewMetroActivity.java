package com.example.hanan.riyadhmetro.manageMetro;

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
import com.example.hanan.riyadhmetro.assign.AssignedMetroListViewActivity;
import com.example.hanan.riyadhmetro.manageTicket.ScanTicket;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_STATION;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_METRO_STATUS_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_NUMBER_OF_SEATS_FIELD;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;
import static com.example.hanan.riyadhmetro.manageMetro.MetroListAdpater.Metro_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.INTENT_METRO_OR_TRIP;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.METRO_CURRENT_STATION;
import static com.example.hanan.riyadhmetro.manageMetro.TrackMetroActivity.METRO_EXTA_NUMBER;

public class ViewMetroActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView mTextViewMetroId;
    private TextView mTextViewnumberSeats;

    private TextView mTextViewStatus;
    private Button mMonitor;
    private Button mTrackMetro;

    private ProgressDialog progressDialog;
    private FirebaseFirestore db ;

    private String metroStation ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_metro);

        initElement();
        viewMetro();
    }


    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();
        mTextViewMetroId =findViewById(R.id.textViewMetroID);

        mMonitor = findViewById(R.id.monitor);
        mTrackMetro = findViewById(R.id.metro_station);
        mTextViewnumberSeats = findViewById(R.id.textViewnumberSeats);
        mTextViewStatus = findViewById(R.id.textViewStatus);


        progressDialog = new ProgressDialog(this);

        mTrackMetro.setOnClickListener(this);
        mMonitor.setOnClickListener(this);
        /*bottom nav*/
        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        setNavigationMenu(navigation,this);
        navigation.setSelectedItemId(R.id.view_metro);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


    /**/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {


                case R.id.view_trip:
                    startActivity(new Intent(ViewMetroActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_metro:
                    startActivity(new Intent(ViewMetroActivity.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_account_monitor:
                    startActivity(new Intent(ViewMetroActivity.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_metro_monitor:
                    startActivity(new Intent(ViewMetroActivity.this,MetroMonitorListViewActivity.class));
                    return true;
                case R.id.scan_ticket:
                    startActivity(new Intent(ViewMetroActivity.this,ScanTicket.class));
                    return true;

            }
            return false;
        }
    };



    /**/
    private void viewMetro() {
        Intent intent = getIntent();
        HashMap<String, String> m = (HashMap<String, String>)intent.getSerializableExtra(Metro_KEY_INTENT);
        Map<String, Object> metro= (Map)m;
        setData(metro);

    }



    /**/
    private void setData( Map<String, Object> metro){
        if(metro.size() > 0) {

            mTextViewMetroId.setText(metro.get(METRO_METRO_ID_FIELD).toString());
            mTextViewnumberSeats.setText(metro.get(METRO_NUMBER_OF_SEATS_FIELD).toString());
            mTextViewStatus.setText(metro.get(METRO_METRO_STATUS_FIELD).toString());

            metroStation = metro.get(METRO_METRO_STATION).toString();


        }
        progressDialog.dismiss();


    }

    @Override
    public void onClick(View view) {

        if(view == mMonitor)
            goToAssignedMonitor();
        else if(view == mTrackMetro){
            goToTrackMetro();
        }
    }

    private void goToTrackMetro() {

        Class trackMetroClass = TrackMetroActivity.class;
        Context context = ViewMetroActivity.this;

        Intent intent = new Intent(context,trackMetroClass);
        intent.putExtra(METRO_CURRENT_STATION,metroStation);
        intent.putExtra(INTENT_METRO_OR_TRIP,METRO_EXTA_NUMBER);
        startActivity(intent);
    }


    private void goToAssignedMonitor() {

        String metroId = mTextViewMetroId.getText().toString();
        Intent intent = new Intent(ViewMetroActivity.this,AssignedMetroListViewActivity.class);
        intent.putExtra("isMointor",true);
        intent.putExtra("metro_id",metroId);
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
        Context context = ViewMetroActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }


}
