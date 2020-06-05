package com.swufe.testapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RateCalculateActivity extends AppCompatActivity {
    private String TAG = "RateCalculateActivity";
    EditText number;
    float rmb;
    float rate = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_calculate);

        String title = getIntent().getStringExtra("title");
        rate = getIntent().getFloatExtra("rate",0f);

        Log.i(TAG, "onCreate: title="+title);
        Log.i(TAG, "onCreate: rate="+rate);

        ((TextView)findViewById(R.id.rate_name_calculate)).setText(title);

        number = (EditText) findViewById(R.id.number_calculate);

        if(!(number.getText().toString()).isEmpty()){
            Log.i(TAG, "onCreate: number="+number.getText().toString());
            rmb = Float.parseFloat(number.getText().toString());

            Log.i(TAG, "onCreate: rmb="+rmb);

            ((TextView)findViewById(R.id.rate_result_calculate)).setText(rmb*rate/100+"");
        }

    }
}
