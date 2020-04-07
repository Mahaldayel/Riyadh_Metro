package com.example.hanan.riyadhmetro.manageUser;

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
import com.example.hanan.riyadhmetro.manageTicket.TicketListViewActivity;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.user_wallet.WalletActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.User_BIRTH_DATE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.User_EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.User_NAME_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.User_NATIONAL_ID_FIELD;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;
import static com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater.ID_KEY_INTENT;


public class ViewUserAccountActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private TextView mTextViewBrithDate;

    private TextView mTextViewNationalid;
    private Button mUpdateButton;
    private TextView mMetro;

    private ProgressDialog progressDialog;
    private Map<String, Object> mUser;
    private String mId;
    private String mMointorEmail;
    private FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user_account);

        initElement();
        getDataFromDatabaseForUser();
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

        progressDialog = new ProgressDialog(this);


        /*bottom nav*/
        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        setNavigationMenu(navigation,this);
        navigation.setSelectedItemId(R.id.view_account_user);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);





    }



    /**/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {


                case R.id.view_trip:
                    startActivity(new Intent(ViewUserAccountActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_account_user:
                    startActivity(new Intent(ViewUserAccountActivity.this,ViewUserAccountActivity.class));
                    return true;
                case R.id.wallet:
                    startActivity(new Intent(ViewUserAccountActivity.this,WalletActivity.class));
                    return true;
                case R.id.view_ticket:
                    startActivity(new Intent(ViewUserAccountActivity.this,TicketListViewActivity.class));
                    return true;

            }
            return false;
        }
    };


    private void getDataFromDatabaseForUser(){

        String email = mFirebaseAuth.getCurrentUser().getEmail().toLowerCase();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection("User").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                    getDateForUser(task);
                } else {

                    progressDialog.dismiss();
                }

            }
        });

    }

    private  void getDateForUser( Task<QuerySnapshot> task) {


        for (DocumentSnapshot document : task.getResult()) {

            Map<String, Object> user = document.getData();
            mUser = user;
            mId = document.getId();


        }
        setData(mUser);



    }

   

    /**/
    private void setData(Map<String, Object> user) {
        if (user != null) {

            mTextViewName.setText(user.get(User_NAME_FIELD).toString());
            mTextViewBrithDate.setText(user.get(User_BIRTH_DATE_FIELD).toString());
            mTextViewEmail.setText(user.get(User_EMAIL_FIELD).toString());
            mTextViewNationalid.setText(user.get(User_NATIONAL_ID_FIELD).toString());
            mMointorEmail = user.get(User_EMAIL_FIELD).toString();
        }
        progressDialog.dismiss();


    }

    /**/
    private void hideUpdateIfAdmin() {
        if (PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY)
            mUpdateButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

        if (view == mUpdateButton)
            GoToUpdateAcountInfo();
        if (view == mMetro) {
            goToChangeUserPassword();

        }

    }

    /**/
    private void GoToUpdateAcountInfo() {

        if (PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY) {

            Class editUserAccountClass = EditUserAccount.class;
            Intent intent = new Intent(ViewUserAccountActivity.this, editUserAccountClass);
            if (mUser != null) {
                intent.putExtra("user", (HashMap) mUser);
                intent.putExtra(ID_KEY_INTENT, mId);
                startActivity(intent);

            }
        }
    }

    private void goToChangeUserPassword() {
        finish();
        Intent intent = new Intent(ViewUserAccountActivity.this, ChangeUserPassword.class);
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
        Context context = ViewUserAccountActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }

}
