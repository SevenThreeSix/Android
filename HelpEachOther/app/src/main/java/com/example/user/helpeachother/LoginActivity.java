package com.example.user.helpeachother;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_ADDRESS;
import static com.example.user.helpeachother.ConstantUtil.SERVER_PORT;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */

    private static String[] PERMISSIONS_LOCATION_ARRAY = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_LOCATION = 2;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUnoView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    MyConnector mc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUnoView = (AutoCompleteTextView) findViewById(R.id.uno);
        SharedPreferences sp = getSharedPreferences("SP",MODE_PRIVATE); // 获得Preferences
        mUnoView.setText(sp.getString("uno", null));
        //populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        //给编辑框添加键盘按键监听
        mUnoView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        //给编辑框添加键盘按键监听
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    attemptLogin();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.progressBarForm);

        checkPermissionStorage();
        checkPermissionLocation();
        /*if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_LOCATION);
        }*/


    }
/*
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }
*/
    private void checkPermissionStorage() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);

        } else {
            Toast.makeText(this, "存储权限授权成功！", Toast.LENGTH_SHORT).show();
            Log.e("", "checkPermission: 存储权限已经授权！");
        }
    }

    private void checkPermissionLocation() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION_ARRAY, REQUEST_LOCATION);

        } else {
            Toast.makeText(this, "定位权限已授权成功！", Toast.LENGTH_SHORT).show();
            Log.e("", "checkPermission: 定位权限已经授权！");
        }
    }
/*
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUnoView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }
*/
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i("requestCode",String.valueOf(requestCode));
        Log.i("grantResultsLength",String.valueOf(grantResults.length));
//        Log.i("grantResults[0]",String.valueOf(grantResults[0]));
        switch(requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("-------->", "授权存储请求被允许");
                    checkPermissionLocation();
                    Toast.makeText(this, "存储权限授权成功！", Toast.LENGTH_SHORT).show();
                    //populateAutoComplete();
                } else {
                    Log.e("-------->", "授权存储请求被拒绝");
                    checkPermissionLocation();
                }
                break;
            case REQUEST_LOCATION:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("-------->", "授权位置请求被允许");
                    Toast.makeText(this, "定位权限授权成功！", Toast.LENGTH_SHORT).show();
                    //populateAutoComplete();
                } else {
                    Log.e("-------->", "授权位置请求被拒绝");
                }
                break;
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUnoView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String uno = mUnoView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        //if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
        if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(uno)) {
            mUnoView.setError(getString(R.string.error_field_required));
            focusView = mUnoView;
            cancel = true;
        } else if (!isEmailValid(uno)) {
            mUnoView.setError(getString(R.string.error_invalid_uno));
            focusView = mUnoView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(uno, password);
            mAuthTask.execute((Void) null);
            //login();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("20");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
/*
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
*/



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                Toast.makeText(LoginActivity.this, "请输入帐号或密码!",
                        Toast.LENGTH_SHORT).show();// 输出提示消息
            } else if (msg.what == 3) {
                Toast.makeText(LoginActivity.this, "用户名或密码错误，请重新输入~~~~~",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 4) {
                Toast.makeText(LoginActivity.this, "读取数据超时", Toast.LENGTH_LONG)
                        .show();
            } else if (msg.what == 5) {
                Toast.makeText(LoginActivity.this, "连接超时", Toast.LENGTH_LONG)
                        .show();
            }
        };
    };
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mUno;
        private final String mPassword;

        UserLoginTask(String uno, String password) {
            mUno = uno;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                try {
                    if (mc == null) {
                        mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT);
                    }else {
                        mc.sayBye();
                        mc = new MyConnector(SERVER_ADDRESS, SERVER_PORT);
                    }
                    String msg = "<#LOGIN#>" + mUno + "|" + mPassword;
                    System.out.println(msg);
                    if(mc.dout == null) {
                        return 2;
                    }
                    mc.dout.writeUTF(msg);//异常捕获有问题 所以提前判断
                    String receivedMsg = mc.din.readUTF();

                    Log.e("Test", "msg = " + receivedMsg);

                    if (receivedMsg.startsWith("<#LOGIN_SUCCESS#>")) {
                        receivedMsg = receivedMsg.substring(17);
                        String[] sa = receivedMsg.split("\\|");

                    } else if (receivedMsg.startsWith("<#LOGIN_FAIL#>")) { // 收到的消息为登录失败

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
                SharedPreferences sp = getSharedPreferences("SP",MODE_PRIVATE); // 获得Preferences
                SharedPreferences.Editor editor = sp.edit(); // 获得Editor
                editor.putString("uno", mUno); // 将用户名存入Preferences
                editor.commit();
                Intent intent = new Intent(LoginActivity.this,NvgDrawerActivity.class);
                intent.putExtra("uno", mUno);
                startActivity(intent); // 启动功能Activity
                //finish();

            } else if (success == 0)  {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                mUnoView.setText("");
                mPasswordView.setText("");
            }
            else{
                handler.sendEmptyMessage(5);
                mUnoView.setError(getString(R.string.error_connect));
                mUnoView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

