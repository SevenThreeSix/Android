package com.example.user.helpeachother;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.helpeachother.Fragment.HomeFragment;
import com.example.user.helpeachother.Fragment.ItemDetailFragment;
import com.example.user.helpeachother.dummy.DummyContentChat;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;

/**
 * Created by User on 2018/3/9.
 */

public class ChatActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    DummyContentChat DC_Chat;
    MyConnector mc;
    View recyclerView;
    int listsize;
    String uano,ubno;
    AutoCompleteTextView tv_message;
    String message;
    Date date;
    SimpleDateFormat simpleDateFormat;

    public File pathExternal;
    public String stringPackage = "/helpeachother/";
    public String folderPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        pathExternal = Environment.getExternalStorageDirectory();
        folderPath = pathExternal+stringPackage;
        tv_message = (AutoCompleteTextView)findViewById(R.id.message_content);
        SharedPreferences sp = getSharedPreferences("SP",MODE_PRIVATE); // 获得Preferences
        uano = sp.getString("uno",null);
        Intent intent = getIntent(); // 获得启动该Activity的Intent对象
        ubno = intent.getStringExtra("ubno");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("Talking to : "+ubno);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_chat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                if(tv_message.getVisibility()==View.GONE) {
                    tv_message.setVisibility(View.VISIBLE);
                    Toast.makeText(ChatActivity.this, "Edit框弹出", Toast.LENGTH_SHORT).show();

                }
                else if(tv_message.getVisibility()==View.VISIBLE){
                    tv_message.setVisibility(View.GONE);
                    Toast.makeText(ChatActivity.this, "收起", Toast.LENGTH_SHORT).show();

                    message = tv_message.getText().toString();
                    if(TextUtils.isEmpty(message)) {
                        tv_message.requestFocus();
                        tv_message.setError(getString(R.string.error_field_required));
                    }else
                        chat();

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    imm.hideSoftInputFromWindow(tv_message.getApplicationWindowToken(), 0 );//键盘消失

                }
            }
        });

        /*if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }*/
        getChatList();
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
            NavUtils.navigateUpTo(this, new Intent(this, ItemDetailActivity.class)//返回跳转
                    .putExtra(ItemDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID)));//解决 itemdetailactivity 无法正常获取到itemid的问题
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_message.setText("");
                    View recyclerView = findViewById(R.id.item_list_chat);
                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView);
                    ((RecyclerView) recyclerView).scrollToPosition(listsize-1);

                    Toast.makeText(ChatActivity.this, "listsize:"+String.valueOf(listsize), Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Toast.makeText(ChatActivity.this, "读取数据超时", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ChatActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler myHandlerChat = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ChatActivity.this, simpleDateFormat.format(date), Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Toast.makeText(ChatActivity.this, "发送消息失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ChatActivity.this, "连接超时", Toast.LENGTH_SHORT).show();

                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void getChatList() {
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
                    mc.dout.writeUTF("<#CHAT_LIST#>" + uano + "|" + ubno + "|"
                            + "1"); // 向服务器发出请求
                    listsize = mc.din.readInt(); // 读取列表的长度

                    String fInfo = mc.din.readUTF();
                    String[] list = fInfo.split("\\,");
                    DC_Chat = new DummyContentChat();
                    DC_Chat.clearItem();

                    simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");// HH:mm:ss//获取当前时间
                    date = new Date(System.currentTimeMillis());

                    for (int i = 0; i < listsize; i++) { // 循环，获取每个好友的信息和头像
                        // 读取好友信息
                        String[] sa = list[i].split("\\|"); // 分割字符串

                        String time =sa[5];
                        if(time.contains(simpleDateFormat.format(date).substring(0,10)))
                            time =  "今天 "+sa[5].substring(11,16);
                        Log.i("ChatTextGB2312",new String(sa[3].getBytes(),"GB2312"));
                        Log.i("ChatTextUTF-8",new String(sa[3].getBytes(),"UTF-8"));
                        Log.i("ChatText",sa[3]);
                        //dateList.add(sa);
                        if(sa[1].equals(uano))
                            DC_Chat.addItem(DummyContentChat.createDummyItem(i,sa[1],sa[3],0,time));
                        else
                            DC_Chat.addItem(DummyContentChat.createDummyItem(i,sa[1],sa[3],1,time));
                    }
                    myHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
//					pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()){
                        //Toast.makeText(ChatActivity.this, "读取数据超时", Toast.LENGTH_LONG).show();
                        myHandler.sendEmptyMessage(1);
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                    else{
                        myHandler.sendEmptyMessage(2);
                        //Toast.makeText(ChatActivity.this, "连接超时", Toast.LENGTH_LONG).show();
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                }

            }
        }.start();
    }

    public void chat() {
        new Thread() {
            @SuppressLint("NewApi")
            public void run() {
                try {
                    mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象

                    String ctext = tv_message.getEditableText().toString().trim();

                    simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");// HH:mm:ss
                    //获取当前时间
                    date = new Date(System.currentTimeMillis());
                    if(mc.dout == null) {
                        myHandlerChat.sendEmptyMessage(2);
                        return ;
                    }
                    // 向服务器发出请求
                    mc.dout.writeUTF("<#CHAT#>" + uano + "|" + ubno + "|"+ ctext+"|"+simpleDateFormat.format(date));
                    String receivedMsg= mc.din.readUTF(); // 读取服务器发来的消息

                    if (receivedMsg.startsWith("<#WRITECHAT_SUCCESS#>")) { // 收到的消息为登录成功消息
                        // receivedMsg = receivedMsg.substring(19);
                        // String [] sa = receivedMsg.split("\\|");
                        myHandlerChat.sendEmptyMessage(0);
                        getChatList();
                        // 转到功能面板
                        /*Intent intent = new Intent(ChatActivity.this,
                                ChatActivity.class);
                        intent.putExtra("uano", uano);
                        intent.putExtra("ubno", ubno);
                        startActivity(intent); // 启动功能Activity
                        finish();*/
                    } else if (receivedMsg.startsWith("<#PubDate_FAIL#>")) { // 收到的消息为发布失败
                        /*Toast.makeText(ChatActivity.this, "发送消息失败",
                                Toast.LENGTH_LONG).show();
                        Looper.loop();
                        Looper.myLooper().quit();*/
                        myHandlerChat.sendEmptyMessage(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//					pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()){
                        myHandlerChat.sendEmptyMessage(1);
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                    else{
                        myHandlerChat.sendEmptyMessage(2);
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                }

            }
        }.start();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContentChat.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final ChatActivity mParentActivity;
        private final List<DummyContentChat.DummyItem> mValues;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContentChat.DummyItem item = (DummyContentChat.DummyItem) view.getTag();
                /*if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }*/
            }
        };

        SimpleItemRecyclerViewAdapter(ChatActivity parent,
                                      List<DummyContentChat.DummyItem> items
                                      ) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == DummyContentChat.RIGHT) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_content_chat_right, parent, false);
                return new ChatRightViewHolder(view);
            }
            else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_content_chat_left, parent, false);
                return new ChatLeftViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(holder instanceof ChatLeftViewHolder) {
                ((ChatLeftViewHolder) holder).mIdView.setText(mValues.get(position).uno);
                ((ChatLeftViewHolder) holder).mContentView.setText(mValues.get(position).text);
                updateHolderImage(holder,mValues.get(position).uno);
                //((ChatLeftViewHolder) holder).mImageViewView.setText(mValues.get(position).text);
                if(position == 0) {
                    ((ChatLeftViewHolder) holder).mTime.setText(mValues.get(position).time);
                    ((ChatLeftViewHolder) holder).mTime.setVisibility(View.VISIBLE);
                }
                else if(mValues.get(position).time.equals(mValues.get(position-1).time))
                    ((ChatLeftViewHolder) holder).mTime.setVisibility(View.GONE);
                else{
                    ((ChatLeftViewHolder) holder).mTime.setText(mValues.get(position).time);
                    ((ChatLeftViewHolder) holder).mTime.setVisibility(View.VISIBLE);
                }

                //holder.itemView.setTag(mValues.get(position));
                //holder.itemView.setOnClickListener(mOnClickListener);
            }
            else if(holder instanceof ChatRightViewHolder) {
                ((ChatRightViewHolder) holder).mIdView.setText(mValues.get(position).uno);
                ((ChatRightViewHolder) holder).mContentView.setText(mValues.get(position).text);
                updateHolderImage(holder,mValues.get(position).uno);
                if(position == 0) {
                    ((ChatRightViewHolder) holder).mTime.setText(mValues.get(position).time);
                    ((ChatRightViewHolder) holder).mTime.setVisibility(View.VISIBLE);
                }
                else if(mValues.get(position).time.equals(mValues.get(position-1).time))
                    ((ChatRightViewHolder) holder).mTime.setVisibility(View.GONE);
                else{
                    ((ChatRightViewHolder) holder).mTime.setText(mValues.get(position).time);
                    ((ChatRightViewHolder) holder).mTime.setVisibility(View.VISIBLE);
                }
            }
        }
        public void updateHolderImage(RecyclerView.ViewHolder holder, String uno){
            String localIconNormal = folderPath+uno+".jpg";//getContext().getFilesDir().getAbsolutePath()+ File.separator+mValues.get(position).ubno+".jpg";//"/data/data/com.android.factorypattern/FPTest.bmp";
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
                    if(holder instanceof ChatLeftViewHolder) {
                        ((ChatLeftViewHolder) holder).mImageView.setImageBitmap(bitmap);
                    }else if(holder instanceof ChatRightViewHolder)
                        ((ChatRightViewHolder) holder).mImageView.setImageBitmap(bitmap);
                    //holder.headImage.setBackgroundResource(R.drawable.face);
                    if (localStream != null) {
                        localStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mValues.get(position).getType();
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ChatLeftViewHolder extends RecyclerView.ViewHolder {
            TextView mIdView;
            ImageView mImageView;
            TextView mContentView;
            TextView mTime;

            ChatLeftViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.tv_item_send_uno_left);
                mContentView = (TextView) view.findViewById(R.id.tv_item_send_txt_left);
                mImageView = (ImageView) view.findViewById(R.id.hisimage);
                mTime = (TextView) view.findViewById(R.id.tv_send_msg_date);

            }
        }

            class ChatRightViewHolder extends RecyclerView.ViewHolder {
                TextView mIdView;
                ImageView mImageView;
                TextView mContentView;
                TextView mTime;

                ChatRightViewHolder(View view) {
                    super(view);
                    mIdView = (TextView) view.findViewById(R.id.tv_item_send_uno_right);
                    mContentView = (TextView) view.findViewById(R.id.tv_item_send_txt_right);
                    mImageView = (ImageView) view.findViewById(R.id.myimage);
                    mTime = (TextView) view.findViewById(R.id.tv_send_msg_date);

                }
            }
    }
}
