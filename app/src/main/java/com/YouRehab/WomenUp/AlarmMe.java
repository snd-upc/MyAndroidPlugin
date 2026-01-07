package com.YouRehab.WomenUp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmMe extends BroadcastReceiver {

    String TAG="Unity";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        
        Log.d(TAG, "AlarMe");
       
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone currentRingtone = RingtoneManager.getRingtone(arg0, notification);
        currentRingtone.play();
        
        for (int i=0; i < 30; i++)
        {
            Toast.makeText(arg0, "Hello Gary, you have a new task now", Toast.LENGTH_SHORT).show();
        }
    }
}