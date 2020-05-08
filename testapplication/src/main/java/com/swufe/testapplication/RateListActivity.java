package com.swufe.testapplication;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class RateListActivity extends ListActivity implements Runnable {
    //ListActivity包含了一个ListView
    //implements Runnable 实现多线程
    String TAG = "RateListActivity";
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] data = {"wait......"};
        //List是一个接口, 长度可变. List有两个重要的实现类：ArrayList和LinkedList
//        ArrayList<String> list1 = new ArrayList<String>();
//        for (int i = 0; i < 100; i++) {
//            list1.add("item" + i);
//        }

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);
        //ListActivity has a default layout that consists of a single,
        // full-screen list in the center of the screen. However, if you desire,
        // you can customize the screen layout by setting your own view layout with setContentView() in onCreate().

        //ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        // ArrayAdapter的数据可以是数组,也可以是list
        setListAdapter(adapter);

        //ListAdapter是绑定Data和Listview的适配器。但是，它是接口，需要使用它的子类。
        //常见的子类有：arrayAdapter，SimpleAdapter ，CursorAdapter
        // android.R.layout.simple_list_item_1   一行text ；
        // android.R.layout.simple_list_item_2   一行title，一行text ；
        // android.R.layout.simple_list_item_single_choice  单选按钮 
        // android.R.layout.simple_list_item_multiple_choice   多选按钮 
        // android.R.layout.simple_list_item_checked    checkbox
        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 7) {
                    ArrayList<String> list2 = (ArrayList<String>) msg.obj;
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(RateListActivity.this, android.R.layout.simple_list_item_1, list2);
                    setListAdapter(adapter2);
                }

            }
        };
    }

    @Override
    public void run() {
        //获取网络数据，放入list带回到主线程中
        ArrayList<String> retlist = new ArrayList<>();

        Document doc = null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run: "+doc.title());
            Elements tables = doc.getElementsByTag("table");  //返回的是Elements列表
            Element table0 = tables.get(0); //获取第0个table
            Elements tds = table0.getElementsByTag("td");
            for (int i=0;i<tds.size();i+=6){
                Log.i(TAG,"run: "+ tds.get(i).text()+"==>"+tds.get(i+5).text());
                retlist.add(tds.get(i).text()+"==>"+tds.get(i+5).text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage();
        msg.what = 7;
        msg.obj = retlist;
        handler.sendMessage(msg);
        //Handler的sendMessage执行后执行handleMessage方法
    }
}
