package com.YouRehab.WomenUp;
 
import android.content.Context;
import android.app.AlarmManager;
import java.util.Calendar;

import android.app.PendingIntent;
import android.util.Log;
import android.net.Uri;
 
import 	java.util.*;

import android.provider.CalendarContract;

import android.content.ContentValues;
 
 
 
import android.content.ContentResolver;

public class CalendarPlugin {
    
    private Context context;
    private static CalendarPlugin instance;
    
    private PendingIntent pendingIntent;
    private AlarmManager alarmMgr;
 
    String TAG="Unity";
 
    public CalendarPlugin() {
        this.instance = this;
    }
 
    public static CalendarPlugin instance() {
        if(instance == null) {
            instance = new CalendarPlugin();
        }
        return instance;
    }
 
    public void setContext(Context context) {
        this.context = context;
    }
 
    public void createCalendar(int year, int month, int day, int hour, int minute, int reminder, String title, String description) {
        Log.d(TAG, "createCalendar with "+ year + "-" + month + "-" + day +" at "+ hour +":"+ minute + "Reminder:"+ reminder + " "+ title +":" + description);
        //Long time = new GregorianCalendar().getTimeInMillis()+24*60*60*1000;
        
        /*
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
        Intent alarmIntent = new Intent(this.context, AlarmMe.class);
        pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarmIntent, 0);
        
        //manager = (AlarmManager)AlarmManager.getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;

        alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(this.context, "Alarm Set", Toast.LENGTH_SHORT).show();
        */
        
        /*
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.MONTH,9);
        cal.set(Calendar.YEAR,2016);
        cal.set(Calendar.DAY_OF_MONTH,24);
        cal.set(Calendar.HOUR_OF_DAY,14);
        cal.set(Calendar.MINUTE,15);
        Intent dialogIntent = new Intent(this.context, AlarmMe.class);
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this.context, 0, dialogIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), 5000, pendingIntent);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+60000,pendingIntent);
        */
        
        /*
        Log.d(TAG, "CalendarContract");
        
   Intent intent = new Intent(Intent.ACTION_INSERT);
   calIntent.setData(CalendarContract.Events.CONTENT_URI);
 intent.setType("vnd.android.cursor.item/event");
 intent.putExtra(Events.TITLE, "Learn Android");
 intent.putExtra(Events.EVENT_LOCATION, "Home suit home");
 intent.putExtra(Events.DESCRIPTION, "Download Examples");

 // Setting dates
 GregorianCalendar calDate = new GregorianCalendar(2016, 9, 25);
 intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calDate.getTimeInMillis());
 intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calDate.getTimeInMillis());

 // Make it a full day event
 intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

// Make it a recurring Event
intent.putExtra(Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");
*/

// Making it private and shown as busy
//intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
//intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);        
      
    /*
    Calendar cal = Calendar.getInstance();              
    Intent intent = new Intent(Intent.ACTION_INSERT);
    intent.setType("vnd.android.cursor.item/event");
    intent.putExtra("beginTime", cal.getTimeInMillis());
    intent.putExtra("allDay", true);
    intent.putExtra("rrule", "FREQ=YEARLY");
    intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
    intent.putExtra(Events.TITLE, "A Test Event from android app");
    */
    //context.startActivity(intent);
        
        
/*
        
    Calendar cal=Calendar.getInstance();
    cal.set(Calendar.MONTH,9);
    cal.set(Calendar.YEAR,2016);
    cal.set(Calendar.DAY_OF_MONTH,24);
    cal.set(Calendar.HOUR_OF_DAY,14);
    cal.set(Calendar.MINUTE,15);

    long startDate;
    long endDate;
    
    //Date startDate = DateConstants.getDateFromString(gameDate,GLOBAL_DATE_FORMAT);
    //long endDate = startDate.getTime() + (10 * 1000) ;

    ContentValues event = new ContentValues();
    event.put("calendar_id", 1);
    event.put("title", "Game#");
    //event.put("description", game.getLocation());
    //event.put("eventLocation", game.getLocation());
    event.put("eventTimezone", TimeZone.getDefault().getID());
    event.put("dtstart", cal.getTimeInMillis());
    event.put("dtend", cal.getTimeInMillis());

    event.put("allDay", 0); // 0 for false, 1 for true
    event.put("eventStatus", 1);
    event.put("hasAlarm", 1); // 0 for false, 1 for true

    String eventUriString = "content://com.android.calendar/events";
    Uri eventUri = context.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), event);
    long eventID = Long.parseLong(eventUri.getLastPathSegment());

    // if reminder need to set


    int minutes=120;

    // add reminder for the event
    ContentValues reminders = new ContentValues();
    reminders.put("event_id", eventID);
    reminders.put("method", "1");
    reminders.put("minutes", minutes);

    String reminderUriString = "content://com.android.calendar/reminders";
    context.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminders);        
        
*/


// Construct event details
long startMillis = 0;
long endMillis = 0;
Calendar beginTime = Calendar.getInstance();
beginTime.set(year, month-1, day, hour, minute);
startMillis = beginTime.getTimeInMillis();
//Calendar endTime = Calendar.getInstance();
//endTime.set(year, month-1, day, hour, minute+20);
endMillis = startMillis + 1200000;

// Insert Event
ContentResolver cr = context.getContentResolver();
ContentValues values = new ContentValues();
TimeZone timeZone = TimeZone.getDefault();
values.put(CalendarContract.Events.DTSTART, startMillis);
values.put(CalendarContract.Events.DTEND, endMillis);
values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
values.put(CalendarContract.Events.TITLE, title);
values.put(CalendarContract.Events.DESCRIPTION, description);
values.put(CalendarContract.Events.CALENDAR_ID, 1);
Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

// Retrieve ID for new event
String eventID = uri.getLastPathSegment();


//int minutes=1;


// add reminder for the event
ContentValues reminders = new ContentValues();
reminders.put("event_id", eventID);
reminders.put("method", "1");
reminders.put("minutes", reminder);

String reminderUriString = "content://com.android.calendar/reminders";
context.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminders);

Log.d(TAG, "Calendar entry OK");
        
    }
}

