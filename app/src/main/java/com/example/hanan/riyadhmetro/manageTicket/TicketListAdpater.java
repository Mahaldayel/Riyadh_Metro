package com.example.hanan.riyadhmetro.manageTicket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TICKET;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_TRIP;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_USER;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_MONITOR_EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ARRIVAL_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_ARRIVAL_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_LEAVING_DESTINATION;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_LEAVING_TIME;
import static com.example.hanan.riyadhmetro.DatabaseName.TICKET_TRIP_CODE;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_AVAILABLE_SEATS;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_BOOKED_SEATS;
import static com.example.hanan.riyadhmetro.DatabaseName.TRIP_TRIP_CODE;
import static com.example.hanan.riyadhmetro.DatabaseName.User_EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.User_WELLAT;


public class TicketListAdpater extends RecyclerView.Adapter<TicketListAdpater.TicketViewHolder> {


    private List<Map<String, Object>> mTickets = new LinkedList<>();
    private List<Map<String, Object>> mTicketsCopy = new LinkedList<>();
    public static final double TICKET_PRICE = 20;

    public static final String Ticket_KEY_INTENT = "ticket";
    public static final String ID_KEY_INTENT = "id";
    private Context mContext;
    private List<String> mIdList;
    final private ListItemClickListener mOnClickListener;
    private FirebaseFirestore db;
    private String mUserId;
    private String mTripId;



    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, List<Map<String, Object>> mTickets);
    }

    public TicketListAdpater(List<Map<String, Object>> Tickets, ListItemClickListener mOnClickListener, Context context, List<String> idList) {
        this.mTickets = Tickets;
        mTicketsCopy = Tickets;
        this.mOnClickListener = mOnClickListener;
        mContext = context;
        mIdList = idList;

    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.ticket_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        TicketViewHolder viewHolder = new TicketViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(TicketViewHolder holder, int position) {

        Map<String, Object> ticket;

        if (mTickets != null) {

            ticket = mTickets.get(position);
            setTheData(ticket, holder);

        }

        db = FirebaseFirestore.getInstance();
        getUserId();
        getTripId(position);
        AddOnClickListenerRecyclerViewMenu(holder,position);
    }



    /**/
    private void setTheData(Map<String, Object> ticket, TicketViewHolder holder) {

        if (ticket != null && ticket.size() != 0) {


            holder.mTextViewLeavingTime.setText(ticket.get(TICKET_LEAVING_TIME).toString());
            holder.mTextViewArrivingPlace.setText(ticket.get(TICKET_ARRIVAL_DESTINATION).toString());
            holder.mTextViewArrivingTime.setText(ticket.get(TICKET_ARRIVAL_TIME).toString());
            holder.mTextViewLeavingPlace.setText(ticket.get(TICKET_LEAVING_DESTINATION).toString());
            holder.mTextViewticketDate.setText(ticket.get(TICKET_DATE).toString());


        }

    }
    /**/
    private void getUserId(){

        String email =  FirebaseAuth.getInstance().getCurrentUser().getEmail();


        if(email != null) {
            db.collection(COLLECTION_USER)
                    .whereEqualTo(User_EMAIL_FIELD, email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (DocumentSnapshot document : task.getResult()) {

                                mUserId = document.getId();

                            }
                        }
                    });
        }

    }
    /**/
    private void getTripId(int position){

        if(mTickets != null) {

            String mTripCode = mTickets.get(position).get(TICKET_TRIP_CODE).toString();
            if (mTripCode != null) {
                db.collection(COLLECTION_TRIP).whereEqualTo(TRIP_TRIP_CODE, mTripCode).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                for (DocumentSnapshot document : task.getResult()) {

                                    mTripId = document.getId();

                                }
                            }
                        });
            }
        }
    }


    /**/
    public void AddOnClickListenerRecyclerViewMenu(final TicketViewHolder holder, final int position){

        holder.mButtonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(mContext,  holder.mButtonViewOption);

                popup.inflate(R.menu.ticket_list_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {


                            case R.id.cancel:
                                Toast.makeText(mContext,"delete",Toast.LENGTH_LONG);
                                displayDulogForDelete(position);

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
        builder1.setMessage("Are you sure you want to cancel the ticket ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(mContext,"delete",Toast.LENGTH_LONG);
                        deleteticket(position);
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
    private void deleteticket(int position){ // use it for cancel ticket

        String id = mIdList.get(position);

        db.collection(COLLECTION_TICKET).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getWallet();
                deleteticketSuccefully();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteticketUnsuccefully(e);
            }
        });


    }

    /**/
    private void getWallet(){



        db.collection(COLLECTION_USER).document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                getDateForWallet(task);
            }
        })         .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteticketUnsuccefully(e);
            }
        });



    }

    private  void getDateForWallet(Task<DocumentSnapshot> task) {


            double wellat = task.getResult().getDouble(User_WELLAT);
            
            double newValueOfWellat = wellat + TICKET_PRICE;
            
            updateWellat(newValueOfWellat);
            

    }
    /**/

    private void updateWellat(double wellat) {


        db.collection(COLLECTION_USER).document(mUserId).update(User_WELLAT,wellat);

        getSeats();

    }

    /**/
    private void getSeats() {

        if(mTripId != null) {
            db.collection(COLLECTION_TRIP).document(mTripId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    getDate(task);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    deleteticketUnsuccefully(e);
                }
            });
        }

    }
    private  void getDate(Task<DocumentSnapshot> task) {


        Long seatsAv = (Long) task.getResult().get(TRIP_AVAILABLE_SEATS);
        Long seatsBo = (Long) task.getResult().get(TRIP_BOOKED_SEATS);
        if(seatsAv != null && seatsBo != null ) {
            Long newValueOfseatsAv = seatsAv + 1;
            Long newValueOfseatsBo = seatsBo - 1;

            updateSeats(newValueOfseatsAv, newValueOfseatsBo);
        }


    }
    /**/

    private void updateSeats(Long seatsAv,Long seatsBo) {


        db.collection(COLLECTION_TRIP).document(mTripId).update(TRIP_AVAILABLE_SEATS,seatsAv,TRIP_BOOKED_SEATS,seatsBo);

    }



    /**/
    private void deleteticketUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(mContext,"Error: "+error, Toast.LENGTH_SHORT).show();

    }

    /**/
    private void deleteticketSuccefully(){

        Toast.makeText(mContext,"The ticket has been deleted succefully!",Toast.LENGTH_SHORT).show();

    }



    /**/
    private void updateView() {

        Class viewTicketsClass = TicketListViewActivity.class;
        Intent intent = new Intent(mContext,viewTicketsClass);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
            return mTickets.size();
    }



    /**/
    class TicketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mButtonViewOption;

        public TextView mTextViewticketDate;
        public TextView mTextViewArrivingTime;
        public TextView mTextViewLeavingTime;
        public TextView mTextViewLeavingPlace;
        public TextView mTextViewArrivingPlace;



        public TextView buttonViewOption;


        public TicketViewHolder(View itemView) {
            super(itemView);

            mTextViewArrivingPlace = itemView.findViewById(R.id.arrivingPlaceText);
            mTextViewLeavingPlace = itemView.findViewById(R.id.laevingPlaceText);
            this.buttonViewOption = itemView.findViewById(R.id.buttonViewOption);
            mTextViewticketDate = itemView.findViewById(R.id.tripDateText);// to be detected
            mTextViewArrivingTime = itemView.findViewById(R.id.arrivingTimeText);
            mTextViewLeavingTime = itemView.findViewById(R.id.leavingTimetext);



            mButtonViewOption = itemView.findViewById(R.id.buttonViewOption);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition,mTickets);


        }

    }



    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                boolean flag = true;
                final ArrayList<Map<String, Object>> searchResults = new ArrayList<Map<String, Object>>();

                if (mTicketsCopy == null)
                    mTicketsCopy = mTickets;
                if (constraint != null & mTicketsCopy != null & mTicketsCopy.size() > 0) {

                    //search Filter
                    for (final Map<String, Object> ticket : mTicketsCopy) {
                        if (ticket.get(TICKET_ARRIVAL_DESTINATION).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else if (ticket.get(TICKET_LEAVING_DESTINATION).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else if (ticket.get(TICKET_LEAVING_TIME).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else if (ticket.get(TICKET_ARRIVAL_TIME).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else if (ticket.get(TICKET_DATE).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(ticket);
                            oReturn.values = searchResults;
                        } else {
                            oReturn.values = searchResults;
                        }
                    }
                }
                return oReturn;
            }

            /**
             * <p>Invoked in the UI thread to publish the filtering results in the
             * user interface. Subclasses must implement this method to display the
             * results computed in {@link #performFiltering}.</p>
             *
             * @param constraint the constraint used to filter the data
             * @param results    the results of the filtering operation
             * @see #filter(CharSequence, FilterListener)
             * @see #performFiltering(CharSequence)
             * @see FilterResults
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTickets = (ArrayList< Map<String, Object>>) results.values;

                if( mTickets == null || mTickets.size() == 0)
                    TicketListViewActivity.getEmptyView().setVisibility(View.VISIBLE);
                else
                    TicketListViewActivity.getEmptyView().setVisibility(View.GONE);


                notifyDataSetChanged();

            }
        };
    }

}


