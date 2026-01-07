package com.YouRehab.WomenUp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.unity3d.player.UnityPlayer; // Importación necesaria

public class NetworkManager extends BroadcastReceiver {

    private static final String TAG = "UnityConnectivity";
    // Objeto Unity que recibirá el mensaje. ¡Ajusta este nombre si es necesario!
    private static final String UNITY_GAME_OBJECT = "AndroidManager";
    private static final String UNITY_METHOD = "OnConnectionStateChanged";

    // Método para registrar el receptor
    public void register(Context context) {
        // Usamos el Context de la aplicación para registrar el receptor
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
        Log.d(TAG, "NetworkChangeReceiver registrado con éxito.");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            // Esto es 'true' si hay una conexión activa y funcional
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            // 1. Determinar el estado y el mensaje a enviar
            String messageToSend;

            if (isConnected) {
                messageToSend = "CONNECTION_RESTORED";
                Log.d(TAG, "Conexión restaurada detectada.");
            } else {
                // NOTA CLAVE: Android envía este evento cuando la red se pierde o cambia a una
                // que no tiene conectividad (isConnected es false).
                messageToSend = "CONNECTION_LOST";
                Log.d(TAG, "Conexión perdida detectada.");
            }

            // 2. Notificar a Unity con el estado correspondiente
            try {
                // UnityMethod ahora se llama OnConnectionStateChanged para ser más genérico
                UnityPlayer.UnitySendMessage(
                        UNITY_GAME_OBJECT,
                        UNITY_METHOD,
                        messageToSend
                );
            } catch (Exception e) {
                Log.e(TAG, "Error enviando mensaje a Unity: " + e.getMessage());
            }
        }
    }
}