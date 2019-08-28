package com.huawei.hmssample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.sns.Constants.UiIntentType;
import com.huawei.hms.support.api.entity.sns.SNSCode;
import com.huawei.hms.support.api.entity.sns.SnsMsg;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.sns.HuaweiSns;
import com.huawei.hms.support.api.sns.IntentResult;
import com.huawei.logger.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;

public class HuaweiSnsActivity extends BaseActivity implements OnClickListener{

	public static final String TAG = "HuaweiSnsActivity";

    private static final int REQUEST_SEND_SNS_MSG = 2001;

    private static final int REQUEST_GET_UI_INTENT = 2002;
    
    boolean isPNG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huaweisns);
        
        findViewById(R.id.sns_start).setOnClickListener(this);
        findViewById(R.id.sns_send_msg).setOnClickListener(this);

		//在sample 界面上显示日志提示信息的窗口，请忽略
		//In sample log information is displayed on the GUI window, Skip
        addLogFragment();
        
        //创建基础权限的登录参数options
		//Create permission of login parameters options
        HuaweiIdSignInOptions signInOptions = new 
        		HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                .build();

        //创建华为移动服务client实例用以使用华为消息服务
		//Create Huawei Mobile Service client instance to use Huawei Messaging Service
        //需要指定api为HuaweiId.SIGN_IN_API以及HuaweiSns.API
		//You need to specify APIs for HUAWEIID.SIGN_IN_API and Huaweisns.api
        //scope为HuaweiId.HUAEWEIID_BASE_SCOPE,可以不指定，HuaweiIdSignInOptions.DEFAULT_SIGN_IN默认使用该scope
		//Scope is huaweiid.huaeweiid_base_scope and can be unspecified, huaweiidsigninoptions.default_sign_in default use of this scope
		//设置相关回调接口
		//Set the related callback interface
        client = new HuaweiApiClient.Builder(this)
        		.addApi(HuaweiId.SIGN_IN_API, signInOptions)
        		.addApi(HuaweiSns.API)
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
     * 启动华为消息服务界面，根据传入的UiIntentType.UI_MSG打开不同的页面
	 * Start Huawei message page, According to the input UiIntentType.UI_MSG open different page
     * UiIntentType.UI_MSG  消息界面/Message page
     * UiIntentType.UI_FRIEND 联系人界面 /Contact page
     * UiIntentType.UI_FAMILY_GROUP 家庭群组/Family group
     * UiIntentType.UI_COMMON_GROUP 群聊界面 /Group chat group
     * 当UiIntentType.UI_MSG是以上4种类型之一的时候， getUiIntent接口的最后一个参数传入0
	 * When Uiintenttype.ui_msg is one of the above 4 types, the last parameter of the Getuiintent interface is passed in 0
     * UI_FAMILY_GROUP_DETAIL
     * UI_COMMON_GROUP_DETAIL
     * UI_CHAT_GROUP
	 * 当UiIntentType.UI_MSG是以上3种类型之一的时候， getUiIntent接口的最后一个参数传入群组的华为帐号ID
	 * When Uiintenttype.ui_msg is one of the above 3 types, the last parameter of the Getuiintent interface is passed into the group's Huawei account ID
     * UI_USER_DETAIL
     * UI_CHAT_FRIEND
     * UI_CHAT_ASSIST
	 * 当UiIntentType.UI_MSG是以上3种类型之一的时候， getUiIntent接口的最后一个参数传入用户的华为帐号ID
	 * When Uiintenttype.ui_msg is one of the above 3 types, the last parameter of the Getuiintent interface is passed into the user's Huawei account ID
     */
    private void startSnsActivity() {  	
    	if(!client.isConnected()) {
    		Log.i(TAG, "Start HuaweiSns failure! Reason: Huaweiapiclient not connected.");
			client.connect(this);
    		return;
    	}
    	
    	PendingResult<IntentResult> snsStartResult = HuaweiSns.HuaweiSnsApi.getUiIntent(client, UiIntentType.UI_MSG, 0);
    	snsStartResult.setResultCallback(new SnsStartResultCallback());
    }
    
    /**
     * callback of Start the message service interface
     */
    private class SnsStartResultCallback implements ResultCallback<IntentResult>{

		@Override
		public void onResult(IntentResult result) {
			if(result == null) {
				//异常场景，请按照调用接口失败处理
				//Abnormal scenarios, Failed to invoke the interface
				return;
			}

			//根据接口返回的intent，通过startActivityForResult启动指定界面
			//Starts the specified interface via Startactivityforresult based on the intent returned by the interface
			Status status = result.getStatus();
			if(SNSCode.OK == status.getStatusCode()) {
				Intent intent = result.getIntent();
				if(intent != null) {
					Log.i(TAG, "Start HuaweiSns.");
					startActivityForResult(intent, REQUEST_GET_UI_INTENT);
				}
			} else {
				//其他错误码以及处理方法请参见API文档
				//Additional error codes and processing methods see API documentation
			}
		}
    }
    
    /**
     * 必须按照指定格式封装社交图文消息
	 * Must be in a specified format encapsulation social text message
	 *
	 * 消息服务图文消息可以指定打开消息的应用，如不指定，则默认使用浏览器打开，指定方式为设置以下参数:
	 * Message service text message can be specified to open the message of the application,
	 * If not specified, The browser is used by default, Specified mode to set the following parameters:
	 *
     * TargetAppPackageName:应用的包名/Application package name
     * TargetAppVersionCode:应用的最低版本号/Application of the lowest version number
     * TargetAppMarketId:应用在华为应用市场的下载APPID，如果消息接收方没有安装指定APP或者APP版本号未达到最低版本号要求，则华为移动服务会去应用市场下载指定应用
	 * 					Application in the Huawei application market download AppID,
	 * 					if the message receiver does not install the specified app or app version number does not meet the minimum version number requirements,
	 * 					then Huawei Mobile services will be applied to the market download specified application
     * CheckTargetApp:是否要检测以上3个参数指定的应用信息，默认设置为false。如果该参数设置为true，则会去检测消息接收方是否安装指定APP以及APP版本号
	 * 				  To be detected is above three parameter specifies the application information, The default value is false.
	 * 				  If this parameter is set to true, The message is to detect whether the receiver installation specified APP and APP version number
     */
    private SnsMsg createSnsMsg() {
    	
    	SnsMsg snsMsg = new SnsMsg();
    	snsMsg.setAppName("TestAppName");
    	snsMsg.setCheckTargetApp(false);
    	//消息标题
		//Message headers
    	snsMsg.setTitle("TestMessageTitle");
    	//消息正文
		//Message body
    	snsMsg.setDescription("TestMessageBody");
    	//消息点击跳转URL
		//Message clicking URL
    	snsMsg.setUrl("http://www.baidu.com");

		//消息图标
		//Message icon
    	//消息服务图文消息的icon需要传递图片的二进制数据
		//Message service text message icon need to transfer images binary data
    	//图标的大小不能超过30K
		//The size of the icon cannot exceed 30K
    	//社交图文消息界面只支持正方形的图片，如果图片非正方形图片，则会进行截取，请开发者注意处理
		//The social graphics and text message interface only supports the square picture,
		//if the picture is not the square picture, then will intercept, invites the developer to pay attention to handle
    	Bitmap bitmap = getImageFromAssetsFile();
    	snsMsg.setLinkIconData(bmpToByteArray(bitmap, 30));

    	return snsMsg;
    }
    
    /**
     * 发送图文消息
	 * Send text message
     */
    private void sendMessage() {  	
    	if(!client.isConnected()) {
    		Log.i(TAG, "Send text message failed！ Reason: Huaweiapiclient not connected.");
			client.connect(this);
    		return;
    	}
    	
    	SnsMsg snsMsg = createSnsMsg();
    	
    	PendingResult<IntentResult> sendMsgResult = HuaweiSns.HuaweiSnsApi.getMsgSendIntent(client, snsMsg, true);
    	sendMsgResult.setResultCallback(new SendMsgResultCallback());
    }
    
    /**
     * 发送图文消息回调接口
	 * callback of Send text message
     */
    private class SendMsgResultCallback implements ResultCallback<IntentResult>{

		@Override
		public void onResult(IntentResult result) {
			if(result == null) {
				//异常场景，请按照调用接口失败处理
				//Abnormal scenarios, Failed to invoke the interface
				return;
			}
			//发送图文消息回调结果
			Status status = result.getStatus();
			if(SNSCode.OK == status.getStatusCode()) {
				Intent intent = result.getIntent();
				if(intent != null) {
					Log.i(TAG, "Start sending text Message page.");
					startActivityForResult(intent, REQUEST_SEND_SNS_MSG);
				}
			} else {
				//其他错误码以及处理方法请参见API文档
				//Additional error codes and processing methods see API documentation
			}
		}
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			
		case R.id.sns_start:
			startSnsActivity();
			break;
			
		case R.id.sns_send_msg:
			sendMessage();
			break;
			
		default:
			break;
		}
	}
	

	/**
	 * 调用消息服务打开界面或者发送图文消息接口返回intent，开发者调用启动intent来授权登录，结果通过onActivityResult返回
	 * Invoke the message service to open the interface or send a message interface to return intent,
	 * the developer invokes the launch intent to authorize the login, and the result is returned via Onactivityresult
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_SEND_SNS_MSG) {
        	if(resultCode == Activity.RESULT_OK) {
        		Log.i(TAG, "Send Message complete.");
        	} else {
                Log.i(TAG, "User canceled.");
        	}
        } else if(requestCode == REQUEST_GET_UI_INTENT) {
        	// TODO: 2018/1/3
		} else if(requestCode == REQUEST_HMS_RESOLVE_ERROR) {
			onActivityResultForResolve(resultCode, data);
        }

    }
    
    /**
     * 发送图文消息需要icon，icon要求不大于30K，并且以二进制流的方式传递
     * 因此该方法首先将bitmap对象压缩到30K以内，并转化为byte数组
	 * Sending text messages icon, Icon requirements is not greater than 30K, And in binary stream mode transfer
	 * Therefore, the method first bitmap object compression to 30K within, And into byte array
     * @param bmp
     * @param size 图片大小 单位为K /Picture size Unit KB
     * @return
     */
    private byte[] bmpToByteArray(Bitmap bmp, int size)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        if (isPNG)
        {
            bmp = replaceTransparentWithWhite(bmp, this);
            bmp.compress(CompressFormat.PNG, 100, output);
        }
        else
        {
            bmp.compress(CompressFormat.JPEG, 100, output);
        }
        
        int options = 100;
        while (output.toByteArray().length > size*1024)
        {
         	//循环判断如果压缩后图片是否大于30kb,大于继续压缩.
			//Cyclic judging if compressed image is greater than 30kb, greater than continue to compression.
            output.reset(); 
            options -= 2;
            if (options <= 0)
            {
                break;
            }
            bmp.compress(Bitmap.CompressFormat.JPEG, options, output);
        }
        
        bmp.recycle();
      
        byte[] result = output.toByteArray();
        try
        {
            output.close();
        }
        catch (IOException e)
        {
        }
        return result;
    }
    
    /**
     * 将PNG图片中的透明部分替换为白色，此方法需要在PNG转JPEG之前调用，否则无效
	 * PNG pictures in the transparent part replacement is white, This method needs to be around the PNG JPEG called before, Otherwise, invalid
     *
     * @param bitmap
     * @param mContext
     * @return
     */
    private Bitmap replaceTransparentWithWhite(Bitmap bitmap, Context mContext)
    {
        Bitmap newBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(newBmp);

        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);

        // 绘制原来的bitmap到画布上
		// Draw original bitmap to canvas
        canvas.drawBitmap(bitmap, 0, 0, mPaint);

        mPaint.setColor(Color.WHITE);
        // 设置转换模式
		// Set the conversion mode
        mPaint.setXfermode(mode);

        int w = getScreenSize((Activity) mContext)[0] / 2 + bitmap.getWidth() / 2;
        int h = getScreenSize((Activity) mContext)[1] / 2 + bitmap.getHeight() / 2;

        // 绘制白色覆盖部分
		// Draw white covers some
        canvas.drawRect(0, 0, w, h, mPaint);

        return newBmp;
    }
    
    /**
     * 获取屏幕尺寸
     * Obtain the screen size
     * @param activity Activity
     * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高/Screen size pixel value, The index is zero value is wide, The index is one value is high
     */
    private int[] getScreenSize(Activity activity)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]
                { metrics.widthPixels, metrics.heightPixels };
    }
    
    /**
     * 该示例代码是模拟从asset文件夹中读取图片文件，用来作为消息服务的图片信息，开发者可以根据实际情况，获取对应源下的图片对象
	 * The example code is simulated from asset read the image file in the folder, Used as message service of the image information,
	 * developers can according to the actual situation, Obtain the source of the image object
     * @return
     */
    private Bitmap getImageFromAssetsFile() {
    	String fileName = "ic_launcher-web.png";
    	
    	if(!fileName.substring(fileName.lastIndexOf("/") + 1).toUpperCase().contains("PNG")){
    		isPNG = true;
    	} else {
    		isPNG = false;
    	}
    	
    	Bitmap bitmap = null;  
    	AssetManager am = getResources().getAssets();  
    	try  {  
    		InputStream is = am.open(fileName);  
    		bitmap = BitmapFactory.decodeStream(is);  
    		is.close();  
    	}  catch (IOException e) {
    		
    	}

    	return bitmap;
    }
}
