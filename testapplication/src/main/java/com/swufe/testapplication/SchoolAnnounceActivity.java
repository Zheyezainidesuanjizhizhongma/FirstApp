package com.swufe.testapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SchoolAnnounceActivity extends AppCompatActivity implements Runnable,AdapterView.OnItemClickListener{
    private final String TAG = "SchoolAnnounceActivity";
    Handler handler;
    ArrayList<String> retlist;
    ArrayList<String> detailList;
    ArrayList<String> list;
    ArrayAdapter adapter;
    EditText searchKeyword;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_announce);

        listView = (ListView) findViewById(R.id.school_list); //必须放在main方法
        Log.i(TAG, "onCreate: listView=" + listView);

        searchKeyword = (EditText)findViewById(R.id.school_hint);

        initListView();

        //获取sp里获取的数据
        SharedPreferences sharedPreferences3 = getSharedPreferences("SchoolAnnouncements", Activity.MODE_PRIVATE);
        //定义一个集合等下返回结果
        ArrayList<String> list3 = new ArrayList<>();
        ArrayList<String> list6 = new ArrayList<>();
        //刚才存的大小此时派上用场了
        int titleNums = sharedPreferences3.getInt("titleNums", 0);
        Log.i(TAG, "onCreate: titleNums="+titleNums);
        //根据键获取到值。
        for (int i = 0; i < titleNums; i++) {
            String searchItem = sharedPreferences3.getString("item_" + i, null);
            //放入新集合并返回
            list3.add(searchItem.split(" ")[0]);
            list6.add(searchItem.split(" ")[1]);
            Log.i(TAG, "onCreate: searchItem"+searchItem.split(" ")[0]);
        }
        String updateDate = sharedPreferences3.getString("update_date","2020-05-01");
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(SchoolAnnounceActivity.this, android.R.layout.simple_list_item_1, list3);
        listView.setAdapter(adapter3);

        //获取当前系统时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date dt= null;
        try {
            dt = sdf.parse(updateDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar updateToday = Calendar.getInstance();
        updateToday.setTime(dt);
        updateToday.add(Calendar.DAY_OF_YEAR,7);//日期加7天
        Date today_update = updateToday.getTime();
        Date today = Calendar.getInstance().getTime();
        final String todayStr = sdf.format(today);

        if(today_update.compareTo(today)!=1){
            Log.i(TAG, "onCreate: 需要更新");
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i(TAG, "onCreate: 不需要更新");
        }

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 3) {

                    Bundle bundle = (Bundle) msg.obj;
                    ArrayList<String> list2 = bundle.getStringArrayList("articleTitles");
                    ArrayList<String> list5 = bundle.getStringArrayList("articleURLs");
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(SchoolAnnounceActivity.this, android.R.layout.simple_list_item_1, list2);
                    listView.setAdapter(adapter2);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("SchoolAnnouncements", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date",todayStr);
                    Log.i(TAG, "handleMessage: update_date"+todayStr);
                    //将结果放入文件，关键是把集合大小放入，为了后面的取出判断大小。
                    editor.putInt("titleNums", list2.size());
                    for (int i = 0; i < list2.size(); i++) {
                        //用条目+i,代表键，解决键的问题，也方便等一下取出，值也对应。
                        editor.putString("item_" + i, list2.get(i)+" "+list5.get(i));
                    }
                    editor.apply(); //commit()和apply()类似

                    Toast.makeText(SchoolAnnounceActivity.this,"标题数据已更新",Toast.LENGTH_SHORT).show();
                }

            }
        };

        listView.setOnItemClickListener(this);
    }

    public void initListView(){
        list = new ArrayList<String>();
        for(int i=0;i<10;i++){
            list.add("item"+i);
        }
        //生成适配器的Item和动态数组对应的元素
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

    protected Bundle getSchoolWebInfo(){
        Bundle bundle = new Bundle();
        //获取网络数据，放入list带回到主线程中
        retlist = new ArrayList<>();
        detailList = new ArrayList<>();

        String url_base = "https://it.swufe.edu.cn/index/tzgg";
        String url = "";
        Document doc = null;

        for(int i=1;i<=58;i++){
            if(i==58){
                url = "https://it.swufe.edu.cn/index/tzgg.htm";
            }else{
                url = url_base+"/"+i+".htm";
            }

            try {
                Thread.sleep(1000);
                doc = Jsoup.connect(url).get();
                Log.i(TAG,"run: "+doc.title());
                Elements articleTitles = doc.getElementsByClass("article-showTitle ");  //返回的是Class列表
                Elements articleURLs = doc.getElementsByAttributeValueContaining("href","/info");
                for (int j=0;j<articleTitles.size();j++){
                    String article_title = articleTitles.get(j).text();
                    String article_url = articleURLs.get(j).attr("href");
                    //截取某个字符串之后的字符
                    String article_URL = "https://it.swufe.edu.cn/"+article_url.substring(article_url.indexOf("info"));

                    Log.i(TAG,"getSchoolWebInfo: article_title="+ article_title);
                    Log.i(TAG, "getSchoolWebInfo: article_URL="+article_URL);
                    retlist.add(article_title);
                    detailList.add(article_URL);
                }
                bundle.putStringArrayList("articleTitles",retlist);
                bundle.putStringArrayList("articleURLs",detailList);
                } catch (IOException e) {
                e.printStackTrace();
                } catch (InterruptedException e) {
                e.printStackTrace();
                }
        }
        return bundle;
    }

    protected void searchKeyword() {
        ArrayList<String> keywordRetList = new ArrayList<>();
        SharedPreferences sharedPreferences4 = getSharedPreferences("SchoolAnnouncements", Activity.MODE_PRIVATE);
        //定义一个集合等下返回结果

        //刚才存的大小此时派上用场了
        int titleNums = sharedPreferences4.getInt("titleNums", 0);
        Log.i(TAG, "searchKeyword: titleNums=" + titleNums);
        //根据键获取到值。
        for (int i = 0; i < titleNums; i++) {
            String article_info = sharedPreferences4.getString("item_" + i, null);
            String article_title = article_info.split(" ")[0];
            if (article_title.indexOf(searchKeyword.getText().toString()) != -1) {
                Log.i(TAG, "searchKeyword: article_title=" + article_title);
                keywordRetList.add(article_title);
            }
        }

        if (keywordRetList.size() == 0) {
            Toast.makeText(SchoolAnnounceActivity.this, "您查找的标题不存在", Toast.LENGTH_SHORT).show();
        } else {
            ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(SchoolAnnounceActivity.this, android.R.layout.simple_list_item_1, keywordRetList);
            listView.setAdapter(adapter4);
        }
    }

    public void onClick(View btn) {
        //获取用户输入内容
        String keyword = searchKeyword.getText().toString();
        if (keyword.length()==0) {
            Toast.makeText(this, "请输入您要查询的关键字", Toast.LENGTH_SHORT).show();
        }else{
            Log.i(TAG, "onClick: keyword=" + keyword);

            searchKeyword();
        }
    }

    @Override
    public void run() {
        Bundle bundle = getSchoolWebInfo();

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage();
        msg.what = 3;
        msg.obj = bundle;
        handler.sendMessage(msg);
        //Handler的sendMessage执行后执行handleMessage方法
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: parent="+parent);
        Log.i(TAG, "onItemClick: view="+view);
        Log.i(TAG, "onItemClick: position="+position);
        Log.i(TAG, "onItemClick: id="+id);
        String titleStr = listView.getItemAtPosition(position).toString();
        Log.i(TAG, "onItemClick: titleStr="+titleStr);

        SharedPreferences sharedPreferences5 = getSharedPreferences("SchoolAnnouncements", Activity.MODE_PRIVATE);
        //刚才存的大小此时派上用场了
        int titleNums = sharedPreferences5.getInt("titleNums", 0);
        //根据键获取到值。
        for (int i = 0; i < titleNums; i++) {
            String article_info = sharedPreferences5.getString("item_" + i, null);
            String article_title = article_info.split(" ")[0];
            String article_url = article_info.split(" ")[1];
           if(titleStr.equals(article_title)){
               Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(article_url));
               startActivity(web);
               break;
           }
        }

    }
}
