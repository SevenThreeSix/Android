package com.example.user.helpeachother;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;

/**
 * Created by User on 2018/3/19.
 */

public class BNaviGuideActivity extends AppCompatActivity {

    public BikeNavigateHelper mNaviHelperBike;
    public BikeNaviLaunchParam param_bike;
    LatLng startPt;
    LatLng endPt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_bnavi_guide);
        //接收对象
        startPt = new LatLng(40.047416,116.312143);//默认
        endPt = new LatLng(40.048424, 116.313513);//默认
        startPt=getIntent().getParcelableExtra("startPt");
        endPt=getIntent().getParcelableExtra("endPt");
        // 获取导航控制类
        mNaviHelperBike = BikeNavigateHelper.getInstance();
        // 引擎初始化
        mNaviHelperBike.initNaviEngine(this, new IBEngineInitListener() {
            public static final String LTAG = "Bike";

            @Override
            public void engineInitSuccess() {
                Log.d(LTAG, "引擎初始化成功");
                Toast.makeText(getApplicationContext(), "引擎初始化成功", Toast.LENGTH_SHORT).show();

                routePlanWithParamBike();

                    View view = mNaviHelperBike.onCreate(BNaviGuideActivity.this);
                    if (view != null) {
                        setContentView(view);
                    }
                    // 开始导航
                    mNaviHelperBike.startBikeNavi(BNaviGuideActivity.this);
            }

            @Override
            public void engineInitFail() {
                Log.d(LTAG, "引擎初始化失败");
                Toast.makeText(getApplicationContext(), "引擎初始化失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 开始算路
     */
    private void routePlanWithParamBike() {
        param_bike = new BikeNaviLaunchParam().stPt(startPt).endPt(endPt);
        mNaviHelperBike.routePlanWithParams(param_bike, new IBRoutePlanListener() {
            public static final String LTAG = "Bike";
            @Override
            public void onRoutePlanStart() {
                Log.d(LTAG, "开始算路");
                Toast.makeText(getApplicationContext(), "开始算路", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d(LTAG, "算路成功,跳转至诱导页面");
                Toast.makeText(getApplicationContext(), "算路成功,跳转至诱导页面", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError error) {
                Log.d(LTAG, "算路失败");
                Toast.makeText(getApplicationContext(), "算路失败", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
