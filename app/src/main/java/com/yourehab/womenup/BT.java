package com.yourehab.womenup;

import android.bluetooth.*;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import com.unity3d.player.UnityPlayer;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.reflect.Method;

public class BT {
    private static final String TAG = "UnityBT";
    private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private OutputStream outStream;
    private InputStream inStream;
    private DataInputStream mmInStream;
    private boolean isConnected = false;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private String targetAddress;
    private String unityReceiver = "VaginalBTDevice"; // name of Unity GameObject to send messages to

    // ---- Initialization ----
    public String InitBT() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) return "No Bluetooth";
        if (!btAdapter.isEnabled()) return "Bluetooth disabled";
        return "Bluetooth enabled";
    }
    public void SetUnityReceiver(String receiverName) {
        Log.d(TAG, "Setunityreceiver: " + receiverName );
        unityReceiver = receiverName;

    }
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

    // ---- Connection (non-blocking) ----
    public void StartBTCommunicationAsync() {
        if (targetAddress == null) {
            sendUnity("OnBTConnection", "Error: device not selected");
            return;
        }

        executor.submit(() -> {
            //sendUnity("OnBTConnection", "Connecting...");

            BluetoothDevice device = btAdapter.getRemoteDevice(targetAddress);
            try {
                btAdapter.cancelDiscovery();

                // Standard SPP connect (blocking)
                btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                btSocket.connect();

                outStream = btSocket.getOutputStream();
                inStream = btSocket.getInputStream();
                mmInStream = new DataInputStream(inStream);
                isConnected = true;

                sendUnity("OnBTConnection", "Connected");
                startReadLoop();

            } catch (IOException e) {
                safeClose();
                sendUnity("OnBTConnection", "Error: " + e.getMessage());
            }
        });
    }

    // ---- Background Read Loop ----
    private void startReadLoop() {
        executor.submit(() -> {
            byte[] buffer = new byte[1024];
            while (isConnected) {
                try {
                    int count = mmInStream.available();
                    if (count > 0) {
                        int bytesRead = mmInStream.read(buffer, 0, count);
                        byte[] data = Arrays.copyOf(buffer, bytesRead);
                        String message = new String(data);
                        sendUnity("OnBTData", message); // forward to Unity
                    }
                    Thread.sleep(20);
                } catch (Exception e) {
                    sendUnity("OnBTError", "Read error: " + e.getMessage());
                    isConnected = false;
                    safeClose();
                }
            }
        });
    }

    // ---- Send Data ----
    public String SendData(String msg) {
        if (!isConnected || outStream == null) return "Not connected";
        try {
            outStream.write(msg.getBytes());
            return "OK";
        } catch (IOException e) {
            return "KO: " + e.getMessage();
        }
    }
    // ---- Read Data ----
    public byte[] readData(boolean debug) {
        if (!isConnected || inStream == null) return new byte[0];

        try {
            int available = inStream.available();
            if (available > 0) {
                byte[] buffer = new byte[available];
                int bytesRead = inStream.read(buffer);
                if (debug) Log.d(TAG, "ReadData: " + bytesRead + " bytes");
                return Arrays.copyOf(buffer, bytesRead);
            } else {
                if (debug) Log.d(TAG, "No data available");
            }
        } catch (IOException e) {
            Log.e(TAG, "ReadData error: " + e.getMessage());
        }
        return new byte[0]; // ✅ always return non-null
    }


    // ---- Helpers ----
    private void sendUnity(String method, String param) {
        try {
            UnityPlayer.UnitySendMessage(unityReceiver, method, param);
        } catch (Exception e) {
            Log.w(TAG, "UnitySendMessage failed: " + e.getMessage());
        }
    }

    private void safeClose() {
        try { if (btSocket != null) btSocket.close(); } catch (Exception ignored) {}
        btSocket = null;
        inStream = null;
        outStream = null;
        mmInStream = null;
        isConnected = false;
    }

    public String StopBTCommunication() {
        try {
            // Stop connection loop
            isConnected = false;

            // Close everything safely
            safeClose();

            // Inform Unity
            sendUnity("OnBTConnection", "Disconnected");

            return "OK";
        } catch (Exception e) {
            Log.e(TAG, "StopBTCommunication error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

}
