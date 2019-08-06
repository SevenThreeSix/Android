package com.example.blootoothuart;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Duan on 2017/2/23.
 */

public class randomLightsService extends Service {
    Handler mHandler = new Handler();
    Runnable mRunnable;
    BluetoothSocket mBTSocket;
    BluetoothAdapter mBTAdapter;
    BluetoothDevice mBTDevice;
    boolean connecting, connection;
    BTConnectorThread connectThread;
    String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    String deviceAddr;
    Handler BTConnectHandler;
    Intent mIntent;
    Handler mainHandler;
    public void setHandler(Handler mHandler){
        mainHandler = mHandler;
    }
    public void setConfig(String deviceAddr){
        BTConnectHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.arg1 == 1){
                    Toast.makeText(getBaseContext(), "random lights ready", Toast.LENGTH_SHORT).show();

                }
            }
        };
        Toast.makeText(getBaseContext(), "Connecting, wait please...", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void write(byte[] bytes){
        btIO IOPip = new btIO(this.mBTSocket);
        IOPip.wirte(bytes);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = intent;
        Bundle serviceData = intent.getExtras();
        deviceAddr = serviceData.getString("deviceAddr");
        mRunnable = new Runnable() {
            @Override
            public void run() {
                int led = (int)(Math.random()*5);
                int turn = (int)(Math.random()*3);
                String construct = "1";
                construct += led;
                construct += turn;
                construct += "&";
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("s", construct);
                msg.setData(bundle);
                //System.out.println(construct);
                if(connection){
                    mainHandler.sendMessage(msg);
                    write(construct.getBytes());
                    System.out.println(construct);
                }else{
                    if(connecting == true){

                    }else{
                        connectThread = new BTConnectorThread(deviceAddr, BTConnectHandler);
                        connectThread.start();
                    }
                }
                mHandler.postDelayed(mRunnable, 100);
            }
        };
        mHandler.postDelayed(mRunnable, 100);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    public class BTConnectorThread extends Thread {
        String macAddr;
        Handler mHandler;
        int connetTime = 0;
        String TAG = "BTSocket";
        public btIO btIOAdapter;
        public BTConnectorThread(String macAddr, Handler  mHandler){
            this.macAddr = macAddr;
            this.mHandler = mHandler;
        }
        public void run(){
            connecting = true;
            connection = false;
            if(mBTAdapter == null){
                mBTAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            mBTDevice = mBTAdapter.getRemoteDevice(this.macAddr);
            mBTAdapter.cancelDiscovery();
            try{
                mBTSocket = mBTDevice.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
            }catch (IOException e){
                System.out.println("BTSocket Connect Error:"+e.toString());
            }
            try {
                mBTSocket.connect();
                //DisplayMessage("杩炴帴鎴愬姛!");
                //connetTime++;
                connection = true;
            } catch (IOException e) {
                //DisplayMessage("杩炴帴澶辫触锛�");
                connetTime++;
                connection = false;
                try {
                    mBTSocket.close();
                    mBTSocket = null;
                } catch (IOException e2) {
                    Log.e(TAG, "Cannot close connection when connection failed");
                }
            } finally {
                connecting = false;
            }
            if (connection == true){
                mainHandlerRun.run();
            }
        }

        Runnable mainHandlerRun = new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.arg1 = 1;
                mHandler.sendMessage(msg);
            }
        };
    }
}
