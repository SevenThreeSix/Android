package com.example.user.ble;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ShowTableActivity extends Activity {

	TextView[] title = new TextView[30];
	TextView[] text = new TextView[5];
	TableLayout flout_text;
	TableLayout flout_title;
	MySQLiteOpenHelper helper;
	String studentsNumber;
	String weeksNumber;
	TextView tv_test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_table);

		flout_title = (TableLayout)findViewById(R.id.flout_title);
		flout_text = (TableLayout)findViewById(R.id.flout_text);
		tv_test = (TextView) findViewById(R.id.tv_sqlexcel_test);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//flout_title.removeAllViews();
		//flout_text.removeAllViews();
		//获取 weeksnumber
		//创建数据库连接
		/*helper = new MySQLiteOpenHelper(ShowTableActivity.this, "myData.db", null, 1);
		SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
		Cursor cursor = sqliteDatabase.rawQuery("select * from table_mydata",null);

		if(cursor.moveToNext()){
			studentsNumber = cursor.getString(2);
			weeksNumber = cursor.getString(3);
			tv_test.setText("周数："+weeksNumber+" 人数："+studentsNumber);
		}
		cursor.close();
		sqliteDatabase.close();*/

		//display(flout_title,ShowTableActivity.this);

		display2(flout_text,ShowTableActivity.this);
	}
	public void display2(TableLayout flout,Activity a){
		flout.removeAllViews();
		TableLayout table = new TableLayout(a); // 表格布局
		table.setWeightSum(1); // 权重

		helper = new MySQLiteOpenHelper(ShowTableActivity.this, "myData.db", null, 1);
		SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
		//Cursor cursor = sqliteDatabase.rawQuery("select * from table_mydata",null);
		Cursor cursor = sqliteDatabase.rawQuery("select * from table_mydata ",null);//select count(*) from table_mydata  where what = 1 and spO2>=90 and spO2<=99
		//TableRow tr = new TableRow(a);
		int i=0;
		while(cursor.moveToNext()){
			TableRow tr = new TableRow(a);
			for(int j = 0;j < 5; j++){
				//cursor.moveToNext();
				//title[0] = new TextView(a);title[0].setText("id");
				text[j] = new TextView(a);

				text[j].setText(String.valueOf(cursor.getString(j))+" | ");
					//table.addView(tr);

				if(i % 2 == 0) {
				    i=0;
                    text[j].setBackgroundColor(Color.parseColor("#0000ff00"));
                }
				else
					text[j].setBackgroundColor(Color.parseColor("#ff00aaaa"));
				tr.addView(text[j]);

			}
			table.addView(tr);
            i++;
		}
		flout.addView(table);
		cursor.close();
		sqliteDatabase.close();


	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.showtablemenu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();

            return true;
        }
		//noinspection SimplifiableIfStatement
		if (id == R.id.menu_refresh) {

			//创建数据库连接
			/*helper = new MySQLiteOpenHelper(ShowTableActivity.this, "myData.db", null, 1);
			SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();

			sqliteDatabase.delete("table_mydata", null, null);
            	*//* 连接数据库 *//**//* 数据库中有表 ， 对表进行操作 *//*
			sqliteDatabase.close();*/
			display2(flout_text,ShowTableActivity.this);

		}
		/*if (id == R.id.menu_scan) {
			//创建数据库连接
			helper = new MySQLiteOpenHelper(ShowTableActivity.this, "myData.db", null, 1);
			SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();

			sqliteDatabase.delete("table_mydata", null, null);
            	*//* 连接数据库 *//**//**//**//* 数据库中有表 ， 对表进行操作 *//*
			sqliteDatabase.close();
		}*/
		if (id == R.id.menu_newTable) {
			//创建数据库连接
			helper = new MySQLiteOpenHelper(ShowTableActivity.this, "myData.db", null, 1);
			SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
			//sqliteDatabase.execSQL("ALTER TABLE date ADD `d_id` INTEGER(11) PRIMARY KEY AUTOINCREMENT");
            sqliteDatabase.execSQL("drop table if exists table_mydata");
            sqliteDatabase.execSQL("create table if not exists table_mydata(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "spO2 INTEGER(5),heartRate INTEGER(5),time VERCHAR(20),what INTEGER(11))");
            sqliteDatabase.close();
		}

		return super.onOptionsItemSelected(item);
	}

}
