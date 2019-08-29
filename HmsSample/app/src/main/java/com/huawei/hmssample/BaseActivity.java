package com.huawei.hmssample;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.logger.Log;
import com.huawei.logger.LoggerActivity;

/**
 * {类描述}
 *
 * @author #user
 * @version #time
 */
public class BaseActivity extends LoggerActivity implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {

    public static final String TAG = "BaseActivity";

    //华为移动服务Client
    protected HuaweiApiClient client;

    //HuaweiApiAvailability.getInstance().resolveError方法会启动activity，为保证不同时弹出多个相同页面，请参照以下方案：
    //调用HuaweiApiAvailability.getInstance().resolveError的时候设置该标志位为true，在onActivityResult中标志位恢复。
    //应用可以根据业务的实际需要使用。
    //The methodHuaweiApiAvailability.getInstance().resolveError initiates the activity, in order to ensure that multiple identical pages are not ejected at the same time,
    //refer to the following scenario: When calling HuaweiApiAvailability.getInstance().resolveError, set the flag to true and sign bit recovery in onActivityResult.
    //Applications can be used in accordance with the actual needs of the business.
    protected boolean isResolve = false;

    protected static final int REQUEST_HMS_RESOLVE_ERROR = 1000;


    //如果开发者在onConnectionFailed调用了resolveError接口，那么错误结果会通过onActivityResult返回,具体的返回码通过该字段获取
    //If the developer calls the Resolveerror interface in onconnectionfailed, the error result is returned via onActivityResult,
    //and the specific return code is obtained through the field
    protected static final String EXTRA_RESULT = "intent.extra.RESULT";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }


    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        //Huawei Mobile Service Client connection successful, handle business own event here
        Log.i(TAG, "HuaweiApiClient Connect Successfully!");
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        //HuaweiApiClient由于异常原因导致断开，如果业务需要继续使用HMS的功能，需要重新连接华为移动服务
        //Huaweiapiclient is disconnected for exceptional reasons and needs to reconnect Huawei mobile service if business needs to continue using HMS Functionality
        Log.i(TAG, "HuaweiApiClient Disconnected!");
        client.connect(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Log.i(TAG, "HuaweiApiClient Connect Failed!  Error code：" + arg0.getErrorCode());

        if(isResolve) {
            //如果解决错误的接口已经被调用，并且没有处理完毕，不要重复调用。
            //Do not repeat the call if the interface that resolved the error has been invoked and has not been processed.
            return;
        }
        if(HuaweiApiAvailability.getInstance().isUserResolvableError(arg0.getErrorCode())) {
            Log.e(TAG, "resolveError");
            HuaweiApiAvailability.getInstance().resolveError(this, arg0.getErrorCode(), REQUEST_HMS_RESOLVE_ERROR);
            isResolve = true;
        } else {
            //其他错误码以及处理方法请参见开发指南-HMS 通用错误码及处理方法。
            //Other error codes and processing methods see Development Guide-HMS Common error codes and processing methods.
        }
    }

    /**
     * 调用HuaweiApiAvailability.getInstance().resolveError 等同于调用 startActivityForResult
     * 处理结果会通过onActivityResult返回给对应的activity处理
     * Call HuaweiApiAvailability.getInstance().resolveError equivalent to calling startActivityForResult
     * Processing results are returned via onActivityResult to the corresponding activity processing
     */
    protected void onActivityResultForResolve(int resultCode, Intent data) {
        isResolve = false;
        if(resultCode == Activity.RESULT_OK) {
            int result = data.getIntExtra(EXTRA_RESULT, -1);

            if(result == ConnectionResult.SUCCESS) {
                Log.i(TAG, "Error resolved successfully!");
                //需要开发者重新连接华为移动服务
                //Requires developers to reconnect Huawei Mobile services
                if (!client.isConnecting() && !client.isConnected()) {
                    client.connect(this);
                }
            } else if(result == ConnectionResult.CANCELED) {
                Log.i(TAG, "Resolve error process canceled by user!");
            } else if(result == ConnectionResult.INTERNAL_ERROR) {
                Log.i(TAG, "Internal error occurred, recommended retry.");
            } else {
                Log.i(TAG, "Other error codes.");
            }
        } else {
            Log.i(TAG, "An error occurred invoking the solution!");
        }
    }

    protected void addLogFragment() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        final LogFragment fragment = new LogFragment();
        transaction.replace(R.id.framelog, fragment);
        transaction.commit();
    }
}
