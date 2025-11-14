package com.yourehab.womenup;

import android.bluetooth.*;
import android.util.Log;
import android.os.SystemClock;
import com.unity3d.player.UnityPlayer;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class BT {
    private static final String TAG = "UnityBT";
    private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private OutputStream outStream;
    private InputStream inStream;
    private DataInputStream mmInStream;
    private boolean isConnected = false;
    private boolean keepMonitoring = false;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private String targetAddress;
    private String unityReceiver = "VaginalBTDevice"; // default receiver

    // ---- Initialization ----
    public String InitBT() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) return "No Bluetooth";
        if (!btAdapter.isEnabled()) return "Bluetooth disabled";
        return "Bluetooth enabled";
    }

    public void SetUnityReceiver(String receiverName) {
        Log.d(TAG, "SetUnityReceiver: " + receiverName);
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

    // ---- Connection with retry and monitoring ----
    public void StartBTCommunicationAsync() {
        if (targetAddress == null) {
            sendUnity("OnBTConnection", "Error: device not selected");
            return;
        }

        executor.submit(() -> {
            BluetoothDevice device = btAdapter.getRemoteDevice(targetAddress);
            btAdapter.cancelDiscovery();

            long startTime = SystemClock.elapsedRealtime();
            boolean connected = false;

            while (!connected && (SystemClock.elapsedRealtime() - startTime) < 10_000) { // retry for 10 sec
                try {
                    btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                    btSocket.connect();
                    connected = true;
                } catch (IOException e) {
                    Log.w(TAG, "Connect attempt failed: " + e.getMessage());
                    safeClose();
                    SystemClock.sleep(1000); // wait before retry
                }
            }

            if (!connected) {
                sendUnity("OnBTConnection", "Error: Unable to connect after retries");
                return;
            }

            try {
                outStream = btSocket.getOutputStream();
                inStream = btSocket.getInputStream();
                mmInStream = new DataInputStream(inStream);
                isConnected = true;
                sendUnity("OnBTConnection", "Connected");
                startReadLoop();
                startConnectionMonitor();
            } catch (IOException e) {
                safeClose();
                sendUnity("OnBTConnection", "Error: " + e.getMessage());
            }
        });
    }

    // ---- Continuous read loop ----
    private void startReadLoop() {
        executor.submit(() -> {
            byte[] buffer = new byte[1024];

            while (isConnected) {
                try {
                    int count = mmInStream.read(buffer); // <-- BLOCKING read

                    if (count == -1) {
                        handleDisconnection("Stream closed");
                        break;
                    }

                    if (count > 0) {
                        byte[] data = Arrays.copyOf(buffer, count);
                        String message = new String(data);
                        sendUnity("OnBTData", message);
                    }

                } catch (IOException e) {
                    handleDisconnection("Read loop error: " + e.getMessage());
                    break;
                }
            }
        });
    }

//    private void startReadLoop() {
//        executor.submit(() -> {
//            byte[] buffer = new byte[1024];
//            while (isConnected) {
//                try {
//                    int count = mmInStream.available();
//                    if (count > 0) {
//                        int bytesRead = mmInStream.read(buffer, 0, count);
//                        if (bytesRead > 0) {
//                            byte[] data = Arrays.copyOf(buffer, bytesRead);
//                            String message = new String(data);
//                            sendUnity("OnBTData", message);
//                        }
//                    }
//                    Thread.sleep(20);
//                } catch (Exception e) {
//                    Log.e(TAG, "Read error: " + e.getMessage());
//                    handleDisconnection("Read error: " + e.getMessage());
//                }
//            }
//        });
//    }

    // ---- Connection monitor thread ----

// BT.java - inside startConnectionMonitor()

    private void startConnectionMonitor() {
        keepMonitoring = true;
        executor.submit(() -> {
            while (keepMonitoring) {
                try {
                    if (btSocket == null || !btSocket.isConnected()) {
                        handleDisconnection("Socket lost or closed (monitor check)");
                        break;
                    }

                    // VITAL PING: Writing a single byte detects a broken link.
                    if (outStream != null) {
                        try {
                            outStream.write(0); // Sending a harmless null byte or space (0x20)
                            outStream.flush(); // Ensure the byte is sent immediately
                        } catch (IOException e) {
                            // CRITICAL: Write failed means connection is broken.
                            handleDisconnection("Ping write failed: " + e.getMessage());
                            break; // Exit the monitoring loop
                        }
                    }

                    SystemClock.sleep(3000); // Check every 3 seconds (or less if needed)
                } catch (Exception e) {
                    handleDisconnection("Connection monitor error: " + e.getMessage());
                    break;
                }
            }
        });
    }
//    private void startConnectionMonitor() {
//        keepMonitoring = true;
//        executor.submit(() -> {
//            while (keepMonitoring) {
//                try {
//                    if (btSocket == null || !btSocket.isConnected()) {
//                        handleDisconnection("Socket lost");
//                        break;
//                    }
//
//                    // small ping check (write a harmless byte)
//                    if (outStream != null) {
//                        outStream.write(" ".getBytes());
//                    }
//
//                    Thread.sleep(3000);
//                } catch (Exception e) {
//                    handleDisconnection("Connection lost: " + e.getMessage());
//                    break;
//                }
//            }
//        });
//    }

    // ---- Data Send ----
    public String SendData(String msg) {
        if (!isConnected || outStream == null) return "Not connected";
        try {
            outStream.write(msg.getBytes());
            return "OK";
        } catch (IOException e) {
            handleDisconnection("Write failed: " + e.getMessage());
            return "KO: " + e.getMessage();
        }
    }

    // ---- Read Data (Unity polling) ----
    public byte[] readData(boolean debug) {
        if (!isConnected || inStream == null) return new byte[0];
        try {
            int available = inStream.available();
            if (available > 0) {
                byte[] buffer = new byte[available];
                int bytesRead = inStream.read(buffer);
                if (debug) Log.d(TAG, "ReadData: " + bytesRead + " bytes");
                return Arrays.copyOf(buffer, bytesRead);
            }
        } catch (IOException e) {
            handleDisconnection("ReadData error: " + e.getMessage());
        }
        return new byte[0];
    }

    // ---- Helpers ----
    private void handleDisconnection(String reason) {
        Log.w(TAG, "Disconnected: " + reason);
        safeClose();
        sendUnity("OnBTConnection", "Disconnected");
    }

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
        keepMonitoring = false;
    }

    public String StopBTCommunication() {
        try {
            keepMonitoring = false;
            isConnected = false;
            safeClose();
            sendUnity("OnBTConnection", "Disconnected");
            return "OK";
        } catch (Exception e) {
            Log.e(TAG, "StopBTCommunication error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
