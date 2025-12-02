package com.yourehab.womenup;

import android.content.Context;
import android.util.Log;

public class AndroidPlugin
{
    private static final String TAG = "Unity";
    private Context context;

    private BT bt1;

    private NetworkManager connectivityReceiver;

    public AndroidPlugin(Context context)
    {
        this.context = context;
    }

    /*******************************************************************************************/
    /*                          BLUETOOTH - CLEAN VERSION                                      */
    /*******************************************************************************************/

    public String InitBT() {
        Log.d(TAG, "ANDROID InitBT");
        bt1 = new BT();
        return bt1.InitBT();
    }

    public void SetUnityReceiver(String receiverName) {
        if (bt1 != null) {
            bt1.SetUnityReceiver(receiverName);
        }
    }

    public String GetPairedDevice() {
        if (bt1 == null) return "";
        return bt1.GetPairedDevice();
    }

    public String InitBTDevice(String deviceName) {
        if (bt1 == null) return "NoBTInstance";
        return bt1.InitBTDevice(deviceName);
    }

    public void StartBTCommunication() {
        if (bt1 != null) {
            bt1.StartBTCommunicationAsync();
        }
    }

    public String SendData(String data) {
        if (bt1 == null) return "NoBTInstance";
        return bt1.SendData(data);
    }

    public String StopBTCommunication() {
        if (bt1 != null) {
            return bt1.StopBTCommunication();
        }
        return "NoBTInstance";
    }

    //------------------------------------------------------------------------------------------
    // NUEVO: Conectividad
    //------------------------------------------------------------------------------------------

    /**
     * Unity llama a este método para iniciar la escucha de cambios de red.
     */
    public void StartConnectivityMonitor() {
        Log.d(TAG, "ANDROID StartConnectivityMonitor");

        if (connectivityReceiver == null) {
            connectivityReceiver = new NetworkManager();
            // ¡Usamos el Context ya inyectado para el registro!
            connectivityReceiver.register(this.context);
            Log.d(TAG, "Monitoreo de red iniciado y BroadcastReceiver registrado.");
        } else {
            Log.d(TAG, "Monitoreo de red ya estaba activo.");
        }
    }
}
