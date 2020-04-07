package com.example.hanan.riyadhmetro.assign;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.manageMetro.MetroListViewActivity;
import com.example.hanan.riyadhmetro.utility.DateDialogUtility;
import com.example.hanan.riyadhmetro.utility.TimeDialogUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_ASSIGNED_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITOR_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITOR_END_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITOR_MONITOR_EMAIL;
import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITOR_START_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITO_METRO_ID;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.manageMetro.MetroListAdpater.Metro_KEY_INTENT;

public class AssignedMonitorToMetroActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText date;
    private Spinner monitor;
    private Button assign;
    private FirebaseFirestore db;
    private ArrayList<String> mMonitroEmails;
    private ProgressDialog progressDialog;
    private String mMetroId;
    private ArrayList<Map<String, Object>> assignedMetro;
    private ArrayList<Map<String, Object>> assignedMonitor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_monitor);


        initElement();


    }

    private void initElement() {

        db = FirebaseFirestore.getInstance();

        date = findViewById(R.id.date);
        showPickerDate();

        assign = findViewById(R.id.assign);
        assign.setOnClickListener(this);

        assignedMetro = new ArrayList<>();
        assignedMonitor = new ArrayList<>();

        mMonitroEmails = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");


        monitor = findViewById(R.id.spinnerMonitor);
        getMonitorEmailList();


    }
    /**/
    private void showPickerTime(EditText time) {

        TimeDialogUtility fromTime = new TimeDialogUtility(time, this);

    }


    /**/
    private void showPickerDate() {

        date.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialogUtility dialog= new DateDialogUtility(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });
    }
    /**/
    private void getMonitorEmailList(){

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        db.collection(COLLECTION_METRO_MONITOR).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (DocumentSnapshot document : task.getResult()) {

                    Map<String, Object> metroMonitor = document.getData();
                    String email = metroMonitor.get("Email").toString();
                    mMonitroEmails.add(email);

                }
                displaySpinner(monitor);
                monitor.setSelection(0);
            }
        });
    }


    /**/
    private void displaySpinner(final Spinner spinner) {


        // Initializing an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,mMonitroEmails);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                monitor.setSelection(i,true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressDialog.dismiss();

    }


    /**/
    @Override
    public void onClick(View view) {

        if(view == assign){
            assignMetro();
        }
    }


    /**/
    private void assignMetro(){

        String email = getMonitorEmail();
        progressDialog.show();

        String id = String.valueOf(db.collection(COLLECTION_ASSIGNED_METRO_MONITOR).whereEqualTo("monitor_email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // ff.collection


                        getAssignedMetro(task);

                    }
                }));
    }
    /**/
    private void getAssignedMetro(Task<QuerySnapshot> task){


        for (DocumentSnapshot document : task.getResult()) {
            Map<String, Object> metro = document.getData();

            assignedMetro.add(metro);
        }


        assignMonitor();

    }


    /**/
    private void assignMonitor(){
        setMetroId();

        String id = String.valueOf(db.collection(COLLECTION_ASSIGNED_METRO_MONITOR).whereEqualTo(ASSIGNED_METRO_MONITO_METRO_ID, mMetroId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // ff.collection


                        getAssignedMonitor(task);

                    }
                }));
    }

    /**/
    private void getAssignedMonitor(Task<QuerySnapshot> task){


        for (DocumentSnapshot document : task.getResult()) {
            Map<String, Object> monitor = document.getData();

            assignedMonitor.add(monitor);

        }

        String enterDate = date.getText().toString();

          if (checkEmptyInput(enterDate)) {



                checkOfConflictTime();
        }else {

            progressDialog.dismiss();
        }
    }

    /**/
    private void checkOfConflictTime() {


        if (assignedMetro != null) {
            if (isThereErrorConflictTime(assignedMetro)) {
                displayErrorConflictTime();
                return;
            }
        }
        if (assignedMonitor != null) {
            if (isThereErrorConflictTime(assignedMonitor)) {
                displayErrorConflictMonitor();
                return;

            }
        }

        if (addMetro()){
            assignMetroSuccefully();
            goToViewMetro();
        }else {

            assignMetroUnsuccefully();

        }


    }
    /**/
    public boolean isThereErrorConflictTime(ArrayList<Map<String, Object>> assignedMetro) {

        for (int i = 0; i < assignedMetro.size() ;i++ ){

            Map<String, Object>  metro = assignedMetro.get(i);

            if(metro != null) {
                String dateStore = metro.get(ASSIGNED_METRO_MONITOR_DATE).toString();
                    if (checkOfConfictTime(dateStore))

                            return true;
                }


                }


        return false;
    }
    /**/
    private boolean checkOfConfictTime(String dateStore){

        String enterDateStr = date.getText().toString();


            Date storeDate = convertTime(dateStore);
            Date enterDate = convertTime(enterDateStr);

              if (storeDate.equals(enterDate))
                return true;





        return false;
    }
    /**/
    private static Date convertTime(String dateStr){

        Date date = null;
        String sDate = dateStr ;

        try {
            date = new SimpleDateFormat("d/m/yyyy").parse(sDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**/
    private void displayErrorConflictTime() {

        date.setError("cannot assign because conflict of time");
        progressDialog.dismiss();
    }
    /**/
    private void displayErrorConflictMonitor() {

        date.setError("cannot assign because it is already assigned to another monitor");
        progressDialog.dismiss();
    }


    /**/
    private boolean addMetro() {


        Map<String,String> metro = getDataFromInput();

        if(metro != null) {
            db.collection(COLLECTION_ASSIGNED_METRO_MONITOR).add(metro)

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            assignMetroUnsuccefully(e);

                        }
                    });
            return true;
        }

        return false;
    }


    /**/
    private Map<String, String> getDataFromInput(){

        Map<String,String> metro = new HashMap<>();


        String dateStr = date.getText().toString();


        if(checkEmptyInput(dateStr))
        {

            AddingMetroTOdb(metro);
            return metro;

        }
        progressDialog.dismiss();

        return null;
    }
    /**/
    private boolean checkEmptyInput( String dateStr){

        boolean notEmpty = true;



        if(TextUtils.isEmpty(dateStr)){
            date.setError("Please enter Date");
            //stopping the function execution further
            notEmpty = false;        }


        return notEmpty ;

    }


    /**/
    public void AddingMetroTOdb(Map<String, String> metro){

        setMetroId();

        String email = getMonitorEmail();


        metro.put(ASSIGNED_METRO_MONITOR_DATE, date.getText().toString());
        metro.put(ASSIGNED_METRO_MONITO_METRO_ID, mMetroId);
        metro.put(ASSIGNED_METRO_MONITOR_MONITOR_EMAIL,email);

    }
    /**/
    private void setMetroId(){

        Intent intent = getIntent();
        Map<String, String> m = (HashMap<String, String>)intent.getSerializableExtra(Metro_KEY_INTENT);
        mMetroId = m.get(ASSIGNED_METRO_MONITO_METRO_ID);
    }

    /**/
    private String getMonitorEmail(){

        return monitor.getSelectedItem().toString();
    }

    /**/
    private void assignMetroUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(AssignedMonitorToMetroActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }
    /**/
    private void assignMetroSuccefully(){

        Toast.makeText(AssignedMonitorToMetroActivity.this,"The Monitor has been assigned succefully!",Toast.LENGTH_SHORT).show();

    }
    /**/
    private void assignMetroUnsuccefully(){

        Toast.makeText(AssignedMonitorToMetroActivity.this,"The Monitor has been not assigned successfully!",Toast.LENGTH_SHORT).show();

    }

    private void goToViewMetro() {

        Context context = AssignedMonitorToMetroActivity.this;
        Class viewMetroClass = MetroListViewActivity.class;
        Intent intent = new Intent(context,viewMetroClass);
        startActivity(intent);
    }







}
