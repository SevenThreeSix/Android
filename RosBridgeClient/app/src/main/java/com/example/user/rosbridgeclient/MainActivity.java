package com.example.user.rosbridgeclient;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.example.user.rosbridgeclient.Fragment.HomeFragment;
import com.example.user.rosbridgeclient.Fragment.InfoFragment;
import com.example.user.rosbridgeclient.jilk.ros.rosbridge.ROSBridgeClient;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,InfoFragment.OnFragmentInteractionListener{

    private FragmentManager fragmentManager;

    private HomeFragment homeFragment;
    private InfoFragment infoFragment;
    private List<Fragment> fragments;
    private FragmentAdapter mAdapter;
    private ViewPager vp;
    /*ROS端的数据*/
    //ROSBridgeClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//viewpage+BottomNavigationView+fragment
        //Log.e("***this***",this.toString());

        //EventBus.getDefault().register(this);  //必须注册 ROS端的处理  除非重新创建
        //client = ((RCApplication) getApplication()).getRosClient();
        //向ROS端订阅数据
        //SubscribleRos();

    }

    private void initView() {//另一个添加fragment的方式
        vp = (ViewPager)findViewById(R.id.vp);
        fragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        infoFragment = new InfoFragment();

        fragments.add(homeFragment);
        fragments.add(infoFragment);
        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        mAdapter.setFragments(fragments);

        vp.setAdapter(mAdapter);
        vp.setOffscreenPageLimit(2);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                //int[] resId = {R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications};
                //navigation.setSelectedItemId(resId[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
    }

    class FragmentAdapter extends FragmentPagerAdapter
    {
        private List<Fragment> fragments;

        FragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        void setFragments(List<Fragment> fragments)
        {
            if (fragments != null && fragments.size() > 0)
            {
                this.fragments = fragments;
            }
        }

        @Override
        public int getCount()
        {
            if (fragments == null || fragments.size() == 0)
            {
                return 0;
            }
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position)
        {
            if (fragments == null || position > fragments.size())
            {
                return null;
            }
            return fragments.get(position);
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    /** 保存MyTouchListener接口的列表 */
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<>();

    /** 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法 */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /** 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法 */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove( listener );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
    public interface MyTouchListener {
        /** onTOuchEvent的实现 */
        boolean onTouchEvent(MotionEvent event);
    }
}
