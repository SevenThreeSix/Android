package com.example.user.helpeachother.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment ;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.helpeachother.ChatActivity;
import com.example.user.helpeachother.ItemDetailActivity;
import com.example.user.helpeachother.MyConnector;
import com.example.user.helpeachother.NvgDrawerActivity;
import com.example.user.helpeachother.R;
import com.example.user.helpeachother.ReleaseActivity;

import com.example.user.helpeachother.dummy.DummyContentFriendList;
import com.example.user.helpeachother.dummy.SpaceItemDecoration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
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

    private NotificationsFragment.OnFragmentInteractionListener mListener;

    MyConnector mc;

    int listsize;

    RecyclerView recyclerView;

    DummyContentFriendList DC;

    private View mProgressView;
    private View mFormView;

    private SwipeRefreshLayout refreshLayout;

    String []friendUnoList;
    int friendUnoListNo=0;

    //int headSize;
    private Bitmap headBitmap;
    File f;
    public File pathExternal;
    public String stringPackage = "/helpeachother/";
    public String folderPath;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboardragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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
        pathExternal = Environment.getExternalStorageDirectory();
        folderPath = pathExternal+stringPackage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        mFormView = rootView.findViewById(R.id.item_list);
        mProgressView = rootView.findViewById(R.id.refresh_progress);
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe);

        recyclerView = rootView.findViewById(R.id.item_list);
        recyclerView.addItemDecoration(new SpaceItemDecoration(5,getContext()));

        FloatingActionButton fab = rootView.findViewById(R.id.fab_refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProgress(true);
                getFriendList();
                //Snackbar.make(view, "刷新ing", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        });
        refreshLayout.setOnRefreshListener(this); // 设置刷新监听
        refreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.purple,R.color.swipefefresh_bg); // 进度动画颜色
        showProgress(true);
        //getUser("2012");
        //getUser("2003");
        //getUser("2004");
        //etUser("2005");
        //getUser("2007");
        getFriendList();

        //getUser("2012");

        return rootView;

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*mFormView.s
            etVisibility(show ? View.GONE : View.VISIBLE);
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



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotificationsFragment.OnFragmentInteractionListener) {
            mListener = (NotificationsFragment.OnFragmentInteractionListener) context;
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
        getFriendList();
        /*refreshLayout.postDelayed(new Runnable() { // 发送延迟消息到消息队列
            @Override
            public void run() {
                refreshLayout.setRefreshing(false); // 是否显示刷新进度;false:不显示
            }
        },3000);*/
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
        recyclerView.setAdapter(new NotificationsFragment.SimpleItemRecyclerViewAdapter(DummyContentFriendList.ITEMS, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<NotificationsFragment.SimpleItemRecyclerViewAdapter.ViewHolder> {

        //private final NvgDrawerActivity mParentActivity;
        private final List<DummyContentFriendList.DummyItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("xyz ",String.valueOf(getItemCount()));
                DummyContentFriendList.DummyItem item = (DummyContentFriendList.DummyItem) view.getTag();
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
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("ubno", item.ubno);

                context.startActivity(intent);
                //}
            }
        };

        SimpleItemRecyclerViewAdapter(
                List<DummyContentFriendList.DummyItem> items,
                boolean twoPane) {
            mValues = items;

            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            /*View View1 =parent.findViewById ( R.id.item_list_content ) ;  //在hidden_view.xml中hidden_layout是root layout
            if ( null != hiddenView ) {
                ViewGroup parent = ( ViewGroup ) hiddenView.getPatent() ;
                parent.remove ( hiddenView ) ;
            }*/
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }



        @Override
        public void onBindViewHolder(final NotificationsFragment.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).id);
            StringBuilder builder = new StringBuilder();
            builder.append("昵称:"+mValues.get(position).ubname);
            /*WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            Toast.makeText(getActivity(), "宽度："+dm.widthPixels, Toast.LENGTH_LONG).show();*/
            /*while(builder.toString().length()<32)//42
                builder.append(" ");*/
            holder.mContentView.setText(builder.toString());

            holder.mTime.setVisibility(View.GONE);

            holder.mContentView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.mContentView.setSingleLine(true);
            holder.mContentView.setSelected(true);
            holder.mContentView.setFocusable(true);
            holder.mContentView.setFocusableInTouchMode(true);
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

            holder.item_uno.setText("Uno："+mValues.get(position).ubno);
            updateHolderImage(holder,mValues.get(position).ubno);
                //getUser(mValues.get(position).ubno);
            //holder.headImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //holder.headImage.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
            //getUser(mValues.get(position).ubno);
            /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Log.e("StringOfItemBitmap",String.valueOf(mValues.get(position).bitmap));
            mValues.get(position).bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();
            Log.e("",Arrays.toString(datas));*/

            /*if(mValues.get(position).bitmap == null) {
                Resources res = getResources();
                Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.face);
                holder.headImage.setImageBitmap(bmp);
            }else{
                holder.headImage.setImageBitmap(mValues.get(position).bitmap);
            }*/

        }
        public void updateHolderImage(final NotificationsFragment.SimpleItemRecyclerViewAdapter.ViewHolder holder,String uno){
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

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView mTime;
            final TextView item_uno;
            final ImageView headImage;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.tv_content);
                mTime = (TextView) view.findViewById(R.id.tv_time);
                item_uno = (TextView)view.findViewById(R.id.tv_uno);
                headImage = (ImageView)view.findViewById(R.id.head_image_view);

            }
        }
    }
    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //friendUnoListNo =0;
                    //getUser(friendUnoList[friendUnoListNo]);

                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView);
                    // recyclerView.notifyAll();
                    Toast.makeText(getActivity(), "listsize:"+String.valueOf(listsize), Toast.LENGTH_LONG).show();
                    showProgress(false);
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "刷新完成", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getActivity(), "读取数据超时", Toast.LENGTH_LONG).show();
                    //showProgress(false);
                    break;
                case 2:
                    Toast.makeText(getActivity(), "连接超时", Toast.LENGTH_LONG).show();
                    //showProgress(false);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /*@SuppressLint("HandlerLeak")
    Handler myHandlerHead = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    if(++friendUnoListNo<listsize) {
                        Log.i("friendUnoListNo",String.valueOf(friendUnoListNo));
                        getUser(friendUnoList[friendUnoListNo]);
                        Toast.makeText(getContext(), "成功读取"+friendUnoList[friendUnoListNo]+"头像", Toast.LENGTH_SHORT).show();
                    }else
                        //Log.i("friendUnoListNo",String.valueOf(friendUnoListNo));
                        //getUserDown = true;
                        Toast.makeText(getContext(), "读取头像结束", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), "headSize："+headSize, Toast.LENGTH_SHORT).show();
                    //getFriendList();
                    break;
                case 1:
                    Toast.makeText(getContext(), "读取数据超时", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getContext(), "连接超时", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getContext(), "文件已存在", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };*/


    public void getFriendList() {
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
                    mc.dout.writeUTF("<#FRIEND_LIST#>" + uno ); // 向服务器发出请求
                    listsize = mc.din.readInt(); // 读取列表的长度
                    Log.i("FriendListSize",String.valueOf(listsize));
                    //friendUnoList =new String[listsize];

                    /*headList = null;
                    headList = new Bitmap[size];
                    dateList = null; // 初始化好友头像列表
                    dateList = new ArrayList<String[]>(size); // 初始化好友信息列表*/

                    String fInfo = mc.din.readUTF();
                    String[] list = fInfo.split("\\,");
                    DC = new DummyContentFriendList();
                    DC.clearItem();
                    //getUser("2012");
                    for (int i = 0; i < listsize; i++) { // 循环，获取每个好友的信息和头像
                        // 读取好友信息
                        String[] sa = list[i].split("\\|"); // 分割字符串
                        //friendUnoList[i]= sa[0];
                        Log.i("UNO",sa[0]);
                        //dateList.add(sa);
                        //getUser(sa[0]);
                        //("2003");
                        //getUser("2012");
                        DC.addItem(DummyContentFriendList.createDummyItem(i,sa[0],sa[1]));

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
                        myHandler.sendEmptyMessage(1);
                    }
                    else{
                        myHandler.sendEmptyMessage(2);
                    }
                }

            }
        }.start();
    }
    /*public void getUser(final String unoTemp) {
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
                    myHandler.sendEmptyMessage(3);
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
                            myHandler.sendEmptyMessage(2);
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
                        //BufferedInputStream bis = new BufferedInputStream(getAssets().open(UserNumber + ".jpg"));
                        //headBitmap = BitmapFactory.decodeStream(buf,null,);
                        //bitmapArray = Base64.decode(string, Base64.DEFAULT);

                        headBitmap = BitmapFactory.decodeByteArray(buf, 0, headSize);

                        //f = new File(newPath);
                        f = new File(folderPath, unoTemp+".jpg");//

                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        headBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
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
                        myHandlerHead.sendEmptyMessage(0); // 发出Handler消息
                    } catch (Exception e) {
                        e.printStackTrace();
                        // pd.dismiss();
                        if (!mc.socket.isClosed() && mc.socket.isConnected()) {
                            myHandlerHead.sendEmptyMessage(1);
                            Log.e("读取头像超时",unoTemp+".jpg");
                            //Looper.loop();
                            //Looper.myLooper().quit();
                        } else {
                            myHandlerHead.sendEmptyMessage(2);
                            //Looper.loop();
                            //Looper.myLooper().quit();
                        }
                    }
                }
            }
        }.start();
        }*/

}
