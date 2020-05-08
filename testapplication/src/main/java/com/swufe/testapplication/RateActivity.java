package com.swufe.testapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable{
    private final String TAG = "rate";
    private float dollarRate = 7.0f;
    private float euroRate = 7.6f;
    private float yenRate = 15f;
    private String updateDate = "";

    EditText rmb;
    TextView show;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);

        //获取sp里获取的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        yenRate = sharedPreferences.getFloat("yen_rate",0.0f);
        updateDate = sharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);
        //java中Calendar.getInstance()和new Date()的差别如下：
        //Calendar.getInstance()是获取一个Calendar对象并可以百进行时间的计算，时区的指定
        //new Date()是创建了度一个date对象，默认是utc格式的。
        //Calendar是Java版本更新的产物，可以设置特定的年月日和时区道等，新的程序就已经可以不用Date类了，
        // 因为这个类留下来主要是为了兼容以内前的程序，如果完全删掉容的话以前用Date的程序就不能运行了。
        //通过Calendar.getInstance().getTime()就可以获取本地当前时间，然后根据的format中不同的（Date and Time Patterns）来展示

        Log.i(TAG,"onCreate: dollarRate="+dollarRate);
        Log.i(TAG,"onCreate: euroRate="+euroRate);
        Log.i(TAG,"onCreate: yenRate="+yenRate);
        Log.i(TAG,"onCreate: updateDate="+updateDate);
        Log.i(TAG,"onCreate: todayStr="+todayStr);
        Log.i(TAG,"onCreate: Activity.MODE_PRIVATE="+Activity.MODE_PRIVATE);
        Log.i(TAG,"onCreate: Context.MODE_PRIVATE="+ Context.MODE_PRIVATE);

        if(!todayStr.equals(updateDate)){
            Log.i(TAG, "onCreate: 需要更新");
            Thread t = new Thread(this);
            t.start(); //执行run()方法
        }else{
            Log.i(TAG, "onCreate: 不需要更新");
        }

        handler = new Handler(){
            //有message就会调用handleMessage这个方法
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what==5){
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    yenRate = bdl.getFloat("yen-rate");
                    Log.i(TAG,"handleMessage: dollarRate="+dollarRate);
                    Log.i(TAG,"handleMessage: euroRate="+euroRate);
                    Log.i(TAG,"handleMessage: yenRate="+yenRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("yen_rate",yenRate);
                    editor.putString("update_date",todayStr);
                    editor.apply(); //commit()和apply()类似

                    Toast.makeText(RateActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                    //如果你的Toast写在一个内部类中，这种情况下，就要写成MainActivity.this, 否则直接写this，这个this指的是当前的内部类对象，而通常情况下这个内部类对象都不会是Context。
                }
            }
        };

    }


    public void run(){
        Log.i(TAG,"run: run()......");
        for(int i=1;i<6;i++){
            Log.i(TAG,"run i="+i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //用于保存获取的汇率
        Bundle bundle = getFromBOC();

        //获取网络数据
//        URL url = null;
//        try {
//            url = new URL("http://www.usd-cny.com/bankofchina.htm");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            //得到响应流
//            InputStream in = http.getInputStream();
//            //要在AndroidManifest.xml中加上网络许可
//            //<uses-permission android:name="android.permission.INTERNET"/>
//            String html = inputStream2String(in);
//            Log.i(TAG,"run: html="+html);
//            Document doc = Jsoup.parse(html);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        msg.obj = bundle;
        handler.sendMessage(msg);
        //Handler的sendMessage执行后执行handleMessage方法

    }

    //从Bank of China获取数据
    private Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run: "+doc.title());
            Elements tables = doc.getElementsByTag("table");  //返回的是Elements列表
            Element table0 = tables.get(0); //获取第0个table
            Elements tds = table0.getElementsByTag("td");
            for (int i=0;i<tds.size();i+=6){
                Log.i(TAG,"run: "+ tds.get(i).text()+"==>"+tds.get(i+5).text());

                if ("美元".equals(tds.get(i).text())){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(tds.get(i+5).text()));
                }
                else if ("欧元".equals(tds.get(i).text())){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(tds.get(i+5).text()));
                }
                else if ("日元".equals(tds.get(i).text())){
                    bundle.putFloat("yen-rate",100f/Float.parseFloat(tds.get(i+5).text()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bundle;
    }

    private String inputStream2String(InputStream inputStream) throws Exception{
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        //StringBuilder类也代表可变字符串对象。实际上，StringBuilder和StringBuffer基本相似，
        // 两个类的构造器和方法也基本相同。不同的是：StringBuffer是线程安全的，而StringBuilder
        // 则没有实现线程安全功能，所以性能略高。
        InputStreamReader in = new InputStreamReader(inputStream,"gb2312");
        for (;;){
            int rsz = in.read(buffer,0,buffer.length);
            if (rsz<0)
                break;
            out.append(buffer,0,rsz);
        } //for(;;)和while(1)一样
        in.close();

        return out.toString();

//        public int read(char[] cbuf,
//        int offset,
//        int length)
//        throws IOException将字符读入数组中的某一部分。
//
//        指定者：
//        类 Reader 中的 read
//        参数：
//        cbuf - 目标缓冲区
//        offset - 从其处开始存储字符的偏移量
//        length - 要读取的最大字符数
//        返回：
//        读取的字符数，如果已到达流的末尾，则返回 -1
//        抛出：
//        IOException - 如果发生 I/O 错误

    }

    public void onClick(View btn) {
        //获取用户输入内容
        String str = rmb.getText().toString();
        float r = 0;
        float val = 0;
        if (str.length() > 0) {
            r = Float.parseFloat(str); //如果输入为空，解析成浮点数就会失败，所以会报错
        } else {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "onClick: r=" + r);

        if (btn.getId() == R.id.btn_dollar) {
            val = r * dollarRate;
        } else if (btn.getId() == R.id.btn_euro) {
            val = r * euroRate;
        } else {
            val = r * yenRate;
        }

        show.setText(String.format("%.2f", val));
    }

//    public void openOne(View btn){
//        Intent hello = new Intent(this,ScoreActivity.class);
//
//        Intent web = new Intent(Intent.ACTION_VIEW,Uri.parse("https://bing.com"));
//
//        Intent intent = new Intent(Intent.ACTION_DIAL);
//        intent.setData(Uri.parse("tel:87859054"));
//        startActivity(intent);
//    }

    private void openConfig(){
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("yen_rate_key",yenRate);

        Log.i(TAG,"openOne: dollar_rate_key="+dollarRate);
        Log.i(TAG,"openOne: euro_rate_key="+euroRate);
        Log.i(TAG,"openOne: yen_rate_key="+yenRate);


        //startActivity(config);

        startActivityForResult(config,1);
    }

    public void openOne(View btn){
        openConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    //右上方的菜单项
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_set){
            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            Intent list = new Intent(this,MyList2Activity.class);
            startActivity(list);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 2) {
            Bundle bdl2 = data.getExtras();
            dollarRate = bdl2.getFloat("key_dollar");
            euroRate = bdl2.getFloat("key_euro");
            yenRate = bdl2.getFloat("key_yen");

            Log.i(TAG, "onActivityResult: dollarRate=" + dollarRate);
            Log.i(TAG, "onActivityResult: euroRate=" + euroRate);
            Log.i(TAG, "onActivityResult: yenRate=" + yenRate);

            //将新设置的汇率写到SP里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            //若文件不存在，则会创建一个
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("yen_rate",yenRate);
            editor.commit(); //commit()和apply()类似

            Log.i(TAG,"onActivityResult: 数据已保存到sharePreferences");

        }

    }

}