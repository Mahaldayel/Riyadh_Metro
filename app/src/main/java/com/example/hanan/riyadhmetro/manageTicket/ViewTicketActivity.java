package com.example.hanan.riyadhmetro.manageTicket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.LogoActivity;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.manageUser.ViewUserAccountActivity;
import com.example.hanan.riyadhmetro.user_wallet.WalletActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ARRIVAL_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ARRIVAL_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_GATE_NUMBER;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ID;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_LEAVING_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_LEAVING_TIME;
import static com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity.setNavigationMenu;
import static com.example.hanan.riyadhmetro.manageTicket.TicketListAdpater.Ticket_KEY_INTENT;

public class ViewTicketActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextViewGate;
    private TextView mTextViewLeavingPlace;
    private TextView mTextViewArrivingPlace;

    private TextView mTextViewTripDate;
    private TextView mTextViewArrivingTime;
    private TextView mTextViewLeavingTime;
    private ImageView mImageViewBarcode;
    private Button mSendEmaillButton;
    private ImageView mSendEmailImage;
    private Button mCalendarButton;
    private ImageView mCalendarImage;
    private  FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db ;
    private String information;
    private Map<String, Object> mTicket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_view);
        initElement();
        hideButton();
        viewTicket();
    }

    /**/
    private void initElement() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mTextViewGate = findViewById(R.id.textViewGate);
        mTextViewLeavingPlace = findViewById(R.id.textViewLeavingPlace);
        mTextViewArrivingPlace = findViewById(R.id.textViewArrivingPlace);
        mTextViewTripDate = findViewById(R.id.dateText);
        mTextViewArrivingTime = findViewById(R.id.arrivingTimeText);
        mTextViewLeavingTime = findViewById(R.id.leavingTimetext);
        mImageViewBarcode = findViewById(R.id.barcode);

        mSendEmaillButton = findViewById(R.id.bEmail);
        mSendEmaillButton.setOnClickListener(this);
        mSendEmailImage = findViewById(R.id.bEmailImage);

        mCalendarButton = findViewById(R.id.calenader);
        mCalendarButton.setOnClickListener(this);
        mCalendarImage = findViewById(R.id.calenaderImage);

        progressDialog = new ProgressDialog(this);

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
                    startActivity(new Intent(ViewTicketActivity.this,TripListViewActivity.class));
                    return true;
                case R.id.view_account_user:
                    startActivity(new Intent(ViewTicketActivity.this,ViewUserAccountActivity.class));
                    return true;
                case R.id.wallet:
                    startActivity(new Intent(ViewTicketActivity.this,WalletActivity.class));
                    return true;
                case R.id.view_ticket:
                    startActivity(new Intent(ViewTicketActivity.this,TicketListViewActivity.class));
                    return true;

            }
            return false;
        }
    };
    /**/
    private void hideButton() {

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY) {

            mSendEmaillButton.setVisibility(View.GONE);
            mSendEmailImage.setVisibility(View.GONE);


        }
        else if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY){
            mCalendarButton.setVisibility(View.GONE);
            mCalendarImage.setVisibility(View.GONE);


        }



    }

    /**/
    @Override
    public void onClick(View view) {
        if(view == mSendEmaillButton){
            sendEmail();
        }
        else if(view == mCalendarButton){

            goToCalendarContract();
        }
    }
    /**/
    private void viewTicket() {
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>) intent.getSerializableExtra(Ticket_KEY_INTENT);
        Map<String, Object> ticket = (Map)t;
        information = ticket.get(TICKET_ID).toString();
        mTicket = ticket;
        setData(ticket);

    }
    /**/
    private void setData( Map<String, Object> tickets){

        if(tickets != null && tickets.size() != 0 ) {




            mTextViewLeavingTime.setText(tickets.get(TICKET_LEAVING_TIME).toString());
            mTextViewArrivingTime.setText(tickets.get(TICKET_ARRIVAL_TIME).toString());

            mTextViewArrivingPlace.setText(tickets.get(TICKET_ARRIVAL_DESTINATION).toString());
            mTextViewLeavingPlace.setText(tickets.get(TICKET_LEAVING_DESTINATION).toString());
            mTextViewTripDate.setText(tickets.get(TICKET_DATE).toString());
            mTextViewGate.setText(tickets.get(TICKET_GATE_NUMBER).toString());
            mImageViewBarcode.setImageBitmap(setQRcode());

        }
        progressDialog.dismiss();


    }


    private Bitmap setQRcode ()  {

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix  result = writer.encode(information, BarcodeFormat.QR_CODE, 100, 100);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitimap = barcodeEncoder.createBitmap(result);
            return bitimap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendEmail() {

        String s = ("Trip Date: "+ mTextViewTripDate.getText().toString() + "\n" +
                "Leaving Time: "+mTextViewLeavingTime.getText().toString() + "\n"
                +"Arriving Time: "+ mTextViewArrivingTime.getText().toString() + "\n"
                // + "Trip Code: "+mTextViewTripCode.getText().toString() + "\n"
                + "Leaving Place: "+mTextViewArrivingPlace.getText().toString() + "\n" +
                "Leaving Place: "+ mTextViewLeavingPlace.getText().toString() + "\n"
                + "Gate Number:"+mTextViewGate.getText().toString() + "\n");
        String to = mAuth.getCurrentUser().getEmail();
        String subject= "Ticket Info";
        String body=s;
        String mailTo = "mailto:" + to +
                "?&subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body);
        Intent emailIntento = new Intent(Intent.ACTION_VIEW);
        emailIntento.setData(Uri.parse(mailTo));
        startActivity(emailIntento);
    }


    /**/
    private void goToCalendarContract(){

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY){
            String Leaving = mTicket.get(TICKET_LEAVING_TIME).toString();
            int hleaving = Integer.parseInt( Leaving.substring(0,Leaving.indexOf(":")));
            int mleaving = Integer.parseInt(Leaving.substring(Leaving.indexOf(":")+1));
            String date=  mTicket.get(TICKET_DATE).toString();
            int day= Integer.parseInt(date.substring(0,date.indexOf("/")));
            int month= Integer.parseInt(date.substring(date.indexOf("/")+1,date.indexOf("/",3)));
            int year= Integer.parseInt(date.substring(date.indexOf("/",3)+1));
            String Arrival = mTicket.get(TICKET_ARRIVAL_TIME).toString();
            int hArrival = Integer.parseInt( Arrival.substring(0,Arrival.indexOf(":")));
            int mArrival = Integer.parseInt(Arrival.substring(Arrival.indexOf(":")+1));

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(year, month-1, day, hleaving, mleaving);
            Calendar endTime = Calendar.getInstance();
            endTime.set(year, month-1, day, hArrival, mArrival);
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, "Riyadh Metro Ticket")
                    .putExtra(CalendarContract.Events.DESCRIPTION, "Riyadh Metro Ticket \n From: "
                            +mTicket.get(TICKET_LEAVING_DESTINATION).toString()+
                            "\n To: "+mTicket.get(TICKET_ARRIVAL_DESTINATION).toString())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION,"Gate Number: "+mTicket.get(TICKET_GATE_NUMBER).toString() )
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    // .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com"
                    ;
            startActivity(intent);
        }
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
        Context context = ViewTicketActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }
}


