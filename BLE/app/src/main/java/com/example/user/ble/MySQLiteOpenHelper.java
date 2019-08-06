package com.example.user.ble;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteDatabase.CursorFactory;

import android.database.sqlite.SQLiteOpenHelper;

/* SQLiteOpenHelper的子类， 用于操作数据库

*

* SQLiteOpenHelper 是一个辅助类，用来管理数据库的创建和版本，提供两方面的功能;

*

* 第一：getReadableDatabase() 、 getWriteableDatabase() 可以获得SQLiteDatabase对象，通过该对象可以对数据库进行操作;

*

* 第二：提供onCreate() ; onUpgrade() 两个回调函数，允许我们再创建和删除数据库时，进行自己的操作;

*

* */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

/* 构造方法，调用父类SQLiteOpenHelper的构造函数 */

/* 参1：上下文环境;参2:数据库名称(以.db结尾) ; 参3：游标工厂(默认为null) ; 参4：代表使用数据库模型版本的证书*/

    public MySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {

        super(context, name, factory, version);

    }

/* 根据需要对SQLiteDatabase 的对象填充表和数据初始化 */

/* 该方法时在第一次创建的时候执行，实际上时第一次得到SQLiteDatabase对象的时侯才会调用这个方法 */

    public void onCreate(SQLiteDatabase db) {

// TODO 创建数据库后，对数据库的操作

    }

/* 将数据库从旧的模型转换为新的模型 *//* 参1：对象 ; 参2：旧版本号 ; 参3：新版本号 */

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

// TODO 更改数据库版本的操作

    }

/* 打开数据库执行的函数 */

    public void onOpen(SQLiteDatabase db) {

// TODO 每次成功打开数据库后首先被执行

        super.onOpen(db);

    }

} 
