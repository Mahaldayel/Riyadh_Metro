package com.example.hanan.riyadhmetro.manageTrip;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.LogoActivity;
import com.example.hanan.riyadhmetro.SigninActivity;
import com.example.hanan.riyadhmetro.buyTicket.ViewTripAndBuyActivity;
import com.example.hanan.riyadhmetro.manageMetro.MetroListViewActivity;
import com.example.hanan.riyadhmetro.manageTicket.ScanTicket;
import com.example.hanan.riyadhmetro.manageTicket.TicketListViewActivity;
import com.example.hanan.riyadhmetro.manageUser.ViewUserAccountActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListViewActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.ViewMetroMonitorActivity;
import com.example.hanan.riyadhmetro.user_wallet.WalletActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TRIP;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_DATE;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;

public class TripListViewActivity extends AppCompatActivity implements TripListAdpater.ListItemClickListener ,View.OnClickListener ,SearchView.OnQueryTextListener {


    private RecyclerView mTripList;
    private TripListAdpater mAdapter;
    private List<String> mIdList;
    private ProgressDialog progressDialog;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseFirestore db ;
    private List<Map<String, Object>> mTrips;
    private static View emptyView;


    private FirebaseAuth mFirebaseAuth;
    private NotificationManagerCompat notificationManager;
    public static final String CHANNEL_1_ID = "Channel1";
    private List<Map<String, Object>> mTickets;
    private int numm = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_list_view);

        initElement();
        viewRecyclerView();
        hideAddButtonForUser();


        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY )
        {
            new NotificationAsyncTask().execute();
        }
        if(mFirebaseAuth.getCurrentUser() == null)
            startActivity(new Intent(TripListViewActivity.this,SigninActivity.class));


    }

    /**/
    private void initElement(){


        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mTickets = new ArrayList<>();

        mIdList = new ArrayList<>();
        mTrips = new ArrayList<>();

        progressDialog = new ProgressDialog(this);


        emptyView = findViewById(R.id.empty_view);
        mTripList = findViewById(R.id.rv);

        mFloatingActionButton = findViewById(R.id.addButton);
        mFloatingActionButton.setOnClickListener(this);

        /*bottom nav*/
        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        setNavigationMenu(navigation,this);
        navigation.setSelectedItemId(R.id.view_trip);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




    }

    /**/
    public static void setNavigationMenu(BottomNavigationView navigation ,Context context){

        if(PreferencesUtility.getAuthority(context) == PreferencesUtility.MONITOR_AUTHORITY )
            navigation.inflateMenu(R.menu.activity_main_bottom_nav_monitor);

        else if(PreferencesUtility.getAuthority(context) == PreferencesUtility.ADMIN_AUTHORITY )
            navigation.inflateMenu(R.menu.activity_main_bottom_nav_admin);

        else if(PreferencesUtility.getAuthority(context) == PreferencesUtility.USER_AUTHORITY )
            navigation.inflateMenu(R.menu.activity_main_bottom_nav_user);



    }

    /**/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.view_trip:
                    startActivity(new Intent(TripListViewActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_metro:
                    startActivity(new Intent(TripListViewActivity.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_metro_monitor:
                    startActivity(new Intent(TripListViewActivity.this,MetroMonitorListViewActivity.class));
                    return true;
                case R.id.scan_ticket:
                    startActivity(new Intent(TripListViewActivity.this,ScanTicket.class));
                    return true;
                case R.id.view_account_monitor:
                    startActivity(new Intent(TripListViewActivity.this,ViewMetroMonitorActivity.class));
                    return true;
                case R.id.view_account_user:
                    startActivity(new Intent(TripListViewActivity.this,ViewUserAccountActivity.class));
                    return true;
                case R.id.wallet:
                    startActivity(new Intent(TripListViewActivity.this,WalletActivity.class));
                    return true;
                case R.id.view_ticket:
                    startActivity(new Intent(TripListViewActivity.this,TicketListViewActivity.class));
                    return true;


            }
            return false;
        }
    };

    /**/
    private void viewRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTripList.setLayoutManager(layoutManager);
        mTripList.setHasFixedSize(true);
        getDataFromDatabase();

    }

    /**/
    private void getDataFromDatabase(){

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();


        db.collection(COLLECTION_TRIP).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                    getDate(task);
                } else {

                    progressDialog.dismiss();
                    Toast.makeText(TripListViewActivity.this,"Error",Toast.LENGTH_LONG);
                }
            }
        });
    }

    /**/
    private  void getDate( Task<QuerySnapshot> task) {



        boolean isEmpty = true;

        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");

        for (DocumentSnapshot document : task.getResult()) {


                Map<String, Object> trip = document.getData();


            Date tripDate = convertStringToDate(trip.get(TRIP_DATE).toString(), sdf);
            Date todayDate = getDateOfToday(sdf);

            String doc = document.getId();

            if (tripDate != null && todayDate != null) {
                if (tripDate.before(getDateOfToday(sdf))) {

                    deleteTrip(doc);

                } else {
                    mIdList.add(document.getId());
                    mTrips.add(trip);

                    if (trip.size() != 0)
                        isEmpty = false;
                }
            }
        }

        displayListView(isEmpty);
    }

    /**/
    private void displayListView(boolean isEmpty){


        if(!isEmpty ){
            emptyView.setVisibility(View.GONE);
            initAdapter();
        }else {
            progressDialog.dismiss();
            emptyView.setVisibility(View.VISIBLE);
        }


    }

    /**/
    private Date getDateOfToday(SimpleDateFormat sdf){


        Calendar c = Calendar.getInstance();
        String today = sdf.format(c.getTime());
        Date date = null;
        try {
            date = sdf.parse(today);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
    /**/
    private Date convertStringToDate(String dateStr,SimpleDateFormat sdf){

        Date date = null;


        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**/
    private void deleteTrip(String doc){


        db.collection(COLLECTION_TRIP).document(doc).delete();
    }

    /**/
    private void initAdapter(){

        mAdapter = new TripListAdpater(mTrips,this,this,mIdList);
        mTripList.setAdapter(mAdapter);
        progressDialog.dismiss();
    }



    @Override
    public void onListItemClick(int clickedItemIndex,List<Map<String, Object>> trips) {

        HashMap<String, Object> trip =(HashMap) trips.get(clickedItemIndex);
        Context context = TripListViewActivity.this;
        Class tripClass;


        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY )
            tripClass = ViewTripActivity.class;
        else
            tripClass = ViewTripAndBuyActivity.class;

        Intent intent = new Intent(context,tripClass);
        intent.putExtra(TRIP_KEY_INTENT, trip);

        startActivity(intent);
    }

    /**/
    @SuppressLint("RestrictedApi")
    public void hideAddButtonForUser(){


        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY
                || PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY ){
            mFloatingActionButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {

        if(view == mFloatingActionButton)
            goToAddTrip();
    }

    private void goToAddTrip() {

        Context context = TripListViewActivity.this;
        Class AddTripClass = AddTripActivity.class;
        Intent intent = new Intent(context,AddTripClass);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if ( TextUtils.isEmpty ( newText) ) {

            mAdapter.getFilter().filter("");
        } else {
            mAdapter.getFilter().filter(newText.toString());
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem m = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) m.getActionView();
        searchView.setOnQueryTextListener(TripListViewActivity.this);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out, menu);


        return true;
    }


    /**/
    public static View getEmptyView(){
        return emptyView;
    }



    /*Sign out*/

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
        Context context = TripListViewActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }


    /*NotificationAsyncTask*/
    class NotificationAsyncTask extends AsyncTask<Long,Void,Void> {

        @Override
        protected Void doInBackground(Long... params) {

            boolean notDone = true;
            while (notDone){

                Calendar cal = Calendar. getInstance();
                Date today = cal. getTime();
                int hour = today.getHours();

                if(hour > 0){
                    createNotificationChannels();
                    notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    getDataFromDatabaseForTickets();
                    notDone = false;
                }
            }

            return null;
        }

        private void createNotificationChannels() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel1 = new NotificationChannel(
                        CHANNEL_1_ID,
                        "channel 1",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel1.setDescription("This is channel 1");
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannels(Collections.singletonList(channel1));
            }
        }


        public void sendOnChannel(String arr) {


            Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Your trip is today: " + arr + ", Check your tickets! ")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();


            notificationManager.notify(numm++, notification);
        }

        /**/
        private void getDataFromDatabaseForTickets() {


            if(mFirebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(TripListViewActivity.this, SigninActivity.class));
                return;
            }

            String email = mFirebaseAuth.getCurrentUser().getEmail();

            db.collection("Tickect").whereEqualTo("User Email", email).get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult() != null) {
                                getDateForTickets(task);
                            } else {
                                Toast.makeText(TripListViewActivity.this, "Error", Toast.LENGTH_LONG);
                            }

                        }
                    });
        }


        /**/
        private void getDateForTickets(Task<QuerySnapshot> task) {

            for (DocumentSnapshot document : task.getResult()) {
                Map<String, Object> ticket = document.getData();

                mTickets.add(ticket);

            }
            filterDate((ArrayList<Map<String, Object>>) mTickets);

        }


        public void filterDate(ArrayList<Map<String, Object>> UserTickets) {
            ArrayList<String> arr = new ArrayList<>();

            String dateStore ;
            Map<String, Object> filtered ;
            for (int i = 0; i < UserTickets.size(); i++) {
                filtered = UserTickets.get(i);

                if (filtered != null) {
                    dateStore = filtered.get("Date").toString();

                    if (dateStore != null){

                        Calendar cal = Calendar.getInstance();
                        Date today =  cal.getTime();
                        Date ticketDate = convertTime(dateStore);

                        if(today.getDate() == ticketDate.getDate() && today.getMonth() == ticketDate.getMonth() && today.getYear() == ticketDate.getYear() )
                            sendOnChannel(dateStore);
                    }
                }

            }

        }


    }

    /**/
    private static Date convertTime(String dateStr){

        Date date = null;
        String sDate = dateStr ;

        try {
            date = new SimpleDateFormat("d/M/yyyy").parse(sDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
