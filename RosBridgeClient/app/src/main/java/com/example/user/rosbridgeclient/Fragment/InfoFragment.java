package com.example.user.rosbridgeclient.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.user.rosbridgeclient.BatteryView;
import com.example.user.rosbridgeclient.MainActivity;
import com.example.user.rosbridgeclient.MapView;
import com.example.user.rosbridgeclient.PublishEvent;
import com.example.user.rosbridgeclient.R;
import com.example.user.rosbridgeclient.RCApplication;
import com.example.user.rosbridgeclient.jilk.ros.rosbridge.ROSBridgeClient;

//import org.json.JSONArray;
//import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.greenrobot.event.EventBus;


public class InfoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View rootView;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout refreshLayout;
    private MapView mapView;

    /*ROS端的数据*/
    ROSBridgeClient client;
    int[][] array;

    private double nLenStart;
    private double nLenEnd;

    public InfoFragment() {
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
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
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
        array=new int[384][384];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_infofragment, container, false);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "InfoFragment", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        mapView = rootView.findViewById(R.id.Map_view);
        //mapView.setOnTouchListener();

        EventBus.getDefault().register(this);  //必须注册 ROS端的处理  除非重新创建
        client = ((RCApplication) getActivity().getApplication()).getRosClient();
        SubscribleRos();

        return rootView;

    }

    //向ROS端订阅数据
    private void SubscribleRos() {// OnRosMessageListener listener
        /*String RosMegPowerTemperature = "{\"op\":\"subscribe\",\"topic\":\"/temperature\"}";
        client.send(RosMegPowerTemperature);
        //获取电量信息
        String RosMegPower = "{\"op\":\"subscribe\",\"topic\":\"/battery_state\"}";
        client.send(RosMegPower);*/

        String RosMegMap = "{\"op\":\"subscribe\",\"topic\":\"/map\"}";
        client.send(RosMegMap);
    }

    /**
     * @Function: 接受订阅的消息
     * @Return:
     */
    public void onEvent(final PublishEvent event) {
        Log.d("onEvent", "came in");
        Log.i("event.name", event.name);
        Log.i("event.msg", event.msg);

        //接受电量信息
        if ("/map".equals(event.name)) {
            parseMapInfo(event);
            return;
        }
    }
    /**
     * @Function: Map的显示
     * @Return:
     */
    private void parseMapInfo(PublishEvent event) {
        int k=0;
        JSONObject jsonObject = JSONObject.parseObject(event.msg);
        Log.e("parseMapInfo",((JSONArray) jsonObject.get("data")).get(0)+" "+((JSONArray) jsonObject.get("data")).get(384*384-1));//384*384=147456
        for(int i=383; i>=0; i--)
            for(int j=0; j<=383; j++) {
                array[i][j] = Integer.valueOf(((JSONArray) jsonObject.get("data")).get(k) + "");
                k++;
                if(array[i][j] == 0)
                    Log.e("parseMapInfo-WHITE", array[i][j] + "");
            }
        Log.e("parseMapInfo-SUM Point", k +"");
        Message message111 = new Message();
        message111.what = 0;
        myhandler.sendMessage(message111);
    }
    @SuppressLint("HandlerLeak")
    public Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mapView.showMap(array);
                    /*new Thread() {
                        @Override
                        public void run() {
                            mapView.showMap(array);
                        }}.start();*/
                    break;
                default:
                    break;
            }
        }
    };
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
            mListener = (InfoFragment.OnFragmentInteractionListener) context;
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


    public boolean onTouchEvent(MotionEvent event) {
        // 返回给ScaleGestureDetector来处理
        int pCount = event.getPointerCount();// 触摸设备时手指的数量

        int action  = event.getAction();// 获取触屏动作。比如：按下、移动和抬起等手势动作

        // 手势按下且屏幕上是两个手指数量时
        if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && pCount == 2){

            // 获取按下时候两个坐标的x轴的水平距离，取绝对值
            int xLen = Math.abs((int)event.getX(0) - (int)event.getX(1));
            // 获取按下时候两个坐标的y轴的水平距离，取绝对值
            int yLen = Math.abs((int)event.getY(0) - (int)event.getY(1));

            // 根据x轴和y轴的水平距离，求平方和后再开方获取两个点之间的直线距离。此时就获取到了两个手指刚按下时的直线距离
            nLenStart = Math.sqrt((double) xLen * xLen + (double) yLen * yLen);

        }else if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP && pCount == 2){// 手势抬起且屏幕上是两个手指数量时

            // 获取抬起时候两个坐标的x轴的水平距离，取绝对值
            int xLen = Math.abs((int)event.getX(0) - (int)event.getX(1));
            // 获取抬起时候两个坐标的y轴的水平距离，取绝对值
            int yLen = Math.abs((int)event.getY(0) - (int)event.getY(1));

            // 根据x轴和y轴的水平距离，求平方和后再开方获取两个点之间的直线距离。此时就获取到了两个手指抬起时的直线距离
            nLenEnd = Math.sqrt((double) xLen * xLen + (double) yLen * yLen);

            // 根据手势按下时两个手指触点之间的直线距离A和手势抬起时两个手指触点之间的直线距离B。比较A和B的大小，得出用户是手势放大还是手势缩小
            if(nLenEnd > nLenStart){
                Toast.makeText(getContext(), "手势放大", Toast.LENGTH_SHORT).show();
            }else if(nLenEnd < nLenStart){
                Toast.makeText(getContext(), "手势缩小", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }


}
