package com.example.blootoothuart;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Duan on 2017/2/22.
 */

public class randomLightThread extends Thread {
    private boolean RUN;
    Handler mainHanlder = new Handler();
    BluetoothSocket mSocket;
    Handler mHandler;
    ImageView led1, led2, led3, led4;
    Runnable runnable;
    ImageView[] leds;
    int times;
    public randomLightThread(BluetoothSocket mSocket, ImageView[] leds, Handler mHandler){
        this.mSocket = mSocket;
        this.RUN = false;
        this.leds = leds;
        this.times = 0;
        this.mHandler = mHandler;
    }
    public void stopLight(){
        this.RUN = false;
        this.stop();
    }
    public void startLight(){
        this.RUN = true;
        this.start();
    }
    public boolean getRunState(){
        return this.RUN;
    }
    public void turnLight(int id, int state){
        ImageView led = leds[id];
        ledBean tmpBean = (ledBean) led.getTag();
        tmpBean.ledState = state;
        if (state == 1){
            led.setImageResource(R.drawable.light_off);

        }else{
            led.setImageResource(R.drawable.light_on);

        }
    }
    @Override
    public void run() {
        //super.run();
         runnable = new Runnable() {
            @Override
            public void run() {
                int led = (int) (Math.random()*4);
                led += 1;
                int state = (int) (Math.random()*2);
                String construct = "1";
                construct += led;
                construct += state;
                construct += "&";
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("s", construct);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
                mainHanlder.postDelayed(runnable, 100);
            }
        };
        mainHanlder.postDelayed(runnable, 100);

//        while(RUN){
//            if (times == 4){
//                try {
//                    this.sleep(200);
//                    times = 0;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            times += 1;
//            if(times == 4){
//                mHandler.sendMessage(msg);
//            }
//        }
    }

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

            }
        }

        public void run(){
            byte[] buffer = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder();
            int bytes;
            while(true){
                try{
                    bytes = mInputStream.read(buffer);
                    stringBuilder.append(new String(buffer, 0, bytes));
                    if(stringBuilder.charAt(stringBuilder.length()-1) == '&'){
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        String strGet = stringBuilder.toString();
                        //do sth...
                        stringBuilder.delete(0, stringBuilder.length());
                    }
                }catch(IOException e){

                }
            }
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
}
