package com.example.hanan.riyadhmetro.manageTicket;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.LogoActivity;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.manageUser.ViewUserAccountActivity;
import com.example.hanan.riyadhmetro.user_wallet.WalletActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TICKET;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_USER_EMAIL;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_DATE;
import static com.example.hanan.riyadhmetro.manageTicket.TicketListAdpater.Ticket_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;


public class TicketListViewActivity extends AppCompatActivity implements  TicketListAdpater.ListItemClickListener ,SearchView.OnQueryTextListener  {


    private RecyclerView mticketList;
    private TicketListAdpater mAdapter;
    private List<String> mIdList;
    private ProgressDialog progressDialog;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseFirestore db ;
    private List<Map<String, Object>> mtickets;
    private List<Map<String, Object>> admintickets; // the com from admin
    private static View emptyView;
    private TextView mEmptyTitleText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_list_view);

        initElement();
        viewRecyclerView();

        hideAddButton();
    }

    /**/

    private void initElement(){

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mIdList = new ArrayList<>();


        emptyView = findViewById(R.id.empty_view);
        mEmptyTitleText = findViewById(R.id.empty_title_text);
        mEmptyTitleText.setText("There no avalible Tickets");

        mtickets = new ArrayList<Map<String, Object>>();
        mticketList = findViewById(R.id.rv);
        progressDialog = new ProgressDialog(this);
        mFloatingActionButton = findViewById(R.id.addButton);
        /*bottom nav*/
        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        setNavigationMenu(navigation,this);
        navigation.setSelectedItemId(R.id.view_ticket);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);





    }



    /**/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.view_trip:
                    startActivity(new Intent(TicketListViewActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_account_user:
                    startActivity(new Intent(TicketListViewActivity.this,ViewUserAccountActivity.class));
                    return true;
                case R.id.wallet:
                    startActivity(new Intent(TicketListViewActivity.this,WalletActivity.class));
                    return true;
                case R.id.view_ticket:
                    startActivity(new Intent(TicketListViewActivity.this,TicketListViewActivity.class));
                    return true;

            }
            return false;
        }
    };

    /**/
    private void viewRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mticketList.setLayoutManager(layoutManager);
        mticketList.setHasFixedSize(true);
        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY ){
            getDataFromAdminList();
        } else {
            getDataFromDatabase();
        }

    }


    /**/
    private void getDataFromAdminList(){
        mtickets = admintickets ;
        if(mtickets != null ){
            emptyView.setVisibility(View.GONE);
            initAdapter();
        }else {
            progressDialog.dismiss();
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    /**/
    private void getDataFromDatabase(){

        String email = firebaseAuth.getCurrentUser().getEmail();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection(COLLECTION_TICKET).whereEqualTo(TICKET_USER_EMAIL, email).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult() != null){
                            getDate(task);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(TicketListViewActivity.this,"Error",Toast.LENGTH_LONG);
                        }

                    }
                });
    }


    /**/
    private  void getDate(Task<QuerySnapshot> task) {

        boolean isEmpty = true;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (DocumentSnapshot document : task.getResult()) {
            Map<String, Object> ticket = document.getData();

            Date ticketDate = convertStringToDate(ticket.get(TRIP_DATE).toString(), sdf);
            Date todayDate = getDateOfToday(sdf);

            String doc = document.getId();

            if(ticketDate.before(todayDate)){
                deleteTicket(doc);
            }
            else{
                mtickets.add(ticket);
                mIdList.add(document.getId());


                if (mtickets != null && mtickets.size() != 0)
                    isEmpty = false;

            }}

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
    private void deleteTicket(String doc){


        db.collection(COLLECTION_TICKET).document(doc).delete();
    }
    /**/
    private void initAdapter(){
        mAdapter = new TicketListAdpater(mtickets,this,this ,mIdList);
        mticketList.setAdapter(mAdapter);
        progressDialog.dismiss();
    }


    /**/
    @SuppressLint("RestrictedApi")
    private void hideAddButton() {

        mFloatingActionButton.setVisibility(View.GONE);

    }


    /**/
    public static View getEmptyView(){

        return emptyView;
    }




    @Override
    public void onListItemClick(int clickedItemIndex, List<Map<String, Object>> tickets) {

        HashMap<String, Object> tickethere = (HashMap) tickets.get(clickedItemIndex);
        Context context = TicketListViewActivity.this;
        Class metroMonitorClass = ViewTicketActivity.class;

        Intent intent = new Intent(context,metroMonitorClass);
        intent.putExtra(Ticket_KEY_INTENT, tickethere);
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
        searchView.setOnQueryTextListener(TicketListViewActivity.this);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out, menu);


        return true;
    }
    /**/

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
        Context context = TicketListViewActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }

}
