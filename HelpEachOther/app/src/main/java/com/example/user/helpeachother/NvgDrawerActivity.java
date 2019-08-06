package com.example.user.helpeachother;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.example.user.helpeachother.Fragment.DashboardFragment;
import com.example.user.helpeachother.Fragment.HomeFragment;
import com.example.user.helpeachother.Fragment.InfoFragment;
import com.example.user.helpeachother.Fragment.NotificationsFragment;
import com.example.user.helpeachother.Fragment.SyncFragment;
import com.example.user.helpeachother.dummy.DummyContentFriendList;
import com.fuqianla.paysdk.FuQianLa;
import com.fuqianla.paysdk.FuQianLaPay;
import com.fuqianla.paysdk.bean.FuQianLaResult;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;

public class NvgDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,HomeFragment.OnFragmentInteractionListener,NotificationsFragment.OnFragmentInteractionListener ,DashboardFragment.OnFragmentInteractionListener ,
        InfoFragment.OnFragmentInteractionListener,SyncFragment.OnFragmentInteractionListener{

    //private TextView mTextMessage;
    private LinearLayout mLinearLayout;
    private FragmentManager fragmentManager;

    private HomeFragment homeFragment;
    private DashboardFragment dashboardFragment;
    private NotificationsFragment notificationsFragment;
    private InfoFragment infoFragment;
    private SyncFragment syncFragment;

    private NoScrollViewPage mainContainer;
    private FragmentAdapter mAdapter;

    private List<Fragment> fragments;
    private BottomNavigationView navigation;
    private BottomNavigationView navigationTask;

    private String UserNumber,UserName;
    public TextView txHeader_UserNumber;
    public TextView txHeader_UserName;
    public TextView titleShow;
    private ImageView headImage;
    private Bitmap headBitmap;
    //private TextView txHeader_UserName;

    MyConnector mc;
    int headSize;
    int quality;
    public File pathExternal;
    public String stringPackage = "/helpeachother/";
    public String folderPath;
    String []friendUnoList;
    int friendUnoListNo=0;
    int headlistsize;
    int taskChosenBuf = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setSelectTab(0);
                    return true;
                case R.id.navigation_dashboard:
                    setSelectTab(1);
                    return true;
                case R.id.navigation_notifications:
                    setSelectTab(2);
                    return true;
            }
            return false;
        }
    };
    private BottomNavigationView.OnNavigationItemSelectedListener mTaskOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_task_circle:
                setSelectTaskTab(0);
                return true;
            case R.id.navigation_task_send:
                setSelectTaskTab(1);
                return true;
            case R.id.navigation_task_rev:
                setSelectTaskTab(2);
                return true;
        }
        return false;
    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvg_drawer);

        pathExternal = Environment.getExternalStorageDirectory();
        folderPath = pathExternal+stringPackage;
        //Intent intent = getIntent(); // 获得启动该Activity的Intent对象
        //UserNumber = intent.getStringExtra("uno"); // 获得当前用户的id
        SharedPreferences sp = getSharedPreferences("SP",MODE_PRIVATE); // 获得Preferences
        UserNumber = sp.getString("uno",null);
        UserName = "null";
        //ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("");
        //ToolBar+侧部导航栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);//处理旋转按钮的点击事件
        toggle.syncState();//添加菜单拖动监听事件  根据菜单的拖动距离 将距离折算成旋转角度
        titleShow = (TextView) findViewById(R.id.title) ;
        //侧部导航栏
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerlLayout = navigationView.inflateHeaderView(R.layout.nav_header_nvg_drawer);
        txHeader_UserNumber = headerlLayout.findViewById(R.id.Header_UserNumber);
        txHeader_UserNumber.setText("UserNumber:"+UserNumber);
        headImage = headerlLayout.findViewById(R.id.imageView);
        txHeader_UserName = headerlLayout.findViewById(R.id.Header_UserName);

        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
            }
        });
        //底部导航栏
        //添加事件
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigationTask = (BottomNavigationView) findViewById(R.id.navigation_task);
        navigationTask.setOnNavigationItemSelectedListener(mTaskOnNavigationItemSelectedListener);
        //设置初始界面
        fragmentManager = getSupportFragmentManager();
        //setSelectTab(0);//BottomNavigationView+fragment FrameLayout
        //initView();//viewpage+BottomNavigationView+fragment
        getMyHeadBmp();
        //getFriendHeadList();
    }

    //获取本地图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String img_url = uri.getPath();//这是本机的图片路径
            ContentResolver cr = this.getContentResolver();
            try {
                headBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //ImageView imageView = (ImageView) findViewById(R.id.write_competerelay_cover_iv);
                //将Bitmap设定到ImageView
                headImage.setImageBitmap(headBitmap);
                headImage.setLayoutParams(new LinearLayout.LayoutParams(150, 200));

            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        //返回结果
        if (requestCode == FuQianLa.REQUESTCODE
                && resultCode == FuQianLa.RESULTCODE
                && data != null) {
            //result结果包括code和message
            FuQianLaResult result = data.getParcelableExtra(FuQianLa.PAYRESULT_KEY);
            //((TextView)findViewById(R.id.textView1)).setText("Response:"+result.toString());
            //Toast.makeText(getApplicationContext(), "Response:"+result.toString(), Toast.LENGTH_LONG).show();
            Snackbar.make((DrawerLayout) findViewById(R.id.drawer_layout), "Response:"+result.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {//另一个添加fragment的方式
        //mainContainer = findViewById(R.id.main_container);
        fragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        notificationsFragment = new NotificationsFragment();
        dashboardFragment = new DashboardFragment();

        fragments.add(homeFragment);
        fragments.add(notificationsFragment);
        fragments.add(dashboardFragment);
        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        mAdapter.setFragments(fragments);
        mainContainer.setAdapter(mAdapter);
        mainContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                int[] resId = {R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications};
                navigation.setSelectedItemId(resId[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
    }

    private void setSelectTab(int index) {
        //clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                titleShow.setText("消息");
                navigationTask.setVisibility(View.GONE);
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.content, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                Bundle bundle0=new Bundle();
                bundle0.putString("uno",UserNumber);//这里的kkk就是要传的值
                homeFragment.setArguments(bundle0);
                break;
            case 1:
                navigationTask.setVisibility(View.VISIBLE);
                setSelectTaskTab(taskChosenBuf);
                Toast.makeText(getApplicationContext(), "taskChosenBuf:"+taskChosenBuf, Toast.LENGTH_SHORT).show();
                return;
                /*if (dashboardFragment == null) {
                    dashboardFragment = new DashboardFragment();
                    transaction.add(R.id.content, dashboardFragment);
                } else {
                    transaction.show(dashboardFragment);
                }
                Bundle bundle1=new Bundle();
                bundle1.putString("uno",UserNumber);//这里的kkk就是要传的值
                dashboardFragment.setArguments(bundle1);*/
                //break;
            case 2:
                titleShow.setText("联系人");
                navigationTask.setVisibility(View.GONE);
                if (notificationsFragment == null) {
                    notificationsFragment = new NotificationsFragment();
                    transaction.add(R.id.content, notificationsFragment);
                } else {
                    transaction.show(notificationsFragment);
                }
                Bundle bundle2=new Bundle();
                bundle2.putString("uno",UserNumber);//这里的kkk就是要传的值
                notificationsFragment.setArguments(bundle2);
                break;
            default:
                break;
        }

        transaction.commit();

    }
    private void setSelectTaskTab(int index) {
        //clearSelection();
        // 开启一个Fragment事务
        Log.e("setSelectTaskTab",String.valueOf(index));
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                Log.e("HERE,所有任务",String.valueOf(index));
                titleShow.setText("所有任务");
                if (dashboardFragment == null) {
                    dashboardFragment = new DashboardFragment();
                    transaction.add(R.id.content, dashboardFragment);
                } else {
                    Log.e("页面存在",String.valueOf(index));
                    transaction.show(dashboardFragment);
                }
                Bundle bundle1=new Bundle();
                bundle1.putString("uno",UserNumber);//这里的kkk就是要传的值
                dashboardFragment.setArguments(bundle1);
                taskChosenBuf = 0;//缓存之前的选择
                break;
            case 1:
                Log.e("HERE,发布的任务",String.valueOf(index));
                titleShow.setText(" 发布的任务");
                if (infoFragment == null) {
                    infoFragment = new InfoFragment();
                    transaction.add(R.id.content, infoFragment);
                } else {
                    Log.e("页面存在",String.valueOf(index));
                    transaction.show(infoFragment);
                }
                Bundle bundle3=new Bundle();
                bundle3.putString("uno",UserNumber);//这里的kkk就是要传的值
                infoFragment.setArguments(bundle3);
                taskChosenBuf = 1;
                break;
            case 2:
                Log.e("HERE,接受的任务",String.valueOf(index));
                titleShow.setText(" 接受的任务");
                if (syncFragment == null) {
                    syncFragment = new SyncFragment();
                    transaction.add(R.id.content, syncFragment);
                } else {
                    Log.e("页面存在",String.valueOf(index));
                    transaction.show(syncFragment);
                }
                Bundle bundle4=new Bundle();
                bundle4.putString("uno",UserNumber);//这里的kkk就是要传的值
                syncFragment.setArguments(bundle4);
                taskChosenBuf = 2;
                break;
            default:
                break;
        }

        transaction.commit();

    }

    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (dashboardFragment != null) {
            transaction.hide(dashboardFragment);
        }
        if (notificationsFragment != null) {
            transaction.hide(notificationsFragment);
        }
        if (infoFragment != null) {
            transaction.hide(infoFragment);
        }
        if (syncFragment != null) {
            transaction.hide(syncFragment);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nvg_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(), "上传头像ing", Toast.LENGTH_SHORT).show();
            updateHead();
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(getApplicationContext(), "缓存头像", Toast.LENGTH_SHORT).show();
            getMyHeadBmp();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(getApplicationContext(), "删除缓存", Toast.LENGTH_SHORT).show();
            deleteFolderFile(pathExternal+"/helpeachother",true);
        } else if (id == R.id.nav_manage) {
            //付钱拉初始化
            FuQianLa.getInstance().init(getApplicationContext());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //支付核心代码
                    FuQianLaPay pay = new FuQianLaPay.Builder(NvgDrawerActivity.this)  //Builder(this)
                            .alipay(true,2)//支付宝通道
                            .wxpay(true)//微信通道
                            .orderID("12345678")//YOUR_ORDERID")//订单号
                            .amount(0.01)//金额
                            .subject("商品名称")
                            .notifyUrl("https://www.alipay.com")//异步通知地址")
                            .build();
                    pay.startPay();
                }
            }).start();
        } else if (id == R.id.nav_share) {
            if(isAppAvilible(this,"com.eg.android.AlipayGphone")){
                Intent intent = new Intent();
                ComponentName cmp=new ComponentName("com.eg.android.AlipayGphone","com.eg.android.AlipayGphone.AlipayLogin");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "打开支付宝com.eg.android.AlipayGphone，成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "该手机未安装支付宝", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_send) {
            if(isAppAvilible(this,"com.tencent.mm")){
                Intent intent = new Intent();
                ComponentName cmp=new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "打开微信com.tencent.mm，成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "该手机未安装微信", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean isAppAvilible(Context context,String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    headImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    headImage.setLayoutParams(new LinearLayout.LayoutParams(150, 200));
                    headImage.setImageBitmap(headBitmap);
                    txHeader_UserName.setText("UserName:"+UserName);
                    Toast.makeText(getApplicationContext(), "成功读取用户信息", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "headSize头像大小："+headSize, Toast.LENGTH_SHORT).show();
                    getFriendHeadList();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "读取数据超时", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "连接超时", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @SuppressLint("HandlerLeak")
    Handler myHandlerInsert = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:


                    /*headImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    headImage.setLayoutParams(new LinearLayout.LayoutParams(150, 200));
                    headImage.setImageBitmap(headBitmap);
                    txHeader_UserName.setText("UserName:"+UserName);
                    Toast.makeText(getApplicationContext(), "成功读取用户信息", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "headSize"+headSize, Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(getApplicationContext(), "预计更新用户头像", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "压缩率百分之"+String.valueOf(100-quality), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "读取数据超时", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "连接超时", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    // 方法：获取用户信息
    public void getMyHeadBmp() {
        new Thread() {
            public void run() {
                //Looper.prepare();
                try {
                    Log.i("get_user_ENTER","true");
                    mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象
                    if(mc.dout == null) {
                        myHandler.sendEmptyMessage(2);
                        return ;
                    }
                    mc.dout.writeUTF("<#UserInfo#>" + UserNumber); // 向服务器发出请求

                    String fInfo = mc.din.readUTF();
                    System.out.println(fInfo);
                    String[] infoList = fInfo.split("\\|"); // 分割字符串
                    UserName = infoList[1];
                    headSize = mc.din.readInt(); // 读取头像大小
                    byte[] buf = new byte[headSize]; // 创建缓冲区
                    Log.i("headSize",String.valueOf(headSize));
                    mc.din.read(buf); // 读取头像信息
                    //Log.i("头像bufByteArray",new String(buf));
                    Log.i("头像ArrayToString", Arrays.toString(buf));
                    //BufferedInputStream bis = new BufferedInputStream(getAssets().open(UserNumber + ".jpg"));
                    //headBitmap = BitmapFactory.decodeStream(buf,null,);
                    //bitmapArray = Base64.decode(string, Base64.DEFAULT);
                    headBitmap = BitmapFactory.decodeByteArray(buf,0,headSize);


                    myHandler.sendEmptyMessage(0); // 发出Handler消息
                } catch (Exception e) {
                    e.printStackTrace();
                    // pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()) {
                        Toast.makeText(NvgDrawerActivity.this, "读取数据超时",
                                Toast.LENGTH_LONG).show();
                        //Looper.loop();
                        //Looper.myLooper().quit();
                    } else {
                        Toast.makeText(NvgDrawerActivity.this, "连接超时",
                                Toast.LENGTH_SHORT).show();
                        //Looper.loop();
                        //Looper.myLooper().quit();
                    }
                }
            }
        }.start();
    }


    // 方法：获取用户信息
    public void updateHead() {
        new Thread() {
            public void run() {
                //Looper.prepare();
                try {
                    Log.i("insertHead_ENTER","true");
                    mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象
                    if(mc.dout == null) {
                        myHandlerInsert.sendEmptyMessage(2);
                        return ;
                    }
                    mc.dout.writeUTF("<#NEW_HEAD#>" + "des|"+UserNumber); // 向服务器发出请求

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    quality = 90;
                    headBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                    byte[] datas = baos.toByteArray();
                    Log.i("头像byte数组转字符串显示",Arrays.toString(datas));
                    Log.i("datasLength",String.valueOf(datas.length));

                    int length = returnActualLength(datas);
                    Log.i("datasByte数组实际长度",String.valueOf(length));
                    mc.dout.writeInt(datas.length);
                    //mc.dout.writeInt(length);
                    for(int i=0;i<datas.length;i++) {//注意之前<=2699,发完mc就关了。但是第2700次还在发，导致异常<=改为<
                        //mc.dout.writeByte(bytesToHexString(datas[i]));
                        //if(datas)
                        if(mc.dout == null) {
                            myHandlerInsert.sendEmptyMessage(2);
                            //mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT);
                            //return ;
                        }
                        mc.dout.writeByte(datas[i]);
                    }
                    //mc.dout.writeBytes(Arrays.toString(datas));
                    //string = Base64.encodeToString(bytes, Base64.DEFAULT);
                    //mc.dout.writeBytes(bytesToHexString(datas));
                    Log.i("bytesToHexString",bytesToHexString(datas));
                    /*String fInfo = mc.din.readUTF(); // 读取好友信息
                    System.out.println(fInfo);
                    String[] infoList = fInfo.split("\\|"); // 分割字符串
                    UserName = infoList[1];
                    headSize = mc.din.readInt(); // 读取头像大小
                    byte[] buf = new byte[headSize]; // 创建缓冲区
                    Log.i("headSize",String.valueOf(headSize));
                    mc.din.read(buf); // 读取头像信息
                    //BufferedInputStream bis = new BufferedInputStream(getAssets().open(UserNumber + ".jpg"));
                    //headBitmap = BitmapFactory.decodeStream(buf,null,);
                    headBitmap = BitmapFactory.decodeByteArray(buf,0,headSize);*/

                    //应添加反馈判断
                    myHandlerInsert.sendEmptyMessage(0); // 发出Handler消息
                } catch (Exception e) {
                    e.printStackTrace();
                    // pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()) {
                        //Toast.makeText(NvgDrawerActivity.this, "读取数据超时",Toast.LENGTH_LONG).show();
                        myHandlerInsert.sendEmptyMessage(1);
                        //Looper.loop();
                        //Looper.myLooper().quit();
                    } else {
                        //Toast.makeText(NvgDrawerActivity.this, "连接超时",Toast.LENGTH_SHORT).show();
                        myHandlerInsert.sendEmptyMessage(2);
                        //Looper.loop();
                        //Looper.myLooper().quit();
                    }
                }
            }
        }.start();
    }
    public static int returnActualLength(byte[] data) {
        int i = 0;
        for (; i < data.length; i++) {
            if (data[i] == '\0')
                break;
        }
        return i;
    }
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    public void updateMyImage(String uno){
        String localIconNormal = HomeFragment.folderPath+uno+".jpg";//getContext().getFilesDir().getAbsolutePath()+ File.separator+mValues.get(position).ubno+".jpg";//"/data/data/com.android.factorypattern/FPTest.bmp";
        Log.i("读图片地址",localIconNormal);
        File localFile;
        FileInputStream localStream = null;
        Bitmap bitmap = null;
        localFile = new File(localIconNormal);
        if (!localFile.exists()) {
            Log.i("Holder", localIconNormal + " 不存在.");
        } else {
            try {
                Log.i("Holder", localIconNormal + " 存在并装载进image控件.");
                localStream = new FileInputStream(localFile);
                //localStream.reset();
                //bitmap = BitmapFactory.decodeStream(localStream);
                bitmap = BitmapFactory.decodeFile(localIconNormal,null);
                Log.i("HolderBitmap", String.valueOf(bitmap));
                //holder.headImage.setBackgroundResource(R.drawable.face);
                headImage.setImageBitmap(bitmap);
                //holder.headImage.setBackgroundResource(R.drawable.face);
                if (localStream != null) {
                    localStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler myHandlerFriendHeadList = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    friendUnoListNo =0;
                    getUserHeadFile(friendUnoList[friendUnoListNo]);
                    Toast.makeText(getApplicationContext(), "刷新完成", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "读取数据超时", Toast.LENGTH_LONG).show();
                    //showProgress(false);
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "连接超时", Toast.LENGTH_LONG).show();
                    //showProgress(false);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler myHandlerLoadHeadFile = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "成功缓存"+friendUnoList[friendUnoListNo]+"头像", Toast.LENGTH_SHORT).show();
                    if(++friendUnoListNo<=headlistsize) {
                        Log.i("friendUnoListNo",String.valueOf(friendUnoListNo));
                        getUserHeadFile(friendUnoList[friendUnoListNo]);
                    }else {
                        setSelectTab(0);
                        Toast.makeText(getApplicationContext(), "读取头像结束", Toast.LENGTH_SHORT).show();
                        updateMyImage(UserNumber);
                        Toast.makeText(getApplicationContext(), "读文件的形式更新头像", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "读取数据超时", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "连接超时", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "文件已存在"+friendUnoList[friendUnoListNo]+".jpg", Toast.LENGTH_SHORT).show();
                    if(++friendUnoListNo<=headlistsize) {
                        Log.i("friendUnoListNo",String.valueOf(friendUnoListNo));
                        getUserHeadFile(friendUnoList[friendUnoListNo]);
                    }else {
                        setSelectTab(0);
                        Toast.makeText(getApplicationContext(), "读取头像结束", Toast.LENGTH_SHORT).show();
                        updateMyImage(UserNumber);
                        Toast.makeText(getApplicationContext(), "读文件的形式更新头像", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void getFriendHeadList() {
        new Thread() {
            public void run() {
                try {
                    /*Bundle bundle=getArguments();
                    uno=bundle.getString("uno");*/

                    mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象
                    if(mc.dout == null) {
                        myHandler.sendEmptyMessage(2);
                        return ;
                    }
                    mc.dout.writeUTF("<#FRIEND_LIST#>" + UserNumber ); // 向服务器发出请求
                    headlistsize = mc.din.readInt(); // 读取列表的长度
                    Log.i("FriendListSize",String.valueOf(headlistsize));
                    friendUnoList =new String[headlistsize+1];

                    /*headList = null;
                    headList = new Bitmap[size];
                    dateList = null; // 初始化好友头像列表
                    dateList = new ArrayList<String[]>(size); // 初始化好友信息列表*/
                    friendUnoList[0] = UserNumber;
                    String fInfo = mc.din.readUTF();
                    String[] list = fInfo.split("\\,");
                    //getUser("2012");
                    for (int i = 0; i < headlistsize; i++) { // 循环，获取每个好友的信息和头像
                        // 读取好友信息
                        String[] sa = list[i].split("\\|"); // 分割字符串
                        friendUnoList[i+1]= sa[0];
                        Log.i("UNO",sa[0]);
                    }

                    myHandlerFriendHeadList.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
//					pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()){
                        myHandlerFriendHeadList.sendEmptyMessage(1);
                    }
                    else{
                        myHandlerFriendHeadList.sendEmptyMessage(2);
                    }
                }

            }
        }.start();
    }
    public void getUserHeadFile(final String unoTemp) {
        new Thread() {
            public void run() {
                //Looper.prepare();
                String newPath = folderPath+unoTemp+".jpg";
                //String path = getContext().getFilesDir().getAbsolutePath()+ File.separator+uno+".jpg";
                Log.i("newPath",newPath);
                Log.i("ExternalStorageDir",String.valueOf(Environment.getExternalStorageDirectory()));
                File localFile = new File(newPath);
                if (localFile.exists()) {
                    Log.i("文件存在", newPath);
                    myHandlerLoadHeadFile.sendEmptyMessage(3);
                } else {
                    try {
                        //新建一个File，传入文件夹目录
                        File fileDir = new File(folderPath);
                        //判断文件夹是否存在，如果不存在就创建，否则不创建
                        if (!fileDir.exists()) {
                            //通过file的mkdirs()方法创建<span style="color:#FF0000;">目录中包含却不存在</span>的文件夹
                            Log.e("目录不存在，已新建",String.valueOf(fileDir));
                            fileDir.mkdirs();
                        }
                        Log.i("头像文件不存在，下面缓存头像文件，目录", newPath);
                        mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象
                        if (mc.dout == null) {
                            myHandlerLoadHeadFile.sendEmptyMessage(2);
                            return;
                        }
                        mc.dout.writeUTF("<#UserInfo#>" + unoTemp); // 向服务器发出请求

                        String fInfo = mc.din.readUTF();
                        System.out.println(fInfo);
                        String[] infoList = fInfo.split("\\|"); // 分割字符串
                        Log.i("判断信息是否错乱",infoList[0]);
                        //UserName = infoList[1];
                        int headSize = mc.din.readInt(); // 读取头像大小
                        Log.i("读头像线程中headsize",String.valueOf(headSize));
                        byte[] buf = new byte[headSize]; // 创建缓冲区
                        //Log.i("headSize",String.valueOf(headSize));
                        mc.din.read(buf); // 读取头像信息
                        //Log.i("头像bufByteArray",new String(buf));
                        Log.i("头像ArrayToString", Arrays.toString(buf));

                        Bitmap headBitmap = BitmapFactory.decodeByteArray(buf, 0, headSize);

                        //f = new File(newPath);
                        File f = new File(folderPath, unoTemp+".jpg");//

                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        headBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        try {
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("保存结束", folderPath+unoTemp+".jpg");
                        //Log.i("已经保存", path);
                        myHandlerLoadHeadFile.sendEmptyMessage(0); // 发出Handler消息
                    } catch (Exception e) {
                        e.printStackTrace();
                        // pd.dismiss();
                        if (!mc.socket.isClosed() && mc.socket.isConnected()) {
                            myHandlerLoadHeadFile.sendEmptyMessage(1);
                            Log.e("读取头像超时",unoTemp+".jpg");
                            //Looper.loop();
                            //Looper.myLooper().quit();
                        } else {
                            myHandlerLoadHeadFile.sendEmptyMessage(2);
                            //Looper.loop();
                            //Looper.myLooper().quit();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     2      * 删除指定目录下文件及目录
     3      * @param deleteThisPath
     4      * @param filepath
     5      * @return
     6      */
      public void deleteFolderFile(String filePath, boolean deleteThisPath) {
                if (!TextUtils.isEmpty(filePath)) {
                         try {
                                 File file = new File(filePath);
                                 if (file.isDirectory()) {// 处理目录
                                         File files[] = file.listFiles();
                                         for (int i = 0; i < files.length; i++) {
                                                 deleteFolderFile(files[i].getAbsolutePath(), true);
                                                 Log.i("files[i]getAbsolutePath",files[i].getAbsolutePath());
                                             }
                                     }
                                 if (deleteThisPath) {
                                         if (!file.isDirectory()) {// 如果是文件，删除
                                                 file.delete();
                                                 Log.i("file.delete",String.valueOf(file));
                                         } else {// 目录
                                             /*if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                                                 file.delete();
                                                 Log.i("file.delete",String.valueOf(file));
                                            }*/
                                             Log.i("file",String.valueOf(file));
                                             Toast.makeText(getApplicationContext(), "头像删除成功，保留文件夹"+String.valueOf(file), Toast.LENGTH_SHORT).show();
                                         }
                                 }
                             } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                         }
                }
      }
}
