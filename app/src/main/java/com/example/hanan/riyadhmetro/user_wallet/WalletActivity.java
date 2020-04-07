package com.example.hanan.riyadhmetro.user_wallet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.LogoActivity;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.manageTicket.TicketListViewActivity;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.manageUser.ViewUserAccountActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;


public class WalletActivity extends AppCompatActivity implements OnClickListener {
    private EditText mTextViewPrice;
    private FirebaseFirestore db ;
    private FirebaseAuth mFirebaseAuth;
    private int mInteger;
    private ProgressDialog progressDialog;
    private Button addmoney;
    private Map<String, Object> user;
    private String email ;
    private String emailId;
    private  String id;
    private String cid;
    private TextView MyMoney;
    private String money;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_wallet);
        initElement();
        getDataFromDatabaseForUser();

        if(mTextViewPrice.getText().toString() != null)
            checkOfpayment();
        else
            Toast.makeText(WalletActivity.this, "You did not enter the amount ", Toast.LENGTH_SHORT).show();

    }


    private void initElement() {

        db = FirebaseFirestore.getInstance();
        mInteger = 1;
        mTextViewPrice = findViewById(R.id.amount);
        addmoney = findViewById(R.id.addMoney);
        mFirebaseAuth = FirebaseAuth.getInstance();
        MyMoney = findViewById(R.id.MyMoney);
        addmoney.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        /*bottom nav*/
        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        setNavigationMenu(navigation,this);
        navigation.setSelectedItemId(R.id.wallet);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }



    /**/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.view_trip:
                    startActivity(new Intent(WalletActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_account_user:
                    startActivity(new Intent(WalletActivity.this,ViewUserAccountActivity.class));
                    return true;
                case R.id.wallet:
                    startActivity(new Intent(WalletActivity.this,WalletActivity.class));
                    return true;
                case R.id.view_ticket:
                    startActivity(new Intent(WalletActivity.this,TicketListViewActivity.class));
                    return true;

            }
            return false;
        }
    };


    private void getDataFromDatabaseForUser(){

        String email = mFirebaseAuth.getCurrentUser().getEmail();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection("User").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                    getDate(task);
                } else {

                }

            }
        });

    }

    private  void getDate( Task<QuerySnapshot> task) {


        for (DocumentSnapshot document : task.getResult()) {

            user = document.getData();

            email = user.get("Email").toString();
            getUserId();
            MyMoney.setText("Account Balance: "+user.get("wallet").toString()+" SAR");

            progressDialog.dismiss();


        }


    }


    private void goToPaymenet() {


        Class buymenetClass = CheckPaymentActivity.class;
        Context context = WalletActivity.this;
        int price = Integer.parseInt(mTextViewPrice.getText().toString());
        if (price > 0) {
            Intent intent = new Intent(context, buymenetClass);
            // intent.putExtra("price",price);
            intent.putExtra("user", (HashMap) user);
            intent.putExtra("id", cid);
            intent.putExtra("price", price);
            startActivity(intent);
        } else
            Toast.makeText(WalletActivity.this, "You did not enter the amount ", Toast.LENGTH_SHORT).show();

    }

    private void getId(){

        String email = mFirebaseAuth.getCurrentUser().getEmail();

        id = String.valueOf(db.collection("User").whereEqualTo("Email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // ff.collection

                        for (DocumentSnapshot document : task.getResult()) {

                            id = document.getId();
                        }
                    }
                }));
    }

    private void getUserId(){

        db.collection("User").whereEqualTo("Email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()) {

                            emailId = document.getId();

                            if(emailId != null){
                                cid = emailId;}
                        }
                    }
                });

    }



    private void checkOfpayment(){

        Intent intent = getIntent();
        boolean isPaymentCorrectly = intent.getBooleanExtra("isPaymentCorrectly",false);
        int price3 = intent.getIntExtra("price",0);


        if(isPaymentCorrectly){
            progressDialog.show();
            getId();

            addToWallet(price3);
            progressDialog.dismiss();


        }else {
        }
    }

    private  void getForIntent(){
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra("user");
        user = (Map)t;
        email = user.get("Email").toString();
        getUserId();
        MyMoney.setText("Account Balance: "+user.get("wallet").toString()+" SAR");
    }




    private void addToWallet(int amount) {
        getForIntent();
        progressDialog.show();
        Intent intent = getIntent();//?
        cid = intent.getStringExtra("id");
        if(user != null) {

            String walletStr = user.get("wallet").toString();
            double oldWallet = Double.parseDouble(walletStr);
            double newWallet = oldWallet + amount;
            user.put("wallet", newWallet);

            if(cid != null) {

                db.collection("User").document(cid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }

        }

    }



    @Override
    public void onClick(View v) {
        if(v == addmoney) {
            checkNotEmpty();
        }
    }

    /**/
    private void checkNotEmpty(){

        money = mTextViewPrice.getText().toString().trim();

        if(TextUtils.isEmpty(money)) {
            mTextViewPrice.setError("Please enter amount");
        }else if(Double.parseDouble(money) <= 0){

            mTextViewPrice.setError("Please number more then 0 ");

        }

        else
            goToPaymenet();


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
        Context context = WalletActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }
}
