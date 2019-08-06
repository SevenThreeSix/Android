package com.example.user.ble;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Random;

import static com.example.user.ble.MyApplication.getContext;


public class RecordActivity extends Activity {

    private static final String TAG = "RecordActivity";
    private static final int SPO2 = 0;
    private static final int HEART_RATE = 1;
    RecyclerView recycler_view_spO2,recycler_view_heartRate;
    LinearLayoutManager linearLayoutManager;

    Paint barPencilFirst = new Paint();
    Paint barPencilSecond = new Paint();
    Paint peakPencilFirst = new Paint();
    Paint peakPencilSecond = new Paint();

    Paint xAxisPencil = new Paint();

    TextView test1;
    MySQLiteOpenHelper helper;

    int numberOfRecycleItem,numberOfRecycleItemInerCount;

    int spO2MaxValue,spO2MinValue;
    int heartRateMaxValue,heartRateMinValue;

    LinearLayout recordlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        test1 = (TextView)findViewById(R.id.test1);
        //simpleWaveform = (SimpleWaveform) findViewById(R.id.simplewaveform);
        recycler_view_spO2 = (RecyclerView)findViewById(R.id.recycler_view_spO2);
        recycler_view_heartRate = (RecyclerView)findViewById(R.id.recycler_view_heartRate);

        recordlayout =(LinearLayout)findViewById(R.id.recordlayout);
        helper = new MySQLiteOpenHelper(RecordActivity.this, "myData.db", null, 1);
        SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select max(what) from table_mydata",null);
        //Cursor cursor = sqliteDatabase.rawQuery("select max(what) from table_mydata",null);
        cursor.moveToFirst();
        numberOfRecycleItem = cursor.getInt(0);
        //cursor.close();

        cursor = sqliteDatabase.rawQuery("select count(*) from (select count(*) from table_mydata  where what = 1 group by time)",null);
        cursor.moveToFirst();
        numberOfRecycleItemInerCount=cursor.getInt(0);
        Log.e(TAG,"what max："+String.valueOf(numberOfRecycleItem)+"|分类计数："+String.valueOf(numberOfRecycleItemInerCount));
        test1.setText("what max："+String.valueOf(numberOfRecycleItem)+"|分类计数："+String.valueOf(numberOfRecycleItemInerCount));
        //cursor.close();
        cursor = sqliteDatabase.rawQuery("select max(spO2) from table_mydata",null);
        cursor.moveToFirst();
        spO2MaxValue=cursor.getInt(0);
        Log.e(TAG,"spO2MaxValue:"+spO2MaxValue);

        cursor = sqliteDatabase.rawQuery("select min(spO2) from table_mydata",null);
        cursor.moveToFirst();
        spO2MinValue=cursor.getInt(0);
        Log.e(TAG,"spO2MinValue:"+spO2MinValue);

        cursor = sqliteDatabase.rawQuery("select max(heartRate) from table_mydata",null);
        cursor.moveToFirst();
        heartRateMaxValue=cursor.getInt(0);
        Log.e(TAG,"heartRateMaxValue:"+heartRateMaxValue);

        cursor = sqliteDatabase.rawQuery("select min(heartRate) from table_mydata",null);
        cursor.moveToFirst();
        heartRateMinValue=cursor.getInt(0);
        Log.e(TAG,"heartRateMinValue:"+heartRateMinValue);
        cursor.close();
        sqliteDatabase.close();

        demoAdvance2();
    }

    private void demoAdvance2() {

        LinkedList<LinkedList<Integer>> amp_list_list_spO2 = new LinkedList();
        LinkedList<LinkedList<Integer>> amp_list_list_heartRate = new LinkedList();
        LinkedList<String> timeList = new LinkedList<>();
        final LinkedList<Integer> width = new LinkedList<>();
        for (int i = 0; i < numberOfRecycleItem; i++) {

            LinkedList<Integer> ampList_spO2 = new LinkedList<>();
            LinkedList<Integer> ampList_heartRate = new LinkedList<>();
            amp_list_list_spO2.add(ampList_spO2);
            amp_list_list_heartRate.add(ampList_heartRate);
            helper = new MySQLiteOpenHelper(RecordActivity.this, "myData.db", null, 1);
            SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
            Cursor cursor = sqliteDatabase.rawQuery("select * from table_mydata  where what = ? group by time",new String[]{String.valueOf(i+1)});
            while(cursor.moveToNext()) {
                Log.e(TAG,"demoAdvance2()—sqlite—spo2Read:"+String.valueOf(cursor.getInt(1)));
                ampList_spO2.add((cursor.getInt(1)-80)*100/(100-80));
                //ampList_spO2.add((cursor.getInt(1)-spO2MinValue)*100/(spO2MaxValue-spO2MinValue));
                Log.e(TAG,"TEMP:"+String.valueOf((cursor.getInt(1)-spO2MinValue)*100/(spO2MaxValue-spO2MinValue)));
                ampList_heartRate.add(cursor.getInt(2)*100/150);//cursor.getInt(2)*100/150

            }
            cursor.moveToFirst();

            timeList.add( cursor.getString(3).substring(11,19));

            Cursor cursor2 = sqliteDatabase.rawQuery("select count(*) from (select count(*) from table_mydata  where what = ? group by time)",new String[]{String.valueOf(i+1)});
            cursor2.moveToFirst();
            Log.e(TAG,"Recycleview"+i+":"+String.valueOf(cursor2.getInt(0)));
            width.add(cursor2.getInt(0));
        }


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_view_spO2.setLayoutManager(linearLayoutManager);//attached to a recycle view
        RecyclerViewAdapter waveAdapter = new RecyclerViewAdapter(SPO2,amp_list_list_spO2,timeList,width);
        recycler_view_spO2.setAdapter(waveAdapter);
        recycler_view_spO2.scrollToPosition(numberOfRecycleItem-1);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_view_heartRate.setLayoutManager(linearLayoutManager);
        final RecyclerViewAdapter waveAdapter2 = new RecyclerViewAdapter(HEART_RATE,amp_list_list_heartRate,timeList,width);
        recycler_view_heartRate.setAdapter(waveAdapter2);
        recycler_view_heartRate.scrollToPosition(numberOfRecycleItem-1);
        recycler_view_heartRate.getWidth();
        Snackbar.make(recordlayout, "已滚动至最新记录", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        //test1.setText(String.valueOf(recycler_view_heartRate.getWidth()));
        test1.setText(String.valueOf(recycler_view_heartRate.computeVerticalScrollRange()));
        final int[] temp = {0};
        recycler_view_heartRate.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i(TAG, "-----------onScrollStateChanged-----------");
                Log.i(TAG, "newState: " + newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//判断是当前layoutManager是否为LinearLayoutManager
//只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    //test1.setText(String.valueOf(firstItemPosition));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "-----------onScrolled-----------");
                Log.i(TAG, "dx: " + dx);
                temp[0] +=dx;
                //test1.setText(String.valueOf(temp[0]));
                //test1.setText(String.valueOf(recycler_view_heartRate.getWidth()));
                //test1.setText(String.valueOf(width.get(0)+width.get(1)+width.get(2)));
                //test1.setText(String.valueOf(recyclerView.getWidth()));
                Log.i(TAG, "dy: " + dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int position = linearManager.findFirstVisibleItemPosition();
                    View firstVisiableChildView = linearManager.findViewByPosition(position);
                    int itemWidth = firstVisiableChildView.getWidth();

                    //test1.setText(String.valueOf((position) * itemWidth - firstVisiableChildView.getLeft()));
                }
                /*LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();
                View firstVisiableChildView = layoutManager.findViewByPosition(position);
                int itemWidth = firstVisiableChildView.getWidth();
                test1.setText(String.valueOf((position) * itemWidth - firstVisiableChildView.getTop()));*/
                // Log.i(TAG, "CHECK_SCROLL_UP: " + recyclerView.canScrollVertically(TAG_CHECK_SCROLL_UP));
               // Log.i(TAG, "CHECK_SCROLL_DOWN: " + recyclerView.canScrollVertically(TAG_CHECK_SCROLL_DOWN));
            }
        });
    }
    private int randomInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        // List<LvRowFile> listItems;
        LinkedList<LinkedList<Integer>> amp_list_list;
        LinkedList<String> timeList;
        LinkedList<Integer> width;
        Integer model;

        public RecyclerViewAdapter(Integer model,LinkedList<LinkedList<Integer>> amp_list_list,LinkedList<String> timeList,LinkedList<Integer> width) {
            this.amp_list_list = amp_list_list;
            this.timeList = timeList;
            this.width = width;
            this.model = model;
        }



        private final View.OnClickListener mOnClickListener_spO2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"getItemCount() item总数："+String.valueOf(getItemCount()));
                //test1.setText(String.valueOf(view.getTag()));
                //final EditText et = new EditText(RecordActivity.this);

                Toast.makeText(getContext(),String.valueOf(view.getTag()),Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                builder.setTitle("数据报表");
                //builder.setView(et);
                StringBuffer dataStringBuffer = new StringBuffer();
                helper = new MySQLiteOpenHelper(RecordActivity.this, "myData.db", null, 1);
                SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
                Cursor cursor = sqliteDatabase.rawQuery("select * from table_mydata where id = (select min(id) from table_mydata  where what = ? group by what)",new String[]{String.valueOf((int)view.getTag()+1)});

               /* while(cursor.moveToNext()) {
                    Log.e(TAG,"demoAdvance2()—sqlite—spo2Read:"+String.valueOf(cursor.getInt(1)));
                    ampList_spO2.add(cursor.getInt(1));
                    ampList_heartRate.add(cursor.getInt(2)*100/150);

                }*/
                cursor.moveToFirst();
                dataStringBuffer.append("记录时间："+cursor.getString(3)+"\n");

                //cursor.close();
                //sqliteDatabase.close();
                cursor = sqliteDatabase.rawQuery("select count(*) from table_mydata  where what = ? and spO2>=90 and spO2<=99",new String[]{String.valueOf((int)view.getTag()+1)});
                cursor.moveToFirst();

                dataStringBuffer.append("spO2区间【90-99】："+cursor.getString(0)+"\n");

                builder.setMessage(dataStringBuffer);

                builder.create().show();
            }
        };

        private final View.OnClickListener mOnClickListener_heartRate = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"getItemCount() item总数："+String.valueOf(getItemCount()));
                //test1.setText(String.valueOf(view.getTag()));
                //final EditText et = new EditText(RecordActivity.this);

                Toast.makeText(getContext(),String.valueOf(view.getTag()),Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                builder.setTitle("数据报表");
                //builder.setView(et);
                StringBuffer dataStringBuffer = new StringBuffer();
                helper = new MySQLiteOpenHelper(RecordActivity.this, "myData.db", null, 1);
                SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
                Cursor cursor = sqliteDatabase.rawQuery("select * from table_mydata where id = (select min(id) from table_mydata  where what = ? group by what)",new String[]{String.valueOf((int)view.getTag()+1)});

               /* while(cursor.moveToNext()) {
                    Log.e(TAG,"demoAdvance2()—sqlite—spo2Read:"+String.valueOf(cursor.getInt(1)));
                    ampList_spO2.add(cursor.getInt(1));
                    ampList_heartRate.add(cursor.getInt(2)*100/150);
                }*/
                cursor.moveToFirst();
                dataStringBuffer.append("记录时间："+cursor.getString(3)+"\n");

                //cursor.close();
                //sqliteDatabase.close();
                cursor = sqliteDatabase.rawQuery("select count(*) from table_mydata  where what = ? and heartRate>=80 and heartRate<=89",new String[]{String.valueOf((int)view.getTag()+1)});
                cursor.moveToFirst();

                dataStringBuffer.append("heartRate区间【80-89】："+cursor.getString(0)+"\n");

                builder.setMessage(dataStringBuffer);

                builder.create().show();
            }
        };

        @Override
        public int getItemViewType(int position) {
            return width.get(position);
        }

        @Override
        public int getItemCount() {
            Log.d("","SimpleWaveform: amp_list_list.size() "+amp_list_list.size());
            return amp_list_list.size();
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // View view = View.inflate(parent.getContext(),
            // R.layout.gridview_pic, null);
            View view = View.inflate(parent.getContext(), R.layout.row_recycler, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(viewType*40,RecyclerView.LayoutParams.WRAP_CONTENT));
            ViewHolder holder = new ViewHolder(view);
            Log.d("onCreateViewHolder","SimpleWaveform: onCreateViewHolder ");
            //view.setOnClickListener(mOnClickListener);
            //test1.setText(String.valueOf(viewType));
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.d(TAG, "SimpleWaveform: position: " + position);
            //test1.append("|"+position);
            holder.simpleWaveform.setDataList(amp_list_list.get(position));
            holder.simpleWaveform.setTime(timeList.get(position));
            holder.tv_head.setText(String.valueOf(position+1));

            holder.itemView.setTag(position);
            Log.d(TAG, "holder.itemView.getTag() :" +  holder.itemView.getTag());
            if(model == SPO2)
                holder.itemView.setOnClickListener(mOnClickListener_spO2);
            else
                holder.itemView.setOnClickListener(mOnClickListener_heartRate);

            //holder.simpleWaveform.setOnClickListener(mOnClickListener);
            holder.simpleWaveform.barPencilSecond.setStrokeWidth(5);
            holder.simpleWaveform.barPencilSecond.setColor(0xffffffff);//0xff1dcfcf


            holder.simpleWaveform.peakPencilSecond.setStrokeWidth(10);
            holder.simpleWaveform.peakPencilSecond.setColor(0xfffeef3f);

            holder.simpleWaveform.textPencil.setTextSize(50);
            holder.simpleWaveform.textPencil.setColor(0xffffffff);
            holder.simpleWaveform.textPencil.setTextAlign(Paint.Align.CENTER);

            //define bar gap
            holder.simpleWaveform.barGap = 40;

            //define x-axis direction
            holder.simpleWaveform.modeDirection = SimpleWaveform.MODE_DIRECTION_LEFT_RIGHT;

            //define if draw opposite pole when show bars在显示栏时定义是否画对杆
            holder.simpleWaveform.modeAmp = SimpleWaveform.MODE_AMP_ORIGIN;
            //define if the unit is px or percent of the view's height
            holder.simpleWaveform.modeHeight = SimpleWaveform.MODE_HEIGHT_PERCENT;
            //define where is the x-axis in y-axis
            holder.simpleWaveform.modeZero = SimpleWaveform.MODE_ZERO_BOTTOM2;
            //if show bars?
            holder.simpleWaveform.showBar = true;

            //define how to show peaks outline定义如何显示峰值轮廓
            holder.simpleWaveform.modePeak = SimpleWaveform.MODE_PEAK_ORIGIN;
            //if show peaks outline?显示峰值大纲?
            holder.simpleWaveform.showPeak = true;

            //show x-axis
            holder.simpleWaveform.showXAxis = true;
            holder.simpleWaveform.xAxisPencil.setStrokeWidth(1);
            holder.simpleWaveform.xAxisPencil.setStrokeWidth(0x88ffffff);

            holder.simpleWaveform.refresh();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public SimpleWaveform simpleWaveform;
            TextView tv_head;

            public ViewHolder(View itemView) {
                super(itemView);
                this.simpleWaveform = (SimpleWaveform) itemView
                        .findViewById(R.id.simplewaveform_row);
                this.tv_head = (TextView)itemView.findViewById(R.id.tv_item_head);

//                this.simpleWaveform.clearScreen();
            }
        }

    }
}
