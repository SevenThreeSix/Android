package com.example.blootoothuart02;

import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ListView BTDeivicesList;
	ImageView refreshBtn;
	Handler deviceLoadHandler;
    String deviceAddr = "";
    String deviceName = "";
    BTConnectorThread connectThread = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //Window window=MainActivity.this.getWindow();
        //window.setFlags(flag, flag);
        setContentView(R.layout.activity_main);

        BTDeivicesList = (ListView) findViewById(R.id.BTDeviceList);
        refreshBtn = (ImageView) findViewById(R.id.refreshBtn);
        
        Handler randomLightsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //new btIO(mBTSocket).wirte(msg.getData().getString("s").getBytes());
            }
        };

        //myRandomlightThread = new randomLightThread(mBTSocket, leds, randomLightsHandler);
        //myRandomlightThread.start();
        
        loadBTDevices();
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBTDevices();
            }
        });
    }
    public void loadBTDevices(){
        deviceLoadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                deviceName = msg.getData().getString("deviceName");
                deviceAddr = msg.getData().getString("deviceAddr");
                TextView tv = (TextView) findViewById(R.id.deviceShow);
                tv.setText("Device Choosen:"+msg.getData().getString("deviceName"));
                //Intent tmpService = new Intent(getBaseContext(), randomLightsService.class);
                Bundle data = new Bundle();
                data.putString("deviceAddr", deviceAddr);
                //tmpService.putExtras(data);
                //final Intent service = tmpService;
                /*randomLightsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean turn = false;
                        int times = 0;
                        if(!turn){
                            startService(service);
//                    handler.removeCallbacks(runnable);
                            turn = false;
                        }else{
                            stopService(service);
                            turn = true;
                        }
                    }
                });*/
                connectThread = new BTConnectorThread(deviceAddr, BTConnectHandler);
                connectThread.start();
                Toast.makeText(getBaseContext(), "Connecting, wait please...", Toast.LENGTH_SHORT).show();
                tv.setText("Connecting, wait please...");

            }
        };
        new fillBtListThread(getBaseContext(), BTDeivicesList, deviceLoadHandler).start();

    }
    



}
