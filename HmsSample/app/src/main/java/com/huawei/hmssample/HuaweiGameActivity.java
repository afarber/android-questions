package com.huawei.hmssample;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.entity.game.GamePlayerInfo;
import com.huawei.hms.support.api.entity.game.GameStatusCodes;
import com.huawei.hms.support.api.entity.game.GameUserData;
import com.huawei.hms.support.api.game.GameLoginHandler;
import com.huawei.hms.support.api.game.GameLoginResult;
import com.huawei.hms.support.api.game.HuaweiGame;
import com.huawei.hms.support.api.game.SavePlayerInfoResult;
import com.huawei.hms.support.api.game.ShowFloatWindowResult;
import com.huawei.hmssample.server.AbstractHttpResponse;
import com.huawei.hmssample.server.CheckPlayerSignRequest;
import com.huawei.hmssample.server.CheckPlayerSignResponse;
import com.huawei.hmssample.server.GameServerAgent;
import com.huawei.hmssample.server.RSAUtil;
import com.huawei.hmssample.server.RespCallback;
import com.huawei.logger.Log;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * {类描述}
 *
 * @author #user
 * @version #time
 */
public class HuaweiGameActivity extends BaseActivity implements View.OnClickListener {

    private final static  String TAG = "HuaweiGameActivity";

    //首次onResume时可能还没有connect成功，记录标记，connect成功之后再调用showFloatWindow，否则首次onResume无法显示浮标
    //The first onresume may not have connect success, record mark, connect successful then call Showfloatwindow, otherwise the first onresume can not show the buoy
    private boolean isShowFloatWindow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huaweigame);

        findViewById(R.id.hwgame_login).setOnClickListener(this);

        //在sample 界面上显示日志提示信息的窗口，请忽略
        //In sample log information is displayed on the GUI window, Skip
        addLogFragment();

        //创建华为移动服务client实例
        //需要指定api为HuaweiGame.GAME_API
        //设置连接回调以及连接失败监听
        //Create Huawei mobile service client instance
        //The API needs to be specified as HuaweiGame.GAME_API
        //Set the related callback interface
        client = new HuaweiApiClient.Builder(this)
                .addApi(HuaweiGame.GAME_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //建议在oncreate的时候连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        //It is recommended in oncreate connected Huawei mobile service
        //Service may be determined according to their service form client connection or disconnection of the time, But ensuring that connect and disconnect must appear in pairs
        client.connect(this);
    }


    /**
     * 请在业务的onResume()中调用显示浮标接口，以展示华为游戏浮标
     * 如果client未连接上，则不应用调用该接口，并记录状态，在华为移动服务连接成功之后再次调用显示浮标接口。
     * Please call the display buoy interface in the business Onresume () to show the Huawei game Buoy
     * If the client is not connected, the call to the interface is not applied and the status is logged and the display buoy interface is invoked again after the Huawei Mobile service connection succeeds.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(client.isConnected()) {
            showFloatWindow();
            isShowFloatWindow = true;
        } else {
            isShowFloatWindow = false;
        }
    }


    /**
     * 显示浮标
     * Show Buoy
     */
    private void showFloatWindow() {
        PendingResult<ShowFloatWindowResult> showFloatWindowResult = HuaweiGame.HuaweiGameApi.showFloatWindow(client, HuaweiGameActivity.this);
        showFloatWindowResult.setResultCallback(new ShowFloatWindowCallback());
    }

    /**
     * 显示浮标接口结果回调
     * Result callback of Show buoy Interface Result
     */
    private class ShowFloatWindowCallback implements ResultCallback<ShowFloatWindowResult> {

        @Override
        public void onResult(ShowFloatWindowResult result) {
            Log.i(TAG, "gameShowFloatWindowResult:" + result.getStatus().getStatusCode());
        }
    }

    /**
     * 需要在业务的onPause中隐藏游戏浮标
     *  Need to hide game buoys in the on pause
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        HuaweiGame.HuaweiGameApi.hideFloatWindow(client, this);
    }

    @Override
    public void onConnected() {
        super.onConnected();Log.i(TAG, "HuaweiApiClient Connect Successfully!");
        if(!isShowFloatWindow) {
            //首次onResume时可能还没有connect成功，记录标记，connect成功之后再调用showFloatWindow，否则首次onResume无法显示浮标
            //The first onresume may not have connect success, record mark, connect successful then call Showfloatwindow, otherwise the first onresume can not show the buoy
            showFloatWindow();
            isShowFloatWindow = true;
        }

        //在华为移动服务连接成功之后，需要调用checkUpdate来对应用进行升级检测
        //After Huawei Mobile Service connection is successful, we need to call Checkupdate to upgrade the application.
        client.checkUpdate(this);
    }

    /**
     * 登录华为帐号
     * Login to Huawei Account
     */
    private void login() {
        if(!client.isConnected()) {
            Log.i(TAG, "Sign in failed！Reason: Huaweiapiclient not connected！");
            client.connect(this);
            return;
        }

        PendingResult<GameLoginResult> loginResult = HuaweiGame.HuaweiGameApi.login(client, this, 1, handler);
        loginResult.setResultCallback(new GameLoginCallback());
    }

    /**
     * 登录华为帐号结果回调
     * Result callback of Login Huawei Account
     */
    private class GameLoginCallback implements ResultCallback<GameLoginResult> {

        @Override
        public void onResult(GameLoginResult result) {
            if(result != null) {
                int statusCode = result.getStatus().getStatusCode();
                if (statusCode == GameStatusCodes.GAME_STATE_PARAM_ERROR) {
                    //传入的参数不正确，有可能为空值，请检查入参。
                    //The parameters passed in are incorrect and may be null, please check the entry.
                } else if (statusCode == GameStatusCodes.GAME_STATE_CALL_REPEAT) {
                    //如果调用过login接口但是SDK还没有处理完毕的时候再次调用login接口
                    //Call the login interface again if the login interface is invoked but the SDK has not been processed yet
                } else if (statusCode == GameStatusCodes.GAME_STATE_SUCCESS) {
                    //表明login接口成功调用，实际的登录结果会在handler中返回
                    //Indicates that the login interface is successfully invoked and the actual login result is returned in handler
                } else {
                    //其他错误码 参见开发指南
                    //Other error codes See Development guide
                }
            }
        }
    }

    /**
     * HMS SDK会通过GameLoginHandler的onResult方法返回登录结果，onChange方法通知游戏进行帐号切换。
     * The HMS SDK will return the login results via the Gameloginhandler Onresult method, onchange the method to notify the game for account switching.
     */
    GameLoginHandler handler = new GameLoginHandler() {
        @Override
        public void onResult(int statusCode, GameUserData gameUserData) {
            Log.i(TAG, "GameEventHandler onResult:" + statusCode);

            //当statusCode为GameStatusCodes.GAME_STATE_SUCCESS
            //表明登录接口成功返回登录结果
            //这边会有2种结果，一种是登录结果，如果apk中于用户的playerId，那么就会先返回playerId
            //应用可以先行处理 ，当APK登录鉴权结果返回的时候，那么就会再次调用该接口返回鉴权结果
            //判断的标准 就是gameUserData.getIsAuth() 值是否未0
            //When statuscode for gamestatuscodes.game_state_success
            // indicates that the login interface successfully returns the login results
            // Here there will be 2 results, one is the login result, if the APK in the user's PLA Yerid, then will return playerID
            // application can be processed first, when the APK login authentication results returned, then call the interface again to return the authentication result//Judge the standard is the Gameuserdata.getisauth () value is not 0
            if(statusCode == GameStatusCodes.GAME_STATE_SUCCESS) {
                if(gameUserData.getIsAuth() == 0) {
                    //非鉴权结果返回，返回数据中只有playerId
                    //游戏收到该次回调结果，获取到玩家的playerId，可以让用户进入游戏。
                    // 并需要异步等待登录鉴权结果的返回并验证登录结果的有效性。
                    // 如果异步验签结果不通过，需要终止玩家游戏并提示用户重新登录。
                    // 游戏也可以忽略该次回调结果，直接等待鉴权结果的返回。
                    // Non-authentication results returned, the return data only playerID
                    // game received the callback result, get to the player's playerID, can let the user into the game.
                    // It is also necessary to wait asynchronously for the return of the logon authentication result and verify the validity of the login results.
                    // If the asynchronous verification results do not pass, you need to terminate the player game and prompt the user to log in again. The game can also ignore the callback result and wait for the return of the authentication result directly.
                    gameUserData.getPlayerId();
                } else if(gameUserData.getIsAuth() == 1) {
                    //游戏收到该次回调结果，需要对登录结果进行验签并确保验签通过，才允许玩家继续游戏。
                    //The game receives the result of the callback and requires that the login result be checked and checked through to allow the player to continue the game.
                    checkPlayerSign(gameUserData);

                    //如果鉴权通过，可以让用户进入游戏
                    //同时必须调用上传玩家信息接口
                    // If authentication is passed, you can let the user enter the game and must also invoke the upload player information interface
                    GamePlayerInfo role = new GamePlayerInfo();
                    role.area = "20";
                    role.rank = "level 56";
                    role.role = "hunter";
                    role.sociaty = "Red Cliff II";
                    PendingResult<SavePlayerInfoResult> savePlayerInfoResult  = HuaweiGame.HuaweiGameApi.savePlayerInfo(client, role);
                    savePlayerInfoResult.setResultCallback(new SavePlayerInfoCallback());
                }
            }
        }

        /**
         * 不依赖playerId识别玩家的游戏可以不处理此回调。
         * 当游戏收到该回调结果的时候，表示用户在游戏浮标中进行了帐号切换，游戏收到通知之后需要返回游戏首页重新调用login接口。
         * Upload Player Information interface result callback
         * Only indicates the success of the interface call. If the application upload player information fails, it is recommended to upload again.
         */
        @Override
        public void onChange() {
            Log.i(TAG, "GameEventHandler onChange.");
            login();
        }
    };

    /**
     * 上传玩家信息接口结果回调
     * 仅表示接口调用是否成功。如果应用上传玩家信息失败，建议再次上传。
     */
    private class SavePlayerInfoCallback implements ResultCallback<SavePlayerInfoResult> {

        @Override
        public void onResult(SavePlayerInfoResult result) {
            Log.i(TAG, "savePlayerInfoResult result:" + result.getStatus().getStatusCode());
        }
    }

    /**
     * 调用登录接口成功后返回帐号信息并包含签名信息
     * 需要对签名信息的合法性进行校验
     * 验证的方法如下:示例代码中验签是在客户端完成的，仅作参考。建议开发者将游戏私钥和验签过程都放在业务服务器完成。
     *1.发送登录结果:
     *     将appid，cpid，playerId, playerLever，ts, gameAuthSign以及业务接口名称(key为method,value固定为external.hms.gs.checkPlayerSign) 以上字段发送给开发者服务端。
     *2.使用游戏私钥签名：
     *      a)	业务服务器将收到的字段按照key值升序排列后以key=value并以&的方式连接起来生成待签名的字符串，使用开发者联盟网站获取的游戏秘钥进行签名 使用SHA256WithRSA签名算法。
     *      b)	将第1步的7个字段以及第2-a步中的签名字段(key为cpSign) 共8个字段按照key值升序排列后以key=value并以&的方式连接起来生成新的字符串。
     *3.验签：
     *      a)	将第2-b步中获取到的字符串 以post方法 并设置content-Type为application/json 请求华为游戏服务器xxx。
     *      b)	等待华为游戏服务器响应。如果返回结果中的返回码为非0，则表明验签失败。如果返回结果中的返回码为0 并且返回了签名结果，表明了华为游戏服务对于该次登录结果验签通过，应用需要对华为游戏服务器返回结果进行再次验证，以确保结果来自于华为游戏服务器。
     *4.使用游戏公钥验签:
     *      a)	将第3-b步中返回的rtnCode(服务器返回) ts(服务器返回) 按照key值升序排列后以key=value并以&的方式连接起来生成待签名的字符串
     *      b)	使用开发者联盟提供的游戏公钥对第4-a步中带签名字符串以及第3-b步中获取的签名信息进行验证，如果验证通过，则表明登录结果有效。
     *5.返回登录验签结果：
     *      开发者服务端将验签结果告知游戏。
     * Return account information and signature information after successful of call the Login interface
     * The legality of the signature information needs to be verified
     * Verify the method is as follows: The sample code verification is done on the client, for reference only.
     * It is recommended that the developer put the game's private key and verification process on the Business Server.
     * 1. Send Login Result:
     *    Send Appid,cpid,playerid, Playerlever,ts, gameauthsign and Business interface name (key is method, Value fixed to external.hms.gs.checkPlayerSign) to the developer server.
     * 2. Use the game private key signature:
     * A) The Business Server arranges the received fields in ascending order of the key values and joins them in a key=value to generate a string to be signed, using a game secret key obtained by the developer affiliate website to sign the SHA256WITHRSA signature algorithm.
     * B) 7 fields in step 1th and the Signature field (key Cpsign) in step Imidazo 8 fields are sorted in ascending order of key values to key=value and then join together to generate a new string.
     * 3:
     * A) The string obtained in step B is presented in the Post method and the Content-type is set for Application/json requesting the Huawei Game server XXX.
     * B) Wait for Huawei game server to respond. If the return code in the returned result is not 0, the verification fails.     If the return code in the returned result is 0 and the signature result is returned, it indicates that the Huawei Gaming service verifies the result of the login, and the application needs to verify the return results of the Huawei game server to ensure the result comes from the Huawei game server.
     * 4. Use the game public key verification:
     * A) the Rtncode (server return) TS (server return) returned in step 3-b are sorted in ascending order of the key value, and then connected together in a key=value and a to create a string to be signed
     * B) using the game public key provided by the Developer Federation     The signature string in step structuring and the signature information obtained in step 3-b are validated and, if validated, indicates that the login result is valid. Pocket.
     * 5.Return to login Verification results:
     * Developer Server will inform the game of verification results.
     * @param gameUserData
     */
    private void checkPlayerSign(final GameUserData gameUserData) {
        CheckPlayerSignRequest req = new CheckPlayerSignRequest(GameConstants.appId, GameConstants.cpId, gameUserData.getPlayerId(),
                String.valueOf(gameUserData.getPlayerLevel()), gameUserData.getGameAuthSign(), gameUserData.getTs());
        GameServerAgent.asyncAckServer(req, new RespCallback(){

            @Override
            public void notifyResult(AbstractHttpResponse response) {
                if(response instanceof CheckPlayerSignResponse)
                {
                    final CheckPlayerSignResponse checkPlayerSignResponse = (CheckPlayerSignResponse)response;
                    if("0".equals(checkPlayerSignResponse.getRtnCode()))
                    {
                        Log.i(TAG, "CheckPlayerSign success!");
                        String nosign = "rtnCode=" + RSAUtil.urlEncode(checkPlayerSignResponse.getRtnCode()) + "&ts=" + RSAUtil.urlEncode(checkPlayerSignResponse.getTs());
                        boolean s = RSAUtil.doCheck(nosign, checkPlayerSignResponse.getRtnSign(), GameConstants.GAME_PUBLIC_RSA);
                        if(s)
                        {
                            Log.i(TAG, "check rtnSign success.");
                        }
                        else
                        {
                            Log.e(TAG, "check rtnSign failed.");
                        }
                    }
                    else
                    {
                        Log.e(TAG, "CheckPlayerSign failed! rtnCode:" + checkPlayerSignResponse.getRtnCode());
                    }
                }
            }
        });

    }

    /**
     * 当用户未登录或者未授权，调用signin接口拉起对应的页面处理完毕后会将结果返回给当前activity处理
     * When you invoke signin interface returned when unauthorized,
     * The developer invokes the intent to authorized to log in to, the result returned by the onActivityResult
     */
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
            case R.id.hwgame_login:
                login();
                break;

            default:
                break;
        }
    }
}
