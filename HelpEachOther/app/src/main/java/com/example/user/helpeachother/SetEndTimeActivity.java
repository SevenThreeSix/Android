package com.example.user.helpeachother;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.mapapi.map.MyLocationConfiguration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;

public class SetEndTimeActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private Context context;
    private LinearLayout llDate, llTime;
    private TextView tvDate, tvTime;
    private TextView taskContent, taskPos;
    private int year, month, day, hour, minute;
    //在TextView上显示的字符
    private StringBuffer date, time;

    Button bt_release;
    private View mReleaseFormView;
    private View mProgressView;

    private UserReleaseTask mAuthTask = null;

    MyConnector mc = null;
    String UserNumber;

    Date nowDate;
    SimpleDateFormat simpleDateFormat;

    String taskLatitude ;
    String taskLongitude ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_end_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("设置时间");//父标题

        context = this;
        date = new StringBuffer();
        time = new StringBuffer();
        initView();
        initDateTime();

        mReleaseFormView = findViewById(R.id.Release_form);
        mProgressView = findViewById(R.id.progressBarForm);

        bt_release = (Button) findViewById(R.id.button_release) ;
        bt_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptRelease();
                //login();
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SetEndTimeActivity.this);
                builder.setMessage("Sure to Release？");  builder.setTitle("提示");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //showProgress(true);
                        attemptRelease();
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

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mReleaseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mReleaseFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mReleaseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

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
            mReleaseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void attemptRelease() {
        if (mAuthTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
        String mDateView = tvDate.getText().toString();
        String mTimeView = tvTime.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mDateView)) {
            tvDate.setError(getString(R.string.error_field_required));
            focusView = tvDate;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(mTimeView)) {
            tvTime.setError(getString(R.string.error_field_required));
            focusView = tvTime;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            Snackbar.make(this.mReleaseFormView, R.string.error_field_required, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Log.i("UserNumber",UserNumber);
            Log.i("taskContent",taskContent.getText().toString());
            mAuthTask = new UserReleaseTask(UserNumber,taskContent.getText().toString());
            mAuthTask.execute((Void) null);
            //login();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {

        Bundle data = getIntent().getExtras();
        UserNumber =data.getString("uno");
        taskLatitude =data.getString("TaskLatitude");
        taskLongitude =data.getString("TaskLongitude");
        taskContent = (TextView) findViewById(R.id.task);
        taskContent.setText(data.getString("TaskContent"));
        taskPos =(TextView) findViewById(R.id.pos);
        StringBuilder builder = new StringBuilder();
        //builder.append(" Released by: "+x);
        builder.append("Latitude:"+taskLatitude+ "\n"+"Longitude:"+taskLongitude);
        if(taskLatitude.equals(ReleaseActivity.DefaultLatitude))
            builder.append("（默认）");
        taskPos.setText(builder.toString());
        llDate = (LinearLayout) findViewById(R.id.ll_date);
        tvDate = (TextView) findViewById(R.id.tv_date);
        llTime = (LinearLayout) findViewById(R.id.ll_time);
        tvTime = (TextView) findViewById(R.id.tv_time);
        llDate.setOnClickListener(this);
        llTime.setOnClickListener(this);
    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_date:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (date.length() > 0) { //清除上次记录的日期
                            date.delete(0, date.length());
                        }
                        tvDate.setText(date.append(String.valueOf(year)).append("/").append(String.valueOf(month+1)).append("/").append(day));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_date, null);
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

                dialog.setTitle("设置日期");
                dialog.setView(dialogView);
                dialog.show();
                //初始化日期监听事件
                datePicker.init(year, month, day, this);
                break;
            case R.id.ll_time:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (time.length() > 0) { //清除上次记录的日期
                            time.delete(0, time.length());
                        }
                        tvTime.setText(time.append(String.valueOf(hour)).append(":").append(String.valueOf(minute)));
                        dialog.dismiss();
                    }
                });
                builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog2 = builder2.create();
                View dialogView2 = View.inflate(context, R.layout.dialog_time, null);
                TimePicker timePicker = (TimePicker) dialogView2.findViewById(R.id.timePicker);
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minute);
                timePicker.setIs24HourView(true); //设置24小时制
                timePicker.setOnTimeChangedListener(this);
                dialog2.setTitle("设置时间");
                dialog2.setView(dialogView2);
                dialog2.show();
                break;
        }
    }


    /**
     * 日期改变的监听事件
     *
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    /**
     * 时间改变的监听事件
     *
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
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
            //NavUtils.navigateUpTo(this, new Intent(this, ReleaseActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                Toast.makeText(SetEndTimeActivity.this, "请输入帐号或密码!",
                        Toast.LENGTH_SHORT).show();// 输出提示消息
            } else if (msg.what == 3) {
                Toast.makeText(SetEndTimeActivity.this, "发布失败",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 4) {
                Toast.makeText(SetEndTimeActivity.this, "读取数据超时", Toast.LENGTH_LONG)
                        .show();
            } else if (msg.what == 5) {
                Toast.makeText(SetEndTimeActivity.this, "连接超时", Toast.LENGTH_LONG)
                        .show();
            }
        };
    };

    public class UserReleaseTask extends AsyncTask<Void, Void, Integer> {

        private final String mUno;
        private final String mTaskContent;

        UserReleaseTask(String uno, String task) {
            mUno = uno;
            mTaskContent = task;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.i("doInBackground","进入");
            // TODO: attempt authentication against a network service.
            try {
                try {
                    if (mc == null) {
                        mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT);
                    }else {
                        mc.sayBye();
                        mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT);
                    }
                    simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");// HH:mm:ss
                    //获取当前时间
                    nowDate = new Date(System.currentTimeMillis());
                    String msg = "<#PubDate#>"+mTaskContent+"|"+"man"+"|"+"70"+"|"+"5"+"|"+mUno+"|"+taskLatitude+"|"+taskLongitude+"|"+simpleDateFormat.format(nowDate)+"|"+tvDate.getText()+" "+tvTime.getText();
                    System.out.println(msg);
                    if(mc.dout == null) {
                        return 2;
                    }
                    mc.dout.writeUTF(msg);//异常捕获有问题 所以提前判断
                    String receivedMsg = mc.din.readUTF();

                    Log.e("Test", "msg = " + receivedMsg);

                    if (receivedMsg.startsWith("<#PubDate_SUCCESS#>")) {
                        receivedMsg = receivedMsg.substring(17);
                        String[] sa = receivedMsg.split("\\|");

                    } else if (receivedMsg.startsWith("<#PubDate_FAIL#>")) { // 收到的消息为登录失败
                        handler.sendEmptyMessage(3);
                        return 0;
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    if (!mc.socket.isClosed() && mc.socket.isConnected()) {
                        handler.sendEmptyMessage(4);
                    } else {
                        handler.sendEmptyMessage(5);
                    }
                    return 0;
                }
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return 0;
            }
            // TODO: register the new account here.
            return 1;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            mAuthTask = null;
            //mc.sayBye();
            showProgress(false);
            if (success == 1) {
                Intent intent = new Intent(SetEndTimeActivity.this,NvgDrawerActivity.class);
                startActivity(intent);// 启动功能Activity
                finish();

            } else if (success == 0)  {
                tvDate.setError(getString(R.string.faled_but_connect_succeed));
                tvDate.requestFocus();
            }
            else{
                handler.sendEmptyMessage(5);
                tvDate.setError(getString(R.string.error_connect));
                tvDate.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}