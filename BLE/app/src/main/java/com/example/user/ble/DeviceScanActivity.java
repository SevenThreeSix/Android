package com.example.user.ble;
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.app.ListActivity;
import android.graphics.Color;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
//import com.example.user.ble.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.example.user.ble.iBeaconClass;
import com.example.user.ble.BluetoothLeClass.OnDataAvailableListener;
import com.example.user.ble.BluetoothLeClass.OnServiceDiscoverListener;
import com.example.user.ble.iBeaconClass.iBeacon;
import com.example.user.ble.R;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
// public class DeviceScanActivity extends ListActivity implements
// View.OnClickListener
public class DeviceScanActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static String[] PERMISSIONS_LOCATION_ARRAY = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION = 2;

    private final static String TAG = "DeviceScanActivity";// DeviceScanActivity.class.getSimpleName();
    public static final int REFRESH = 0x000001;
    private final static int REQUEST_CODE = 1;

    public static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR1 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR2 = "0000fff2-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR3 = "0000fff3-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR4 = "0000fff4-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR5 = "0000fff5-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR6 = "0000fff6-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR7 = "0000fff7-0000-1000-8000-00805f9b34fb";
    public static String UUID_HERATRATE = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String UUID_TEMPERATURE = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static String UUID_0XFFA6 = "0000ffa6-0000-1000-8000-00805f9b34fb";

    static BluetoothGattCharacteristic gattCharacteristic_char1 = null;
    static BluetoothGattCharacteristic gattCharacteristic_char5 = null;
    static BluetoothGattCharacteristic gattCharacteristic_char6 = null;
    static BluetoothGattCharacteristic gattCharacteristic_heartrate = null;
    static BluetoothGattCharacteristic gattCharacteristic_keydata = null;
    static BluetoothGattCharacteristic gattCharacteristic_temperature = null;
    static BluetoothGattCharacteristic gattCharacteristic_0xffa6 = null;

    private LeDeviceListAdapter mLeDeviceListAdapter = null;
    // 搜索BLE终端
    private BluetoothAdapter mBluetoothAdapter;
    // 读写BLE终端
    static private BluetoothLeClass mBLE;
    public String bluetoothAddress;
    static private byte writeValue_char1 = 0;
    private boolean mScanning;
    private Button btn;

    private MyThread mythread = null;
    private byte color = 0;

    private int mRssi;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;//60*60*24*30;  //最长扫描30天
    /**
     * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
     */
    private static  final  int  REQUEST_CODE_BLUETOOTH_ON = 1313;
    /**
     * Bluetooth 设备可见时间，单位：秒。
     */
    private static final int  BLUETOOTH_DISCOVERABLE_DURATION = 250;
    private SwipeRefreshLayout refreshLayout;
    private ProgressDialog mProgress;
    BluetoothLeScanner mBluetoothLeScanner;
/*    Handler handler;
    Runnable runnable;*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getActionBar().setTitle(R.string.title_devices);
        MyFragment fragment = new MyFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();*/
        //setContentView(R.layout.item_list_device_content);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_device_scan);
        // getActionBar().setTitle(R.string.title_devices);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(this); // 设置刷新监听
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark); // 进度动画颜色R.color.green, R.color.purple,R.color.swipefefresh_bg

        getActionBar().setDisplayHomeAsUpEnabled(true);
        // findViewById(R.id.button_new_encoder).setOnClickListener(this);

        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
        } else {
            Log.i(TAG, "initialize Bluetooth, has BLE system");
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        /*final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            Log.i(TAG, "mBluetoothAdapter = " + mBluetoothAdapter);
        }*/
        //BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        //mBluetoothAdapter = bluetoothManager.getAdapter();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        // Initializes Bluetooth adapter.

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            //Toast.makeText(this, "打开蓝牙", Toast.LENGTH_SHORT).show();
            this.turnOnBluetooth();
        }else
            checkPermissionLocation();

        // ????蓝牙
        //mBluetoothAdapter.enable();
        //Log.i(TAG, "mBluetoothAdapter.enable");

        mBLE = new BluetoothLeClass(this);
        if (!mBLE.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
        }
        Log.i(TAG, "mBLE = " + mBLE);

        // 发现BLE终端的Service时回??
        mBLE.setOnServiceDiscoverListener(mOnServiceDiscover);

        // 收到BLE终端数据交互的事??
        mBLE.setOnDataAvailableListener(mOnDataAvailable);

        //new MyThread().start();
        //handler=new Handler();
    }


    private void Scan()
    {

        Log.e(TAG," Scan()->Enter");
        if (mBluetoothAdapter.isEnabled()) {
            Log.e(TAG," Scan()->mBluetoothAdapter.isEnabled");
            /*mProgress = new ProgressDialog(this);
            mProgress.setMessage("正在搜索周围设备...");
            mProgress.setCancelable(false);
            mProgress.show();*/
            //newtext.setText("蓝牙已经打开，关闭蓝牙请在设置中设置");
            //mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
// Initializes list view adapter.
            if(mLeDeviceListAdapter != null) {
                Log.e(TAG, " Scan()->mLeDeviceListAdapter Not Null");
                mLeDeviceListAdapter.clear();
            }
            mLeDeviceListAdapter = new LeDeviceListAdapter(this);
            setListAdapter(mLeDeviceListAdapter);

            //显示dialog
           /* new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        if (mProgress != null) {
                            mProgress.dismiss();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/
            //mLeDeviceListAdapter.clearDevice();
            scanLeDevice(true);
        }
    }


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        int count = 0;
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REFRESH) {
                /*count++;
                if (count == 0)
                    getActionBar().setTitle("正在拼命扫描设备中.");
                else if (count == 1)
                    getActionBar().setTitle("正在拼命扫描设备中..");
                else if (count == 2)
                    getActionBar().setTitle("正在拼命扫描设备中...");
                else if (count == 3)
                    getActionBar().setTitle("正在拼命扫描设备中....");
                else {
                    count = 0;
                    getActionBar().setTitle("正在拼命扫描设备中.....");

                    // 读取uuid
                    // if(mBLE != null)
                    // {
                    // if(mBLE.mBluetoothGatt != null)
                    // {
                    // if(mBLE.mBluetoothGatt.connect())
                    // {
                    // mBLE.mBluetoothGatt.readRemoteRssi();
                    // }
                    // }
                    // }
                }*/
            }
            super.handleMessage(msg);
        }
    };

    private void turnOnBluetooth()
    {
        /*if (mBluetoothAdapter != null)
        {
            return mBluetoothAdapter.enable();
        }
        return false;*/
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        //requestBluetoothOn.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);//默认120s
        // 设置 Bluetooth 设备可见时间
        //requestBluetoothOn.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_DISCOVERABLE_DURATION);
        // 请求开启 Bluetooth
        this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
    }
    private void checkPermissionLocation() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION_ARRAY, REQUEST_LOCATION);

        } else {
            Toast.makeText(this, "定位权限已授权成功！", Toast.LENGTH_SHORT).show();
            Log.e("", "checkPermissionLocation()定位权限已经授权！");
            scanLeDevice(false);
            Scan();
        }
    }
    protected void  onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        Toast.makeText(this,"进入回调,resultCode:"+resultCode, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "进入回调" );
        Log.i(TAG, "requestCode:" +requestCode);
        Log.i(TAG, "resultCode:" +resultCode);
        if (requestCode == REQUEST_CODE_BLUETOOTH_ON) {
            switch (resultCode) {
                // 点击确认按钮
                case Activity.RESULT_OK: {
                    // TODO 用户选择开启 Bluetooth，Bluetooth 会被开启
                    Toast.makeText(this,"蓝牙开启", Toast.LENGTH_SHORT).show();
                    checkPermissionLocation();
                    /*mLeDeviceListAdapter = new LeDeviceListAdapter(this);
                    setListAdapter(mLeDeviceListAdapter);
                    scanLeDevice(true);*/
                    //Scan();
                    /*Intent intent = new Intent();
                    intent.setClass(DeviceScanActivity.this, DeviceScanActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);*/
                }
                break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED: {
                    // TODO 用户拒绝打开 Bluetooth, Bluetooth 不会被开启
                    Toast.makeText(this,"已取消打开蓝牙", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this,"使用此功能，请打开蓝牙", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
                default:
                    break;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i("requestCode",String.valueOf(requestCode));
        Log.i("grantResultsLength",String.valueOf(grantResults.length));
//        Log.i("grantResults[0]",String.valueOf(grantResults[0]));
        switch(requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("-------->", "授权位置请求被允许");
                    Toast.makeText(this, "定位权限授权成功！", Toast.LENGTH_SHORT).show();
                    //this.turnOnBluetooth();
                    //mBluetoothAdapter.
                    scanLeDevice(false);
                    Scan();
                    /*mLeDeviceListAdapter = new LeDeviceListAdapter(this);
                    setListAdapter(mLeDeviceListAdapter);
                    scanLeDevice(true);*/
                    /*Intent intent = new Intent();
                    intent.setClass(DeviceScanActivity.this, DeviceScanActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);*/
                } else {
                    Log.e("-------->", "授权位置请求被拒绝");
                    finish();
                }
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();

            return true;
        }
        //noinspection SimplifiableIfStatement
       /* if (id == R.id.menu_refresh) {
            Toast.makeText(getApplicationContext(), "重新加载Activity", Toast.LENGTH_SHORT).show();
            //Scan();
            Intent intent = new Intent();
            intent.setClass(DeviceScanActivity.this, DeviceScanActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

        }
        if (id == R.id.menu_scan) {
            Toast.makeText(getApplicationContext(), "开始扫描", Toast.LENGTH_SHORT).show();
            scanLeDevice(true);
        }*/
        if (id == R.id.menu_stop) {
            Toast.makeText(getApplicationContext(), "停止扫描", Toast.LENGTH_SHORT).show();
            //handler.removeCallbacks(runnable);
            scanLeDevice(false);
        }

        return super.onOptionsItemSelected(item);
    }

    static public void writeChar1() {
        byte[] writeValue = new byte[1];
        Log.i(TAG, "gattCharacteristic_char1 = " + gattCharacteristic_char1);
        if (gattCharacteristic_char1 != null) {
            writeValue[0] = writeValue_char1++;
            Log.i(TAG, "gattCharacteristic_char1.setValue writeValue[0] ="
                    + writeValue[0]);
            boolean bRet = gattCharacteristic_char1.setValue(writeValue);
            mBLE.writeCharacteristic(gattCharacteristic_char1);
        }
    }

    static public void writeChar6(String string) {
        // byte[] writeValue = new byte[1];
        Log.i(TAG, "gattCharacteristic_char6 = " + gattCharacteristic_char6);
        if (gattCharacteristic_char6 != null) {
            // writeValue[0] = writeValue_char1++;
            // Log.i(TAG, "gattCharacteristic_char6.setValue writeValue[0] =" +
            // writeValue[0]);
            // byte[] writebyte = new byte[4];

            boolean bRet = gattCharacteristic_char6.setValue(string.getBytes());
            mBLE.writeCharacteristic(gattCharacteristic_char6);
        }
    }

    // 字节发送
    static public void writeChar6_in_bytes(byte bytes[]) {
        // byte[] writeValue = new byte[1];
        Log.i(TAG, "gattCharacteristic_char6 = " + gattCharacteristic_char6);
        if (gattCharacteristic_char6 != null) {
            boolean bRet = gattCharacteristic_char6.setValue(bytes);
            mBLE.writeCharacteristic(gattCharacteristic_char6);
        }
    }

    static public void read_char1() {
        byte[] writeValue = new byte[1];
        Log.i(TAG, "readCharacteristic = ");
        if (gattCharacteristic_char1 != null) {
            mBLE.readCharacteristic(gattCharacteristic_char1);
        }
    }

    static public void read_uuid_0xffa6() {
        Log.i(TAG, "readCharacteristic = ");
        if (gattCharacteristic_0xffa6 != null) {
            mBLE.readCharacteristic(gattCharacteristic_0xffa6);
        }
    }

    @Override
    public void onRefresh() {
        scanLeDevice(false);
        Log.e(TAG,"clear之前mLeDeviceListAdapter.getCount()"+mLeDeviceListAdapter.getCount());
        mLeDeviceListAdapter.clear();
        Log.e(TAG,"clear之后mLeDeviceListAdapter.getCount()"+mLeDeviceListAdapter.getCount());
        //mLeDeviceListAdapter.clearDevice();
        //mBLE.disconnect();//BluetoothAdapter not initialized

        Toast.makeText(getApplicationContext(), "开始搜索蓝牙设备", Toast.LENGTH_SHORT).show();
        //mBLE = new BluetoothLeClass(this);

        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
        Log.e(TAG,"适配器之后mLeDeviceListAdapter.getCount()"+mLeDeviceListAdapter.getCount());
        //Toast.makeText(getApplicationContext(), "重新加载", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*Intent intent = new Intent();
                intent.setClass(DeviceScanActivity.this, DeviceScanActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);*/

                // 加载完数据设置为不刷新状态，将下拉进度收起来
                refreshLayout.setRefreshing(false);
                scanLeDevice(false);
            }
        }, 15000);

    }

    public class MyThread extends Thread {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                Message msg = new Message();
                msg.what = REFRESH;
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void DisplayStart() {
        Log.i(TAG, "DisplayStart+++");

        if (mythread == null) {
            mythread = new MyThread();
            mythread.start();
            // mythread.setThread(true);
        } else {
            // mythread.setThread(true);
        }
    }

    public void DisplayStop() {
        if (mythread != null) {
            // mythread.setThread(false);
            // delay(3000);
        }
        Log.i(TAG, "DisplayStop---");
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "---> onResume");
        super.onResume();
        //mBLE.close();
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        setListAdapter(mLeDeviceListAdapter);
        //mScanning = false;
        //mBluetoothAdapter.stopLeScan(mLeScanCallback);
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "---> onPause");
        super.onPause();
        //跳转时会调用，所以这部分注释掉
        //scanLeDevice(false);
        //mLeDeviceListAdapter.clear();
        //mBLE.disconnect();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "---> onStop");
        super.onStop();
        DisplayStop();
        // mBLE.close();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "---> onDestroy");
        super.onDestroy();
        Log.e(TAG, "start onDestroy~~~");
        scanLeDevice(false);
        mBLE.disconnect();
        mBLE.close();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final iBeacon device = mLeDeviceListAdapter.getDevice(position);
        if (device == null)
            return;

        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }

        Log.i(TAG, "mBluetoothAdapter.enable");
        bluetoothAddress = device.bluetoothAddress;
        boolean bRet = mBLE.connect(device.bluetoothAddress);

        Log.i(TAG, "connect bRet = " + bRet);

        Toast toast = Toast.makeText(getApplicationContext(), "正在连接设备并获取服务中",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            Handler handler=new Handler();
            // Stops scanning after a pre-defined scan period.
            /*runnable=new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            };
            if (handler == null)
                handler=new Handler();
            handler.postDelayed(runnable, SCAN_PERIOD);*/
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                    Toast.makeText(getApplicationContext(), "stopLeScan", Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    /**
     * 搜索到BLE终端服务的事??
     */
    private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new OnServiceDiscoverListener() {

        @Override
        public void onServiceDiscover(BluetoothGatt gatt) {
            displayGattServices(mBLE.getSupportedGattServices());
        }

    };

    /**
     * 收到BLE终端数据交互的事??
     */
    private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new OnDataAvailableListener() {
        /**
         * BLE终端数据被读的事??
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            // 执行 mBLE.readCharacteristic(gattCharacteristic); 后就会收到数?? if
            // (status == BluetoothGatt.GATT_SUCCESS)
            Log.e(TAG,
                    "onCharRead " + gatt.getDevice().getName() + " read "
                            + characteristic.getUuid().toString() + " -> "
                            + Utils.bytesToHexString(characteristic.getValue()));

            DisplayMainActivity.char6_display(Utils.bytesToString(characteristic
                    .getValue()), characteristic.getValue(), characteristic
                    .getUuid().toString());
        }

        /**
         * 收到BLE终端写入数据回调
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, "onCharWrite " + gatt.getDevice().getName() + " write "
                    + characteristic.getUuid().toString() + " -> "
                    + new String(characteristic.getValue()));

            // OtherActivity.char6_display(Utils.bytesToHexString(characteristic.getValue()));

            DisplayMainActivity.char6_display(Utils.bytesToString(characteristic
                    .getValue()), characteristic.getValue(), characteristic
                    .getUuid().toString());
        }
    };

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {

            final iBeacon ibeacon = iBeaconClass.fromScanData(device, rssi,
                    scanRecord);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(ibeacon);
                    mLeDeviceListAdapter.notifyDataSetChanged();

                    // 发现小米3必须加以下的这3个语句，否则不更新数据，而三星的机子s3则没有这个问题
                    if (mScanning == true) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    }
                }
            });

            // rssi
            Log.i(TAG, "rssi = " + rssi);
            Log.i(TAG, "mac = " + device.getAddress());
            Log.i(TAG, "scanRecord.length = " + scanRecord.length);
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        BluetoothGattCharacteristic Characteristic_cur = null;

        for (BluetoothGattService gattService : gattServices) {
            // -----Service的字段信??----//
            int type = gattService.getType();
            Log.e(TAG, "-->service type:" + Utils.getServiceType(type));
            Log.e(TAG, "-->includedServices size:"
                    + gattService.getIncludedServices().size());
            Log.e(TAG, "-->service uuid:" + gattService.getUuid());

            // -----Characteristics的字段信??----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());

                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG,
                        "---->char permission:"
                                + Utils.getCharPermission(permission));

                int property = gattCharacteristic.getProperties();
                Log.e(TAG,
                        "---->char property:"
                                + Utils.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    Log.e(TAG, "---->char value:" + new String(data));
                }

                if (gattCharacteristic.getUuid().toString().equals(UUID_CHAR5)) {
                    gattCharacteristic_char5 = gattCharacteristic;
                }

                if (gattCharacteristic.getUuid().toString().equals(UUID_CHAR6)) {
                    // 把char1 保存起来??以方便后面读写数据时使用
                    gattCharacteristic_char6 = gattCharacteristic;
                    Characteristic_cur = gattCharacteristic;
                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
                    Log.i(TAG, "+++++++++UUID_CHAR6");
                }

                if (gattCharacteristic.getUuid().toString()
                        .equals(UUID_HERATRATE)) {
                    // 把heartrate 保存起来??以方便后面读写数据时使用
                    gattCharacteristic_heartrate = gattCharacteristic;
                    Characteristic_cur = gattCharacteristic;
                    // 接受Characteristic被写的????收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
                    Log.i(TAG, "+++++++++UUID_HERATRATE");
                }

                if (gattCharacteristic.getUuid().toString()
                        .equals(UUID_KEY_DATA)) {
                    // 把heartrate 保存起来??以方便后面读写数据时使用
                    gattCharacteristic_keydata = gattCharacteristic;
                    Characteristic_cur = gattCharacteristic;
                    // 接受Characteristic被写的????收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
                    Log.i(TAG, "+++++++++UUID_KEY_DATA");
                }

                if (gattCharacteristic.getUuid().toString()
                        .equals(UUID_TEMPERATURE)) {
                    // 把heartrate 保存起来??以方便后面读写数据时使用
                    gattCharacteristic_temperature = gattCharacteristic;
                    Characteristic_cur = gattCharacteristic;
                    // 接受Characteristic被写的????收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
                    Log.i(TAG, "+++++++++UUID_TEMPERATURE");
                }


                if (gattCharacteristic.getUuid().toString()
                        .equals(UUID_0XFFA6)) {
                    // 把heartrate 保存起来??以方便后面读写数据时使用
                    gattCharacteristic_0xffa6 = gattCharacteristic;
                    Characteristic_cur = gattCharacteristic;
                    Log.i(TAG, "+++++++++UUID_0XFFA6");
                }

                // -----Descriptors的字段信??----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic
                        .getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(TAG,
                            "-------->desc permission:"
                                    + Utils.getDescPermission(descPermission));

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
                        Log.e(TAG, "-------->desc value:" + new String(desData));
                    }
                }
            }
        }//

        Intent intent = new Intent();
        intent.setClass(DeviceScanActivity.this, DisplayMainActivity.class);
        intent.putExtra("mac_addr", bluetoothAddress);
        intent.putExtra("char_uuid", Characteristic_cur.getUuid().toString());
        intent.putExtra("flag", true);
        startActivityForResult(intent, REQUEST_CODE);


        // startActivity(new Intent (DeviceScanActivity.this,
        // AmoComActivity.class) );

    }

    // public void onClick(View v)
    // {
    // switch(v.getId())
    // {
    // case R.id.button_new_encoder:
    // break;
    // }
    // }

}
