package com.swufe.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TemperatureActivity extends AppCompatActivity implements View.OnClickListener{
    EditText temp_c;
    TextView temp_f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        temp_c = findViewById(R.id.temperatureInput);
        temp_f = findViewById(R.id.temperatureOutput);

        Button btn = findViewById(R.id.btn_transform);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        showTemperature(temp_c);
    }

    private void showTemperature(EditText temp_c){
        Log.i("show","temp_c="+temp_c);
        String tc = temp_c.getText().toString();
        float tf = (float) (32+Float.parseFloat(tc)*1.8);
        temp_f.setText(""+tf);
    }
}
