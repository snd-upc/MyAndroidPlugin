
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