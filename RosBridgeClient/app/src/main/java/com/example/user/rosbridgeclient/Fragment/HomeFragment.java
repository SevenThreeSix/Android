package com.example.user.rosbridgeclient.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.user.rosbridgeclient.BatteryView;
import com.example.user.rosbridgeclient.IStatus;
import com.example.user.rosbridgeclient.PublishEvent;
import com.example.user.rosbridgeclient.R;
import com.example.user.rosbridgeclient.RCApplication;
import com.example.user.rosbridgeclient.jilk.ros.rosbridge.ROSBridgeClient;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DecimalFormat;

import de.greenrobot.event.EventBus;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View rootView;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout refreshLayout;
    /*界面初始化*/
    private TextView DC_TextViewShowElectricPercentage;
    private BatteryView DC_Custom_Battery_view;
    private ImageButton DC_ImgBtn_Up;
    private ImageButton DC_ImgBtn_Down;
    private ImageButton DC_ImgBtn_Left;
    private ImageButton DC_ImgBtn_Right;
    private TextView tv_temperarture;
    /*ROS端的数据*/
    ROSBridgeClient client;
    String temperarture_value;

    private boolean Btn_LongPressUp = false;
    private boolean Btn_LongPressDown = false;
    private boolean Btn_LongPressLeft = false;
    private boolean Btn_LongPressRight = false;

    Double voltage_back=13.0;
    float percentage=0f;
    Integer power_number=0;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboardragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            if (uno == null)
                uno = getArguments().getString("uno");
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pathExternal = Environment.getExternalStorageDirectory();*/
        //folderPath = pathExternal+stringPackage;
        //EventBus.getDefault().register(this);  //必须注册 ROS端的处理  除非重新创建
        //client = ((RCApplication) getActivity().getApplication()).getRosClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        /*refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe);

        refreshLayout.setOnRefreshListener(this); // 设置刷新监听
        refreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.purple,R.color.swipefefresh_bg); // 进度动画颜色*/
        DC_TextViewShowElectricPercentage = rootView.findViewById(R.id.DC_TextViewShowElectricPercentage);
        DC_Custom_Battery_view = rootView.findViewById(R.id.DC_Custom_Battery_view);
        DC_ImgBtn_Up = rootView.findViewById(R.id.DC_ImgBtn_Up);
        //  DC_ImgBtn_Up.setOnClickListener(this);
        DC_ImgBtn_Up.setOnTouchListener(new HomeFragment.ComponentOnLongTouch());
        DC_ImgBtn_Down = rootView.findViewById(R.id.DC_ImgBtn_Down);
        //  DC_ImgBtn_Down.setOnClickListener(this);
        DC_ImgBtn_Down.setOnTouchListener(new HomeFragment.ComponentOnLongTouch());
        DC_ImgBtn_Left = rootView.findViewById(R.id.DC_ImgBtn_Left);
        //  DC_ImgBtn_Left.setOnClickListener(this);
        DC_ImgBtn_Left.setOnTouchListener(new HomeFragment.ComponentOnLongTouch());
        DC_ImgBtn_Right = rootView.findViewById(R.id.DC_ImgBtn_Right);
        // DC_ImgBtn_Right.setOnClickListener(this);
        DC_ImgBtn_Right.setOnTouchListener(new HomeFragment.ComponentOnLongTouch());

        tv_temperarture = rootView.findViewById(R.id.tv_temperature);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* for (int i = 1; i <= 4; i++) {
                    DummyContent.addItem(DummyContent.createDummyItem(i,"test"));
                }*/
                //showProgress(true);
                //getLatestChatList();
                Snackbar.make(view, "HomeFragment", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        EventBus.getDefault().register(this);  //必须注册 ROS端的处理  除非重新创建
        client = ((RCApplication) getActivity().getApplication()).getRosClient();
        TurnStop();//可以避免websocket初始化时出现rosbridge_websocket节点不是cmd_vel的警告
        //向ROS端订阅数据
        SubscribleRos();
        DC_Custom_Battery_view.setPower(50, 1);
        return rootView;
    }

    //向ROS端订阅数据
    private void SubscribleRos() {// OnRosMessageListener listener
        String RosMegPowerTemperature = "{\"op\":\"subscribe\",\"topic\":\"/temperature\"}";
        client.send(RosMegPowerTemperature);
        //获取电量信息
        String RosMegPower = "{\"op\":\"subscribe\",\"topic\":\"/battery_state\"}";
        client.send(RosMegPower);

        //String RosMegMap = "{\"op\":\"subscribe\",\"topic\":\"/map\"}";
        //client.send(RosMegMap);
    }
    /*
    {\"op\":\"call_service\",\"service\":\"/add_two_ints\",\"args\":{\"a\":2,\"b\":8}}
        {\"op\":\"call_service\",\"service\":\"scanmap\",\"args\":{\"type\":\"save\"},\"info\":{\"mapName\":\"tianyicesuo\",\"type\":8}}");//"{\"op\":\"call_service\",\"service\":\"scanmap\",\"args\":{\"a\":\"start\"}}
            {\"op\":\"call_service\",\"service\":\"scanmap\",\"args\":{\"type\":\"save\"},\"info\":{\"mapName\":\"tianyicesuo\",\"type\":8}}
*/
    /**
     * @Function: 接受订阅的消息
     * @Return:
     */
    public void onEvent(final PublishEvent event) {
        Log.d("onEvent", "came in");
        Log.i("event.name", event.name);
        Log.i("event.msg", event.msg);
        if ("/temperature".equals(event.name)) {
            //if(TemperatureUpdatek++ == 10) {
            //    TemperatureUpdatek = 0;
            parseGetTemperature(event);
            return;
            // }
        }
        //接受电量信息
        if ("/battery_state".equals(event.name)) {
            parsePowerInfo(event);
            return;
        }
    }
    /**
     * @Function: 温度的显示
     * @Return:
     */
    private void parseGetTemperature(PublishEvent event) {
        try {
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(event.msg);
            temperarture_value = Double.toString((Double)jsonObject.get("data"));
            //tv_temperarture.setText(event.msg);
            Log.i("jsondata:",temperarture_value);
            //tv_temperarture.setText(temperarture_value);
            Message message111 = new Message();
            message111.what = 0;
            myhandler.sendMessage(message111);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    /**
     * @Function: 获取电量信息
     * @Return: 0   毫安时  1 电量   2     3 电压    4：0 放电 1充电   5 电流的大小
     */
    private void parsePowerInfo(PublishEvent event) {
        Double voltage_full=12.5d;
        DecimalFormat df = new DecimalFormat("0.00000");
        //还是阿里的包好用
        JSONObject jsonObject = JSONObject.parseObject(event.msg);
        Double voltage = Double.valueOf(df.format(jsonObject.getDouble("voltage")));
        //Integer power_supply_status =  jsonObject.getInteger("power_supply_status");
        Double percentage_original =  Double.valueOf(df.format(jsonObject.getDouble("percentage")));
        Log.i("percentage_original",percentage_original+"");
        if ((voltage_back - voltage) >= 0.00125) { //消抖0.1/100*12.5=0.00125
            //Log.e("percentage_back",percentage_back+"");
            Log.i("voltage",voltage+"");
            //Log.e("power_supply_statusback",power_supply_status_back+"");
            //Log.e("power_supply_status",power_supply_status+"");
            voltage_back = voltage;
            //power_supply_status_back = power_supply_status;
            percentage = (float)(int)(voltage_back/voltage_full*1000)/10;
            power_number = (int)percentage;
            Log.i(IStatus.STATE_Log_Info + "power_number", power_number + "");
            //Log.i(IStatus.STATE_Log_Info + "power_supply_status", power_supply_status + "");
            Message message111 = new Message();
            if(voltage_back>11.7)
                message111.what = 1;
            else if (voltage_back>11 && voltage_back < 11.7)
                message111.what = 2;
            else
                message111.what = 3;
            myhandler.sendMessage(message111);
        }
    }

    @SuppressLint("HandlerLeak")
    public Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    tv_temperarture.setText(temperarture_value);
                    break;
                case 1:
                    DC_TextViewShowElectricPercentage.setText(percentage + " %");
                    DC_TextViewShowElectricPercentage.setTextColor(getResources().getColor(R.color.white));
                    DC_Custom_Battery_view.setPower(power_number , 0);
                    break;
                case 2:
                    DC_TextViewShowElectricPercentage.setText(percentage + " %");
                    DC_TextViewShowElectricPercentage.setTextColor(getResources().getColor(R.color.white));
                    DC_Custom_Battery_view.setPower(power_number , 1);
                    break;
                case 3:
                    DC_TextViewShowElectricPercentage.setText(percentage + " %");
                    DC_TextViewShowElectricPercentage.setTextColor(getResources().getColor(R.color.red));
                    DC_Custom_Battery_view.setPower(power_number , 2);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @Function: 控制前后左右的运动的长按 触发
     * @Return:
     */
    private class ComponentOnLongTouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.DC_ImgBtn_Up:    //前进
                    onTouchChange("Up", event.getAction());
                    break;
                case R.id.DC_ImgBtn_Down:  //后退
                    onTouchChange("Down", event.getAction());
                    break;
                case R.id.DC_ImgBtn_Left:  //左转
                    onTouchChange("Left", event.getAction());
                    break;
                case R.id.DC_ImgBtn_Right: //右转
                    onTouchChange("Right", event.getAction());
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    float linear_vel =0;
    float linear_vel_max = 0.22f;
    float angle_vel =0;
    float angle_vel_max = 2.84f;
    int k=0;
    //控制方向的转动
    class ControlMoveThread extends Thread {
        @Override
        public void run() {
            while (Btn_LongPressUp) {
                //Log.e("Action","upThread");
                if(k == 5) {
                    k=0;
                    linear_vel=linear_vel < linear_vel_max ?  linear_vel+0.01f : linear_vel_max;
                    Log.e("linear_vel",Double.toString(linear_vel));
                }
                TurnUp();
                try {
                    Thread.sleep(20);
                    k++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            while (Btn_LongPressDown) {
                //Log.e("Action","downThread");
                if(k == 5) {
                    k=0;
                    linear_vel=linear_vel > -linear_vel_max ?  linear_vel-0.01f : -linear_vel_max;
                    Log.e("linear_vel",Double.toString(linear_vel));
                }
                TurnDown();
                try {
                    Thread.sleep(20);
                    k++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            while (Btn_LongPressLeft) {
                if(k == 5) {
                    k=0;
                    angle_vel=angle_vel < angle_vel_max ?  angle_vel+0.1f : angle_vel_max;
                    Log.e("angle_vel",Double.toString(angle_vel));
                }
                TurnLeft();
                try {
                    Thread.sleep(20);
                    k++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            while (Btn_LongPressRight) {
                if(k == 5) {
                    k=0;
                    angle_vel=angle_vel > -angle_vel_max ?  angle_vel-0.1f : angle_vel_max;
                    Log.e("angle_vel",Double.toString(angle_vel));
                }
                TurnRight();
                try {
                    Thread.sleep(20);
                    k++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    //判断事件
    private void onTouchChange(String methodName, int eventAction) {
        //接触误按，多个按键一起按下
        if (Btn_LongPressUp && Btn_LongPressDown && Btn_LongPressLeft && Btn_LongPressRight) {
            //Log.e("Enter","all down");
            Btn_LongPressUp = false;
            Btn_LongPressDown = false;
            Btn_LongPressLeft = false;
            Btn_LongPressRight = false;
        }
        ControlMoveThread controlMoveThread = new ControlMoveThread();
        //判断按下了哪个
        if ("Up".equals(methodName)) {  //前进
            if (eventAction == MotionEvent.ACTION_DOWN) {
                //Log.e("Action","up");
                controlMoveThread.start();
                Btn_LongPressUp = true;
                DC_ImgBtn_Up.setImageResource(R.drawable.move_up_press);
            } else if (eventAction == MotionEvent.ACTION_UP) {
                //Log.e("Enter","up");
                TurnStop();
                if (controlMoveThread != null)
                    Btn_LongPressUp = false;
                DC_ImgBtn_Up.setImageResource(R.drawable.move_up);
            }
        }
        if ("Down".equals(methodName)) {  //后退
            if (eventAction == MotionEvent.ACTION_DOWN) {
                controlMoveThread.start();
                Btn_LongPressDown = true;
                DC_ImgBtn_Down.setImageResource(R.drawable.move_down_press);
            } else if (eventAction == MotionEvent.ACTION_UP) {
                //Log.e("enter","down");
                TurnStop();
                if (controlMoveThread != null)
                    Btn_LongPressDown = false;
                DC_ImgBtn_Down.setImageResource(R.drawable.move_down);
            }
        }
        if ("Left".equals(methodName)) {  //左转
            if (eventAction == MotionEvent.ACTION_DOWN) {
                controlMoveThread.start();
                Btn_LongPressLeft = true;
                DC_ImgBtn_Left.setImageResource(R.drawable.move_left_press);
            } else if (eventAction == MotionEvent.ACTION_UP) {
                TurnStop();
                if (controlMoveThread != null)
                    Btn_LongPressLeft = false;
                DC_ImgBtn_Left.setImageResource(R.drawable.move_left);
            }
        }
        if ("Right".equals(methodName)) {  //右转
            if (eventAction == MotionEvent.ACTION_DOWN) {
                controlMoveThread.start();
                Btn_LongPressRight = true;
                DC_ImgBtn_Right.setImageResource(R.drawable.move_right_press);
            } else if (eventAction == MotionEvent.ACTION_UP) {
                TurnStop();
                if (controlMoveThread != null)
                    Btn_LongPressRight = false;
                DC_ImgBtn_Right.setImageResource(R.drawable.move_right);
            }
        }
    }
    //  前进
    private void TurnUp() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + linear_vel + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + 0 + "}}}";
        client.send(MegRight);
    }

    //后退
    private void TurnDown() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + linear_vel + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + 0 + "}}}";
        client.send(MegRight);
    }

    //左转
    private void TurnLeft() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + 0 + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + angle_vel + "}}}";
        client.send(MegRight);
    }

    //右转
    private void TurnRight() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + 0 + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + angle_vel + "}}}";
        client.send(MegRight);
    }

    //停止
    private void TurnStop() {
        //Log.e("Publish","Stop");
        angle_vel=0;linear_vel=0;
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + 0 + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + 0 + "}}}";
        client.send(MegRight);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment.OnFragmentInteractionListener) {
            mListener = (HomeFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onRefresh() {
        //Toast.makeText(getActivity(), "刷新ING", Toast.LENGTH_LONG).show();
        //getLatestChatList();
        /*refreshLayout.postDelayed(new Runnable() { // 发送延迟消息到消息队列

            @Override
            public void run() {
                //refreshLayout.setRefreshing(false); // 是否显示刷新进度;false:不显示
                Toast.makeText(getActivity(), "刷新完成", Toast.LENGTH_SHORT).show();
            }
        },3000);*/
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}
