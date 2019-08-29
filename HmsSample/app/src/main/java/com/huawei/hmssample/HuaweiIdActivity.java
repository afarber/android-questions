package com.huawei.hmssample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.HuaweiIdStatusCodes;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.SignInResult;
import com.huawei.hms.support.api.hwid.SignOutResult;
import com.huawei.logger.Log;

public class HuaweiIdActivity extends BaseActivity implements OnClickListener{

	public static final String TAG = "HuaweiIdActivity";

    private static final int REQUEST_SIGN_IN_AUTH = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huaweiid); 
        
        findViewById(R.id.hwid_sign_in).setOnClickListener(this);
        findViewById(R.id.hwid_sign_out).setOnClickListener(this);
        
        //在sample 界面上显示日志提示信息的窗口，请忽略
		//In sample log information is displayed on the GUI window, Skip
        addLogFragment();
        
        //创建基础权限的登录参数options
		//Create permission of login parameters options
        HuaweiIdSignInOptions signInOptions = new
        		HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                .build();

        //创建华为移动服务client实例用以登录华为帐号
		//Create Huawei mobile service client instance is used to log in to the Huawei ID
        //The API needs to be specified as Huaweiid.sign_in_api
        //scope为HuaweiId.HUAEWEIID_BASE_SCOPE,可以不指定，HuaweiIdSignInOptions.DEFAULT_SIGN_IN默认使用该scope
		//Scope is huaweiid.huaeweiid_base_scope and can be unspecified, huaweiidsigninoptions.default_sign_in default use of this scope
        //设置相关回调接口
		//Set the related callback interface
        client = new HuaweiApiClient.Builder(this)
        		.addApi(HuaweiId.SIGN_IN_API, signInOptions)
        		.addScope(HuaweiId.HUAEWEIID_BASE_SCOPE)
        		.addConnectionCallbacks(this)
        		.addOnConnectionFailedListener(this)
        		.build();


		//建议在oncreate的时候连接华为移动服务
		//It is recommended in oncreate connected Huawei mobile service
		//业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
		//Service may be determined according to their service form client connection or disconnection of the time, But ensuring that connect and disconnect must appear in pairs
		client.connect(this);
	}

    /**
     * 登录华为帐号
	 * Log in to the Huawei ID
     */
    private void signIn() {
		//调用业务接口需确保华为移动服务client处于连接状态，否则需要重新连接华为移动服务。
		//Call the business interface to ensure that the Huawei Mobile service client is connected, otherwise the Huawei Mobile service needs to be reconnected.
    	if(!client.isConnected()) {
    		Log.i(TAG, "Login failed！Reason: Huaweiapiclient not connected！");
			client.connect(this);
    		return;
    	}
    	
    	PendingResult<SignInResult> signInResult = HuaweiId.HuaweiIdApi.signIn(this, client);
    	signInResult.setResultCallback(new SignInResultCallback());
    }

    /**
     * 登录结果回调
	 * Login result callback
     */
    private class SignInResultCallback implements ResultCallback<SignInResult> {

		@Override
		public void onResult(SignInResult result) {
			if(result == null) {
				//异常场景，接口调用失败
				//Abnormal scenarios, Failed to invoke the interface
				return;
			}

			if(result.isSuccess()){
				Log.i(TAG, "Login successful!");
				//可以获取帐号的 openid，昵称，头像 at信息
				//You can obtain account openid, Nickname, Portrait and accesstoken
				SignInHuaweiId account = result.getSignInHuaweiId();

				if(account != null) {
					//获取帐号相关信息
					//Get account information
					findViewById(R.id.user_acount_layout).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.hwid_nickname)).setText("Nickname:" + account.getDisplayName());
					((TextView) findViewById(R.id.hwid_openid)).setText("Openid:" + account.getOpenId());
					((TextView) findViewById(R.id.hwid_at)).setText("AccessToken:" + account.getAccessToken());
					((TextView) findViewById(R.id.hwid_photo)).setText("HeadPhotoUrl:" + account.getPhotoUrl());
				}
			} else {
				//当未授权，回调的result中包含处理该种异常的intent，开发者需要通过getData将对应异常的intent获取出来
				//并通过startActivityForResult启动对应的异常处理界面。
				//When unauthorized, Callback result contains the processing the exception of intent,
				//Developers need to use getData the corresponding abnormal intent obtained
				// through startActivityForResult starts the corresponding exception handling page
				if(result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_AUTH){
					Log.i(TAG, "Account is logged in and requires user authorization!");
					Intent intent = result.getData();
					if(intent != null) {
						startActivityForResult(intent, REQUEST_SIGN_IN_AUTH);
					} else {
						//异常场景，请作为登录失败处理
						//Abnormal scenarios, Failed to invoke the interface
					}
				} else if(result.getStatus().getStatusCode() == HuaweiIdStatusCodes.SIGN_IN_NETWORK_ERROR) {
					//网络异常,建议检查网络
					//Network is abnormal, you are advised to check the network
				} else {
					//异常场景，请作为登录失败处理
					//Abnormal scenarios, Failed to invoke the interface
				}
			}
		}  	
    }
    
    /**
     * 退出帐号，即取消用户的授权，下次再登录的时候需要用户再次确认授权
	 * Exit account, that is, to cancel the user's authorization, the next time you log in to require the user to confirm the authorization
     */
    private void signOut() {
		//调用业务接口需确保华为移动服务client处于连接状态，否则需要重新连接华为移动服务。
		//Call the business interface to ensure that the Huawei Mobile service client is connected, otherwise the Huawei Mobile service needs to be reconnected.
    	if(!client.isConnected()) {
    		Log.i(TAG, "Login Out failed！Reason: Huaweiapiclient not connected！");
			client.connect(this);
    		return;
    	}
    	
    	PendingResult<SignOutResult> signOutResult = HuaweiId.HuaweiIdApi.signOut(client);
    	signOutResult.setResultCallback(new SignOutResultCallback());
    }
    
    /**
     * 退出登录回调
	 * Log out of the callback
     */
    private class SignOutResultCallback implements ResultCallback<SignOutResult>{

		@Override
		public void onResult(SignOutResult result) {
			Status status = result.getStatus();
			//退出帐号，返回结果为0表示退出成功
			//Exit account, return result 0 indicates exit success
			if(status.getStatusCode() == 0) {
				Log.i(TAG, "Login Out Successful!");
				findViewById(R.id.user_acount_layout).setVisibility(View.GONE);
			} else {
				Log.i(TAG, "Login Out Faild!");
			}
		}
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hwid_sign_in:
			signIn();
			break;
			
		case R.id.hwid_sign_out:
			signOut();		
			break;
			
		default:
			break;
		}
	}
	

	/**
	 * 当调用signin接口的时候返回未授权，开发者调用启动intent来授权登录，结果通过onActivityResult返回
	 * When you invoke signin interface returned when unauthorized,
	 * The developer invokes the intent to authorized to log in to, the result returned by the onActivityResult
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SIGN_IN_AUTH) {
        	//返回值是-1,表明用户确认授权
			//If the return value is -1,  indicates that the user confirms the authorization
            if(resultCode == Activity.RESULT_OK) {
            	Log.i(TAG, "User has authorized!");
            	SignInResult result = HuaweiId.HuaweiIdApi.getSignInResultFromIntent(data);
				if(result == null) {
					return;
				}

            	if (result.isSuccess()) {
            		//授权成功，通过result.getSignInHuaweiId()获取华为帐号信息
					//Authorized success through RESULT.GETSIGNINHUAWEIID () to obtain Huawei account information
            		Log.i(TAG, "User authorization successful, return account information");
    				findViewById(R.id.user_acount_layout).setVisibility(View.VISIBLE);
    				SignInHuaweiId account = result.getSignInHuaweiId();
					if(account != null) {
						((TextView) findViewById(R.id.hwid_nickname)).setText("Nickname:" + account.getDisplayName());
						((TextView) findViewById(R.id.hwid_openid)).setText("Openid:" + account.getOpenId());
						((TextView) findViewById(R.id.hwid_at)).setText("AccessToken:" + account.getAccessToken());
						((TextView) findViewById(R.id.hwid_photo)).setText("HeadPhotoUrl:" + account.getPhotoUrl());
					}
            	} else {
            		// 授权失败，通过result.getStatus()获取错误原因
					//Authorization failed with Result.getstatus () to get the cause of the error
            		Log.i(TAG, "Failed authorization! Reason:" + result.getStatus().toString());
            	}
            } else {
				//异常场景，请作为登录失败处理
				//Abnormal scenarios, Failed to invoke the interface
            	Log.i(TAG, "User not authorized!");
            }
        } else if(requestCode == REQUEST_HMS_RESOLVE_ERROR) {
			onActivityResultForResolve(resultCode, data);
        }
    }
}
