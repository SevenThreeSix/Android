package freemouse.itsxld.com.lightcontroll;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends Activity {

    ImageView led1, led2, led3, led4;
    View.OnClickListener ledProcessor;
    ListView BTDeivicesList;
    Button randomLightsBtn;
    Handler deviceLoadHandler, BTConnectHandler;
    ImageView refreshBtn;
    LinearLayout ledLayout, mainLayout;
    Boolean connecting = false;
    Boolean connection = false;
    String deviceAddr = "";
    String deviceName = "";
    BluetoothAdapter mBTAdapter = null;
    BluetoothSocket mBTSocket = null;
    BluetoothDevice mBTDevice = null;;
    BTConnectorThread connectThread = null;
    randomLightThread myRandomlightThread = null;
    String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    randomLightsService mRandomLightsService;
    public final int BT_CONNECTED = 1;
    int btState = 0;

    /*
    * Bluetooth state
    * */
    final int BTSTATE_DIS = 0;
    final int BTSTATE_EN = 1;
    int BTSTATE = BTSTATE_DIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window=MainActivity.this.getWindow();
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_main);

        BTDeivicesList = (ListView) findViewById(R.id.BTDeviceList);
        ledLayout = (LinearLayout) findViewById(R.id.leds);
        refreshBtn = (ImageView) findViewById(R.id.refreshBtn);
        randomLightsBtn = (Button) findViewById(R.id.randomLightsBtn);
        led1 = (ImageView) findViewById(R.id.led1);
        led2 = (ImageView) findViewById(R.id.led2);
        led3 = (ImageView) findViewById(R.id.led3);
        led4 = (ImageView) findViewById(R.id.led4);

        led1.setTag(new ledBean());
        led2.setTag(new ledBean());
        led3.setTag(new ledBean());
        led4.setTag(new ledBean());

        ImageView[] leds = {led1, led2, led3, led4};
        Handler randomLightsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                new btIO(mBTSocket).wirte(msg.getData().getString("s").getBytes());
            }
        };

        myRandomlightThread = new randomLightThread(mBTSocket, leds, randomLightsHandler);
        myRandomlightThread.start();

        ledProcessor = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView tmpImageView = (ImageView) v;
                ledBean bean = (ledBean) v.getTag();
                if (bean.ledState == 1){
                    tmpImageView.setImageResource(R.drawable.light_off);
                    ((ledBean) v.getTag()).ledState = 0;
                }else{
                    tmpImageView.setImageResource(R.drawable.light_on);
                    ((ledBean) v.getTag()).ledState = 1;
                }
                byte[] bytes = null;
                String sendConstruct = "";
                sendConstruct += "1";
                switch (v.getId()){
                    case R.id.led1:
                        sendConstruct += "1";
                        break;
                    case R.id.led2:
                        sendConstruct += "2";
                        break;
                    case R.id.led3:
                        sendConstruct += "3";
                        break;
                    case R.id.led4:
                        sendConstruct += "4";
                        break;
                }
                sendConstruct += (bean.ledState == 1)?"1":"0";
                sendConstruct += "&";
                bytes = sendConstruct.getBytes();
                new btIO(mBTSocket).wirte(bytes);
            }
        };
        led1.setOnClickListener(ledProcessor);
        led2.setOnClickListener(ledProcessor);
        led3.setOnClickListener(ledProcessor);
        led4.setOnClickListener(ledProcessor);

        loadBTDevices();
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBTDevices();
            }
        });

        BTConnectHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                TextView tv = (TextView) findViewById(R.id.deviceShow);
                tv.setText("Device Choosen:"+msg.getData().getString("deviceName"));
                if (msg.arg1 == 1){
                    Toast.makeText(getBaseContext(), "OK, Connected successfully!", Toast.LENGTH_SHORT).show();
                    tv.setText("Connected! device:"+deviceName);
                    BTDeivicesList.setVisibility(View.GONE);
                    refreshBtn.setVisibility(View.GONE);
                    ledLayout.setVisibility(View.VISIBLE);
                    randomLightsBtn.setVisibility(View.VISIBLE);
                    //mainLayout.setOrientation(LinearLayout.HORIZONTAL);
                    //ledLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
                }else {
                    Toast.makeText(getBaseContext(), "Terrible, error(s) occurred! \nPlease fix your bluetooth device.", Toast.LENGTH_SHORT).show();
                    tv.setText("Error, the device you choose may be too far from your mobile phone");
                }
            }
        };

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
                Intent tmpService = new Intent(getBaseContext(), randomLightsService.class);
                Bundle data = new Bundle();
                data.putString("deviceAddr", deviceAddr);
                tmpService.putExtras(data);
                final Intent service = tmpService;
                randomLightsBtn.setOnClickListener(new View.OnClickListener() {
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
                });
                connectThread = new BTConnectorThread(deviceAddr, BTConnectHandler);
                connectThread.start();
                Toast.makeText(getBaseContext(), "Connecting, wait please...", Toast.LENGTH_SHORT).show();
                tv.setText("Connecting, wait please...");

            }
        };
        new fillBtListThread(getBaseContext(), BTDeivicesList, deviceLoadHandler).start();

    }

    Long lastMS;
    Long currentMS;

    Long lastSendTime = new Date().getTime();
    View.OnTouchListener panelTouchLitener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Date currentTime = new Date();
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
            return true;
        }
    };


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
                //DisplayMessage("连接成功!");
                //connetTime++;
                connection = true;
            } catch (IOException e) {
                //DisplayMessage("连接失败！");
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
                btState = BT_CONNECTED;
                this.mHandler.post(mainHandlerRun);
                btIOAdapter = new btIO(mBTSocket);
                btIOAdapter.start();
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

