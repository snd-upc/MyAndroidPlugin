
package com.yourehab.womenup;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import java.io.DataInputStream;
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;

import java.util.UUID;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.Intent;
import android.util.Log;

import java.nio.charset.Charset;

import android.os.ParcelUuid;
import java.lang.reflect.Method;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;


import 	java.lang.reflect.Method;

public class BT {
    String TAG="Unity";
    int deviceNumber;
    boolean BTOK;
    boolean BTDeviceNameOK;
    boolean BTDeviceOK;
    private int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    String address = "00:00:00:00:00:00";
    private String BTName="";
    private int DEVICENUMBER;
    final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Set<BluetoothDevice> pairedDevices;
    
    public String InitBT() {
      btAdapter = BluetoothAdapter.getDefaultAdapter();
      if (btAdapter == null) {
          Log.d(TAG, "NO Bluetooth");
          return "No Bluetooth";
      }

      if (!btAdapter.isEnabled()) {
          Log.d(TAG, "Bluetooth disabled");
          return "Bluetooth disabled";
          //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
          //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      }else{
          Log.d(TAG, "Bluetooth enabled");
          BTOK=true;
          return "Bluetooth enabled";
      }
    }

    //public void 
  
    public String GetPairedDevice(){
        if(!BTOK) return "Bluetooth not initialized";
        String returnValue="";
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Log.d(TAG, "PairedDevices count : " + pairedDevices.size());
            for (BluetoothDevice device : pairedDevices) {
                String deviceBTName = device.getName();
                Log.d(TAG, "Paired device : "+deviceBTName);
                //Log.d(TAG, "Address:"+device.getAddress());
                returnValue+=deviceBTName+"#";
            }
        }
        if(returnValue.endsWith("#") )
            returnValue = returnValue.substring(0, returnValue.length()-1); 
        return returnValue;
    }
    
    public String InitBTDevice(String sBTName){
        if(!BTOK) return "Bluetooth not initialized";
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
          Log.d(TAG, "PairedDevices count : " + pairedDevices.size());
          for (BluetoothDevice device : pairedDevices) {
              String deviceBTName = device.getName();
              Log.d(TAG, "Paired device : "+deviceBTName);
              //Log.d(TAG, "Address:"+device.getAddress());

              if(deviceBTName.equals(sBTName)) {
                  //deviceNumber++;
                  //DEVICENUMBER=deviceNumber;
                  address = device.getAddress();
                  BTName=""+device.getName();
                  Log.d(TAG, "Init device : "+BTName + " address:" + address);
                  BTDeviceNameOK=true;
                  return "OK";
              }
          }
        }
        return "KO";
    }    

    /* (Before)
    public String StartBTCommunication() {
        if(!BTDeviceNameOK) return "Device not initialized";
        //Log.d(TAG, "...In onResume - Attempting client connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

//        try {
//            Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//            btSocket = (BluetoothSocket) m.invoke(device, 1);
//            //btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//        } catch (Exception e) {
//            Log.d(TAG,"Exception createRfcommSocketToServiceRecord:"+e.getMessage());
//            return "createRfcommSocket Error";
//            // errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
//        }

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (Exception e) {
            Log.d(TAG,"Exception createRfcommSocketToServiceRecord: " + e.getMessage());
            return "createRfcommSocket Error";
        }

        Log.d(TAG, "StartBTCommunication OK : "+BTName + " address:" + address);

        waitALittle();
        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting to Remote...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection established and data link opened...");
        } catch (IOException e) {
            Log.e(TAG, "Error connecting socket: " + e.getMessage(), e);
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "Error closing socket after failed connection: " + e2.getMessage(), e2);
            }
            return "Error01 Socket";
        }


        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Creating Socket...");

        try {
            outStream = btSocket.getOutputStream();
            Log.d(TAG,"1 outStream:"+outStream);
        } catch (IOException e) {
            Log.d(TAG,"getOutputStream:"+e.getMessage());
            //errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
            return "Error02 Socket";
        }

        try {
            inStream = btSocket.getInputStream();
            Log.d(TAG,"1 inStream:"+inStream);

            mmInStream = new DataInputStream(inStream);
            //mmInStream = new DataInputStream(new BufferedInputStream(inStream));
            Log.d(TAG,"1 mmInStream:"+mmInStream);
        } catch (IOException e) {
            Log.d(TAG,"getInputStream:"+e.getMessage());
            //errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
            return "Error03 Socket";
        }

        Log.d(TAG, "StartBTCommunication terminated");
        return "OK";
    }
*/

    // SND 11-11
    public String StartBTCommunication() {
        if (!BTDeviceNameOK) return "Device not initialized";

        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        Log.d(TAG, "StartBTCommunication OK : " + BTName + " address:" + address);

        // Cancel discovery antes de conectar
        try { btAdapter.cancelDiscovery(); } catch (Exception e) { Log.w(TAG, "cancelDiscovery failed: " + e.getMessage()); }

        // 1) Intentar obtener UUIDs vía SDP (puede necesitar permisos)
        ParcelUuid[] uuids = null;
        try {
            device.fetchUuidsWithSdp();
            // esperar un poco para que se rellenen
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            uuids = device.getUuids();
        } catch (Exception e) {
            Log.w(TAG, "fetchUuidsWithSdp/getUuids exception: " + e.getMessage());
        }

        // Helper para cerrar socket en caso de fallo
        java.util.function.Consumer<BluetoothSocket> safeClose = (sock) -> {
            if (sock != null) {
                try { sock.close(); } catch (IOException ignored) {}
            }
        };

        // Intentar con cada UUID que el dispositivo anuncia
        if (uuids != null) {
            for (ParcelUuid pUuid : uuids) {
                UUID uuid = pUuid.getUuid();
                Log.d(TAG, "Probando UUID desde device.getUuids(): " + uuid.toString());
                BluetoothSocket trialSocket = null;
                try {
                    trialSocket = device.createRfcommSocketToServiceRecord(uuid);
                    trialSocket.connect();
                    // si llegamos aquí: conectado
                    btSocket = trialSocket;
                    outStream = btSocket.getOutputStream();
                    inStream = btSocket.getInputStream();
                    mmInStream = new DataInputStream(inStream);
                    Log.d(TAG, "Conexión establecida usando UUID device.getUuids(): " + uuid.toString());
                    return "OK";
                } catch (Exception e) {
                    Log.w(TAG, "Fallo con UUID " + uuid.toString() + ": " + e.getMessage());
                    safeClose.accept(trialSocket);
                }
            }
        } else {
            Log.d(TAG, "device.getUuids() devolvio null o no disponible");
        }

        // 2) Intentar con UUID SPP estándar
        try {
            UUID sppUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            Log.d(TAG, "Probando UUID SPP estándar: " + sppUUID.toString());
            BluetoothSocket sppSocket = device.createRfcommSocketToServiceRecord(sppUUID);
            sppSocket.connect();
            btSocket = sppSocket;
            outStream = btSocket.getOutputStream();
            inStream = btSocket.getInputStream();
            mmInStream = new DataInputStream(inStream);
            Log.d(TAG, "Conexión establecida con UUID SPP estándar");
            return "OK";
        } catch (Exception e) {
            Log.w(TAG, "Fallo con UUID SPP estándar: " + e.getMessage());
        }

        // 3) Fallback: intentar crear socket RFCOMM por reflection (canal 1)
        try {
            Log.d(TAG, "Intentando reflection fallback (createRfcommSocket(int))");
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            BluetoothSocket reflSocket = (BluetoothSocket) m.invoke(device, 1);
            reflSocket.connect();
            btSocket = reflSocket;
            outStream = btSocket.getOutputStream();
            inStream = btSocket.getInputStream();
            mmInStream = new DataInputStream(inStream);
            Log.d(TAG, "Conexión establecida con reflection fallback");
            return "OK";
        } catch (Exception e) {
            Log.e(TAG, "Fallo con reflection fallback: " + e.getMessage());
        }

        // Si llegamos aquí, todo falló
        Log.e(TAG, "StartBTCommunication terminated - no se pudo conectar");
        return "Error01 Socket";
    }

  long start_time = System.nanoTime();
  long inter_time1 = System.nanoTime();
  long inter_time2 = System.nanoTime();
  long inter_time3 = System.nanoTime();
  long end_time = System.nanoTime();
  DataInputStream mmInStream;
  
    public byte[] readData(boolean showMessage) {
        start_time = System.nanoTime();
        byte[] buffer = new byte[10000];  // buffer store for the stream
        try {
            //inter_time1 = System.nanoTime();
            int bytesCount = mmInStream.available(); 
            byte[] data = new byte[bytesCount];
            //Log.d(TAG, "BT.Java;readData;BytesCount:"+bytesCount);
            if(bytesCount>0){
                mmInStream.read(buffer,0,bytesCount);
                //inter_time2 = System.nanoTime();
                System.arraycopy(buffer,0,data,0,bytesCount);
                //inter_time3 = System.nanoTime();
                if(showMessage){
                    String readMessage = new String(buffer, 0, bytesCount);
                    String readMessage2 = new String(buffer, Charset.forName("UTF8"));
                    Log.d(TAG,"Message2:*"+readMessage2+"*");
                    Log.d(TAG, "Message1:"+readMessage+"*");
                }
                //end_time = System.nanoTime();
                //Log.d(TAG, "bytesCount :"+bytesCount+" total "+(end_time-start_time)/1e6 + " inter2:" +(inter_time2-inter_time1)/1e6 + " inter3:" +(inter_time3-inter_time2)/1e6);
            }     
            return data;
        } catch (IOException e) {
            Log.d(TAG,"readData IOException:"+e.getMessage());
            byte[] myvar = "KO".getBytes();
            return myvar;
            // errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }
    }
    
    public void looptest() {
        // buffer store for the stream
        int bytesCount=0; // bytes returned from read()
        for(int i=0;i<100000;i++){
            byte[] buffer = new byte[10000];
            start_time = System.nanoTime();
            
            try {
                bytesCount = inStream.available();
                Log.d(TAG, "bytesCount:"+bytesCount);
                if(bytesCount>1000){
                    inStream.read(buffer,0,100);
                }
                //bytesCount = mmInStream.read(buffer,0,100);
                //mmInStream.readFully(buffer, 0, 100);
            } catch (IOException e) {
                Log.d(TAG,"readData IOException:"+e.getMessage());
            }                
            inter_time1 = System.nanoTime();
            //byte[] data = new byte[bytesCount];
            //System.arraycopy(buffer,0,data,0,bytesCount);
            end_time = System.nanoTime();
            Log.d(TAG, "Size:"+bytesCount+" Time:"+(end_time-start_time)/1e6 + "us ReadBufferTime:" +(inter_time1-start_time)/1e6 +"us" );
            try{
                Thread.sleep(10);
            }
            catch (Exception e) {
                Log.d(TAG,"readData Exception:"+e.getMessage());
            }              
        }
    }    

    public String sendData(String message) {
        
        byte[] msgBuffer = message.getBytes();
        Log.d(TAG, "...Sending data: " + message + "...");
        try {
            outStream.write(msgBuffer);
            return "OK";
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
            Log.d(TAG, "sendData exception:"+msg);
            return "KO";
            //errorExit("Fatal Error", msg);
        }
    }
    
    public void waitALittle(){
        Log.d(TAG, "Wait");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
    }    
}


/* INTENTO CON CHATGPT DE 11-11-25
package com.yourehab.womenup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

//import com.unity3d.player.UnityPlayer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class BT {

    private final String TAG = "Unity";
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private OutputStream outStream;
    private InputStream inStream;
    private DataInputStream mmInStream;

    private String address = "00:00:00:00:00:00";
    private String BTName = "";
    private boolean BTOK = false;
    private boolean BTDeviceNameOK = false;

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public String InitBT() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Log.d(TAG, "No Bluetooth adapter");
            return "No Bluetooth";
        }

        if (!btAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth disabled");
            return "Bluetooth disabled";
        } else {
            BTOK = true;
            Log.d(TAG, "Bluetooth enabled");
            return "Bluetooth enabled";
        }
    }

    public String GetPairedDevice() {
        if (!BTOK) return "Bluetooth not initialized";

        StringBuilder returnValue = new StringBuilder();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "Paired device: " + device.getName());
                returnValue.append(device.getName()).append("#");
            }
        }

        if (returnValue.length() > 0) returnValue.setLength(returnValue.length() - 1);
        return returnValue.toString();
    }

    public String InitBTDevice(String sBTName) {
        if (!BTOK) return "Bluetooth not initialized";

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices != null) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(sBTName)) {
                    address = device.getAddress();
                    BTName = device.getName();
                    BTDeviceNameOK = true;
                    Log.d(TAG, "Init device: " + BTName + " address: " + address);
                    return "OK";
                }
            }
        }
        return "KO";
    }

    // Asynchronous connection method for Unity callback
    public void StartBTCommunicationAsync(String unityObject, String unityCallback) {
        new Thread(() -> {
            String result = StartBTCommunicationInternal();
            //UnityPlayer.UnitySendMessage(unityObject, unityCallback, result);
        }).start();
    }

    private String StartBTCommunicationInternal() {
        if (!BTDeviceNameOK) return "Device not initialized";

        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        Log.d(TAG, "StartBTCommunication OK: " + BTName + " address: " + address);

        try { btAdapter.cancelDiscovery(); } catch (Exception e) { Log.w(TAG, "cancelDiscovery failed: " + e.getMessage()); }

        Consumer<BluetoothSocket> safeClose = sock -> {
            if (sock != null) {
                try { sock.close(); } catch (IOException ignored) {}
            }
        };

        // 1️⃣ Try UUIDs from device.getUuids()
        try {
            device.fetchUuidsWithSdp();
            Thread.sleep(200);
        } catch (Exception e) {
            Log.w(TAG, "fetchUuidsWithSdp exception: " + e.getMessage());
        }

        ParcelUuid[] uuids = device.getUuids();
        if (uuids != null) {
            for (ParcelUuid pUuid : uuids) {
                UUID uuid = pUuid.getUuid();
                try {
                    BluetoothSocket trialSocket = device.createRfcommSocketToServiceRecord(uuid);
                    trialSocket.connect();
                    setupStreams(trialSocket);
                    Log.d(TAG, "Connected with UUID: " + uuid);
                    return "OK";
                } catch (Exception e) {
                    Log.w(TAG, "Failed UUID " + uuid + ": " + e.getMessage());
                }
            }
        } else {
            Log.d(TAG, "device.getUuids() returned null");
        }

        // 2️⃣ Try standard SPP UUID
        try {
            BluetoothSocket sppSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            sppSocket.connect();
            setupStreams(sppSocket);
            Log.d(TAG, "Connected with standard SPP UUID");
            return "OK";
        } catch (Exception e) {
            Log.w(TAG, "SPP UUID failed: " + e.getMessage());
        }

        // 3️⃣ Reflection fallback
        try {
            Method m = device.getClass().getMethod("createRfcommSocket", int.class);
            BluetoothSocket reflSocket = (BluetoothSocket) m.invoke(device, 1);
            reflSocket.connect();
            setupStreams(reflSocket);
            Log.d(TAG, "Connected with reflection fallback");
            return "OK";
        } catch (Exception e) {
            Log.e(TAG, "Reflection fallback failed: " + e.getMessage());
        }

        Log.e(TAG, "All connection methods failed");
        return "Error01 Socket";
    }

    private void setupStreams(BluetoothSocket socket) throws IOException {
        btSocket = socket;
        outStream = btSocket.getOutputStream();
        inStream = btSocket.getInputStream();
        mmInStream = new DataInputStream(inStream);
    }

    public String StartBTCommunication() {
        return StartBTCommunicationInternal();
    }
    public byte[] readData(boolean showMessage) {
        byte[] buffer = new byte[10000];
        try {
            int bytesCount = mmInStream.available();
            byte[] data = new byte[bytesCount];
            if (bytesCount > 0) {
                mmInStream.read(buffer, 0, bytesCount);
                System.arraycopy(buffer, 0, data, 0, bytesCount);
                if (showMessage) {
                    String message = new String(buffer, 0, bytesCount, Charset.forName("UTF8"));
                    Log.d(TAG, "Message: " + message);
                }
            }
            return data;
        } catch (IOException e) {
            Log.d(TAG, "readData IOException: " + e.getMessage());
            return "KO".getBytes();
        }
    }

    public String sendData(String message) {
        try {
            byte[] msgBuffer = message.getBytes();
            outStream.write(msgBuffer);
            return "OK";
        } catch (IOException e) {
            Log.d(TAG, "sendData exception: " + e.getMessage());
            return "KO";
        }
    }

    public void waitALittle() {
        try { Thread.sleep(100); } catch (Exception ignored) {}
    }
}
*/