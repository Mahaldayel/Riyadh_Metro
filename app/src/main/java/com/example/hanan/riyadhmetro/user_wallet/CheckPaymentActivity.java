package com.example.hanan.riyadhmetro.user_wallet;

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

public class CheckPaymentActivity extends AppCompatActivity implements View.OnClickListener{


    private ProgressDialog progressDialog;
    private Map<String, Object> user;
    private String id;
    private EditText cardNumber, ExDate, secutityCode;
    private Button buy;
    private FirebaseFirestore db;
    private int mInteger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyment);


        initElement();
        buy.setText("Add Money");


    }

    /**/
    private void checkUnsuccefully() {

        progressDialog.dismiss();
        Toast.makeText(CheckPaymentActivity.this, "You did not enter the correct information " , Toast.LENGTH_SHORT).show();

    }

    /**/
    private void checkSuccefully() {

        getTripFromIntent();
        goBackToViewTrip();
        progressDialog.dismiss();



    }

    private void goBackToViewTrip() {

        Toast.makeText(CheckPaymentActivity.this, "The money has been added to your wallet!", Toast.LENGTH_SHORT).show();

        Context context = CheckPaymentActivity.this;
        Class addMoney =WalletActivity.class;
        Intent intent = new Intent(context,addMoney);
        intent.putExtra("user", (HashMap)user);
        intent.putExtra("id",id);
        intent.putExtra("isPaymentCorrectly",true);
        intent.putExtra("price",mInteger);
        startActivity(intent);
    }

    private void check() {
        //   Map<String,String> bank=new HashMap<>();
        String secCode = secutityCode.getText().toString().trim();
        String date = ExDate.getText().toString().trim();
        String number = cardNumber.getText().toString().trim();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection("Bank").whereEqualTo("card_number", number).whereEqualTo("expiration_date", date).whereEqualTo("security_code", secCode)
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


    private  void getDate( Task<QuerySnapshot> task) {

        int size ;


        size = task.getResult().size();



        if(size != 0)
            checkSuccefully();
        else
            checkUnsuccefully();
    }
    private void initElement(){

        progressDialog = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();
        ExDate = findViewById(R.id.editTextExDate);
        cardNumber = findViewById(R.id.CardNumber);
        secutityCode = findViewById(R.id.securityCode);

        buy = findViewById(R.id.buy);
        buy.setOnClickListener(this);

    }
    /**/
    private  void getTripFromIntent(){
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra("user");
       id  = intent.getStringExtra("id");
        mInteger = intent.getIntExtra("price",0);

        user = (Map)t;


    }


    @Override
    public void onClick(View view) {
        if (view == buy) {
            check();
        }

    }
}
