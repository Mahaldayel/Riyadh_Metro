package com.example.hanan.riyadhmetro.manageMetro;

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
import com.example.hanan.riyadhmetro.manageTicket.ScanTicket;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListViewActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.ViewMetroMonitorActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;
import static com.example.hanan.riyadhmetro.manageMetro.MetroListAdpater.Metro_KEY_INTENT;

public class MetroListViewActivity extends AppCompatActivity implements MetroListAdpater.ListItemClickListener ,View.OnClickListener ,SearchView.OnQueryTextListener {


    private RecyclerView mMetroList;
    private MetroListAdpater mAdapter;
    private List<String> mIdList;
    private ProgressDialog progressDialog;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseFirestore db ;
    private List<Map<String, Object>> mMetro;
    private static View emptyView;
    private TextView mEmptyTitleText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metro_list_view_activity);

        initElement();
        viewRecyclerView();
        hideAddButtonForMonitor();

    }

    /**/
    private void initElement(){

        emptyView = findViewById(R.id.empty_view);

        mIdList = new ArrayList<>();
        mMetro = new ArrayList<Map<String,Object>>();
        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        mMetroList = findViewById(R.id.rv);
        mFloatingActionButton = findViewById(R.id.addButton);

        mFloatingActionButton.setOnClickListener(this);

        initEmptyView();

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
                    startActivity(new Intent(MetroListViewActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_metro:
                    startActivity(new Intent(MetroListViewActivity.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_account_monitor:
                    startActivity(new Intent(MetroListViewActivity.this,ViewMetroMonitorActivity.class));
                    return true;
                case R.id.view_metro_monitor:
                    startActivity(new Intent(MetroListViewActivity.this,MetroMonitorListViewActivity.class));
                    return true;
                case R.id.scan_ticket:
                    startActivity(new Intent(MetroListViewActivity.this,ScanTicket.class));
                    return true;

            }
            return false;
        }
    };


    /**/
    private void initEmptyView(){

        emptyView = findViewById(R.id.empty_view);

        mEmptyTitleText = findViewById(R.id.empty_title_text);
        mEmptyTitleText.setText("There no Metro");
    }

    /**/
    private void viewRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mMetroList.setLayoutManager(layoutManager);
        mMetroList.setHasFixedSize(true);
        getDataFromDatabase();

    }

    /**/
    private void getDataFromDatabase(){

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();


        db.collection(COLLECTION_METRO).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                    getDate(task);
                } else {

                    progressDialog.dismiss();
                    Toast.makeText(MetroListViewActivity.this,"Error",Toast.LENGTH_LONG);
                }

            }
        });



    }


    private  void getDate( Task<QuerySnapshot> task) {

        boolean isEmpty = true;

        for (DocumentSnapshot document : task.getResult()) {

            Map<String, Object> metro = document.getData();
            mIdList.add(document.getId());
            mMetro.add(metro);

            if (metro.size() != 0)
                isEmpty = false;
        }
        if(!isEmpty ){

            emptyView.setVisibility(View.GONE);
            initAdapter();
        }else {

            progressDialog.dismiss();
            emptyView.setVisibility(View.VISIBLE);

        }
    }

    /**/
    private void initAdapter(){

        mAdapter = new MetroListAdpater(mMetro,this,this,mIdList);
        mMetroList.setAdapter(mAdapter);
        progressDialog.dismiss();
    }

    /**/
    @Override
    public void onClick(View view) {

        if(view == mFloatingActionButton)
            goToAddMetro();
    }


    /**/
    private void goToAddMetro() {

        Context context = MetroListViewActivity.this;
        Class AddMetroClass = addMetro.class;

        Intent intent = new Intent(context,AddMetroClass);
        startActivity(intent);
    }



    @SuppressLint("RestrictedApi")
    public void hideAddButtonForMonitor(){


        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY )
            mFloatingActionButton.setVisibility(View.GONE);
    }

    /**/
    public static View getEmptyView(){
        return emptyView;
    }


    @Override
    public void onListItemClick(int clickedItemIndex,List<Map<String, Object>> metroList) {

        HashMap<String, Object> metro =(HashMap) metroList.get(clickedItemIndex);
        Context context = MetroListViewActivity.this;

        Class metroClass = ViewMetroActivity.class;

        Intent intent = new Intent(context,metroClass);
        intent.putExtra(Metro_KEY_INTENT, metro);
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
        searchView.setOnQueryTextListener(MetroListViewActivity.this);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out, menu);


        return true;
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
        Context context = MetroListViewActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }

}
