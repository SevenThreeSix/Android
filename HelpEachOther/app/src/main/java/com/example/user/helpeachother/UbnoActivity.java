package com.example.user.helpeachother;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.helpeachother.Fragment.HomeFragment;
import com.example.user.helpeachother.Fragment.ItemDetailFragment;
import com.example.user.helpeachother.Fragment.SyncFragment;
import com.example.user.helpeachother.dummy.DummyContentRecieve;
import com.example.user.helpeachother.dummy.DummyContentSend;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;

public class UbnoActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    String itemID;
    private DummyContentSend.DummyItem mItem;
    MyConnector mc;
    TextView tv_ubno;
    TextView tv_ubname;
    ImageView ubImage;
    String ubno;
    String ubname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubno);

        itemID =getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID);
        mItem = DummyContentSend.ITEM_MAP.get(itemID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //actionBar.setTitle(mItem.dId);//父标题
        actionBar.setTitle("接受者个人信息");//父标题
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ubno != null) {
                    Snackbar.make(view, "\"Chat with \" " + ubno, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Intent intent = new Intent(UbnoActivity.this, ChatActivity.class);    //创建Intent对象
                    intent.putExtra("ubno", ubno);
                    startActivity(intent);
                }
                else
                    Snackbar.make(view, "用户数据加载中，请稍后重试", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

            }
        });
        initView();
        findUbno(mItem.dId);
    }

    void initView() {
        tv_ubno = (TextView) findViewById(R.id.ubno);
        tv_ubname = (TextView) findViewById(R.id.ubname);
        ubImage = (ImageView) findViewById(R.id.ubimage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ubno, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateImage(ImageView imageView , String uno){
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
                imageView.setImageBitmap(bitmap);
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
    Handler myHandlerUserInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_ubname.setText(ubname);
                    Toast.makeText(getApplicationContext(), "ubname:"+ubname, Toast.LENGTH_SHORT).show();
                    updateImage(ubImage,ubno);
                    Toast.makeText(getApplicationContext(), "对方数据加载完成", Toast.LENGTH_SHORT).show();
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
    Handler myHandlerFindUno = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.i("赋值Textview",ubno);
                    if(ubno.equals("0")) {
                        Toast.makeText(getApplicationContext(), "此任务仍未被领取", Toast.LENGTH_LONG).show();
                        finish();
                    } else
                        Toast.makeText(getApplicationContext(), "ubno:"+ubno+"领取了此条任务", Toast.LENGTH_SHORT).show();
                    tv_ubno.setText(ubno);
                    Log.i("赋值Textview完成",ubno);
                    getUbnoInfo(ubno);
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
    public void findUbno(final String didTemp) {
        new Thread() {
            public void run() {
                //Looper.prepare();
                try {
                    Log.e("GET_UBNO_ENTER","true");
                    Log.i("did",didTemp);
                    mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象
                    if(mc.dout == null) {
                        myHandlerFindUno.sendEmptyMessage(2);
                        return ;
                    }
                    mc.dout.writeUTF("<#FindUbno#>" + didTemp); // 向服务器发出请求

                    ubno = mc.din.readUTF();

                    Log.i("RECIEVE_FINF_UBNO",ubno);
                    myHandlerFindUno.sendEmptyMessage(0); // 发出Handler消息
                } catch (Exception e) {
                    e.printStackTrace();
                    // pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()) {
                        myHandlerFindUno.sendEmptyMessage(1);
                        //Looper.loop();
                        //Looper.myLooper().quit();
                    } else {
                        myHandlerFindUno.sendEmptyMessage(2);
                        //Looper.loop();
                        //Looper.myLooper().quit();
                    }
                }
            }
        }.start();
    }

    // 方法：获取用户信息
    public void getUbnoInfo(final String UserNumber) {
        new Thread() {
            public void run() {
                //Looper.prepare();
                try {
                    Log.i("get_user_ENTER","true");
                    mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象
                    if(mc.dout == null) {
                        myHandlerUserInfo.sendEmptyMessage(2);
                        return ;
                    }
                    mc.dout.writeUTF("<#UserInfo#>" + UserNumber); // 向服务器发出请求

                    String fInfo = mc.din.readUTF();
                    System.out.println(fInfo);
                    String[] infoList = fInfo.split("\\|"); // 分割字符串
                    ubname = infoList[1];


                    myHandlerUserInfo.sendEmptyMessage(0); // 发出Handler消息
                } catch (Exception e) {
                    e.printStackTrace();
                    // pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()) {
                        myHandlerUserInfo.sendEmptyMessage(1);
                        //Looper.loop();
                        //Looper.myLooper().quit();
                    } else {
                        myHandlerUserInfo.sendEmptyMessage(2);
                        //Looper.loop();
                        //Looper.myLooper().quit();
                    }
                }
            }
        }.start();
    }
}
