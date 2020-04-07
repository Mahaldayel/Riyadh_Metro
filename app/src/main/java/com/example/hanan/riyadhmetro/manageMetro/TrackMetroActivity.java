package com.example.hanan.riyadhmetro.manageMetro;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hanan.riyadhmetro.R;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.util.ArrayList;
import java.util.Date;

public class TrackMetroActivity extends AppCompatActivity {


    public static final int GREY = Color.rgb(190, 190, 190);
    public static final int LIGHT_BLUE = Color.rgb(166, 241, 255);
    public static final String INTENT_METRO_OR_TRIP = "INTENT_METRO_OR_TRIP";
    public static final String METRO_CURRENT_STATION = "METRO_CURRENT_STATION";
    public static final String TRIP_START_STATION = "TRIP_START_STATION";
    public static final String TRIP_END_STATION = "TRIP_END_STATION";

    public static final int METRO_EXTA_NUMBER = 1;
    public static final int TRIP_EXTA_NUMBER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_metro);

        getMetroStationFromIntent();



    }

    /**/
    private void getMetroStationFromIntent(){

        String currentStation;
        String startStation;
        String endStation;
        Intent intent = getIntent();

        int intentNumber = intent.getIntExtra(INTENT_METRO_OR_TRIP,1);


        if(intentNumber == 1){
            currentStation = intent.getStringExtra(METRO_CURRENT_STATION);
            displayPathForMetro(currentStation) ;
        }else if(intentNumber == 2){
            startStation = intent.getStringExtra(TRIP_START_STATION);
            endStation = intent.getStringExtra(TRIP_END_STATION);
            displayPathForTrip(startStation,endStation);


        }
    }
    private void displayPathForMetro(String currentStation){

        displayStation(null,null,currentStation);
    }

    private void displayPathForTrip(String startStation, String endStation){

        displayStation(startStation,endStation,null);
    }


    /**/


    /**/
    private void displayStation(String startStation,String endStation,String currentStation){

        ArrayList<TimelineRow> timelineSationList = new ArrayList<>();

        String[] riyadhStations = getResources().getStringArray(R.array.riyadh_stations);

        boolean isStartStation = false;
        boolean isBeforeEndStation = false;

        for(int i = 0 ; i < riyadhStations.length;i++) {
            // Create new timeline row (Row Id)
            TimelineRow myRow = new TimelineRow(0);

            myRow.setTitle(riyadhStations[i]);
            myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_metro_station));

            myRow.setBellowLineSize(6);
            myRow.setImageSize(50);
            myRow.setBellowLineColor(GREY);
            myRow.setBackgroundSize(60);


            if (currentStation != null){

                if (currentStation.equals(riyadhStations[i])) {
                    myRow.setBackgroundColor(LIGHT_BLUE);
                } else {
                    myRow.setBackgroundColor(GREY);

                }
            }else {

                if (startStation.equals(riyadhStations[i]))
                    isStartStation = true;
                else if(endStation.equals(riyadhStations[i]))
                    myRow.setBackgroundColor(LIGHT_BLUE);


                if (isStartStation &&!isBeforeEndStation){
                    myRow.setBackgroundColor(LIGHT_BLUE);
                    myRow.setBellowLineColor(LIGHT_BLUE);

                }
                else
                    myRow.setBackgroundColor(GREY);


                if( i+1 != riyadhStations.length && endStation.equals(riyadhStations[i+1]))
                    isBeforeEndStation = true;

                if( endStation.equals(riyadhStations[i])){
                    myRow.setBackgroundColor(LIGHT_BLUE);

                }


            }



            timelineSationList.add(myRow);

        }
        // Create the Timeline Adapter
        ArrayAdapter<TimelineRow> metroSationAdapter = new TimelineViewAdapter(this, 0, timelineSationList,
                false);

        // Get the ListView and Bind it with the Timeline Adapter
        ListView metroStationListView =  findViewById(R.id.timeline_listView);


        metroStationListView.setAdapter(metroSationAdapter);

    }
}
