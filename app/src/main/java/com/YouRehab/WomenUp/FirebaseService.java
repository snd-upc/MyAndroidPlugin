package com.YouRehab.WomenUp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.unity3d.player.UnityPlayer;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FCM";
    private static final String UNITY_OBJECT = "AndroidManager";
    private static final String UNITY_METHOD_MESSAGE = "OnFirebaseMessage";
    private static final String UNITY_METHOD_TOKEN = "OnFirebaseToken";


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "SND FCM New token: " + token);

        try {
            UnityPlayer.UnitySendMessage(UNITY_OBJECT, UNITY_METHOD_TOKEN, token);
        } catch (Exception e) {
            Log.w(TAG, "Unity not ready yet, token not sent");
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "SND FCM Message received");

        String payload = "";

        if (remoteMessage.getNotification() != null) {
            payload = remoteMessage.getNotification().getBody();
        }
        else if (!remoteMessage.getData().isEmpty()) {
            payload = remoteMessage.getData().toString();
        }

        Log.d(TAG, "Payload: " + payload);

        try {
            UnityPlayer.UnitySendMessage(UNITY_OBJECT, UNITY_METHOD_MESSAGE, payload);
        } catch (Exception e) {
            Log.w(TAG, "Unity not ready — message queued?");
        }
    }
}
