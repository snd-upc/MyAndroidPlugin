package com.yourehab.womenup;

import android.os.Environment;
import android.os.BatteryManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.util.Log;


//import android.app.AlarmManager;

public class AndroidPlugin
{
	// Needed to get the battery level.
	private Context context;
    int number=0;
    String TAG="Unity";
    BT bt1;
    //BT bt2;
    
    //Alarm
    //private static AlarmManager am;
    
    /*
     public static void InitAlarm()
    {
        am=(AlarmManager)UnityPlayer.currentActivity.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "InitAlarm");
    }*/
    
    public AndroidPlugin(Context context)
	{
		this.context = context;
	}

    /*
 public static void SenderEvent(int HOUR_OF_DAY,String statusMessage, String title, String content)
 {
     Log.d(TAG, "Event Start");
     Calendar cal = Calendar.getInstance();
     cal.add(Calendar.SECOND, HOUR_OF_DAY);
     Intent intent = new Intent(UnityPlayer.currentActivity, TimeAlarm.class);
     intent.putExtra("alarm_status", statusMessage);
     intent.putExtra("alarm_title", title);
     intent.putExtra("alarm_content", content);
     //Log.d(TAG, "przygotowane dane");
     PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
     am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
     Log.d(TAG, "Event End");
 }    */
    
    /*******************************************************************************************/
    /*                          BLUETOOTH                    */
    /*******************************************************************************************/
    
    public String InitBT(){
        String returnValue="";
        Log.d(TAG,"ANDROID Init");
        bt1=new BT();
        returnValue=bt1.InitBT();
        //String deviceName=bt1.GetPairedDevice();
        //bt1.InitBTDevice(deviceName);
        //bt1.StartBTCommunication();
        //bt1.sendData("wbasds31110010\r");
        //bt1.sendData("wbaom7\r");
        return returnValue;
    }
    
    public String GetPairedDevice(){
        String returnValue="";
        returnValue=bt1.GetPairedDevice();
        return returnValue;
    }
    
    public String InitBTDevice(String deviceName){
        String returnValue="";
        returnValue = bt1.InitBTDevice(deviceName);
        return returnValue;
    }
    
    public String StartBTCommunication(){
        String returnValue="";
        returnValue=bt1.StartBTCommunication();
        return returnValue;
    }
   
    public String SendData(String data){
        String returnValue="";
        returnValue=bt1.sendData(data);
        return returnValue;
    }
    
    public byte[] ReadData(boolean debug){
        //Log.d(TAG,"Android ReadData");
        //byte[] tmp=new byte[300];
        byte[] tmp=bt1.readData(debug);
        return tmp;
    }
    
    
    public void StartLoop(){
        bt1.looptest();
    }
    
}