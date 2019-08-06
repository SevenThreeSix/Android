//package freemouse.itsxld.com.lightcontroll;
package com.example.bluetoothuart;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Duan on 2017/2/23.
 */

public class btIO extends Thread{
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    public btIO(BluetoothSocket mSocket){
        this.mSocket = mSocket;
        try{
            this.mInputStream = mSocket.getInputStream();
            this.mOutputStream = mSocket.getOutputStream();
        }catch (IOException e){
            System.out.println("error");
        }
    }

    public void run(){

    }

    public boolean wirte(byte[] buffer){
        try{
            mOutputStream.write(buffer);
        }catch (IOException e){
            return false;
        }
        return true;
    }
}
