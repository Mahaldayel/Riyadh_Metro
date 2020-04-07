package com.example.hanan.riyadhmetro.assign;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListViewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITOR_DATE;
import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITOR_MONITOR_EMAIL;
import static com.example.hanan.riyadhmetro.DatabaseName.ASSIGNED_METRO_MONITO_METRO_ID;


public class AssignedMetroListAdpater extends RecyclerView.Adapter<AssignedMetroListAdpater.AssignedMetroHolder> {


    private List<Map<String, Object>> mAssignedMetro;
    private List<Map<String, Object>> mMetroMonitorsCopy ;

    public static final String ID_KEY_INTENT = "id";


    private boolean mIsMointor;




    public AssignedMetroListAdpater(List<Map<String, Object>> mAssignedMetro, Context context ,boolean isMonitor) {
        this.mAssignedMetro = mAssignedMetro;
        mMetroMonitorsCopy = mAssignedMetro;
        mIsMointor = isMonitor;
    }

    @Override
    public AssignedMetroHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.assigned_metro_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        AssignedMetroHolder viewHolder = new AssignedMetroHolder(view);



        return viewHolder;
    }



    @Override
    public void onBindViewHolder(AssignedMetroHolder holder, int position) {

        Map<String, Object> metro = mAssignedMetro.get(position);
        if (mIsMointor)
            changeImgaeForMonitor(holder);
        setData(metro,holder);




    }

    /**/
    private void changeImgaeForMonitor(AssignedMetroHolder holder){

        holder.mMetroMointor.setImageResource(R.drawable.ic_person_24dp);


    }

    /**/
    private void setData( Map<String, Object> metroMonitors,AssignedMetroHolder holder) {

        if (metroMonitors.size() != 0 && holder != null) {


            holder.mDate.setText(metroMonitors.get(ASSIGNED_METRO_MONITOR_DATE).toString());
            if(mIsMointor)
                holder.mMetroId.setText(metroMonitors.get(ASSIGNED_METRO_MONITOR_MONITOR_EMAIL).toString());
            else
                holder.mMetroId.setText(metroMonitors.get(ASSIGNED_METRO_MONITO_METRO_ID).toString());

        }

    }

    @Override
    public int getItemCount() {
        return mAssignedMetro.size();
    }


    /**/
    class AssignedMetroHolder extends RecyclerView.ViewHolder {


        public TextView mDate;
        public TextView mMetroId;
        public ImageView mMetroMointor;





        public AssignedMetroHolder(View itemView) {
            super(itemView);

            mDate = itemView.findViewById(R.id.TextViewDate);
            mMetroId = itemView.findViewById(R.id.metro_id);
            mMetroMointor = itemView.findViewById(R.id.metro_mointor);

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
                    mMetroMonitorsCopy = mAssignedMetro;
                if (constraint != null & mMetroMonitorsCopy != null & mMetroMonitorsCopy.size() > 0) {

                    String valueName ;
                    if(mIsMointor)
                        valueName = ASSIGNED_METRO_MONITOR_MONITOR_EMAIL;
                    else
                        valueName = ASSIGNED_METRO_MONITO_METRO_ID;

                    //search Filter
                    for (final Map<String, Object> metroMonitor : mMetroMonitorsCopy) {
                        if (metroMonitor.get(valueName).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            searchResults.add(metroMonitor);
                            oReturn.values = searchResults;
                        }
                        else if (metroMonitor.get(ASSIGNED_METRO_MONITOR_DATE).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {

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
                mAssignedMetro = (ArrayList<Map<String, Object>>) results.values;



                if(mAssignedMetro.size() == 0)
                    MetroMonitorListViewActivity.getEmptyView().setVisibility(View.VISIBLE);
                else {

                    MetroMonitorListViewActivity.getEmptyView().setVisibility(View.GONE);

                }

                notifyDataSetChanged();

            }
        };

    }

}

