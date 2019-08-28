package com.huawei.hmssample;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.*;
import com.huawei.logger.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class HuaweiPushActivity extends BaseActivity implements OnClickListener{

	public static final String TAG = "HuaweiPushActivity";
    
	private UpdateUIBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huaweipush);
        
        findViewById(R.id.push_gettoken_sync).setOnClickListener(this);
        findViewById(R.id.push_gettoken_asyn).setOnClickListener(this);
        findViewById(R.id.push_get_status).setOnClickListener(this);
        findViewById(R.id.push_msg_checkbox).setOnClickListener(this);
		findViewById(R.id.push_msg_checkbox_notify).setOnClickListener(this);
        findViewById(R.id.delete_token).setOnClickListener(this);

		//在sample 界面上显示日志提示信息的窗口，请忽略
		//In sample log information is displayed on the GUI window, Skip
        addLogFragment();

        //创建华为移动服务client实例用以使用华为push服务
        //需要指定api为HuaweiId.PUSH_API
        //连接回调以及连接失败监听
		//Create Huawei Mobile Service client instance to use Huawei Push service
		//The API needs to be specified as Huaweiid.push_api
		//Connection callback and connection failure listening
        client = new HuaweiApiClient.Builder(this)
        		.addApi(HuaweiPush.PUSH_API)
        		.addConnectionCallbacks(this)
        		.addOnConnectionFailedListener(this)
        		.build();
        
    	//建议在oncreate的时候连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
		//It is recommended in oncreate connected Huawei mobile service
		//Service may be determined according to their service form client connection or disconnection of the time, But ensuring that connect and disconnect must appear in pairs
    	client.connect(this);     		
    	
    	//和业务不相关，请忽略
		//is not related to the business, please ignore
    	registerBroadcast();
    }

	/**
	 * 使用同步接口来获取pushtoken
	 * 结果通过广播的方式发送给应用，不通过标准接口的pendingResul返回
	 * 同步获取token和异步获取token的方法,开发者只要根据自身需要选取一种方式即可
	 * Using the synchronization interface to obtain Pushtoken
	 * results are broadcast to the application, not through the standard interface of the Pendingresul return
	 * Synchronous acquisition of token and asynchronous access to token method, developers as long as they need to choose a way to
	 */
	private void getTokenSync() {
    	if(!client.isConnected()) {
    		Log.i(TAG, "Failed to get token reason: Huaweiapiclient not connected!");
			client.connect(this);
    		return;
    	}

    	//需要在子线程中调用函数
		//Need to call a function in a child thread
    	new Thread() {

    		public void run() {
    			Log.i(TAG, "Sync interface To Get Push token");
    			PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
    			TokenResult result = tokenResult.await();
    			if(result.getTokenRes().getRetCode() == 0) {
					Log.i(TAG, "Get push token success, waiting for broadcast");
					// 结果通过广播发送
					// Results sent by broadcast
    			}
    		}
    	}.start();
	}
	
	/**
	 * 使用异步接口来获取pushtoken
	 * 结果通过广播的方式发送给应用，不通过标准接口的pendingResul返回
	 * 同步获取token和异步获取token的方法，开发者只要根据自身需要选取一种方式即可
	 * Use asynchronous interface to get Pushtoken
	 * results are sent to the application by broadcast, not through the standard interface Pendingresul return
	 * Synchronous acquisition of token and asynchronous access to token method, developers as long as they need to choose a way to
	 */
	private void getTokenAsyn() {
    	if(!client.isConnected()) {
    		Log.i(TAG, "Failed to get token reason: Huaweiapiclient not connected!");
			client.connect(this);
			return;
    	}
    	
		Log.i(TAG, "Asynchronous interface to Get Push token");
		PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
		tokenResult.setResultCallback(new ResultCallback<TokenResult>() {

			@Override
			public void onResult(TokenResult result) {
				// 结果通过广播发送
				// Results sent by broadcast
			}
		});
	}
	
	/**
	 * 异步方式获取PUSH的连接状态
	 * 结果会通过通知发送出来
	 * Asynchronous way to get push connection status
	 * Results are sent by notification
	 */
	private void getPushStatus() {
    	if(!client.isConnected()) {
    		Log.i(TAG, "Failed to get push connection state! Reason: Huaweiapiclient not connected");
			client.connect(this);
    		return;
    	}
    	
    	//需要在子线程中调用函数
		//Need to call a function in a child thread
    	new Thread() {
    		public void run() {
    			Log.i(TAG, "Start getting push connection status");
    	    	HuaweiPush.HuaweiPushApi.getPushState(client);
    	    	// 结果通过广播发送
				// Results sent by broadcast
    		}
    	}.start();
	}
   
	/**
	 * 设置是否允许应用接收PUSH透传消息（不展示在通知栏，直接将消息发送给应用，由应用负责接收处理）
	 * 若不调用该方法则默认为开启
	 * 在开发者网站上发送push消息分为通知和透传消息
	 * 通知为直接在通知栏收到通知，通过点击可以打开网页，应用 或者富媒体，不会收到onPushMsg消息
	 * 透传消息不会展示在通知栏，应用会收到onPushMsg
	 * 此开关只对透传消息有效
	 *
	 * Set whether the application is allowed to receive push-through messages (not shown in the notification bar, send the message directly to the application and processed by application
	 * If this method is not invoked, the default is to open
	 * The push message on the developer's website is divided into notification and transmission message
	 * Notification for direct notification in the notification bar, Click to open the Web page, application or rich media, will not receive ONPUSHMSG message
	 * Transmission message will not be displayed in the notification bar, the application will receive ONPUSHMSG
	 * This switch is only valid for the transmission message
	 * @param flag true 允许/Allow  false 不允许/Not allowed
	 */
    private void setReceiveNormalMsg(boolean flag) {
    	if(!client.isConnected()) {
    		Log.i(TAG, "Setting whether to receive push pass-through message failed! Reason: huaweiapiclient is not connected.");
			client.connect(this);
			return;
    	}
    	if(flag) {
        	Log.i(TAG, "Allow application to receive push pass message.");
    	} else {
    		Log.i(TAG, "Prohibit application receive push pass message.");
    	}
    	HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(client, flag);
    }

	/**
	 * 设置是否允许应用接收PUSH通知栏消息
	 * 若不调用该方法则默认为开启
	 * 在开发者网站上发送push消息分为通知和透传消息
	 * 通知为直接在通知栏收到通知，通过点击可以打开网页，应用 或者富媒体，不会收到onPushMsg消息
	 * 透传消息不会展示在通知栏，应用会收到onPushMsg
	 * 此开关只对通知栏消息有效
	 *
	 * Setting whether to allow the application to receive push notification bar messages
	 * If this method is not invoked, the default is to open
	 * Send a push message on the developer site into a notification and a transmission message
	 * notification for direct notification in the notification bar, by clicking can open the Web page, application or rich media,
	 * will not receive onpushmsg The pass message will not be displayed in the notification bar, the application will receive ONPUSHMSG
	 * This switch is only valid for notification bar messages
	 * @param flag true 允许/Allow   false 不允许/Not allowed
	 */
	private void setReceiveNotifyMsg(final boolean flag) {
		if(!client.isConnected()) {
			Log.i(TAG, "Setting whether to receive push notification bar message failed! Reason: Huaweiapiclient not connected.");
			client.connect(this);
			return;
		}

		if(flag) {
			Log.i(TAG, "Allow application to receive push notification bar messages.");
		} else {
			Log.i(TAG, "Prevent application from receiving push notification bar messages.");
		}

		//需要在子线程中调用函数
		//Need to call a function in a child thread
		new Thread() {
			public void run() {
				HuaweiPush.HuaweiPushApi.enableReceiveNotifyMsg(client, flag);
			}

		}.start();

	}


	/**
     * 应用删除通过getToken接口获取到的token
     * 应用调用注销token接口成功后，客户端就不会再接收到PUSH消息
     * 开发者应该在调用该方法后，自行处理本地保存的通过gettoken接口获取到的TOKEN
	 * Application Deletes the token
	 * After the application calls the interface,the client will no longer receive the push message
	 * The developer should, after calling the method, handle the locally saved token obtained via the GetToken interface
     */
    private void deleteToken() {
    	
    	if(!client.isConnected()) {
    		Log.i(TAG, "Delete token failed! Reason: Huaweiapiclient not connected.");
			client.connect(this);
			return;
    	}
    	
    	//需要在子线程中执行删除token操作
		//Need to call a function in a child thread
    	new Thread() {
    		@Override
    		public void run() {
    			String token = ((TextView)findViewById(R.id.push_token_view)).getText().toString();
    			Log.i(TAG, "Delete Token：" + token);
    			if (!TextUtils.isEmpty(token)){
    				try {
        				HuaweiPush.HuaweiPushApi.deleteToken(client, token);
					} catch (PushException e) {
						Log.i(TAG, "Delete Token Faild:" + e.getMessage());
					}
    			}
    		}
    	}.start();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_HMS_RESOLVE_ERROR) {
			onActivityResultForResolve(resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.push_gettoken_asyn:
			getTokenAsyn();
			break;
			
		case R.id.push_gettoken_sync:
			getTokenSync();		
			break;
			
		case R.id.push_get_status:
			getPushStatus();
			break;
			
		case R.id.push_msg_checkbox:
			Boolean flag = ((CheckBox)findViewById(R.id.push_msg_checkbox)).isChecked();
			setReceiveNormalMsg(flag);
			break;

		case R.id.push_msg_checkbox_notify:
			Boolean flag_notify = ((CheckBox)findViewById(R.id.push_msg_checkbox_notify)).isChecked();
			setReceiveNotifyMsg(flag_notify);
			break;

		case R.id.delete_token:
			deleteToken();
			break;
			
		default:
			break;
		}
	}

    
    /**
     * 以下代码为sample自身逻辑，和业务能力不相关
     * 作用仅仅为了在sample界面上显示push相关信息
	 *
	 * The following code for the logic of the sample itself,
	 * and the business capability is not related to the role of only to display the push-related information in the sample interface
     */
    private void registerBroadcast() {
    	String ACTION_UPDATEUI = "action.updateUI"; 

	    IntentFilter filter = new IntentFilter();  
	    filter.addAction(ACTION_UPDATEUI);  
	    broadcastReceiver = new UpdateUIBroadcastReceiver();  
	    registerReceiver(broadcastReceiver, filter);
    }
    
    /** 
     * 定义广播接收器（内部类）
	 * Define a broadcast receiver (inner class)
     */  
    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {  

		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getExtras().getInt("type"); 
			if(type == 1) {
				String token = intent.getExtras().getString("token"); 
				((TextView)findViewById(R.id.push_token_view)).setText(token);
			} else if (type == 2) {
				boolean status = intent.getExtras().getBoolean("pushState"); 
				if(status) {
					((TextView)findViewById(R.id.push_status_view)).setText("Connected");
				} else {
					((TextView)findViewById(R.id.push_status_view)).setText("Not connected");
				}
			}
		}  

    }   
}
