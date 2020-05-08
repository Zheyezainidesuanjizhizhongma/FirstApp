package com.swufe.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView out;
    EditText inp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        out = (TextView)findViewById(R.id.test_textView);
        inp = (EditText)findViewById(R.id.test_editText);

        //Button btn = (Button)findViewById(R.id.test_button);
        //btn.setOnClickListener(this);
    }

    public void btnClick(View btn){
        Log.i("click","btnClick called...");
        String str = inp.getText().toString();
        out.setText("Hello "+ str);
    }

//    @Override
//    public void onClick(View v) {
//        Log.i("click","onClick...");
//        String str = inp.getText().toString();
//        out.setText("Hello "+ str);
//    }
}
