package com.yourehab.womenup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Base64;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BT {

    private static final String TAG = "UnityBT";
    private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private InputStream inStream;
    private OutputStream outStream;

    private boolean isConnected = false;
    private String targetAddress = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    // Unity receiver GameObject
    private String unityReceiver = "VaginalBTDevice";

    // ------------------------------------------------------------------------
    // Initialization
    // ------------------------------------------------------------------------

    public String InitBT() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) return "No Bluetooth";
        if (!btAdapter.isEnabled()) return "Bluetooth disabled";

        return "Bluetooth enabled";
    }

    public void SetUnityReceiver(String receiverName) {
        Log.d(TAG, "Receiver set to: " + receiverName);
        this.unityReceiver = receiverName;
    }

    // ------------------------------------------------------------------------
    // Get paired device list
    // ------------------------------------------------------------------------

    public String GetPairedDevice() {
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();

        if (devices == null || devices.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (BluetoothDevice d : devices) {
            sb.append(d.getName()).append("#");
        }

        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    // Select device
    // ------------------------------------------------------------------------

    public String InitBTDevice(String name) {
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();

        for (BluetoothDevice d : devices) {
            if (d.getName().equals(name)) {
                targetAddress = d.getAddress();
                return "OK";
            }
        }
        return "KO";
    }

    // ------------------------------------------------------------------------
    // Start connection
    // ------------------------------------------------------------------------

    public void StartBTCommunicationAsync() {

        if (targetAddress == null) {
            sendUnity("OnBTConnection", "Error: device not selected");
            return;
        }

        executor.submit(() -> {
            try {
                BluetoothDevice device = btAdapter.getRemoteDevice(targetAddress);

                btAdapter.cancelDiscovery();
                btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);

                btSocket.connect(); // <-- BLOCKING CONNECT

                outStream = btSocket.getOutputStream();
                inStream = btSocket.getInputStream();

                isConnected = true;

                sendUnity("OnBTConnection", "Connected");

                startReadLoop();

            } catch (Exception e) {
                safeClose();
                sendUnity("OnBTConnection", "Error: " + e.getMessage());
            }
        });
    }

    // ------------------------------------------------------------------------
    // Read Loop (ONLY ONE READER)
    // ------------------------------------------------------------------------

    private void startReadLoop() {
        executor.submit(() -> {

            byte[] buffer = new byte[1024];

            while (isConnected) {
                try {
                    // Blocking read()
                    int bytesRead = inStream.read(buffer);

                    if (bytesRead == -1) throw new IOException("Stream closed");

                    if (bytesRead > 0) {
                        byte[] realData = Arrays.copyOf(buffer, bytesRead);
                        String base64 = Base64.encodeToString(realData, Base64.NO_WRAP);
                        sendUnity("OnBTData", base64);
                    }

                } catch (IOException e) {
                    // Device probably turned OFF, lost signal, etc.
                    sendUnity("OnBTConnection", "Disconnected");
                    safeClose();
                }
            }
        });
    }

    // ------------------------------------------------------------------------
    // Send Data
    // ------------------------------------------------------------------------

    public String SendData(String msg) {
        if (!isConnected || outStream == null) return "Not connected";

        try {
            outStream.write(msg.getBytes());
            return "OK";
        } catch (IOException e) {
            return "KO: " + e.getMessage();
        }
    }

    // ------------------------------------------------------------------------
    // Safe Close
    // ------------------------------------------------------------------------

    private void safeClose() {
        try { if (btSocket != null) btSocket.close(); } catch (Exception ignore) {}

        btSocket = null;
        inStream = null;
        outStream = null;
        isConnected = false;
    }

    // ------------------------------------------------------------------------
    // Stop Communication
    // ------------------------------------------------------------------------

    public String StopBTCommunication() {
        isConnected = false;
        safeClose();
        sendUnity("OnBTConnection", "Disconnected");
        return "OK";
    }

    // ------------------------------------------------------------------------
    // Unity Message Helper
    // ------------------------------------------------------------------------

    private void sendUnity(String method, String param) {
        try {
            UnityPlayer.UnitySendMessage(unityReceiver, method, param);
        } catch (Exception e) {
            Log.w(TAG, "UnitySendMessage failed: " + e.getMessage());
        }
    }
}
