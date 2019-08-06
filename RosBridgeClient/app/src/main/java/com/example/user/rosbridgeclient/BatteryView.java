package com.example.user.rosbridgeclient;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.example.user.rosbridgeclient.R;


/**
 * @author donkor
 * 自定义水平\垂直电池控件
 */
public class BatteryView extends View {
    private static int mPower = 101;
    private static int flagcharge=0;
    private int orientation;
    // FlagCharge  1 充电      0  放电

    private int width;
    private int height;
    private int mColor;

    private static int flagunchargeToast = 0;
    private static int flagchargeToast = 0;

    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Battery);
        mColor = typedArray.getColor(R.styleable.Battery_batteryColor, 0xFFFFFFFF);
        orientation = typedArray.getInt(R.styleable.Battery_batteryOrientation, 0);
        flagcharge = typedArray.getInt(R.styleable.Battery_batteryCharge, 0);
        mPower = typedArray.getInt(R.styleable.Battery_batteryPower, 100);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
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
        width = getMeasuredWidth();
        //对View上的內容进行测量后得到的View內容占据的高度
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawHorizontalBatteryUnCharge(canvas,flagcharge);

    }

    /**
     * @param canvas
     * @Function: 绘制水平的充电电池的放电
     * x:0 normal
     * x:1 请充电
     * @Return:
     */
    private void drawHorizontalBatteryUnCharge(Canvas canvas,int x) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = width / 20.f;
        float strokeWidth_2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        RectF r1 = new RectF(strokeWidth_2, strokeWidth_2, width - strokeWidth - strokeWidth_2, height - strokeWidth_2);
        //设置外边框颜色为灰色
        paint.setColor(Color.GRAY);
        canvas.drawRect(r1, paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        //画电池内矩形电量
        float offset = (width - strokeWidth * 2) * mPower / 100.f;
        RectF r2 = new RectF(strokeWidth, strokeWidth, offset, height - strokeWidth);
        switch (x){
            case 0:
                paint.setColor(Color.GREEN);
                break;
            case 1:
                paint.setColor(Color.YELLOW);
                break;
            case 2:
                paint.setColor(Color.RED);
                break;
            default:
                break;
        }
        //根据电池电量决定电池内矩形电量颜色
        /*if (mPower < 20) {
            flagunchargeToast++;
            paint.setColor(Color.RED);
            if (flagunchargeToast == 1) {
                Toast.makeText(getContext(), "电量较低，请充电", Toast.LENGTH_SHORT).show();
            }
        }
        if (mPower >= 20 && mPower < 60) {
            paint.setColor(Color.BLUE);
        }
        if (mPower >= 60) {
            paint.setColor(Color.GREEN);
        }*/
        canvas.drawRect(r2, paint);
        //画电池头
        RectF r3 = new RectF(width - strokeWidth, height * 0.25f, width, height * 0.75f);
        //设置电池头颜色为黑色
        paint.setColor(Color.GRAY);
        canvas.drawRect(r3, paint);
    }


    /**
     * @param canvas
     * @Function: 绘制水平的充电电池的充电
     * @Return:
     */
    private void drawHorizontalBatteryCharge(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = width / 20.f;
        float strokeWidth_2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        RectF r1 = new RectF(strokeWidth_2, strokeWidth_2, width - strokeWidth - strokeWidth_2, height - strokeWidth_2);
        //设置外边框颜色为灰色
        paint.setColor(Color.GRAY);
        canvas.drawRect(r1, paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        //画电池内矩形电量
        float offset = (width - strokeWidth * 2) * mPower / 100.f;
        RectF r2 = new RectF(strokeWidth, strokeWidth, offset, height - strokeWidth);
        //根据电池电量决定电池内矩形电量颜色
        if (mPower < 20) {
            flagchargeToast++;
            paint.setColor(Color.RED);
            if (flagchargeToast == 1) {
                Toast.makeText(getContext(), "正在充电", Toast.LENGTH_SHORT).show();
            }
        }
        if (mPower >= 20 && mPower < 60) {
            paint.setColor(Color.BLUE);
        }
        if (mPower >= 60) {
            paint.setColor(Color.GREEN);
        }
        canvas.drawRect(r2, paint);
        //画电池头
        RectF r3 = new RectF(width - strokeWidth, height * 0.25f, width, height * 0.75f);
        //设置电池头颜色为黑色
        paint.setColor(Color.GRAY);
        canvas.drawRect(r3, paint);
    }


    /**
     * 绘制水平电池
     *
     * @param canvas
     */
    private void drawHorizontalBattery(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = width / 20.f;
        float strokeWidth_2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        RectF r1 = new RectF(strokeWidth_2, strokeWidth_2, width - strokeWidth - strokeWidth_2, height - strokeWidth_2);
        //设置外边框颜色为灰色
        paint.setColor(Color.GRAY);
        canvas.drawRect(r1, paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        //画电池内矩形电量
        float offset = (width - strokeWidth * 2) * mPower / 100.f;
        RectF r2 = new RectF(strokeWidth, strokeWidth, offset, height - strokeWidth);
        //根据电池电量决定电池内矩形电量颜色
        if (mPower < 20) {
            paint.setColor(Color.RED);
            Toast.makeText(getContext(), "电量较低，请充电", Toast.LENGTH_SHORT).show();
        }
        if (mPower >= 20 && mPower < 60) {
            paint.setColor(Color.BLUE);
        }
        if (mPower >= 60) {
            paint.setColor(Color.GREEN);
        }
        canvas.drawRect(r2, paint);
        //画电池头
        RectF r3 = new RectF(width - strokeWidth, height * 0.25f, width, height * 0.75f);
        //设置电池头颜色为黑色
        paint.setColor(Color.GRAY);
        canvas.drawRect(r3, paint);
    }

    /**
     * 绘制垂直电池
     *
     * @param canvas
     */
    private void drawVerticalBattery(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = height / 20.0f;
        float strokeWidth2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        int headHeight = (int) (strokeWidth + 0.5f);
        RectF rect = new RectF(strokeWidth2, headHeight + strokeWidth2, width - strokeWidth2, height - strokeWidth2);
        canvas.drawRect(rect, paint);
        paint.setStrokeWidth(0);
        float topOffset = (height - headHeight - strokeWidth) * (100 - mPower) / 100.0f;
        RectF rect2 = new RectF(strokeWidth, headHeight + strokeWidth + topOffset, width - strokeWidth, height - strokeWidth);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect2, paint);
        RectF headRect = new RectF(width / 4.0f, 0, width * 0.75f, headHeight);
        canvas.drawRect(headRect, paint);
    }

    /**
     * 设置电池电量
     *
     * @param power
     */
    public void setPower(int power, int flagDir) {
        this.mPower = power;
        this.flagcharge = flagDir;
        if (mPower < 0) {
            mPower = 0;
        }
        if (mPower > 100) {
            mPower = 100;
        }
        invalidate();//刷新VIEW
    }

    /**
     * 设置电池颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    /**
     * 获取电池电量
     *
     * @return
     */
    public int getPower() {
        return mPower;
    }
}