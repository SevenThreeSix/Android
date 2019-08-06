package com.example.user.helpeachother;

import android.os.Bundle;
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
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;

/**
 * Created by User on 2018/3/19.
 */

public class WNaviGuideActivity extends AppCompatActivity {

    public WalkNavigateHelper mNaviHelper;
    public WalkNaviLaunchParam param;
    LatLng startPt;
    LatLng endPt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //接收对象
        startPt = new LatLng(40.047416,116.312143);//默认
        endPt = new LatLng(40.048424, 116.313513);//默认
        startPt=getIntent().getParcelableExtra("startPt");
        endPt=getIntent().getParcelableExtra("endPt");
        // 获取导航控制类
        mNaviHelper = WalkNavigateHelper.getInstance();
        // 引擎初始化
        mNaviHelper.initNaviEngine(this, new IWEngineInitListener(){
            public static final String LTAG = "Walk";

            @Override
            public void engineInitSuccess() {
                Log.d(LTAG, "引擎初始化成功");
                Toast.makeText(getApplicationContext(), "引擎初始化成功", Toast.LENGTH_SHORT).show();

                routePlanWithParam();

                View view = mNaviHelper.onCreate(WNaviGuideActivity.this);
                if (view != null) {
                    setContentView(view);
                }
                // 开始导航
                mNaviHelper.startWalkNavi(WNaviGuideActivity.this);
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
    private void routePlanWithParam() {
        param = new WalkNaviLaunchParam().stPt(startPt).endPt(endPt);
        mNaviHelper.routePlanWithParams(param, new IWRoutePlanListener(){
            public static final String LTAG = "Walk";
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
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                Log.d(LTAG, "算路失败");
                Toast.makeText(getApplicationContext(), "算路失败", Toast.LENGTH_SHORT).show();
            }


        });
    }

}
