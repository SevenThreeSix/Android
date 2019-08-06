package com.example.user.helpeachother;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.user.helpeachother.Fragment.ItemDetailFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;

public class ReleaseActivity extends AppCompatActivity{

    public static final String DefaultLatitude = "30.495978";
    private MapView mMapView ;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    private BitmapDescriptor customMarker;
    private MyLocationConfiguration config;
    private ReleaseActivity.MyLocationListener myListener = new ReleaseActivity.MyLocationListener();
    TextView release_title;
    Button bt_next;
    AutoCompleteTextView mTask;

    String UserNumber;
    private TextView tv_info;
    private TextView tv_user_info;

    String taskLatitude = "30.495978" ;//默认任务点经纬度
    String taskLongitude = "114.396702";

    ScrollView scrollView;
    Date date;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_release);


        //initView();//初始化时间控件 and click监听
        //initDateTime();//获取时间
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();//初始化显示地图
        tv_info = (TextView)findViewById(R.id.info);
        tv_user_info = (TextView)findViewById(R.id.user_position_info);

        Intent intent = getIntent(); // 获得启动该Activity的Intent对象
        UserNumber = intent.getStringExtra("uno");

        mTask = (AutoCompleteTextView) findViewById(R.id.task_content);
        bt_next = (Button) findViewById(R.id.next_button) ;
        release_title = (TextView) findViewById(R.id.release_title) ;
        Toolbar toolbar = (Toolbar) findViewById(R.id.release_toolbar);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
        actionBar.setTitle(UserNumber);//父标题
        actionBar.setSubtitle("YourID");//子标题
        release_title.setText("发布任务");



        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptRelease();
                //login();
                AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseActivity.this);
                builder.setMessage("Sure to Next？");  builder.setTitle("提示");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mTask.setError(null);

                        // Store values at the time of the login attempt.
                        String mtaskContent = mTask.getText().toString();

                        boolean cancel = false;
                        View focusView = null;


                        // Check for a valid email address.
                        if (TextUtils.isEmpty(mtaskContent)) {
                            mTask.setError(getString(R.string.error_field_required));
                            focusView = mTask;
                            focusView.requestFocus();
                        }else{
                            Intent intent = new Intent(getApplication(), SetEndTimeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("TaskContent", mtaskContent);
                            bundle.putString("TaskLatitude", taskLatitude);
                            bundle.putString("TaskLongitude", taskLongitude);
                            bundle.putString("uno", UserNumber);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }

                        //attemptRelease();
                        //ReleaseActivity.this.finish();
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
        });



        System.out.println("MapActivity.onCreate()");
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();//初始化显示地图
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener); // 注册监听函数,请求结果放在监听函数中
        initLocation();
        mLocationClient.start();

        /*mMapView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    Toast.makeText(getApplication(), "ACTION_UP", Toast.LENGTH_SHORT).show();
                    //scrollView.requestDisallowInterceptTouchEvent(false);
                }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    Toast.makeText(getApplication(), "ACTION_MOVE", Toast.LENGTH_SHORT).show();
                    //scrollView.requestDisallowInterceptTouchEvent(true);
                }else{
                    Toast.makeText(getApplication(), "else", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });*/

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                taskLatitude = String.valueOf(point.latitude);
                taskLongitude= String.valueOf(point.longitude);
                tv_info.setText(point.toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                imm.hideSoftInputFromWindow(mTask.getApplicationWindowToken(), 0 );//键盘消失

                //mBaiduMap.setMyLocationEnabled(true);
                mBaiduMap.clear();
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo2);
                MarkerOptions options = new MarkerOptions().position(point)
                        .icon(bitmap);
                // 在地图上添加Marker，并显示
                mBaiduMap.addOverlay(options);


                Toast.makeText(getApplication(), "任务点设置成功", Toast.LENGTH_SHORT).show();
                //scrollView.requestDisallowInterceptTouchEvent(false);
                /*mBaiduMap.clear();
                // 添加圆
                OverlayOptions ooCircle = new CircleOptions().fillColor(0xAAFFFF00)
                        .center(point).stroke(new Stroke(5, 0xAA00FF00))
                        .radius(1400);
                mBaiduMap.addOverlay(ooCircle);
                LatLng llA = point;
                OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
                        .zIndex(9).draggable(true);
                mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));*/
            }

            public boolean onMapPoiClick(MapPoi poi) {
                return false;
            }
        });

        //       重写onTouch()事件,在事件里通过requestDisallowInterceptTouchEvent(boolean)方法来设置父类的不可用,true表示父类的不可用
        //解决地图的touch事件和scrollView的touch事件冲突问题
        /*mMapView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    scrollView.requestDisallowInterceptTouchEvent(false);
                }else{
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.threemodel, menu);
        MenuItem actionWalkGuide = menu.findItem(R.id.action_walk_guide);
        actionWalkGuide.setVisible(false);
        MenuItem actionBikeGuide = menu.findItem(R.id.action_bike_guide);
        actionBikeGuide.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            //NavUtils.navigateUpTo(this, new Intent(this, NvgDrawerActivity.class));
            finish();
            return true;
        }
        if (id == R.id.Map_COMPASS) {
            config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, customMarker);
            mBaiduMap.setMyLocationConfiguration(config);
            Toast.makeText(this, R.string.Map_COMPASS, Toast.LENGTH_SHORT).show();

        }
        if (id == R.id.Map_FOLLOWING) {
            config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, customMarker);
            mBaiduMap.setMyLocationConfiguration(config);
            Toast.makeText(this, R.string.Map_FOLLOWING, Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.Map_NORMAL) {
            config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, customMarker);
            mBaiduMap.setMyLocationConfiguration(config);
            Toast.makeText(this, R.string.Map_NORMAL, Toast.LENGTH_SHORT).show();
        }
        /*if (id == R.id.action_navigation) {
            mBaiduMap.clear();
            //mBaiduMap.setMyLocationEnabled(false);
        }*/
        return super.onOptionsItemSelected(item);
    }

    /*
    * 初始化定位信息
    */
    private void initLocation() {
        System.out.println("LocationDemo.initLocation()");
        /*LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");// coordinate。可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);*/
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(2000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        // 1.还需要打开定位图层
        mBaiduMap.setMyLocationEnabled(true);

        customMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, customMarker);
        mBaiduMap.setMyLocationConfiguration(config);
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18.0f);//200m 3~22
        mBaiduMap.animateMapStatus(u);

    }
    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("LocationDemo.onStart()");

// mLocationClient.start();//开启定位比较耗时，在启动的时候就调用
        new Thread(){
            public void run() {

                mLocationClient.start();//开启定位比较耗时，在启动的时候就调用
            }
        }.start();
    }
    /**
     * 1.接收异步返回的定位结果，参数是BDLocation类型参数。
     * @author username
     *
     */
    public class MyLocationListener implements BDLocationListener {


        /**
         * 定位成功后回调
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            /*tv_user_pos_no.setText("你的位置："+location.getLatitude()+" | "+location.getLongitude());
            tv_user_pos_name.setText("地址："+location.getAddrStr());//location.getCity()+"
            tv_user_pos_loctype.setText("@"+location.getLocType());
            tv_user_pos_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);//跑马灯效果
            //tv_user_pos_name.setSingleLine(true);
            tv_user_pos_name.setSelected(true);
            tv_user_pos_name.setFocusable(true);
            tv_user_pos_name.setFocusableInTouchMode(true);*/
            //tv_pos_name.setText(location.getCity()+",地址："+location.getAddrStr());
            tv_user_info.setText("你的位置："+location.getLatitude()+" | "+location.getLongitude());
            System.out.println("定位结果："+location.getCity()+",地址："+location.getAddrStr());
            System.out.println("经纬度："+location.getLatitude()+" | "+location.getLongitude());

            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    //.direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locationData);//设置定位数据到地图上
        }
    }


}
