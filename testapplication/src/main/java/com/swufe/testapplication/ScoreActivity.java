package com.swufe.testapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {
    TextView teamScore;
    TextView teamScore1;
    String TAG = "ScoreActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Log.i(TAG, "onCreate: ");
        teamScore = (TextView) findViewById(R.id.score);
        teamScore1 = (TextView) findViewById(R.id.score1);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String scorea = ((TextView) findViewById(R.id.score)).getText().toString();
        String scoreb = ((TextView) findViewById(R.id.score1)).getText().toString();
        outState.putString("teama_score",scorea);
        outState.putString("teamb_score",scoreb);

        Log.i(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        String scorea = savedInstanceState.getString("teama_score");
        String scoreb = savedInstanceState.getString("teamb_score");
        ((TextView) findViewById(R.id.score)).setText(scorea);
        ((TextView) findViewById(R.id.score1)).setText(scoreb);

        Log.i(TAG, "onRestoreInstanceState: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    public void btn_add1(View v){
        if(v.getId()==R.id.score_1)
            showScore(1);
        else
            showScore1(1);
    }

    public void btn_add2(View v){
        if(v.getId()==R.id.score_2)
            showScore(2);
        else
            showScore1(2);
    }

    public void btn_add3(View v){
        if(v.getId()==R.id.score_3)
            showScore(3);
        else
            showScore1(3);
    }

    private void showScore(int inc){
        Log.i("show","inc="+inc);
        String oldScore = teamScore.getText().toString();
        int newScore = (Integer.valueOf(oldScore)+inc);
        teamScore.setText(""+newScore);
    }

    private void showScore1(int inc){
        Log.i("show","inc="+inc);
        String oldScore1 = teamScore1.getText().toString();
        int newScore1 = (Integer.valueOf(oldScore1)+inc);
        teamScore1.setText(""+newScore1);
    }

    public void btn_reset(View v){
        teamScore.setText("0");
        teamScore1.setText("0");
    }
}

