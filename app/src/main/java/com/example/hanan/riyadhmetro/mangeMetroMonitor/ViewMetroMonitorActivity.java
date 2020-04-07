package com.example.hanan.riyadhmetro.mangeMetroMonitor;

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
import com.example.hanan.riyadhmetro.manageMetro.MetroListViewActivity;
import com.example.hanan.riyadhmetro.manageTrip.TripListAdpater;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.manageUser.ChangeUserPassword;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_BIRTH_DATE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_NAME_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_NATIONAL_ID_FIELD;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;
import static com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater.METRO_MONITOR_KEY_INTENT;


public class ViewMetroMonitorActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private TextView mTextViewBrithDate;

    private TextView mTextViewNationalid;
    private Button mUpdateButton;
    private TextView mMetro ;

    private ProgressDialog progressDialog;
    private Map<String, Object> mMetroMonitor;
    private String mId;
    private String mMointorEmail;
    private FirebaseFirestore db ;
    private FirebaseAuth mFirebaseAuth;

    private TextView mPassword ;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metro_monitor_view);

        initElement();

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY )
            getDataFromDatabaseForMonitor();
        else if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY )
            viewMonitorInfo();
        hideUpdateIfAdmin();
    }

    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();


        mTextViewName = findViewById(R.id.monitor_name);
        mTextViewBrithDate = findViewById(R.id.monitor_birth_date);
        mTextViewEmail = findViewById(R.id.monitor_email);
        mTextViewNationalid = findViewById(R.id.monitor_National_id);
        mMetro = findViewById(R.id.metro);
        mUpdateButton = findViewById(R.id.update_button);
        mMetro.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);


        mPassword = findViewById(R.id.passwordLabel);
        mPassword.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);



        /*bottom nav*/
        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        navigation.setSelectedItemId(R.id.view_account_monitor);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setNavigationMenu(navigation,this);



    }


    /**/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.view_trip:
                    startActivity(new Intent(ViewMetroMonitorActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_metro:
                    startActivity(new Intent(ViewMetroMonitorActivity.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_account_monitor:
                    startActivity(new Intent(ViewMetroMonitorActivity.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_metro_monitor:
                    startActivity(new Intent(ViewMetroMonitorActivity.this,MetroMonitorListViewActivity.class));
                    return true;

            }
            return false;
        }
    };



    /**/
    private void getDataFromDatabaseForMonitor(){

        String email = mFirebaseAuth.getCurrentUser().getEmail().toLowerCase();
        String emailStr = "hananF@metro.riyadhmetro.com";
        progressDialog.setMessage("Loading ...");
        progressDialog.show();



        db.collection(COLLECTION_METRO_MONITOR).whereEqualTo(METRO_MONITOR_EMAIL_FIELD, email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                    getDateForMonitor(task);
                } else {
                    progressDialog.dismiss();

                }

            }
        });

    }


    private  void getDateForMonitor( Task<QuerySnapshot> task) {


        for (DocumentSnapshot document : task.getResult()) {

            Map<String, Object> metroMonitor = document.getData();
            mMetroMonitor = metroMonitor;
            mId = document.getId();


        }
        setData(mMetroMonitor);



    }


    /**/
    private void viewMonitorInfo() {
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(METRO_MONITOR_KEY_INTENT);
        mMetroMonitor = (Map)t;
        mId  = intent.getStringExtra(TripListAdpater.ID_KEY_INTENT);
        setData(mMetroMonitor);

    }


    /**/
    private void setData( Map<String, Object> monitor){
        if(monitor != null ) {

            mTextViewName.setText(monitor.get( METRO_MONITOR_NAME_FIELD).toString());
            mTextViewBrithDate.setText(monitor.get(METRO_MONITOR_BIRTH_DATE_FIELD).toString());
            mTextViewEmail.setText(monitor.get(METRO_MONITOR_EMAIL_FIELD).toString());
            mTextViewNationalid.setText(monitor.get(METRO_MONITOR_NATIONAL_ID_FIELD).toString());
            mMointorEmail = monitor.get(METRO_MONITOR_EMAIL_FIELD).toString();
        }
        progressDialog.dismiss();

    }

    /**/
    private void hideUpdateIfAdmin(){
        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY )
        mUpdateButton.setVisibility(View.GONE);
    }





    @Override
    public void onClick(View view) {


        if(view == mUpdateButton)
            GoToUpdateAcountInfo();
        if(view == mMetro ){
            goToAssignedMetro();
        }
        if(view == mPassword ){
            goToChangeUserPassword();
        }

    }

    /**/
    private void GoToUpdateAcountInfo(){

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY ){

            Class  editMetroMonitoClass = EditMetroMonitorActivity.class;
            Intent intent = new Intent(ViewMetroMonitorActivity.this,editMetroMonitoClass);
            if(mMetroMonitor != null){
                intent.putExtra(METRO_MONITOR_KEY_INTENT,(HashMap) mMetroMonitor );
                intent.putExtra(ID_KEY_INTENT,mId);
                startActivity(intent);
            }
        }
    }

    private void goToAssignedMetro() {

        Intent intent = new Intent(ViewMetroMonitorActivity.this,AssignedMetroListViewActivity.class);
        intent.putExtra("email",mMointorEmail);
        intent.putExtra("isMointor",false);
        startActivity(intent);
    }

    private void goToChangeUserPassword() {
        Intent intent = new Intent(ViewMetroMonitorActivity.this,ChangeUserPassword.class);
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
        Context context = ViewMetroMonitorActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }

}
