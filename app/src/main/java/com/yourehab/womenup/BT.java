//Vuelta a empezar - version base - compatible 18-11
package com.yourehab.womenup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import com.unity3d.player.UnityPlayer;
import java.io.DataInputStream;
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
    private OutputStream outStream;
    private InputStream inStream;
    private DataInputStream mmInStream;
    private boolean isConnected = false;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private String targetAddress;
    private String unityReceiver = "VaginalBTDevice";

    public String InitBT() {
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.btAdapter == null) {
            return "No Bluetooth";
        } else {
            return !this.btAdapter.isEnabled() ? "Bluetooth disabled" : "Bluetooth enabled";
        }
    }

    public void SetUnityReceiver(String receiverName) {
        Log.d("UnityBT", "Setunityreceiver: " + receiverName);
        this.unityReceiver = receiverName;
    }

    public String GetPairedDevice() {
        Set<BluetoothDevice> devices = this.btAdapter.getBondedDevices();
        if (devices != null && !devices.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator var3 = devices.iterator();

            while(var3.hasNext()) {
                BluetoothDevice d = (BluetoothDevice)var3.next();
                sb.append(d.getName()).append("#");
            }

            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public String InitBTDevice(String name) {
        Set<BluetoothDevice> devices = this.btAdapter.getBondedDevices();
        Iterator var3 = devices.iterator();

        BluetoothDevice d;
        do {
            if (!var3.hasNext()) {
                return "KO";
            }

            d = (BluetoothDevice)var3.next();
        } while(!d.getName().equals(name));

        this.targetAddress = d.getAddress();
        return "OK";
    }

    public void StartBTCommunicationAsync() {
        if (this.targetAddress == null) {
            this.sendUnity("OnBTConnection", "Error: device not selected");
        } else {
            this.executor.submit(() -> {
                BluetoothDevice device = this.btAdapter.getRemoteDevice(this.targetAddress);

                try {
                    this.btAdapter.cancelDiscovery();
                    this.btSocket = device.createRfcommSocketToServiceRecord(this.SPP_UUID);
                    this.btSocket.connect();
                    this.outStream = this.btSocket.getOutputStream();
                    this.inStream = this.btSocket.getInputStream();
                    this.isConnected = true;
                    this.sendUnity("OnBTConnection", "Connected");
                } catch (IOException var3) {
                    this.safeClose();
                    this.sendUnity("OnBTConnection", "Error: " + var3.getMessage());
                }

            });
        }
    }


    public String SendData(String msg) {
        if (this.isConnected && this.outStream != null) {
            try {
                this.outStream.write(msg.getBytes());
                return "OK";
            } catch (IOException var3) {
                return "KO: " + var3.getMessage();
            }
        } else {
            return "Not connected";
        }
    }

    public byte[] readData(boolean debug) {
        if (this.isConnected && this.inStream != null) {
            try {
                int available = this.inStream.available();
                if (available > 0) {
                    byte[] buffer = new byte[available];
                    int bytesRead = this.inStream.read(buffer);
                    if (debug) {
                        Log.d("UnityBT", "ReadData: " + bytesRead + " bytes");
                    }

                    return Arrays.copyOf(buffer, bytesRead);
                }

                if (debug) {
                    Log.d("UnityBT", "No data available");
                }
            } catch (IOException var5) {
                Log.e("UnityBT", "ReadData error: " + var5.getMessage());
            }

            return new byte[0];
        } else {
            return new byte[0];
        }
    }

    private void sendUnity(String method, String param) {
        try {
            UnityPlayer.UnitySendMessage(this.unityReceiver, method, param);
        } catch (Exception var4) {
            Log.w("UnityBT", "UnitySendMessage failed: " + var4.getMessage());
        }

    }

    private void safeClose() {
        try {
            if (this.btSocket != null) {
                this.btSocket.close();
            }
        } catch (Exception var2) {
        }

        this.btSocket = null;
        this.inStream = null;
        this.outStream = null;
        this.isConnected = false;
    }

    public String StopBTCommunication() {
        try {
            this.isConnected = false;
            this.safeClose();
            this.sendUnity("OnBTConnection", "Disconnected");
            return "OK";
        } catch (Exception var2) {
            Log.e("UnityBT", "StopBTCommunication error: " + var2.getMessage());
            return "Error: " + var2.getMessage();
        }
    }
}