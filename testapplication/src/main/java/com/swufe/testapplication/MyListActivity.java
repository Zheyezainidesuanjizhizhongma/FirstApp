package com.swufe.testapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyListActivity extends AppCompatActivity {

    List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

//        ListView listView = (ListView) findViewById(R.id.mylist);
        GridView listView = (GridView) findViewById(R.id.mylist);
//        String data[] = {"111","222"};
        //init data
        for(int i=0;i<10;i++){
            data.add("item"+i);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }
}
