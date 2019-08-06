package com.example.user.helpeachother.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.user.helpeachother.MyConnector;
import com.example.user.helpeachother.R;
import com.example.user.helpeachother.ReleaseActivity;
import com.example.user.helpeachother.ItemRevDetailActivity;
import com.example.user.helpeachother.dummy.DummyContentRecieve;
import com.example.user.helpeachother.dummy.SpaceItemDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SyncFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SyncFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SyncFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String uno;

    private boolean mTwoPane;

    private View rootView;

    private OnFragmentInteractionListener mListener;

    MyConnector mc;

    int listsize;

    RecyclerView recyclerView;

    DummyContentRecieve DC;

    private SwipeRefreshLayout refreshLayout;

    ImageView delete_image_view;

    Date nowDate;
    SimpleDateFormat simpleDateFormat;
    private View mProgressView;
    public SyncFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SyncFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SyncFragment newInstance(String param1, String param2) {
        SyncFragment fragment = new SyncFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (uno == null)
                uno = getArguments().getString("uno");
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_syncfragment, container, false);

        mProgressView = rootView.findViewById(R.id.refresh_progress);
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe);

        refreshLayout.setOnRefreshListener(this); // 设置刷新监听
        refreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.purple,R.color.swipefefresh_bg); // 进度动画颜色

        recyclerView = rootView.findViewById(R.id.item_list);
        recyclerView.addItemDecoration(new SpaceItemDecoration(5,getContext()));

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* for (int i = 1; i <= 4; i++) {
                    DummyContent.addItem(DummyContent.createDummyItem(i,"test"));
                }*/

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getActivity(),ReleaseActivity.class);	//创建Intent对象
                intent.putExtra("uno", uno);
                startActivity(intent);
            }
        });

        /*for (int i = 1; i <= 4; i++) {
            DummyContent.addItem(DummyContent.createDummyItem(i));
        }*/
        /*if(DC != null)
            Snackbar.make(rootView, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
        showProgress(true);
        getDateList();



        return rootView;

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            /*mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //recyclerView = rootView.findViewById(R.id.item_list);
                    assert recyclerView != null;
                    //recyclerView.addItemDecoration(new SpaceItemDecoration(30,getContext()));
                    setupRecyclerView((RecyclerView) recyclerView);
                    Toast.makeText(getActivity(), "listsize:"+String.valueOf(listsize), Toast.LENGTH_SHORT).show();
                    showProgress(false);
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "刷新完成", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getActivity(), "读取数据超时", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "连接超时", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void getDateList() {
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

                    mc.dout.writeUTF("<#DATE_LIST#>" + uno + "|" + "1|"+uno); // 向服务器发出请求
                    listsize = mc.din.readInt(); // 读取列表的长度
                    /*headList = null;
                    headList = new Bitmap[size];
                    dateList = null; // 初始化好友头像列表
                    dateList = new ArrayList<String[]>(size); // 初始化好友信息列表*/

                    String fInfo = mc.din.readUTF();
                    String[] list = fInfo.split("\\,");
                    DC = new DummyContentRecieve();
                    DC.clearItem();

                    simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");// HH:mm:ss//获取当前时间
                    //date = new Date(System.currentTimeMillis());

                    for (int i = 0; i < listsize; i++) { // 循环，获取每个好友的信息和头像
                        // 读取好友信息
                        String[] sa = list[i].split("\\|"); // 分割字符串
                        //dateList.add(sa);
                        /*String time =sa[8];
                        if(time.contains(simpleDateFormat.format(date).substring(0,10)))
                            time =  "今天 "+sa[8].substring(11,16);
                        else
                            time = time.substring(5,16);//2018/不显示，保证时间显示美观*/
                        DC.addItem(DummyContentRecieve.createDummyItem(i,sa[0],sa[7],sa[1],sa[3],sa[4],sa[8],sa[9]));

                        // 将好友信息添加到相应的列表中
                        // int headSize = mc.din.readInt(); //读取头像大小
                        // byte[] buf = new byte[headSize]; //创建缓冲区
                        // mc.din.read(buf); //读取头像信息
                        // headList[i] = BitmapFactory.decodeByteArray(buf, 0,
                        // headSize);
                        // headList[i] =
                        // BitmapFactory.decodeFile("/sdcard/date.data/"+sa[7]+".jpg");
                        // //从本地取图片
                        /*BufferedInputStream bis = new BufferedInputStream(
                                getAssets().open(sa[7] + ".jpg"));
                        headList[i] = BitmapFactory.decodeStream(bis);*/
                    }

                    // 读取用户信息
                    /*mc.dout.writeUTF("<#UserInfo#>" + uno); // 向服务器发出请求

                    String fInfo1 = mc.din.readUTF(); // 读取自己信息
                    infoList = fInfo1.split("\\|"); // 分割字符串
*/
                    myHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
//					pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()){
                        Toast.makeText(getActivity(), "读取数据超时", Toast.LENGTH_LONG).show();
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                    else{
                        Toast.makeText(getActivity(), "连接超时", Toast.LENGTH_LONG).show();
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                }

            }
        }.start();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        showProgress(true);
        getDateList();
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContentRecieve.ITEMS, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        //private final NvgDrawerActivity mParentActivity;
        private final List<DummyContentRecieve.DummyItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("xyz ",String.valueOf(getItemCount()));
                DummyContentRecieve.DummyItem item = (DummyContentRecieve.DummyItem) view.getTag();
                /*if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {*/
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemRevDetailActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                context.startActivity(intent);
                //}
            }
        };

       /* private final View.OnClickListener ImageOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"删除item !",Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "删除item !", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                mValues.remove(item);
                notifyItemRemoved(item);
                notifyItemRangeChanged(0,mValues.size());
            }
        };*/

        SimpleItemRecyclerViewAdapter(
                List<DummyContentRecieve.DummyItem> items,
                boolean twoPane) {
            mValues = items;

            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);
            holder.dId.setText(mValues.get(position).dId);

            holder.mContentView.setEllipsize(TextUtils.TruncateAt.MARQUEE);//跑马灯效果
            holder.mContentView.setSingleLine(true);
            holder.mContentView.setSelected(true);
            holder.mContentView.setFocusable(true);
            holder.mContentView.setFocusableInTouchMode(true);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.delete_image_view.setVisibility(View.VISIBLE);
            holder.delete_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("归还任务？");  builder.setTitle("提示");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Log.d("position", String.valueOf(position));
                            Log.d("getAdapterPosition", String.valueOf(holder.getAdapterPosition()));
                            backTask(mValues.get(position).dId);
                            mValues.remove(holder.getAdapterPosition());//-1
                            notifyItemRemoved(holder.getAdapterPosition());
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
            //holder.item.setBackgroundColor(R.color.orange);
            holder.item_uno.setText(mValues.get(position).uno);
            String time =mValues.get(position).time;
            nowDate = new Date(System.currentTimeMillis());
            if(time.contains(simpleDateFormat.format(nowDate).substring(0,10)))
                time =  "今天 "+time.substring(11,16);
            else
                time = time.substring(5,16);//2018/不显示，保证时间显示美观
            holder.time.setText(time);

            holder.deadLine.setVisibility(View.VISIBLE);
            holder.deadLineTime.setText("截止时间："+mValues.get(position).deadline);

            String RemainTime = calRemainTime(nowDate,mValues.get(position).deadline,true);
            Log.i("RemainTime",RemainTime);
            if(RemainTime.equals("已过期")){
                holder.remainAndSum.setText("任务已过期");
                holder.remainAndSum.setTextColor(Color.parseColor("#FF0033"));//String.valueOf(R.color.red))
            }else {
                holder.remainAndSum.setTextColor(Color.parseColor("#000000"));//String.valueOf(R.color.red))
                //vb holder.remainAndSum.setTextColor(R.color.black);
                holder.remainAndSum.setText("剩余" + RemainTime + "/" + calRemainTime(new Date(mValues.get(position).time), mValues.get(position).deadline, true));
            }

            holder.linearLayout_progress.setVisibility(View.VISIBLE);
            String max = calRemainTime(new Date(mValues.get(position).time),mValues.get(position).deadline,false);
            String status = calRemainTime(nowDate,mValues.get(position).deadline,false);
            holder.progressBar.setMax(Integer.valueOf(max));
            Log.i("max",max);
            Log.i("status",status);
            holder.progressBar.setProgress(Integer.valueOf(status));//Integer.valueOf(Double.valueOf(status).doubleValue()/Double.valueOf(max).doubleValue()*100));
            updateHolderImage(holder,mValues.get(position).uno);
        }
        public void updateHolderImage(final SyncFragment.SimpleItemRecyclerViewAdapter.ViewHolder holder,String uno){
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
                    holder.headImage.setImageBitmap(bitmap);
                    //holder.headImage.setBackgroundResource(R.drawable.face);
                    if (localStream != null) {
                        localStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public  String calRemainTime(Date DateOfFromTime, String toTime,boolean modle) {
            String StringOfFromTime = simpleDateFormat.format(DateOfFromTime);
            //String toDate = simpleDateFormat.format(new Date(toTime));

            //Log.i("FromDate",fromDate);
            long  from = 0;
            try {
                from = simpleDateFormat.parse(StringOfFromTime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long to = 0;
            try {
                to = simpleDateFormat.parse(toTime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int days = (int) ((to - from)/(1000 * 60 * 60 * 24));
            int hours = (int) ((to - from)/(1000 * 60 * 60));
            int minutes = (int) ((to - from)/(1000 * 60));
            if (modle) {
                if(days<0 || hours<0 || minutes<0)
                    return "已过期";
                if (days == 0) {
                    if (hours != 0)
                        return String.valueOf(hours) + "小时";
                    else
                        return String.valueOf(minutes) + "分钟";
                } else
                    return String.valueOf(days) + "天";
            }
            else {
                if(minutes >= 0)
                    return String.valueOf(minutes);
                else
                    return String.valueOf(0);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final ImageView delete_image_view;
            final TextView item_uno;
            final TextView dId;
            final TextView time;
            final LinearLayout deadLine;
            final TextView deadLineTime;
            final TextView remainAndSum;
            final LinearLayout linearLayout_progress;
            final ProgressBar progressBar;
            final ImageView headImage;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.tv_content);
                delete_image_view =(ImageView)view.findViewById(R.id.delete_image_view) ;
                item_uno = (TextView)view.findViewById(R.id.tv_uno);
                dId = (TextView)view.findViewById(R.id.did);
                time = (TextView)view.findViewById(R.id.tv_time);
                deadLine = (LinearLayout)view.findViewById(R.id.linearLayout_deadline);
                deadLineTime = (TextView)view.findViewById(R.id.deadline);
                remainAndSum = (TextView)view.findViewById(R.id.RemainAndSum);
                linearLayout_progress  = (LinearLayout)view.findViewById(R.id.linearLayout_progress);
                progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
                headImage = (ImageView)view.findViewById(R.id.head_image_view);
            }
        }
    }
    @SuppressLint("HandlerLeak")
    Handler myHandlerBack = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getContext(), "任务归还成功", Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Toast.makeText(getContext(), "任务归还失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getContext(), "连接超时", Toast.LENGTH_SHORT).show();

                    break;
            }
            super.handleMessage(msg);
        }
    };
    public void backTask(final String d_id) {
        new Thread() {
            @SuppressLint("NewApi")
            public void run() {
                try {
                    mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT); // 创建MyConnector对象


                    if(mc.dout == null) {
                        myHandlerBack.sendEmptyMessage(2);
                        return ;
                    }
                    // 向服务器发出请求
                    mc.dout.writeUTF("<#RecieveTask#>" + d_id + "|" + 0);
                    Log.i("<#RecieveTask#>","<#RecieveTask#>" + d_id + "|" + 0);
                    String receivedMsg= mc.din.readUTF(); // 读取服务器发来的消息

                    if (receivedMsg.startsWith("<#RecieveTask_SUCCESS#>")) { // 收到的消息为登录成功消息
                        // receivedMsg = receivedMsg.substring(19);
                        // String [] sa = receivedMsg.split("\\|");
                        myHandlerBack.sendEmptyMessage(0);
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
                        myHandlerBack.sendEmptyMessage(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//					pd.dismiss();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()){
                        myHandlerBack.sendEmptyMessage(1);
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                    else{
                        myHandlerBack.sendEmptyMessage(2);
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                }

            }
        }.start();
    }

}
