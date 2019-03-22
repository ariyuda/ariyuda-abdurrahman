package com.example.ta.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity {
    private LinearLayout ll_loading;
    private AVLoadingIndicatorView avli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //perintah loading
        ll_loading = findViewById(R.id.ll_loading);

        avli= findViewById(R.id.avli);
        avli.setIndicator("BallClipRotateMultipleIndicator");

        //splaschreen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(MainActivity.this,home.class);
                startActivity(i);

                //mengakhiri activity
                finish();
            }

        }, 3000);
    }


}
