package com.swufe.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {
    private final String TAG = "config";
    EditText dollarText;
    EditText euroText;
    EditText yenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Intent intent = getIntent();
        float dollar2 = intent.getFloatExtra("dollar_rate_key",0.0f);
        float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
        float yen2 = intent.getFloatExtra("yen_rate_key",0.0f);

        Log.i(TAG,"onCreate: dollar2="+dollar2);
        Log.i(TAG,"onCreate: euro2="+euro2);
        Log.i(TAG,"onCreate: yen2="+yen2);

        dollarText = (EditText) findViewById(R.id.dollar_rate);
        euroText = (EditText) findViewById(R.id.euro_rate);
        yenText = (EditText) findViewById(R.id.yen_rate);

        dollarText.setText(String.valueOf(dollar2));
        euroText.setText(String.valueOf(euro2));
        yenText.setText(String.valueOf(yen2));

    }

    public void save(View btn){
        Log.i(TAG,"save:");

        float newDollar = Float.parseFloat(dollarText.getText().toString());
        float newEuro = Float.parseFloat(euroText.getText().toString());
        float newYen = Float.parseFloat(yenText.getText().toString());

        Log.i(TAG,"save: newDollar="+newDollar);
        Log.i(TAG,"save: newEuro="+newEuro);
        Log.i(TAG,"save: newYen="+newYen);

        Intent intent = getIntent(); //现在有dollar_rate_key,euro_rate_key,yen_rate_key
        Bundle bdl = new Bundle();
        //bdl.putFloat("dollar_rate_key",newDollar);  会覆盖之前的key
        bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_yen",newYen);
        intent.putExtras(bdl);

        setResult(2,intent);
        finish();
    }
}
