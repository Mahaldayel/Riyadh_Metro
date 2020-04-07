package com.example.hanan.riyadhmetro.assign;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITOR_MONITOR_EMAIL;
import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITO_METRO_ID;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_ASSIGNED_METRO_MONITOR;

public class AssignedMetroListViewActivity extends AppCompatActivity implements  SearchView.OnQueryTextListener {


    private RecyclerView mAssignedMetroList;
    private AssignedMetroListAdpater mAdapter;
    private ProgressDialog progressDialog;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseFirestore db ;
    private List<Map<String, Object>> mAssignedMetro;
    private static View emptyView;
    private TextView mEmptyTitleText;
    private FirebaseAuth mFirebaseAuth;
    private boolean mIsMointor;
    private String mMetroId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assigned_metro_list_view);

        getIsMointorFromIntent();
        initElement();
        viewRecyclerView();
        hideAdd();


        }
     /**/
     private void getIsMointorFromIntent(){

         Intent intent = getIntent();
         mIsMointor = intent.getBooleanExtra("isMointor",false);
         if(mIsMointor)
             mMetroId = intent.getStringExtra("metro_id");
     }
    /**/
    private void initElement(){

        initEmptyView();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAssignedMetro = new ArrayList<Map<String, Object>>();
        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        mAssignedMetroList = findViewById(R.id.rv);
        mFloatingActionButton = findViewById(R.id.addButton);

    }
    /**/
    private void initEmptyView(){

        emptyView = findViewById(R.id.empty_view);

        mEmptyTitleText = findViewById(R.id.empty_title_text);
        if(mIsMointor)
            mEmptyTitleText.setText("There no assigned Monitor");
        else
            mEmptyTitleText.setText("There no assigned Metro");
    }

    /**/
    private void viewRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAssignedMetroList.setLayoutManager(layoutManager);
        mAssignedMetroList.setHasFixedSize(true);

        if(mIsMointor)
            getDataFromDatabaseMonitor();
        else
            getDataFromDatabaseMetro();
    }
    /**/
    private void getDataFromDatabaseMonitor() {


        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection(COLLECTION_ASSIGNED_METRO_MONITOR).whereEqualTo(ASSIGNED_METRO_MONITO_METRO_ID, mMetroId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.getResult() != null) {

                    getDate(task);
                } else {

                    progressDialog.dismiss();
                    Toast.makeText(AssignedMetroListViewActivity.this, "Error", Toast.LENGTH_LONG);
                }

            }
        });
    }


    /**/
    private void getDataFromDatabaseMetro(){


        String email ;
        email = getIntent().getStringExtra("email");

        if(email == null)
            email = mFirebaseAuth.getCurrentUser().getEmail();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection(COLLECTION_ASSIGNED_METRO_MONITOR).whereEqualTo(ASSIGNED_METRO_MONITOR_MONITOR_EMAIL, email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                    getDate(task);
                } else {

                    progressDialog.dismiss();
                    Toast.makeText(AssignedMetroListViewActivity.this,"Error",Toast.LENGTH_LONG);
                }

            }
        });

    }

    private  void getDate( Task<QuerySnapshot> task) {

        boolean isEmpty = true;

        for (DocumentSnapshot document : task.getResult()) {

            mAssignedMetro.add(document.getData());

            if (mAssignedMetro != null)
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

        mAdapter = new AssignedMetroListAdpater(mAssignedMetro,this,mIsMointor);
        mAssignedMetroList.setAdapter(mAdapter);
        progressDialog.dismiss();
    }



    /**/
    @SuppressLint("RestrictedApi")
    private void hideAdd(){

        mFloatingActionButton.setVisibility(View.GONE);
    }

    /**/
    public static View getEmptyView(){
        return emptyView;
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
        searchView.setOnQueryTextListener(AssignedMetroListViewActivity.this);


        return true;
    }
}
