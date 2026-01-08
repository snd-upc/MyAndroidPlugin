package com.YouRehab.WomenUp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.util.Map;

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

        Log.d(TAG, "[CustomPlugin SND] Message received");

        String title = "";
        String body  = "";

        // 1️⃣ Prefer DATA payload (best for foreground)
        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();
            title = data.get("title");
            body  = data.get("body");
        }

        // 2️⃣ Fallback to NOTIFICATION payload
        if ((title == null || body == null) && remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notif = remoteMessage.getNotification();
            title = notif.getTitle();
            body  = notif.getBody();
        }

        // 3️⃣ Build JSON manually (Unity-safe)
        try {
            JSONObject json = new JSONObject();
            json.put("title", title != null ? title : "");
            json.put("body", body != null ? body : "");

            String payload = json.toString();

            Log.d(TAG, "Sending to Unity: " + payload);

            UnityPlayer.UnitySendMessage(
                    UNITY_OBJECT,
                    UNITY_METHOD_MESSAGE,
                    payload
            );

        } catch (Exception e) {
            Log.e(TAG, "Failed to send message to Unity", e);
        }
    }

}
