package com.example.hanan.riyadhmetro.manageTrip;

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
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.buyTicket.ViewTripAndBuyActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
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
import java.util.concurrent.CountDownLatch;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TICKET;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TRIP;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_USER;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_TRIP_CODE;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_USER_EMAIL;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_ARRIVAL_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_ARRIVAL_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_AVAILABLE_SEATS;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_LEAVING_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_LEAVING_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_TRIP_CODE;
import static com.example.hanan.riyadhmetro.DatabaseName.User_EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.User_WELLAT;
import static com.example.hanan.riyadhmetro.manageTicket.TicketListAdpater.TICKET_PRICE;


public class TripListAdpater extends RecyclerView.Adapter<TripListAdpater.TripViewHolder> implements Filterable {


    private List<Map<String, Object>> mTrips = new LinkedList<>();
    private List<Map<String, Object>> mTripsCopy = new LinkedList<>();


    public static final String TRIP_KEY_INTENT = "trip";
    public static final String ID_KEY_INTENT = "id";

    private Context mContext;
    private List<String> mIdList;
    final private ListItemClickListener mOnClickListener;

    private FirebaseFirestore db;

    private boolean mDoneUpadet = true;

    private CountDownLatch doneSignal = new CountDownLatch(1);



    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex,List<Map<String, Object>> mTrips);
    }

    public TripListAdpater(List<Map<String, Object>> mTrips, ListItemClickListener mOnClickListener,Context context ,List<String> idList) {
        this.mTrips = mTrips;
        mTripsCopy = mTrips;
        this.mOnClickListener = mOnClickListener;
        mContext = context;
        mIdList = idList;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForListItem = R.layout.trip_item;

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        TripViewHolder viewHolder = new TripViewHolder(view);


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {

        Map<String, Object> trip = mTrips.get(position);
        db = FirebaseFirestore.getInstance();

        setData(trip,holder);

        AddOnClickListenerRecyclerViewMenu(holder,position);

        final TextView buttonViewOption = holder.buttonViewOption;
        hideButtonViewOptionForUser(buttonViewOption);


    }


    /**/
    private void setData( Map<String, Object> trip,TripViewHolder holder) {

        if (trip.size() != 0 ) {


            holder.mTextViewLeavingTime.setText(trip.get(TRIP_LEAVING_TIME).toString());
            holder.mTextViewArrivingTime.setText(trip.get(TRIP_ARRIVAL_TIME).toString());

            holder.mTextViewArrivingPlace.setText(trip.get(TRIP_ARRIVAL_DESTINATION).toString());
            holder.mTextViewLeavingPlace.setText(trip.get(TRIP_LEAVING_DESTINATION).toString());
            holder.mTextViewTripDate.setText(trip.get(TRIP_DATE).toString());

            if(Integer.parseInt(trip.get(TRIP_AVAILABLE_SEATS).toString())  == 0 )
                holder.fullLine.setBackgroundResource(R.drawable.line_full);

        }

    }

    /**/
    public void AddOnClickListenerRecyclerViewMenu(final TripViewHolder holder, final int position){

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
                                displayDulogForDelete(position);
                                break;
                            case R.id.edit:
                                Toast.makeText(mContext,"edit",Toast.LENGTH_LONG);
                                goToEditTrip(position);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    /**/
    private void displayDulogForDelete(final int position){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to delete the trip?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(mContext,"delete",Toast.LENGTH_LONG);

                        checkOfTicket(position);
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
    private void checkOfTicket(final int position) {


        String tripCode = (String) mTrips.get(position).get(TRIP_TRIP_CODE);


        if(tripCode != null) {
            db.collection(COLLECTION_TICKET).whereEqualTo(TICKET_TRIP_CODE, tripCode).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    ArrayList<String> ids = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {

                        ids.add(document.getId());
                    }
                    if (ids.size() == 0)
                        deleteTrip(position);
                    else
                        displayUndeletedTrip();
                }
            });
        }else
            displayUndeletedTrip();

    }

    /**/
    private void deleteTrip(final int position){

        String id = mIdList.get(position);


        db.collection(COLLECTION_TRIP).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                deleteTripSuccefully();
                updateView();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteTripUnsuccefully(e);
            }
        });


    }

    /**/
    private void displayUndeletedTrip() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setTitle("The trip did not delete");
        builder.setMessage("Unfortunately, the trip did not delete\n" +
                "because of a booked ticket on the trip");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**/
    private void updateView() {

        Class viewTripClass = TripListViewActivity.class;
        Intent intent = new Intent(mContext,viewTripClass);
        mContext.startActivity(intent);
    }



    /**/
    private void deleteTripUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(mContext,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void deleteTripSuccefully(){

        Toast.makeText(mContext,"The Trip has been deleted succefully!",Toast.LENGTH_SHORT).show();

    }


    /**/
    private void goToEditTrip(int position){

        HashMap<String, Object> trip =(HashMap) mTrips.get(position);

        String id = mIdList.get(position);

        Class editTripClass = EditTripAvtivity.class;
        Intent intent = new Intent(mContext,editTripClass);
        intent.putExtra(TRIP_KEY_INTENT, trip);
        intent.putExtra(ID_KEY_INTENT,id);
        mContext.startActivity(intent);
    }


    /**/
    private void hideButtonViewOptionForUser(TextView buttonViewOption){

        if(PreferencesUtility.getAuthority(mContext) == PreferencesUtility.USER_AUTHORITY
                || PreferencesUtility.getAuthority(mContext) == PreferencesUtility.MONITOR_AUTHORITY)
            buttonViewOption.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }


    /**/
    class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mButtonViewOption;

        public TextView mTextViewTripDate;
        public TextView mTextViewArrivingTime;
        public TextView mTextViewLeavingTime;

        public TextView mTextViewLeavingPlace;
        public TextView mTextViewArrivingPlace;

        public TextView buttonViewOption;

        public LinearLayout fullLine;


        public TripViewHolder(View itemView) {
            super(itemView);

            mTextViewArrivingPlace = itemView.findViewById(R.id.arrivingPlaceText);
            mTextViewLeavingPlace = itemView.findViewById(R.id.laevingPlaceText);
            this.buttonViewOption = itemView.findViewById(R.id.buttonViewOption);
            mTextViewTripDate = itemView.findViewById(R.id.tripDateText);
            mTextViewArrivingTime = itemView.findViewById(R.id.arrivingTimeText);
            mTextViewLeavingTime = itemView.findViewById(R.id.leavingTimetext);
            mButtonViewOption = itemView.findViewById(R.id.buttonViewOption);
            fullLine = itemView.findViewById(R.id.full_line);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition,mTrips);



        }

    }



    /**/
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                boolean flag = true;
                final ArrayList<Map<String, Object>> searchResults = new ArrayList<Map<String, Object>>();

                if (mTripsCopy == null) {
                    mTripsCopy = mTrips;

                }
                if (constraint != null & mTripsCopy != null & mTripsCopy.size() > 0) {

                    //search Filter
                    for (final Map<String, Object> trip : mTripsCopy) {
                        if (trip.get(TRIP_ARRIVAL_DESTINATION).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);

                            oReturn.values = searchResults;
                        }else if (trip.get(TRIP_LEAVING_DESTINATION).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
                            oReturn.values = searchResults;
                        }else if (trip.get(TRIP_LEAVING_TIME).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
                            oReturn.values = searchResults;
                        }else if (trip.get(TRIP_ARRIVAL_TIME).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
                            oReturn.values = searchResults;
                        }else if (trip.get(TRIP_DATE).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
                            oReturn.values = searchResults;
                        }else if (trip.get(TRIP_TRIP_CODE).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(trip);
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
                mTrips = (ArrayList< Map<String, Object>>) results.values;

                if(mTrips.size() == 0)
                    TripListViewActivity.getEmptyView().setVisibility(View.VISIBLE);
                else
                    TripListViewActivity.getEmptyView().setVisibility(View.GONE);


                notifyDataSetChanged();

            }
        };



    }




}

