package com.example.hanan.riyadhmetro.buyTicket;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.BANK_CARD_NUMBER;
import static com.example.hanan.riyadhmetro.DatabaseName.BANK_EXPIRATION_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.BANK_SECURITY_CODE;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_BANK;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;


public class BuyClass extends AppCompatActivity implements View.OnClickListener{
    public static final boolean SussecfullyInfo = true;


    private ProgressDialog progressDialog;
    private Map<String, Object> trip;
    private String mTripId;
    private EditText cardNumber, ExDate, secutityCode;
    private Button buy;
    private FirebaseFirestore db;
    private int mInteger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyment);


        initElement();

    }

    /**/

    private void initElement(){

        progressDialog = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();
        ExDate = findViewById(R.id.editTextExDate);
        cardNumber = findViewById(R.id.CardNumber);
        secutityCode = findViewById(R.id.securityCode);

        buy = findViewById(R.id.buy);
        buy.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view == buy) {
            check();
        }

    }

    /**/
    private void check() {
        String secCode = secutityCode.getText().toString().trim();
        String date = ExDate.getText().toString().trim();
        String number = cardNumber.getText().toString().trim();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection(COLLECTION_BANK).whereEqualTo(BANK_CARD_NUMBER, number).whereEqualTo(BANK_EXPIRATION_DATE, date).whereEqualTo(BANK_SECURITY_CODE, secCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult() != null){

                            getDate(task);
                        }
                    }
                });
    }

    /**/
    private  void getDate( Task<QuerySnapshot> task) {

        int size ;


        size = task.getResult().size();



        if(size != 0)
            checkSuccefully();
        else
            checkUnsuccefully();
    }



    /**/
    private void checkUnsuccefully() {

        progressDialog.dismiss();
        Toast.makeText(BuyClass.this, "You did not enter the correct information " , Toast.LENGTH_SHORT).show();

    }

    /**/
    private void checkSuccefully() {

        getTripFromIntent();
        goBackToViewTrip();
        progressDialog.dismiss();



    }


    /**/
    private  void getTripFromIntent(){
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(TRIP_KEY_INTENT);
        mTripId  = intent.getStringExtra(ID_KEY_INTENT);
        mInteger = intent.getIntExtra("mInteger",0);

        trip = (Map)t;


    }

    /**/
    private void goBackToViewTrip() {

        Toast.makeText(BuyClass.this, "Your ticket has been bought succefully!", Toast.LENGTH_SHORT).show();

        Context context = BuyClass.this;
        Class BuyTicket = ViewTripAndBuyActivity.class;
        Intent intent = new Intent(context,BuyTicket);
        intent.putExtra(TRIP_KEY_INTENT, (HashMap)trip);
        intent.putExtra(ID_KEY_INTENT,mTripId);
        intent.putExtra("isPaymentCorrectly",true);
        intent.putExtra("mInteger",mInteger);
        startActivity(intent);
    }




}



