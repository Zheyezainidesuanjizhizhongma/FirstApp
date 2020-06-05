package com.swufe.testapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<String> data = new ArrayList<>();
    private String TAG = "MyListActivity";
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        ListView listView = (ListView) findViewById(R.id.mylist);
//        GridView listView = (GridView) findViewById(R.id.mylist);
//        String data[] = {"111","222"};
        //init data
        for(int i=0;i<10;i++){
            data.add("item"+i);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.mylist));

        //添加一个点击事件，点击Item之后会做出相应
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: position="+position);
        Log.i(TAG, "onItemClick: parent="+parent);  //返回的是id为mylist的ListView,parent就是一个ListView控件

        adapter.remove(parent.getItemAtPosition(position));
        adapter.notifyDataSetChanged();
    }
}
