package com.huawei.hmssample;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.pay.HuaweiPay;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class HuaweiPayActivity extends BaseActivity implements OnClickListener, ISetHuaweiClient {

	public static final String TAG = "HuaweiPayActivity";

    private PayFragment payFragment = null;
    private PMSFragment pmsFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huaweipay);

        addLogFragment();

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        //创建华为移动服务client实例用以实现支付功能
        //Create Huawei Mobile Service client instance to realize payment function
        //需要指定api为HuaweiPay.PAY_API
        //The API needs to be specified as Huaweipay.pay_api
        //连接回调以及连接失败监听
        //Set the related callback interface
        client = new HuaweiApiClient.Builder(this)
        		.addApi(HuaweiPay.PAY_API)
        		.addOnConnectionFailedListener(this)
        		.addConnectionCallbacks(this)
        		.build();
        
    	//建议在oncreate的时候连接华为移动服务
        //It is recommended in oncreate connected Huawei mobile service
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        //Service may be determined according to their service form client connection or disconnection of the time, But ensuring that connect and disconnect must appear in pairs
        client.connect(this);
    }

	@Override
	public void onClick(View v) {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();

		switch (v.getId()) {
            case R.id.button1:
                if(payFragment == null) {
                    payFragment = new PayFragment();
                }

                transaction.replace(R.id.framepay, payFragment);
                transaction.commit();
                break;

            case R.id.button2:
                if(pmsFragment == null) {
                    pmsFragment = new PMSFragment();
                }

                transaction.replace(R.id.framepay, pmsFragment);
                transaction.commit();
                break;

		    default:
			    break;
		}
	}
	

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_HMS_RESOLVE_ERROR) {
            onActivityResultForResolve(resultCode, data);
        } else {
            Fragment fragment= getFragmentManager().findFragmentById(R.id.framepay);
            if(fragment instanceof OnFragmentResultListener){

                OnFragmentResultListener listener=(OnFragmentResultListener) fragment;
                listener.OnFragmentResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public HuaweiApiClient getHuaweiClient() {
        return client;
    }
}
