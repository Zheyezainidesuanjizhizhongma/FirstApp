package com.swufe.testapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {
    Handler handler;
    private ArrayList<HashMap<String,String>> listItems; //存放文字、图片信息
    private SimpleAdapter listItemAdapter; //适配器
    String TAG = "MyList2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_list2);
        initListView();
        this.setListAdapter(listItemAdapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 9) {
                    ArrayList<HashMap<String,String>> list3 = (ArrayList<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this,list3,
                            R.layout.activity_my_list2,
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail});
                    setListAdapter(listItemAdapter);
                }

            }
        };

        getListView().setOnItemClickListener(this);
    }

    public void initListView(){
        listItems = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<10;i++){
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("ItemTitle","Rate: "+i); //标题文字
            map.put("ItemDetail","detail: "+i); //详情描述
            listItems.add(map);
        }
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this,listItems, //listItems数据源
                R.layout.activity_my_list2,//listItems的XML布局实现
                new String[]{"ItemTitle","ItemDetail"},
                new int[]{R.id.itemTitle,R.id.itemDetail});
    }

    @Override
    public void run() {
        //获取网络数据，放入list带回到主线程中
        ArrayList<HashMap<String,String>> retlist = new ArrayList<HashMap<String,String>>();

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
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("ItemTitle",tds.get(i).text());
                map.put("ItemDetail",tds.get(i+5).text());
                retlist.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage();
        msg.what = 9;
        msg.obj = retlist;
        handler.sendMessage(msg);
        //Handler的sendMessage执行后执行handleMessage方法
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: parent="+parent);
        Log.i(TAG, "onItemClick: view="+view);
        Log.i(TAG, "onItemClick: position="+position);
        Log.i(TAG, "onItemClick: id="+id);
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG, "onItemClick: titleStr="+titleStr);
        Log.i(TAG, "onItemClick: detailStr="+detailStr);

        //打开新的页面，传入参数
        Intent rateCalculate = new Intent(this,RateCalculateActivity.class);
        rateCalculate.putExtra("title",titleStr);
        rateCalculate.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalculate);

    }
}
