package com.YouRehab.WomenUp;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.unity3d.player.UnityPlayer;

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

    //------------------------------------------------------------------------------------------
    // TESTS DE FB
    //------------------------------------------------------------------------------------------
    public void TestFB() {
        // 1. Manually initialize Firebase before using any Firebase services
        /*
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
            Log.d("FCM", "Firebase manual initialization successful.");
        }

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        Log.d("FCM", "Google Play Services status = " + status);
*/
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey("AIzaSyBGmnKWYIloEXPc4H4B6iYsiA9n1O9enRQ")       // From google-services.json -> current_key
                    .setApplicationId("1:787090042834:android:57333acdee7a7f148ea20b")  // From google-services.json -> mobilesdk_app_id
                    .setProjectId("womenup-b3331")  // From google-services.json -> project_id
                    .setGcmSenderId("787090042834")
                    .build();

            FirebaseApp.initializeApp(context, options);
        }
        // Now it is safe to call FirebaseMessaging
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM", "Manual token fetch: " + token);

                    UnityPlayer.UnitySendMessage("AndroidManager", "OnFirebaseToken", token);
                });
    }}
