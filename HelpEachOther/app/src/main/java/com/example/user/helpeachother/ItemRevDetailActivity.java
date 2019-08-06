package com.example.user.helpeachother;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.user.helpeachother.Fragment.ItemDetailFragment;
import com.example.user.helpeachother.Fragment.ItemRevDetailFragment;
import com.example.user.helpeachother.dummy.DummyContentRecieve;

import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link NvgDrawerActivity}.
 */
public class ItemRevDetailActivity extends AppCompatActivity {

    String mUno;
    private DummyContentRecieve.DummyItem mItem;
    String itemID;
    //String d_id;
    MyConnector mc;
    View CL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        CL = findViewById(R.id.detail_drawer_layout);
        itemID =getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);



        SharedPreferences sp = getSharedPreferences("SP",MODE_PRIVATE); // 获得Preferences
        mUno = sp.getString("uno", null);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemRevDetailFragment fragment = new ItemRevDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem = DummyContentRecieve.ITEM_MAP.get(getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
                if(mUno.equals(mItem.uno))
                    Snackbar.make(view, "Can't talk to yourself !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else {
                    Toast.makeText(ItemRevDetailActivity.this, "Chat with " + mItem.uno, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ItemRevDetailActivity.this, ChatActivity.class);    //创建Intent对象
                    intent.putExtra("ubno", mItem.uno);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, itemID);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nvg_drawer_map, menu);
        MenuItem actionRecieve = menu.findItem(R.id.action_recieve);
        actionRecieve.setVisible(false);
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_map) {
            Intent intent = new Intent(ItemRevDetailActivity.this, MapActivity.class);    //创建Intent对象
            //intent.putExtra("ubno", mItem.content.substring(0, 4));
            //intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, itemID);
            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, itemID);
            intent.putExtra("from", "ItemRevDetailActivity");
            //Log.i("from", String.valueOf(getApplicationContext()));
            Toast.makeText(ItemRevDetailActivity.this, String.valueOf(getApplicationInfo()), Toast.LENGTH_SHORT).show();
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_recieve) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("确认接受此任务？");  builder.setTitle("提示");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //Log.d("deleteUno", mUno);
                    mItem = DummyContentRecieve.ITEM_MAP.get(getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
                    //d_id =mItem.dId;
                    if(mUno.equals(mItem.uno))
                        Snackbar.make(CL, "Can't recieve yourself's task !", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else {
                        //Toast.makeText(ItemDetailActivity.this, "Chat with " + mItem.uno, Toast.LENGTH_SHORT).show();
                        Log.i("dId",mItem.dId);
                        recieveTask(mItem.dId);
                    }


                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("HandlerLeak")
    Handler myHandlerRecieve = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ItemRevDetailActivity.this, "接收任务成功", Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Toast.makeText(ItemRevDetailActivity.this, "接收任务失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ItemRevDetailActivity.this, "连接超时", Toast.LENGTH_SHORT).show();

                    break;
            }
            super.handleMessage(msg);
        }
    };
    public void recieveTask(final String d_id) {
        new Thread() {
            @SuppressLint("NewApi")
            public void run() {
                try {
                    mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象


                    if(mc.dout == null) {
                        myHandlerRecieve.sendEmptyMessage(2);
                        return ;
                    }
                    // 向服务器发出请求
                    mc.dout.writeUTF("<#RecieveTask#>" + d_id + "|" + mUno);
                    Log.i("<#RecieveTask#>","<#RecieveTask#>" + d_id + "|" + mUno);
                    String receivedMsg= mc.din.readUTF(); // 读取服务器发来的消息

                    if (receivedMsg.startsWith("<#RecieveTask_SUCCESS#>")) { // 收到的消息为登录成功消息
                        // receivedMsg = receivedMsg.substring(19);
                        // String [] sa = receivedMsg.split("\\|");
                        myHandlerRecieve.sendEmptyMessage(0);
                        //getChatList();
                        // 转到功能面板
                        /*Intent intent = new Intent(ChatActivity.this,
                                ChatActivity.class);
                        intent.putExtra("uano", uano);
                        intent.putExtra("ubno", ubno);
                        startActivity(intent); // 启动功能Activity
                        finish();*/
                    } else if (receivedMsg.startsWith("<#RecieveTask_FAIL#>")) { // 收到的消息为发布失败
                        /*Toast.makeText(ChatActivity.this, "发送消息失败",
                                Toast.LENGTH_LONG).show();
                        Looper.loop();
                        Looper.myLooper().quit();*/
                        myHandlerRecieve.sendEmptyMessage(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//					pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()){
                        myHandlerRecieve.sendEmptyMessage(1);
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                    else{
                        myHandlerRecieve.sendEmptyMessage(2);
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                }

            }
        }.start();
    }
}
