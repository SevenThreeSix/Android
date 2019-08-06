package com.example.user.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

public class EmptyActivity extends AppCompatActivity {

    private Button bt11,bt12,bt13,bt21,bt22,bt23,bt31,bt32;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        bt11 = (Button)findViewById(R.id.FullScreen);
        bt11.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"FullscreenActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, FullscreenActivity.class);
                startActivity(intent);//登陆
            }
        });

        bt12 = (Button)findViewById(R.id.ItemList);
        bt12.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"ItemListActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, ItemListActivity.class);
                startActivity(intent);//登陆
            }
        });

        bt13 = (Button)findViewById(R.id.Login);
        bt13.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"LoginActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, LoginActivity.class);
                startActivity(intent);//登陆
            }
        });

        bt21 = (Button)findViewById(R.id.Navigation);
        bt21.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"NavigationActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, MainActivity.class);
                startActivity(intent);//登陆
            }
        });

        bt22 = (Button)findViewById(R.id.NvgDrawer);
        bt22.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"NvgDrawerActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, NvgDrawerActivity.class);
                startActivity(intent);//登陆
            }
        });

        bt23 = (Button)findViewById(R.id.Scroling);
        bt23.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"ScrolingActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, ScrollingActivity.class);
                startActivity(intent);//登陆
            }
        });

        bt31 = (Button)findViewById(R.id.Setting);
        bt31.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"SettingsActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, SettingsActivity.class);
                startActivity(intent);//登陆
            }
        });

        bt32 = (Button)findViewById(R.id.Tab);
        bt32.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"TabActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, TabActivity.class);
                startActivity(intent);//登陆
            }
        });

        bt11 = (Button)findViewById(R.id.FullScreen);
        bt11.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast.makeText(EmptyActivity.this,"FullscreenActivity",0).show();
                Intent intent = new Intent();
                intent.setClass(EmptyActivity.this, FullscreenActivity.class);
                startActivity(intent);//登陆
            }
        });


        //bt11 = (Button)findViewById(R.id.FullScreen);
    }
}
