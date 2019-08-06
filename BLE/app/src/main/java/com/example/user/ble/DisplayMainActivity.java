package com.example.user.ble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import static com.example.user.ble.MyApplication.getContext;


/**
 * Created by User on 2018/5/5.
 */

public class DisplayMainActivity extends Activity implements View.OnClickListener{
    private final static String TAG = "DisplayMainActivity";
    private static boolean FLAG_FIRST_ENTER = false;
    private final String ACTION_NAME_RSSI = "AMOMCU_RSSI"; // 其他文件广播的定义必须一致
    private final String ACTION_CONNECT = "AMOMCU_CONNECT"; // 其他文件广播的定义必须一致

    private final static String str_spo2_edit_high_default = "100";
    private final static String str_spo2_edit_low_default = "85";
    private final static String str_heartRate_edit_high_default = "150";
    private final static String str_heartRate_edit_low_default = "50";
    private final static String str_pi_edit_high_default = "20";
    private final static String str_pi_edit_low_default = "0";

    static TextView Text_Recv;
    static TextView Text_Newest_Recv,nowTime;
    static TextView tv_SPo2,tv_bpmPR,tv_PI;
    static TextView tv_SPo2_boundary,tv_bpmPR_boundary,tv_PI_boundary;
    private static String str_spo2_edit_high,str_spo2_edit_low,str_heartRate_edit_high,str_heartRate_edit_low,str_pi_edit_high,str_pi_edit_low;

    static ProgressBar progressBar,progressBarTime;
    static ArrayList arrayList;
    static Integer lightValue;

    static String Str_Recv;

    Button button_setting,button_help,button_ble,button_record,button_start;
    ToggleButton button_alarm;

    static String ReciveStr;
    static ScrollView scrollView;
    static Handler mHandler;
    static boolean isAlrarmOn = true;
    static boolean ifDisplayTimeOnOff = true;
    static TextView textview_recive_send_info;
    static int Totol_Send_bytes = 0;
    static int Totol_recv_bytes = 0;
    static int Totol_recv_bytes_temp = 0;
    static String SendString = "0123";

    ToggleButton toggleHexStr;
    ToggleButton toggleTime;
    boolean if_use_hex_send = false;

    // 根据rssi 值计算距离， 只是参考作用， 不准确---amomcu
    static final int rssibufferSize = 10;
    int[] rssibuffer = new int[rssibufferSize];
    int rssibufferIndex = 0;
    boolean rssiUsedFalg = false;

    static MediaPlayer player;
    static boolean isAlarmSwitchOn_spo2,isAlarmSwitchOn_heartRate,isAlarmSwitchOn_pi;
    static boolean isAlarmSwitchOn_spo2_high,isAlarmSwitchOn_heartRate_high,isAlarmSwitchOn_pi_high,isAlarmSwitchOn_spo2_low,isAlarmSwitchOn_heartRate_low,isAlarmSwitchOn_pi_low;
    static boolean isNormal_spo2 ,isNormal_heartRate,isNormal_pi;

    static SimpleWaveform simpleWaveform ;
    Paint xAxisPencil = new Paint();
    RecyclerView recycler_view;

    LinearLayoutManager linearLayoutManager;
    static final LinkedList<Integer> ampList = new LinkedList<>();

    Paint barPencilFirst = new Paint();
    Paint barPencilSecond = new Paint();
    Paint peakPencilFirst = new Paint();
    Paint peakPencilSecond = new Paint();

    static MySQLiteOpenHelper helper;
    static Date nowDate;
    static SimpleDateFormat simpleDateFormat;

    boolean flag = true;
    int times = 0;
    CountDownTimer timer;
    int sumTime= 60;//sec
    static boolean startSqliteFlag = false;
    static int whatValue = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaymain);

        Intent intent = getIntent();

        FLAG_FIRST_ENTER = intent.getBooleanExtra("flag",false);

        mHandler = new Handler();
        //Str_Recv = new String();
        registerBoradcastReceiver();
        scrollView = (ScrollView) findViewById(R.id.scroll);

        Totol_recv_bytes = 0;
        Totol_recv_bytes_temp = 0;
        isNormal_spo2 = true;
        isNormal_heartRate = true;
        isNormal_pi = true;

        button_setting = (Button) findViewById(R.id.button_setting);
        findViewById(R.id.button_setting).setOnClickListener(this);
        button_alarm = (ToggleButton) findViewById(R.id.button_alarm);
        button_help = (Button) findViewById(R.id.button_help);
        findViewById(R.id.button_help).setOnClickListener(this);
        button_ble =(Button) findViewById(R.id.button_ble);
        findViewById(R.id.button_ble).setOnClickListener(this);
        button_record =(Button) findViewById(R.id.button_record);
        findViewById(R.id.button_record).setOnClickListener(this);
        button_start =(Button) findViewById(R.id.button_start);
        findViewById(R.id.button_start).setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarTime = (ProgressBar) findViewById(R.id.progressBarTime);
        progressBarTime.setProgress(100);

        tv_SPo2 = (TextView) findViewById(R.id.tv_spo2);
        tv_bpmPR = (TextView) findViewById(R.id.tv_bpmpr);
        tv_PI = (TextView) findViewById(R.id.tv_pi);
        tv_SPo2_boundary = (TextView) findViewById(R.id.tv_spo2_boundary);
        tv_bpmPR_boundary = (TextView) findViewById(R.id.tv_bpmpr_boundary);
        tv_PI_boundary = (TextView) findViewById(R.id.tv_pi_boundary);
        Text_Recv = (TextView) findViewById(R.id.device_address);
        Text_Recv.setVisibility(View.GONE);
        nowTime = (TextView) findViewById(R.id.nowTime);

        textview_recive_send_info = (TextView) this.findViewById(R.id.textview_recive_send_info);
        Text_Newest_Recv = (TextView) findViewById(R.id.newest_recieve);
        player = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        //音量调成最高
        player.setVolume(1.0f, 1.0f);
        //循环播放
        player.setLooping(true);
        //开始播放
        player.start();
        player.pause();

        button_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                Log.i(TAG, "onCheckedChanged  arg1= " + arg1);
                isAlrarmOn = arg1;
                if (isAlrarmOn == true) {
                    button_alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.alarm_on));
                    /*
                    player = MediaPlayer.create(getApplicationContext(),R.raw.alarm_down);
                    //音量调成最高
                    player.setVolume(1.0f,1.0f);
                    //循环播放
                    player.setLooping(true);
                    //开始播放
                    player.start();
                    */
                    player.setVolume(1.0f, 1.0f);
                    //player.start();
                } else {
                    player.setVolume(0, 0);
                    button_alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.alarm_off));
                    //player.pause();
                }
            }
        });

        getPreferenceBoundrayValue();
        getPreferenceSwitchValue();
        arrayList = new ArrayList<Integer>();
        simpleWaveform = (SimpleWaveform) findViewById(R.id.simplewaveform);
        //recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        simpleWaveform.setVisibility(View.VISIBLE);
        //recycler_view.setVisibility(View.GONE);
        initPaint();
        /*simpleWaveform.setVisibility(View.GONE);
        recycler_view.setVisibility(View.VISIBLE);
        demoAdvance2();*/
        //initPaint();
        //progressBar.setProgress(50);

        //创建数据库连接
        helper = new MySQLiteOpenHelper(this, "myData.db", null, 1);
        SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
        sqliteDatabase.execSQL("create table if not exists table_mydata(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "spO2 INTEGER(5),heartRate INTEGER(5),time VERCHAR(20),what INTEGER(11))");
        sqliteDatabase.close();


        /*ImageView img = new ImageView(DisplayMainActivity.this);
        img.setImageResource(R.drawable.image_ble_disconnect);
        Toast.makeText(getApplicationContext(), "已断开连接！", Toast.LENGTH_LONG).show();
        player.setVolume(0, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayMainActivity.this);
        //builder.setTitle("提示");
        builder.setView(img);

        builder.create().show();*/
        /*AlertDialog alertDialog = new AlertDialog.Builder(DisplayMainActivity.this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        alertDialog.show();
        alertDialog.setContentView(R.layout.alertdialog_layout);*/

        if(!FLAG_FIRST_ENTER){
            //FLAG_FIRST_ENTER=true;
            AlertDialog alertDialog = new AlertDialog.Builder(DisplayMainActivity.this).create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
            alertDialog.show();
            alertDialog.setContentView(R.layout.alertdialog_layout_first);
            //findViewById(R.id.dialog_image).setBackground(getResources().getDrawable(R.drawable.image_ble_connect));
        }

    }


    public void getPreferenceBoundrayValue()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器

        str_spo2_edit_high = sharedPreferences.getString("spo2_edit_high","");
        if(str_spo2_edit_high==null||str_spo2_edit_high.isEmpty()) {
            Log.e("str_spo2_edit_high_init",str_spo2_edit_high);
            editor.putString("spo2_edit_high", str_spo2_edit_high_default);
        }
        str_spo2_edit_low = sharedPreferences.getString("spo2_edit_low","");
        //tv_SPo2_boundary.setText(str_spo2_edit_low+"/"+str_spo2_edit_high);
        if(str_spo2_edit_low==null||str_spo2_edit_low.isEmpty())
            editor.putString("spo2_edit_low",str_spo2_edit_low_default);

        str_heartRate_edit_high = sharedPreferences.getString("heartRate_edit_high","");
        if(str_heartRate_edit_high==null||str_heartRate_edit_high.isEmpty()) {
            Log.e("str_heartRate_high_init",str_heartRate_edit_high);
            editor.putString("heartRate_edit_high", str_heartRate_edit_high_default);
        }
        str_heartRate_edit_low = sharedPreferences.getString("heartRate_edit_low","");
        if(str_heartRate_edit_low==null||str_heartRate_edit_low.isEmpty()) {
            Log.e("str_heartRate_low_init",str_heartRate_edit_low);
            editor.putString("heartRate_edit_low", str_heartRate_edit_low_default);
        }
        //tv_bpmPR_boundary.setText(str_heartRate_edit_low+"/"+str_heartRate_edit_high);

        str_pi_edit_high = sharedPreferences.getString("pi_edit_high","");
        if(str_pi_edit_high==null||str_pi_edit_high.isEmpty()) {
            Log.e("str_pi_edit_high_init",str_pi_edit_high);
            editor.putString("pi_edit_high", str_pi_edit_high_default);
        }
        str_pi_edit_low = sharedPreferences.getString("pi_edit_low","");
        if(str_pi_edit_low==null||str_pi_edit_low.isEmpty())
            editor.putString("pi_edit_low",str_pi_edit_low_default);
        //tv_PI_boundary.setText(str_pi_edit_low+"/"+str_pi_edit_high);
        editor.commit();//提交修改

    }

    private void getPreferenceSwitchValue(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isAlarmSwitchOn_spo2 = sharedPreferences.getBoolean("spo2_switch",true);
        Log.e("switch",String.valueOf(sharedPreferences.getBoolean("spo2_switch",true)));
        isAlarmSwitchOn_spo2_high = sharedPreferences.getBoolean("spo2_switch_high",true);
        isAlarmSwitchOn_spo2_low = sharedPreferences.getBoolean("spo2_switch_low",true);

        isAlarmSwitchOn_heartRate = sharedPreferences.getBoolean("heartRate_switch",true);
        isAlarmSwitchOn_heartRate_high = sharedPreferences.getBoolean("heartRate_switch_high",true);
        isAlarmSwitchOn_heartRate_low = sharedPreferences.getBoolean("heartRate_switch_low",true);


        isAlarmSwitchOn_pi = sharedPreferences.getBoolean("pi_switch",true);
        isAlarmSwitchOn_pi_high = sharedPreferences.getBoolean("pi_heartRate_switch_high",true);
        isAlarmSwitchOn_pi_low = sharedPreferences.getBoolean("pi_switch_low",true);

    }

    // 注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_NAME_RSSI);
        myIntentFilter.addAction(ACTION_CONNECT);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
    // 接收 rssi 的广播
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ACTION_NAME_RSSI)) {
                int rssi = intent.getIntExtra("RSSI", 0);

                // 以下这些参数我 amomcu 自己设置的， 不太具有参考意义，
                // 实际上我的本意就是根据rssi的信号前度计算以下距离，
                // 以便达到定位目的， 但这个方法并不准 ---amomcu---------20150411

                int rssi_avg = 0;
                int distance_cm_min = 10; // 距离cm -30dbm
                int distance_cm_max_near = 1500; // 距离cm -90dbm
                int distance_cm_max_middle = 5000; // 距离cm -90dbm
                int distance_cm_max_far = 10000; // 距离cm -90dbm
                int near = -72;
                int middle = -80;
                int far = -88;
                double distance = 0.0f;

                if (true) {
                    rssibuffer[rssibufferIndex] = rssi;
                    rssibufferIndex++;

                    if (rssibufferIndex == rssibufferSize)
                        rssiUsedFalg = true;

                    rssibufferIndex = rssibufferIndex % rssibufferSize;

                    if (rssiUsedFalg == true) {
                        int rssi_sum = 0;
                        for (int i = 0; i < rssibufferSize; i++) {
                            rssi_sum += rssibuffer[i];
                        }

                        rssi_avg = rssi_sum / rssibufferSize;

                        if (-rssi_avg < 35)
                            rssi_avg = -35;

                        if (-rssi_avg < -near) {
                            distance = distance_cm_min
                                    + ((-rssi_avg - 35) / (double) (-near - 35))
                                    * distance_cm_max_near;
                        } else if (-rssi_avg < -middle) {
                            distance = distance_cm_min
                                    + ((-rssi_avg - 35) / (double) (-middle - 35))
                                    * distance_cm_max_middle;
                        } else {
                            distance = distance_cm_min
                                    + ((-rssi_avg - 35) / (double) (-far - 35))
                                    * distance_cm_max_far;
                        }
                    }
                }

                /*getActionBar().setTitle(
                        "RSSI:" + rssi_avg + "dbm" + "," + "距离:"
                                + (int) distance + "cm");*/
            } else if (action.equals(ACTION_CONNECT)) {
                int status = intent.getIntExtra("CONNECT_STATUC", 0);
                if (status == 0) {
                    //getActionBar().setTitle("已断开连接");
                    ImageView img = new ImageView(DisplayMainActivity.this);
                    img.setBackground(getResources().getDrawable(R.drawable.image_ble_disconnect));
                    Toast.makeText(getApplicationContext(), "已断开连接！", Toast.LENGTH_LONG).show();
                    player.setVolume(0, 0);
                    AlertDialog alertDialog = new AlertDialog.Builder(DisplayMainActivity.this).create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                    alertDialog.show();
                    alertDialog.setContentView(R.layout.alertdialog_layout);
                    //findViewById(R.id.dialog_image).setBackground(getResources().getDrawable(R.drawable.image_ble_disconnect));
                } else {
                    //getActionBar().setTitle("已连接");
                }
            }
        }
    };

    public static synchronized void char6_display(String str, byte[] data,
                                                  String uuid) {
        Log.i(TAG, "char6_display str = " + str);

        if (uuid.equals(DeviceScanActivity.UUID_HERATRATE)) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            String TimeStr = formatter.format(curDate);
            byte[] ht = new byte[str.length()];
            // System.arraycopy(ht, Totol_Send_bytes,
            // Utils.hexStringToBytes(str), 0, str.length());

            String DisplayStr = "[" + TimeStr + "] " + "HeartRate : " + data[0]
                    + "=" + data[1];
            // Text_Recv.append(DisplayStr + "\r\n");
            Str_Recv = DisplayStr + "\r\n";
        } else if (uuid.equals(DeviceScanActivity.UUID_TEMPERATURE)) // 温度测量
        {
            byte[] midbytes = str.getBytes();
            String HexStr = Utils.bytesToHexString(midbytes);
            // Text_Recv.append(HexStr);
            Str_Recv = HexStr;
        } else if (uuid.equals(DeviceScanActivity.UUID_CHAR6)) // amomcu 的串口透传
        {

                String HexStr = Utils.bytesToHexString(data);
                Str_Recv = HexStr;


        } else // 默认显示 hex
        {
            byte[] midbytes = str.getBytes();
            String HexStr = Utils.bytesToHexString(midbytes);
            // Text_Recv.append(HexStr);
            Str_Recv = HexStr;
        }

        Totol_recv_bytes += str.length();
        Totol_recv_bytes_temp += str.length();

        mHandler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public synchronized void run() {
                //scrollView.fullScroll(ScrollView.FOCUS_DOWN);// 滚动到底
                Text_Recv.append(Str_Recv);
                Text_Newest_Recv.setText(Str_Recv);
                /*Text_Newest_Recv.setSingleLine(false);
                Text_Newest_Recv.setMaxLines(2);
                Text_Newest_Recv.setEllipsize(end);*/
                if(Str_Recv.startsWith("fe",0)) {
                    String str_spo2 = String.valueOf(Integer.parseInt(Str_Recv.substring(6, 8),16));
                    String str_bpmPR = String.valueOf(Integer.parseInt(Str_Recv.substring(8, 10),16));
                    String str_pi = String.valueOf(String.valueOf(Integer.parseInt(Str_Recv.substring(10, 12),16))+"."+
                            String.valueOf(Integer.parseInt(Str_Recv.substring(12, 14),16)));

                    tv_SPo2.setText(str_spo2);
                    Log.i("SPo2",str_spo2);
                    tv_bpmPR.setText(str_bpmPR);
                    tv_PI.setText(str_pi);

                    alramCheck(str_spo2,str_bpmPR,str_pi);


                    //setBarProgress();
                    /*if(isOutOfBoundrayfloat(str_pi,str_pi_edit_low,str_pi_edit_high)){
                        Log.e(TAG,str_pi+","+str_pi_edit_low+","+str_pi_edit_high);
                        player.start();
                        tv_PI.setTextColor(0xff0000);
                    }else{
                        Log.i(TAG,str_pi+","+str_pi_edit_low+","+str_pi_edit_high);
                        player.pause();
                    }*/

                    /*if(isOutOfBoundray(str_spo2,str_spo2_edit_low,str_spo2_edit_high) || isOutOfBoundray(str_bpmPR,str_heartRate_edit_low,str_heartRate_edit_high)
                            || isOutOfBoundrayfloat(str_pi,str_pi_edit_low,str_pi_edit_high)) {
                        //player = MediaPlayer.create(DisplayMainActivity.this.g,R.raw.alarm_down);
                        Log.e(TAG,str_spo2+","+str_spo2_edit_low+","+str_spo2_edit_high+"|"+
                                str_bpmPR+","+str_heartRate_edit_low+","+str_heartRate_edit_high+"|"+
                                str_pi+","+str_pi_edit_low+","+str_pi_edit_high+"|");
                        player.start();
                    }else {
                        Log.i(TAG,str_spo2+","+str_spo2_edit_low+","+str_spo2_edit_high+"|"+
                                str_bpmPR+","+str_heartRate_edit_low+","+str_heartRate_edit_high+"|"+
                                str_pi+","+str_pi_edit_low+","+str_pi_edit_high+"|");
                        player.pause();
                    }*/
                    setBarProgress();

                    simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    //获取当前时间
                    nowDate = new Date(System.currentTimeMillis());
                    String time = simpleDateFormat.format(nowDate);
                    //Toast.makeText(MyApplication.getContext(), time, Toast.LENGTH_SHORT).show();
                    nowTime.setText(time);
                    //Toast.makeText(MyApplication.getContext(), time, Toast.LENGTH_SHORT).show();

                    if(startSqliteFlag) {

                        Log.e(TAG,"时间:"+time);

                        //存sqlite数据
                        ContentValues cv = new ContentValues();
                        helper = new MySQLiteOpenHelper(getContext(), "myData.db", null, 1);
                        SQLiteDatabase sqliteDatabase_temp = helper.getWritableDatabase();

                        cv.put("spO2", str_spo2);
                        cv.put("heartRate", str_bpmPR);
                        cv.put("time", time);
                        cv.put("what",whatValue);
                        //判断里头是否有数据
                        /*Cursor cursor = sqliteDatabase_temp.rawQuery("select max(what) from table_mydata",null);
                        cursor.moveToFirst();
                        if(cursor.getInt(0) == 0)
                            cv.put("what",1);
                        else
                            cv.put("what",cursor.getInt(0)+1);*/
                        sqliteDatabase_temp.insert("table_mydata", null, cv);

                        cv.clear();
                        sqliteDatabase_temp.close();
                    }


                    //存sqlite数据
                    /*ContentValues cv = new ContentValues();
                    helper = new MySQLiteOpenHelper(MyApplication.getContext(), "myData.db", null, 1);
                    SQLiteDatabase sqliteDatabase_temp = helper.getWritableDatabase();
                    cv.put("spO2", str_spo2);
                    cv.put("heartRate", str_bpmPR);
                    cv.put("time", time);
                    sqliteDatabase_temp.insert("table_mydata", null, cv);

                    cv.clear();
                    sqliteDatabase_temp.close();*/

                }
                //Toast.makeText(MyApplication.getContext(), "Static toast！", Toast.LENGTH_SHORT).show();

                //ToastShow1.ToastShow1();
                //String temp = new String();
                //temp = Str_Recv;
                //tv_spo2.setText(Str_Recv.length());

                if (Totol_recv_bytes_temp > 10000) // 数据太多时清空数据
                {
                    Totol_recv_bytes_temp = 0;
                    Text_Recv.setText("");
                }

                update_display_recv_info(Totol_recv_bytes);
            }
        });
    }

    private static void setBarProgress()
    {
        lightValue = Integer.parseInt(Str_Recv.substring(16,18)+Str_Recv.substring(14, 16),16);
        Log.e(TAG,"setBarProgress() lightValue:"+Str_Recv.substring(16,18)+Str_Recv.substring(14, 16)+"|"+String.valueOf(lightValue));
        arrayList.add(lightValue);
        //arrayList
        //Integer lightValue = Integer.parseInt(Str_Recv.substring(16,18)+Str_Recv.substring(14, 16),16);
        if (arrayList.size() < 80) {
            //Log.i(TAG,"arraylist.size():"+arrayList.size());
            //arrayList.add(lightValue);
            Log.e(TAG,"还未缓存80个数据");
            Log.i(TAG,"arraylist.size():"+arrayList.size());
        } else {
            Log.e(TAG,"缓存满80个数据");
            Log.e(TAG, "arraylist.toString():" + arrayList.toString());
            Log.e(TAG, "arraylist.size():" + arrayList.size());

            int max = ArrayListMax(arrayList);
            int min = ArrayListMin(arrayList);
            //double result =max-min;

            Log.e(TAG,"max - min = "+String.valueOf(max)+" - "+String.valueOf(min)+" = "+String.valueOf(max-min));
            if(max != min)
            {
                Log.i(TAG,"Progress:"+(int)(((double)(lightValue-min))/(max-min)*100));
                progressBar.setProgress((int)(((double)(lightValue-min))/(max-min)*100));
                Paint((int)(((double)(lightValue-min))/(max-min)*100));
            }else{
                progressBar.setProgress(100);
                Paint(100);
            }
            arrayList.remove(0);
        }

        //progressBar.setProgress();
    }

    //获取ArrayList中的最大值

    public static int ArrayListMax(ArrayList sampleList)
    {
        Log.e(TAG,"ArrayListMax()");
        try
        {
            int maxDevation = 0;
            int totalCount = sampleList.size();
            if (totalCount >= 1)
            {
                int max = (int)sampleList.get(0);

                for (int i = 0; i < totalCount; i++)
                {
                    int temp = (int)sampleList.get(i);
                    if (temp > max)
                    {
                        max = temp;
                    }
                    Log.i(TAG,"max:"+max);
                } maxDevation = max;
            }
            return maxDevation;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    //获取ArrayList中的最小值

    public static int ArrayListMin(ArrayList sampleList)
    {
        try
        {
            int mixDevation = 0;
            int totalCount = sampleList.size();
            if (totalCount >= 1)
            {
                int min = (int)sampleList.get(0);
                for (int i = 0; i < totalCount; i++)
                {
                    int temp = (int)sampleList.get(i);
                    if (min > temp)
                    {
                        min = temp;
                    }
                    Log.i(TAG,"min:"+min);
                } mixDevation = min;
            }
            return mixDevation;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    private static void alramCheck(String str_spo2,String str_bpmPR,String str_pi)
    {
        Log.e(TAG,"alarmCheck()");
        isNormal_spo2 = true;isNormal_heartRate = true;isNormal_pi = true;
        //血氧量不正常警报
        if(isAlarmSwitchOn_spo2) {
            if(isAlarmSwitchOn_spo2_low){
                if(isLowOfBoundrayInt(str_spo2, str_spo2_edit_low)){
                    Log.e(TAG, str_spo2 + ",str_spo2_edit_low:" + str_spo2_edit_low);
                    //player.start();

                    if(Integer.valueOf(str_spo2)!=0) {
                        tv_SPo2.setTextColor(Color.parseColor("#00cc33"));
                        isNormal_spo2 = false;
                    }
                }
            }
            if(isAlarmSwitchOn_spo2_high){
                if(isOverOfBoundrayInt(str_spo2, str_spo2_edit_high)){
                    Log.e(TAG, str_spo2 + ",str_spo2_edit_high:"+str_spo2_edit_high);
                    //player.start();
                    tv_SPo2.setTextColor(Color.parseColor("#ff0000"));
                    isNormal_spo2 = false;
                }
            }

        }
        if(isNormal_spo2){
            Log.i(TAG, str_spo2+","+str_spo2_edit_low+","+str_spo2_edit_high);
            //player.pause();
            tv_SPo2.setTextColor(Color.parseColor("#ffffff"));
        }
        //心率不正常警报
        if(isAlarmSwitchOn_heartRate) {
            if(isAlarmSwitchOn_heartRate_low){
                if(isLowOfBoundrayInt(str_bpmPR, str_heartRate_edit_low)){
                    Log.e(TAG, str_bpmPR + ",str_heartRate_edit_low:" + str_heartRate_edit_low);
                    //player.start();

                    if(Integer.valueOf(str_bpmPR)!=0) {
                        tv_bpmPR.setTextColor(Color.parseColor("#00cc33"));
                        isNormal_heartRate = false;
                    }
                }
            }
            if(isAlarmSwitchOn_heartRate_high){
                if(isOverOfBoundrayInt(str_bpmPR, str_heartRate_edit_high)){
                    Log.e(TAG, str_bpmPR + ",str_heartRate_edit_high:"+str_heartRate_edit_high);
                    //player.start();
                    tv_bpmPR.setTextColor(Color.parseColor("#ff0000"));
                    isNormal_heartRate = false;
                }
            }

        }
        if(isNormal_heartRate){
            Log.i(TAG, str_bpmPR+","+str_heartRate_edit_low+","+str_heartRate_edit_high);
            //player.pause();
            tv_bpmPR.setTextColor(Color.parseColor("#ffffff"));
        }
        //PI不正常警报
        if(isAlarmSwitchOn_pi) {
            if(isAlarmSwitchOn_pi_low){
                if(isLowOfBoundrayDouble(str_pi, str_pi_edit_low)){
                    Log.e(TAG, str_pi + ",str_pi_edit_low:" + str_pi_edit_low);
                    //player.start();
                    tv_PI.setTextColor(Color.parseColor("#00cc33"));
                    isNormal_pi = false;
                }
            }
            if(isAlarmSwitchOn_pi_high){
                if(isOverOfBoundrayDouble(str_pi, str_pi_edit_high)){
                    Log.e(TAG, str_pi + ",str_pi_edit_high:"+str_pi_edit_high);
                    //player.start();

                    if(Integer.valueOf(str_pi)!=0) {
                        tv_PI.setTextColor(Color.parseColor("#ff0000"));
                        isNormal_pi = false;
                    }
                }
            }

        }
        if(isNormal_pi){
            Log.i(TAG, str_pi+","+str_pi_edit_low+","+str_pi_edit_high);
            //player.pause();
            tv_PI.setTextColor(Color.parseColor("#ffffff"));
        }
        if(isNormal_spo2 && isNormal_heartRate && isNormal_pi) {
            player.pause();
            //tv_SPo2.setTextColor(Color.parseColor("#000000"));
            //tv_bpmPR.setTextColor(Color.parseColor("#000000"));
            //tv_PI.setTextColor(Color.parseColor("#000000"));
        }
        else
            player.start();
    }

    private static boolean isLowOfBoundrayInt(String x,String low){
        if((Integer.valueOf(x)-Integer.valueOf(low)<0))
            return true;
        return false;
    }

    private static boolean isOverOfBoundrayInt(String x,String high) {
        if((Integer.valueOf(x)-Integer.valueOf(high)>0))
            return true;
        return false;
    }

    private static boolean isLowOfBoundrayDouble(String x,String low) {
        if((Double.valueOf(x)-Integer.valueOf(low)<0))
            return true;
        return false;
    }

    private static boolean isOverOfBoundrayDouble(String x,String high) {
        if((Double.valueOf(x)-Integer.valueOf(high)>0))
            return true;
        return false;
    }

    public synchronized static String GetLastData() {
        String string = Str_Recv;
        return string;
    }

    public synchronized static void update_display_recv_info(int recv) {
        String info1 = String.format("接收%4d [字节]", recv);
        textview_recive_send_info.setText(info1);
        //Toast.makeText(DisplayMainActivity, "定位权限已授权成功！", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_setting:
                startActivity(new Intent(DisplayMainActivity.this, SettingActivity.class));
                break;
            case R.id.button_help:
                //startActivity(new Intent(DisplayMainActivity.this, DeviceScanActivity.class));//HelpActivity
                startActivity(new Intent(DisplayMainActivity.this, ShowTableActivity.class));
                Toast.makeText(getApplicationContext(),"显示数据库部分数据",Toast.LENGTH_SHORT).show();
                //player.start();
                break;
            case R.id.button_ble:
                //startActivity(new Intent(DisplayMainActivity.this, ShowTableActivity.class));
                startActivity(new Intent(DisplayMainActivity.this, DeviceScanActivity.class));
                //player.start();
                break;
            case R.id.button_record:
                startActivity(new Intent(DisplayMainActivity.this, RecordActivity.class));
                //player.start();
                break;
            case R.id.button_start:
                Log.e(TAG,"flag:"+flag);
                if(flag)
                {
                    helper = new MySQLiteOpenHelper(getContext(), "myData.db", null, 1);
                    SQLiteDatabase sqliteDatabase_temp = helper.getWritableDatabase();
                    Cursor cursor = sqliteDatabase_temp.rawQuery("select max(what) from table_mydata",null);
                    cursor.moveToFirst();
                    if(cursor.getInt(0) == 0)
                        whatValue = 1;
                    else
                        whatValue= cursor.getInt(0)+1;
                    button_start.setBackground(getResources().getDrawable(R.drawable.button_stop_record));
                    //button_start.setText("结束录制");
                    startSqliteFlag =true;
                    flag = false;
                    timer = new CountDownTimer(sumTime*1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            times++;
                            //Toast.makeText(MyApplication.getContext(), String.valueOf(sumTime-times)+"s"+((sumTime-times)*100)/sumTime, Toast.LENGTH_SHORT).show();
                            progressBarTime.setProgress(((sumTime-times)*100)/sumTime);
                        }

                        @Override
                        public void onFinish() {
                            //button_start.setText("xml_button_start_record");
                            button_start.setBackground(getResources().getDrawable(R.drawable.button_start_record));
                            startSqliteFlag=false;
                            flag = true;
                            times = 0;
                            progressBarTime.setProgress(0);
                        }
                    }.start();
                }else
                {
                    startSqliteFlag=false;
                    //button_start.setText("xml_button_start_record");
                    button_start.setBackground(getResources().getDrawable(R.drawable.button_start_record));
                    flag = true;
                    times = 0;
                    if(timer != null)
                        timer.cancel();
                }
                //startActivity(new Intent(DisplayMainActivity.this, RecordActivity.class));
                //player.start();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume()");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        str_spo2_edit_high = sharedPreferences.getString("spo2_edit_high","");
        Log.e("str_spo2_edit_high",str_spo2_edit_high);
        str_spo2_edit_low = sharedPreferences.getString("spo2_edit_low","");
        tv_SPo2_boundary.setText(str_spo2_edit_low+"/"+str_spo2_edit_high);

        str_heartRate_edit_high = sharedPreferences.getString("heartRate_edit_high","");
        Log.e("str_heartRate_edit_high",str_heartRate_edit_high);
        str_heartRate_edit_low = sharedPreferences.getString("heartRate_edit_low","");
        Log.e("str_heartRate_edit_low",str_heartRate_edit_low);
        tv_bpmPR_boundary.setText(str_heartRate_edit_low+"/"+str_heartRate_edit_high);

        str_pi_edit_high = sharedPreferences.getString("pi_edit_high","");
        Log.e("str_pi_edit_high",str_pi_edit_high);
        str_pi_edit_low = sharedPreferences.getString("pi_edit_low","");
        tv_PI_boundary.setText(str_pi_edit_low+"/"+str_pi_edit_high);

        Log.e("pi_switch",String.valueOf(sharedPreferences.getBoolean("pi_switch",true)));
        getPreferenceSwitchValue();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause()");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy()");
    }

    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event)

    {

        if (keyCode == KeyEvent.KEYCODE_BACK)

        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("确认退出？");  builder.setTitle("提示");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    /*android.os.Process.killProcess(android.os.Process.myPid());
                    //System.exit(0);
                    finish();*/
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
                    i.addCategory(Intent.CATEGORY_HOME);
                    startActivity(i);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        return false;
    }
            @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG,"onBackPressed()");
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认退出？");  builder.setTitle("提示");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();*/
    }

    private int randomInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }



    private static void Paint(final int x) {

        ampList.addFirst(x);
        if (ampList.size() > simpleWaveform.width / simpleWaveform.barGap + 2) {
            ampList.removeLast();
            Log.d("", "SimpleWaveform: ampList remove last node, total " + ampList.size());
        }
        simpleWaveform.refresh();

    }

    private void initPaint()
    {
        simpleWaveform.init();

        simpleWaveform.setDataList(ampList);

        //define bar gap
        simpleWaveform.barGap = 40;

        //define x-axis direction
        simpleWaveform.modeDirection = SimpleWaveform.MODE_DIRECTION_LEFT_RIGHT;

        //define if draw opposite pole when show bars在显示栏时定义是否画对杆
        simpleWaveform.modeAmp = SimpleWaveform.MODE_AMP_ORIGIN;
        //define if the unit is px or percent of the view's height定义单位是px还是视图高度的百分比
        simpleWaveform.modeHeight = SimpleWaveform.MODE_HEIGHT_PERCENT;
        //define where is the x-axis in y-axis定义y轴的x轴
        simpleWaveform.modeZero = SimpleWaveform.MODE_ZERO_BOTTOM;
        //if show bars?
        simpleWaveform.showBar = false;

        //define how to show peaks outline定义如何显示峰值轮廓
        simpleWaveform.modePeak = SimpleWaveform.MODE_PEAK_ORIGIN;
        //if show peaks outline?显示峰值大纲?
        simpleWaveform.showPeak = true;

        //show x-axis
        simpleWaveform.showXAxis = false;
        xAxisPencil.setStrokeWidth(1);
        xAxisPencil.setColor(0x88ffffff);
        simpleWaveform.xAxisPencil = xAxisPencil;

        //define pencil to draw bar 定义铅笔画条
        barPencilFirst.setStrokeWidth(15);
        barPencilFirst.setColor(0xff1dcf0f);
        simpleWaveform.barPencilFirst = barPencilFirst;
        barPencilSecond.setStrokeWidth(15);
        barPencilSecond.setColor(0xff1dcfcf);
        simpleWaveform.barPencilSecond = barPencilSecond;

        //define pencil to draw peaks outline定义铅笔来绘制山峰轮廓
        peakPencilFirst.setStrokeWidth(5);
        peakPencilFirst.setColor(0xfffe2f3f);
        simpleWaveform.peakPencilFirst = peakPencilFirst;
        peakPencilSecond.setStrokeWidth(5);
        peakPencilSecond.setColor(0xfffeef3f);
        simpleWaveform.peakPencilSecond = peakPencilSecond;

        //the first part will be draw by PencilFirst 第一部分是用铅笔写的
        simpleWaveform.firstPartNum = 20;

        //define how to clear screen
        simpleWaveform.clearScreenListener = new SimpleWaveform.ClearScreenListener() {
            @Override
            public void clearScreen(Canvas canvas) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
        };
        simpleWaveform.progressTouch = new SimpleWaveform.ProgressTouch() {
            @Override
            public void progressTouch(int progress, MotionEvent event) {
                Log.d(TAG, "you touch at: " + progress);
                if(progress < 0)
                    progress=0;
                simpleWaveform.firstPartNum = progress;
                simpleWaveform.refresh();
            }
        };
    }

}
