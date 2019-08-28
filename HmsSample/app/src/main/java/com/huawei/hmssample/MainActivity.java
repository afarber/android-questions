package com.huawei.hmssample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class MainActivity extends Activity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.hw_id).setOnClickListener(this);
        findViewById(R.id.hw_pay).setOnClickListener(this);
        findViewById(R.id.hw_sns).setOnClickListener(this);
        findViewById(R.id.hw_push).setOnClickListener(this);
        findViewById(R.id.hw_game).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hw_id:
                Intent intent = new Intent();
                intent.setClass(this, HuaweiIdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.hw_pay:
                Intent payIntent = new Intent();
                payIntent.setClass(this, HuaweiPayActivity.class);
                payIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(payIntent);
                break;

            case R.id.hw_sns:
                Intent snsIntent = new Intent();
                snsIntent.setClass(this, HuaweiSnsActivity.class);
                snsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(snsIntent);
                break;

            case R.id.hw_push:
                Intent pushIntent = new Intent();
                pushIntent.setClass(this, HuaweiPushActivity.class);
                pushIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(pushIntent);
                break;

            case R.id.hw_game:
                Intent gameIntent = new Intent();
                gameIntent.setClass(this, HuaweiGameActivity.class);
                gameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gameIntent);
                break;

            default:
                break;
        }
    }
}
