package com.example.user.helpeachother;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.example.user.helpeachother.Fragment.ItemDetailFragment;
import com.example.user.helpeachother.dummy.DummyContent;
import com.example.user.helpeachother.dummy.DummyContentRecieve;

import static android.Manifest.permission.READ_CONTACTS;

public class MapActivity extends AppCompatActivity {

    private MapView mMapView ;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private BitmapDescriptor customMarker;
    private MyLocationConfiguration config;
    private TextView tv_task_pos_no;
    private TextView tv_task_pos_name;
    private TextView tv_user_pos_no;
    private TextView tv_user_pos_name;
    private TextView tv_user_pos_loctype;

    public BikeNavigateHelper mNaviHelperBike;
    public BikeNaviLaunchParam param_bike;
    public WalkNavigateHelper mNaviHelperWalk;
    public WalkNaviLaunchParam param_walk;
    public LatLng  startPt ;
    public LatLng  endPt ;

    String itemID,from;
    private DummyContent.DummyItem mItem;
    private DummyContentRecieve.DummyItem mRevItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_map);


        itemID = getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID);
        from =getIntent().getStringExtra("from");
        tv_task_pos_no = (TextView)findViewById(R.id.task_position_no);
        tv_task_pos_name = (TextView)findViewById(R.id.task_position_name);
        tv_user_pos_no = (TextView)findViewById(R.id.user_position_no);
        tv_user_pos_name = (TextView)findViewById(R.id.user_position_name);
        tv_user_pos_loctype = (TextView)findViewById(R.id.user_position_loctyoe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("地图");//父标题
        System.out.println("MapActivity.onCreate()");
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();//初始化显示地图
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener); // 注册监听函数,请求结果放在监听函数中
        initLocation();
        mLocationClient.start();

        //显示任务定位点
        if(from.contains("Rev")) {
            mRevItem = DummyContentRecieve.ITEM_MAP.get(itemID);
            tv_task_pos_no.setText("Task_Position:" + mRevItem.latitude);
            tv_task_pos_name.setText(mRevItem.longitude);
            final LatLng point = new LatLng(Double.valueOf(mRevItem.latitude), Double.valueOf(mRevItem.longitude));
            // 构建MarkerOption，用于在地图上添加Marker
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo2);
            MarkerOptions options = new MarkerOptions().position(point)
                    .icon(bitmap);
            // 在地图上添加Marker，并显示
            mBaiduMap.addOverlay(options);

            endPt = new LatLng(Double.valueOf(mRevItem.latitude), Double.valueOf(mRevItem.longitude));
        }
        else {
            mItem = DummyContent.ITEM_MAP.get(itemID);
            tv_task_pos_no.setText("Task_Position:" + mItem.latitude);
            tv_task_pos_name.setText(mItem.longitude);
            final LatLng point = new LatLng(Double.valueOf(mItem.latitude), Double.valueOf(mItem.longitude));
            // 构建MarkerOption，用于在地图上添加Marker
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo2);
            MarkerOptions options = new MarkerOptions().position(point)
                    .icon(bitmap);
            // 在地图上添加Marker，并显示
            mBaiduMap.addOverlay(options);

            endPt = new LatLng(Double.valueOf(mItem.latitude), Double.valueOf(mItem.longitude));
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.threemodel, menu);

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
            //NavUtils.navigateUpTo(this, new Intent(this, ItemDetailActivity.class).putExtra(ItemDetailFragment.ARG_ITEM_ID, itemID));
            finish();
            /*Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, itemID);
            startActivity(intent);*/
            return true;
        }
        //noinspection SimplifiableIfStatement
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
        if (id == R.id.action_walk_guide) {
            Toast.makeText(this, "开启步行导航", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(this, WNaviGuideActivity.class);
            intent.putExtra("startPt",startPt);
            intent.putExtra("endPt",endPt);
            startActivity(intent);

        }
        if (id == R.id.action_bike_guide) {
            Toast.makeText(this, "开启骑行导航", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(this, BNaviGuideActivity.class);
            intent.putExtra("startPt",startPt);
            intent.putExtra("endPt",endPt);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }





    /**
     * 1.接收异步返回的定位结果，参数是BDLocation类型参数。
     * @author username
     *
     */
    public class MyLocationListener implements BDLocationListener{


        /**
         * 定位成功后回调
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            startPt = new LatLng(location.getLatitude(),location.getLongitude());
            tv_user_pos_no.setText("你的位置："+String.valueOf(location.getLatitude())+" | "+String.valueOf(location.getLongitude()));
            tv_user_pos_name.setText("地址："+location.getAddrStr());//location.getCity()+"
            tv_user_pos_loctype.setText("@"+location.getLocType());
            tv_user_pos_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);//跑马灯效果
            //tv_user_pos_name.setSingleLine(true);
            tv_user_pos_name.setSelected(true);
            tv_user_pos_name.setFocusable(true);
            tv_user_pos_name.setFocusableInTouchMode(true);
            //tv_pos_name.setText(location.getCity()+",地址："+location.getAddrStr());
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
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(19.0f);//200m 3~22
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


}
