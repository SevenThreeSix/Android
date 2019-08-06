package com.example.user.rosbridgeclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


/**
 * @author donkor
 * 自定义水平\垂直电池控件
 */
public class MapView extends View {
    private static int mPower = 101;
    private static int flagcharge=0;
    private int orientation;
    // FlagCharge  1 充电      0  放电

    private int width;
    private int height;
    private int mColor;

    private static int flagunchargeToast = 0;
    private static int flagchargeToast = 0;
    private int mMap[][];
    private Canvas mCanvas;

    public MapView(Context context) {
        super(context);

        Log.e("MapView","MapView(Context context)");
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Battery);
        mColor = typedArray.getColor(R.styleable.Battery_batteryColor, 0xFFFFFFFF);
        orientation = typedArray.getInt(R.styleable.Battery_batteryOrientation, 0);
        flagcharge = typedArray.getInt(R.styleable.Battery_batteryCharge, 0);
        mPower = typedArray.getInt(R.styleable.Battery_batteryPower, 100);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        Log.e("MapView","MapView(Context context, AttributeSet attrs) "+"Width:"+width+"Height:"+height);
        /**
         * recycle() :官方的解释是：回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
         * 在TypedArray后调用recycle主要是为了缓存。当recycle被调用后，这就说明这个对象从现在可以被重用了。
         *TypedArray 内部持有部分数组，它们缓存在Resources类中的静态字段中，这样就不用每次使用前都需要分配内存。
         */
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //对View上的內容进行测量后得到的View內容占据的宽度
        width = getMeasuredWidth()/2-1;
        //对View上的內容进行测量后得到的View內容占据的高度
        height = getMeasuredHeight()/2-1;
        mMap = new int[width][height];
        for(int j=0; j<height; j++)
            for(int i=0; i<width; i++) {
                this.mMap[i][j]=-1;
            }
        Log.e("onMeasure","onMeasure(int widthMeasureSpec, int heightMeasureSpec)"+"Width:"+width+"Height:"+height);
    }

    private Handler mHandler;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //this.mCanvas=canvas;
        Log.e("onDraw","onDraw(Canvas canvas)");
        /*Message msg = Message.obtain();
        Bundle b = new Bundle();
        b.putParcelable("Canvas", (Parcelable) canvas);
        msg.setData(b);
        mHandler.sendMessage(msg);*/
        //drawHorizontalBatteryUnCharge(canvas,flagcharge);
        /*new Thread(){
            @SuppressLint("HandlerLeak")
            public void run() {
                //mHandler = new Handler() {//2、绑定handler到CustomThread实例的Looper对象
                    //public void handleMessage(Message msg) {//3、定义处理消息的方法
                        //Canvas objectRcvd = (Canvas) msg.getData().getParcelable("Canvas");
                        drawMap(mCanvas, mMap);
                    }
               // };
            //}
        }.start();*/
        drawMap(canvas, mMap);

    }

    /**
     * @param canvas
     * @Function: 绘制水平的充电电池的放电
     * x:0 normal
     * x:1 请充电
     * @Return:
     */
    private void drawMap(Canvas canvas,int [][]map) {
        Log.e("drawMap", "enter");
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.FILL);//STROKE
        //float strokeWidth = width / 20.f;
        //paint.setStrokeWidth(strokeWidth);
        RectF r1 = new RectF(0, 0, (width+1)*2, (height+1)*2);
        //设置外边框颜色为灰色
        paint.setColor(Color.GRAY);
        canvas.drawRect(r1, paint);
        //paint.setColor(Color.WHITE);
        //canvas.drawRect(0,0,10,10, paint);
        RectF r2;
        for (int j = 0; j < 384; j++)
            for (int i = 0; i < 384; i++) {
                //Log.e("drawMap","i:" + i + " j:"+ j +" map[i][j]:" + map[i][j]);
                    if (map[i][j] == -1) {
                        //paint.setColor(Color.GRAY);
                        //canvas.drawCircle(i, j, 1, paint);
                        //canvas.drawRect(r1, paint);
                    } else if (map[i][j] == 0) {
                        paint.setColor(Color.WHITE);
                        //canvas.drawCircle(i, j, 1, paint);
                        canvas.drawRect(i*2,j*2,i*2+2,j*2+2, paint);
                        //Log.e("drawMap-WHITE", map[i][j] + "");
                    } else if (map[i][j] == 100) {
                        paint.setColor(Color.BLACK);
                        //canvas.drawCircle(i, j, 1, paint);
                        canvas.drawRect(i*2,j*2,i*2+2,j*2+2, paint);
                        //Log.e("drawMap-BLACK", map[i][j] + "");
                    } else
                        Log.e("drawMap", map[i][j] + "");
                }

    }


    public void showMap(int [][]map) {

        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++)
                mMap[i][j] = map[i][j];
        invalidate();//刷新VIEW
    }

    class drawMapThread extends Thread {


    }

}