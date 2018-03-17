package com.iiht.student.nearme;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(com.iiht.student.nearme.R.layout.activity_main);

        new CountDownTimer(2000,1000) {

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                Intent myIntent=new Intent(MainActivity.this,FirstActivity.class);
                startActivity(myIntent);
                finish();
            }

            @Override
            public void onTick(long arg0) {
                // TODO Auto-generated method stub

            }
        }.start();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}
