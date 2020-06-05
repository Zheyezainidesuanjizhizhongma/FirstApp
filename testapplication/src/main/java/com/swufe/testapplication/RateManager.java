package com.swufe.testapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RateManager {
    private DBHelper dbHelper;
    private String TBNAME;

    public RateManager(Context context){
        dbHelper = new DBHelper(context);
        TBNAME = DBHelper.TB_NAME;
    }

    public void add(RateItem item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //创建并打开数据库
        // 创建ContentValues对象
        ContentValues values = new ContentValues();

        // 向该对象中插入键值对
        values.put("curname", item.getCurName());
        values.put("currate", item.getCurRate());
        //其中，key代表列名，value代表该列要插入的值
        //注：ContentValues内部实现就是HashMap，但是两者还是有差别的
        //ContenValues Key只能是String类型，Value只能存储基本类型数据，不能存储对象

        // 调用insert()方法将数据插入到数据库当中
        db.insert(TBNAME, null, values);
        // 第一个参数：要操作的表名称
        // 第二个参数：SQl不允许一个空列，如果ContentValues是空的，那么这一列被明确的指明为NULL值
        // 第三个参数：ContentValues对象

        db.close();
    }

    public List<RateItem> listAll(){
        List<RateItem> rateList = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TBNAME,null,null,null,null,null,null);
        //cursor是一个游标
        if(cursor!=null){
            rateList = new ArrayList<>();
            while (cursor.moveToNext()){
                RateItem item = new RateItem();
                item.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                item.setCurName(cursor.getString(cursor.getColumnIndex("CURNAME")));
                item.setCurRate(cursor.getString(cursor.getColumnIndex("CURRATE")));
                rateList.add(item);
            }
        }

        cursor.close();

        db.close();
        return rateList;
    }

    public void deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME,null,null);
        db.close();
    }
}
