package com.yourehab.womenup;

import android.content.Context;
import android.util.Log;

public class AndroidPlugin
{
    private static final String TAG = "Unity";
    private Context context;

    private BT btVag;
    private BT btAbd;

    private NetworkManager connectivityReceiver;

    public AndroidPlugin(Context context)
    {
        this.context = context;
    }

    /*******************************************************************************************/
    /*                          BLUETOOTH - CLEAN VERSION                                      */
    /*******************************************************************************************/

    public String InitBT(int idBT) {
        Log.d(TAG, "ANDROID InitBT");
        if (idBT == 0) {
            if (btVag == null) {
                btVag = new BT();
            }
            return btVag.InitBT();
        } else if (idBT == 1) {
            if (btAbd == null) {
                btAbd = new BT();
            }
            return btAbd.InitBT();
        } else {
            return "InvalidBTInstanceID";
        }
    }

    public void SetUnityReceiver(String receiverName, int idBT) {
        if (idBT == 0 && btVag != null) {
            btVag.SetUnityReceiver(receiverName);
        }
        if (idBT == 1 && btAbd != null) {
            btAbd.SetUnityReceiver(receiverName);
        }
    }

    public String GetPairedDevice(int idBT) {
        if (idBT == 0) {
            if (btVag == null) return "";
            return btVag.GetPairedDevice();
        } else {
            if (btAbd == null) return "";
            return btAbd.GetPairedDevice();
        }

    }

    public String InitBTDevice(String deviceName, int idBT) {
        if (idBT == 0) {
            if (btVag == null) return "NoBTInstance";
            return btVag.InitBTDevice(deviceName);
        } else {
            if (btAbd == null) return "NoBTInstance";
            return btAbd.InitBTDevice(deviceName);
        }

    }

    public void StartBTCommunication(int idBT) {
        if (idBT == 0) {
            if (btVag != null) {
                btVag.StartBTCommunicationAsync();
            }
        } else {
            if (btAbd != null) {
                btAbd.StartBTCommunicationAsync();
            }
        }

    }

    public String SendData(String data, int idBT) {
        if (idBT == 0) {
            if (btVag == null) return "NoBTInstance";
            return btVag.SendData(data);
        } else {
            if (btAbd == null) return "NoBTInstance";
            return btAbd.SendData(data);
        }
    }

    public String StopBTCommunication(int idBT) {
        if (idBT == 0) {
            if (btVag != null) {
                return btVag.StopBTCommunication();
            }
            return "NoBTInstance";
        } else {
            if (btAbd != null) {
                return btAbd.StopBTCommunication();
            }
            return "NoBTInstance";
        }
    }

    //------------------------------------------------------------------------------------------
    // Conectividad
    //------------------------------------------------------------------------------------------

    /**
     * Unity llama a este método para iniciar la escucha de cambios de red.
     */
    public void StartConnectivityMonitor() {
        Log.d(TAG, "ANDROID StartConnectivityMonitor");

        if (connectivityReceiver == null) {
            connectivityReceiver = new NetworkManager();
            connectivityReceiver.register(this.context);
            Log.d(TAG, "Monitoreo de red iniciado y BroadcastReceiver registrado.");
        } else {
            Log.d(TAG, "Monitoreo de red ya estaba activo.");
        }
    }
}
