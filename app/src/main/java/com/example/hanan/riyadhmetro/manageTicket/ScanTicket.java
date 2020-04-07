package com.example.hanan.riyadhmetro.manageTicket;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.hanan.riyadhmetro.LogoActivity;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.manageMetro.MetroListViewActivity;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.app.Activity;
import android.support.annotation.Nullable;

import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TICKET;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ID;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;


public class ScanTicket extends AppCompatActivity {
    private Button scan;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String ticketCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initElement();
    }
    private void initElement(){
        setContentView(R.layout.activity_scan_ticket);
        ticketCode=null;
        scan =(Button)findViewById(R.id.scan);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final Activity activity = this;
        progressDialog = new ProgressDialog(this);


        /*bottom nav*/
        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        setNavigationMenu(navigation,this);
        navigation.setSelectedItemId(R.id.scan_ticket);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator= new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);// in orginall code false :)
                integrator.initiateScan();
            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.view_trip:
                    startActivity(new Intent(ScanTicket.this,TripListViewActivity.class));
                    return true;
                case R.id.view_metro:
                    startActivity(new Intent(ScanTicket.this,MetroListViewActivity.class));
                    return true;
                case R.id.view_metro_monitor:
                    startActivity(new Intent(ScanTicket.this,MetroMonitorListViewActivity.class));
                    return true;
                case R.id.scan_ticket:
                    startActivity(new Intent(ScanTicket.this,ScanTicket.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result!=null){
            if (result.getContents()==null){
                Toast.makeText(this,"You canceled the scanner",Toast.LENGTH_LONG).show();
            }
            else {
                // Toast.makeText(this,"Notnull",Toast.LENGTH_LONG).show();
                check(result.getContents());
                //
            }
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void check(final String result) {
        final String ticketId = result.trim() ;
        Map<String, Object> tickete = new HashMap<String, Object>();
        tickete.put(TICKET_ID,ticketId);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection("Ticket").whereEqualTo(TICKET_ID , ticketId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if(task.getResult() != null ){
                                                   setTicketCode(result);
                                                   getDate(task);
                                               }
                                           }
                                       }
                );
        progressDialog.dismiss();
    }

    private void setTicketCode(String result) {

        ticketCode = result;
    }
    private String getTicketCode() {

        return ticketCode;
    }
    private  void getDate( @NonNull Task<QuerySnapshot> task) {
        boolean isEmpty = true;

        for (DocumentSnapshot document : task.getResult()) {

            Map<String, Object> tick = document.getData();
            isEmpty = false;
        }
        if(!isEmpty){
            // Toast.makeText(this,getTicketCode(),Toast.LENGTH_LONG).show();
            deleteTicket();
            checkSuccefully();
        }
        else {
            checkUnsuccefully();
        }
    }



    /**/
    private void checkUnsuccefully() {
        Toast.makeText(this, "Your Ticket code is not exciting" , Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    /**/
    private void checkSuccefully() {
        Toast.makeText(this,"Ticket can pass",Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
    }
    private void whereareyhou(){
        Toast.makeText(this,"In success delete",Toast.LENGTH_LONG).show();
    }
    /**/
    private void deleteTicket() {
        db.collection("Ticket").document(ticketCode).delete().addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //whereareyhou();
                        setTicketCode(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteTripUnsuccefully(e);
            }
        });




    }



    private void deleteTripUnsuccefully(Exception e) {
        Toast.makeText(this, "Your ticket is already Passed" , Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
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
        Context context = ScanTicket.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }
}