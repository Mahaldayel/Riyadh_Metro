package com.example.hanan.riyadhmetro;

public class DatabaseName {

    /*
     *
     * User
      */
    public static  final String COLLECTION_USER = "User";
    public static  final String User_BIRTH_DATE_FIELD = "Birth";
    public static  final String User_EMAIL_FIELD = "Email";
    public static  final String User_NATIONAL_ID_FIELD = "Nationalid";
    public static  final String User_PASSWORD_FIELD = "Password";
    public static  final String User_NAME_FIELD = "name";
    public static  final String User_WELLAT = "wallet";



    /*
    *
    * Metro_Monitor
    * */
    public static  final String COLLECTION_METRO_MONITOR = "Metro_Monitor";
    public static  final String METRO_MONITOR_BIRTH_DATE_FIELD = "Birth";
    public static  final String METRO_MONITOR_EMAIL_FIELD = "Email";
    public static  final String METRO_MONITOR_NATIONAL_ID_FIELD = "Nationalid";
    public static  final String METRO_MONITOR_PASSWORD_FIELD = "Password";
    public static  final String METRO_MONITOR_NAME_FIELD = "name";


    /*
     *
     * Metro
     * */

    public static  final String COLLECTION_METRO = "Metro";
    public static  final String METRO_METRO_ID_FIELD = "metro_id";
    public static  final String METRO_METRO_STATUS_FIELD = "status";
    public static  final String METRO_NUMBER_OF_SEATS_FIELD = "number_of_seats";
    public static  final String METRO_METRO_STATION = "metro_station";





    /*
     *
     * AssignedMetroMonitor
     * */
    public static  final String COLLECTION_ASSIGNED_METRO_MONITOR = "AssignedMetroMonitor";
    public static  final String ASSIGNED_METRO_MONITOR_DATE = "date";
    public static  final String ASSIGNED_METRO_MONITOR_START_TIME = "start_time";
    public static  final String ASSIGNED_METRO_MONITOR_END_TIME = "end_time";
    public static  final String ASSIGNED_METRO_MONITOR_MONITOR_EMAIL = "monitor_email";
    public static  final String ASSIGNED_METRO_MONITO_METRO_ID = "metro_id";


    /*
    *
    * Bank
    * */
    public static  final String COLLECTION_BANK = "Bank";
    public static  final String BANK_CARD_NUMBER = "card_number";
    public static  final String BANK_EXPIRATION_DATE = "expiration_date";
    public static  final String BANK_SECURITY_CODE = "security_code";


    /**
     * Tickect
     *
     */

    public static  final String COLLECTION_TICKET = "Tickect";
    public static  final String TICKET_USER_EMAIL = "User Email";
    public static  final String TICKET_METRO_ID = "metro_id";
    public static  final String TICKET_TRIP_CODE = "Trip code";
    public static  final String TICKET_DATE = "Date";
    public static  final String TICKET_LEAVING_TIME = "Leaving time";
    public static  final String TICKET_LEAVING_DESTINATION = "Leaving destination";
    public static  final String TICKET_ARRIVAL_DESTINATION = "Arrival destination";
    public static  final String TICKET_ARRIVAL_TIME = "Arrival time";
    public static  final String TICKET_GATE_NUMBER = "Gate number";
    public static  final String TICKET_ID = "Ticket id";









    /**
     * Trip
     */

    public static  final String COLLECTION_TRIP = "Trip";
    public static  final String TRIP_METRO_ID = "metro_id";
    public static  final String TRIP_TRIP_CODE = "Trip code";
    public static  final String TRIP_LEAVING_TIME = "Leaving time";
    public static  final String TRIP_LEAVING_DESTINATION = "Leaving destination";
    public static  final String TRIP_ARRIVAL_DESTINATION = "Arrival destination";
    public static  final String TRIP_ARRIVAL_TIME = "Arrival time";
    public static  final String TRIP_AVAILABLE_SEATS = "Available seats";
    public static  final String TRIP_BOOKED_SEATS = "Booked seats";
    public static  final String TRIP_GATE_NUMBER = "Gate number";
    public static  final String TRIP_DATE = "Date";












}
