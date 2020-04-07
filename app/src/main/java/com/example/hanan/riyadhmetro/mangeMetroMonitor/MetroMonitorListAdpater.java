package com.example.hanan.riyadhmetro.mangeMetroMonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITOR_MONITOR_EMAIL;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_ASSIGNED_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_BIRTH_DATE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_NAME_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_NATIONAL_ID_FIELD;


public class MetroMonitorListAdpater extends RecyclerView.Adapter<MetroMonitorListAdpater.AssignedMetroViewHolder> {


    private List<Map<String, Object>>  mMetroMonitors = new LinkedList<>();
    private List<Map<String, Object>>  mMetroMonitorsCopy = new LinkedList<>();

    public static final String METRO_MONITOR_KEY_INTENT = "MetroMonitor";
    public static final String ID_KEY_INTENT = "id";

    private Context mContext;
    private List<String> mIdList;
    final private ListItemClickListener mOnClickListener;

    private FirebaseFirestore db;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex,List<Map<String, Object>>  mMetroMonitors);
    }

    public MetroMonitorListAdpater(List<Map<String, Object>> mMetroMonitors, ListItemClickListener mOnClickListener, Context context , List<String> idList) {
        this.mMetroMonitors = mMetroMonitors;
        mMetroMonitorsCopy = mMetroMonitors;
        this.mOnClickListener = mOnClickListener;
        mContext = context;
        mIdList = idList;
    }

    @Override
    public AssignedMetroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.metro_monitor_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        AssignedMetroViewHolder viewHolder = new AssignedMetroViewHolder(view);



        return viewHolder;
    }


    @Override
    public void onBindViewHolder(AssignedMetroViewHolder holder, int position) {

        Map<String, Object> trip = mMetroMonitors.get(position);
        setData(trip,holder);


        AddOnClickListenerRecyclerViewMenu(holder,position);


    }


    /**/
    private void setData( Map<String, Object> metroMonitors,AssignedMetroViewHolder holder) {

        if (metroMonitors.size() != 0 && holder != null) {


            holder.mMonitor_name.setText(metroMonitors.get(METRO_MONITOR_NAME_FIELD).toString());
        }

    }

    @Override
    public int getItemCount() {
        return mMetroMonitors.size();
    }

    /**/
    /**/
    public void AddOnClickListenerRecyclerViewMenu(final AssignedMetroViewHolder holder, final int position){

        holder.mButtonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext,  holder.mButtonViewOption);

                //inflating menu from xml resource
                popup.inflate(R.menu.base_list_menu);

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {


                            case R.id.delete:
                                Toast.makeText(mContext,"delete",Toast.LENGTH_LONG);
                                displayDulogForDelete(position);

                                break;
                            case R.id.edit:
                                Toast.makeText(mContext,"edit",Toast.LENGTH_LONG);

                                goToEditMetroMonitor(position);
                                break;

                                //handle menu2 click

                        }
                        return true;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    /**/
    private void displayDulogForDelete(final int position){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to delete the metro monitor ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(mContext,"delete",Toast.LENGTH_LONG);
                        deleteMetroMonitor(position);
                        updateView();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    /**/
    private void deleteMetroMonitor(final int position){

        String id = mIdList.get(position);
        db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_METRO_MONITOR).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getIdForDeletMonitorFromAssign(position);
                deleteMetroMonitorSuccefully();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteMetroMonitorUnsuccefully(e);
            }
        });


    }
    /**/
    private void getIdForDeletMonitorFromAssign(int position) {

        String email = (String) mMetroMonitors.get(position).get(METRO_MONITOR_EMAIL_FIELD);

        db.collection(COLLECTION_ASSIGNED_METRO_MONITOR).whereEqualTo(ASSIGNED_METRO_MONITOR_MONITOR_EMAIL,email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                ArrayList<String> ids = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {

                    ids.add(document.getId());
                }
                deletMonitorFromAssign(ids);
            }
        });
    }

    /**/
    private void deletMonitorFromAssign(ArrayList<String> ids) {


        if(ids != null){

            for (String id : ids){
                db.collection(COLLECTION_ASSIGNED_METRO_MONITOR).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteMetroMonitorUnsuccefully(e);
                    }
                });


            }
        }

    }

    /**/
    private void deleteMetroMonitorSuccefully(){

        Toast.makeText(mContext,"The Metro Monitor has been deleted succefully!",Toast.LENGTH_LONG).show();

    }

    /**/
    private void deleteMetroMonitorUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(mContext,"Error: "+error,Toast.LENGTH_SHORT).show();
    }


    /**/
    private void updateView() {

        Class viewMetroMonitorClass = MetroMonitorListViewActivity.class;
        Intent intent = new Intent(mContext,viewMetroMonitorClass);
        mContext.startActivity(intent);
    }




    /**/
    private void goToEditMetroMonitor(int position){

        HashMap<String, Object> metroMonitor =(HashMap) mMetroMonitors.get(position);

        String id = mIdList.get(position);

        Class  editMetroMonitoClass = EditMetroMonitorActivity.class;
        Intent intent = new Intent(mContext,editMetroMonitoClass);
        intent.putExtra(METRO_MONITOR_KEY_INTENT, metroMonitor);
        intent.putExtra(ID_KEY_INTENT,id);
        mContext.startActivity(intent);
    }


    /**/
    class AssignedMetroViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mButtonViewOption;

        public TextView mMonitor_name;



        public AssignedMetroViewHolder(View itemView) {
            super(itemView);

            mMonitor_name = itemView.findViewById(R.id.monitor_name);

            mButtonViewOption = itemView.findViewById(R.id.buttonViewOption);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition,mMetroMonitors);


        }

    }

    /**/
    /**/
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                boolean flag = true;
                final ArrayList<Map<String, Object>> searchResults = new ArrayList<Map<String, Object>>();

                if (mMetroMonitorsCopy == null)
                    mMetroMonitorsCopy = mMetroMonitors;
                if (constraint != null & mMetroMonitorsCopy != null & mMetroMonitorsCopy.size() > 0) {

                    //search Filter
                    for (final Map<String, Object> metroMonitor : mMetroMonitorsCopy) {
                        if (metroMonitor.get(METRO_MONITOR_NAME_FIELD).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(metroMonitor);
                            oReturn.values = searchResults;
                        } else if (metroMonitor.get(METRO_MONITOR_EMAIL_FIELD).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(metroMonitor);
                            oReturn.values = searchResults;
                        }else if (metroMonitor.get(METRO_MONITOR_NATIONAL_ID_FIELD).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(metroMonitor);
                            oReturn.values = searchResults;
                        }else if (metroMonitor.get(METRO_MONITOR_BIRTH_DATE_FIELD).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(metroMonitor);
                            oReturn.values = searchResults;
                        }
                        else {
                            oReturn.values = searchResults;
                        }
                    }
                }


                return oReturn;


            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mMetroMonitors = (ArrayList< Map<String, Object>>) results.values;



                if( mMetroMonitors == null|| mMetroMonitors.size() == 0)
                    MetroMonitorListViewActivity.getEmptyView().setVisibility(View.VISIBLE);
                else
                    MetroMonitorListViewActivity.getEmptyView().setVisibility(View.GONE);



                notifyDataSetChanged();

            }
        };



    }




}

