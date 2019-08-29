package com.huawei.hmssample.server;

import com.huawei.hmssample.GameConstants;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by l00216976 on 2017/7/11.
 */

public class CheckPlayerSignRequest extends AbstractHttpRequest {

    private static final String TAG = "request.checkPlayerSign";

    /**
     * 请求的方法名(对应业务接口)
     */
    public static final String METHOD = "external.hms.gs.checkPlayerSign";

    public static final String URL = "https://gss-cn.game.hicloud.com/GameService-HMS/api/gbClientApi";


    private String appId;

    private String cpId;

    private String ts;

    private String playerId;

    private String playerLevel;

    private String playerSSign;
    private String cpSign;

    public CheckPlayerSignRequest(String appId, String cpId, String playerId, String playerLevel, String playerSSign, String ts)
    {
        this.appId = appId;
        this.cpId = cpId;
        this.playerId = playerId;
        this.playerLevel = playerLevel;
        this.playerSSign = playerSSign;
        this.ts = ts;
        this.cpSign = getCpSign();
    }

    private String getCpSign() {
        Map<String,String> params = new HashMap<String,String>();
        params.put("appId", appId);
        params.put("cpId", cpId);
        params.put("playerId", playerId);
        params.put("playerLevel", playerLevel);
        params.put("playerSSign", playerSSign);
        params.put("ts", ts);
        params.put("method", METHOD);

        String noSign = getSignData(params);

        String sign = RSAUtil.sign(noSign, GameConstants.GAME_PRIVATE_RSA);

        return sign;
    }

    public static String getSignData(Map<String, String> params)
    {
        StringBuffer content = new StringBuffer();

        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++)
        {
            String key = (String)keys.get(i);
            String value = params.get(key);
            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + RSAUtil.urlEncode(value));
            }
        }
        return content.toString();
    }


    protected URL getURL() throws IOException
    {
        return new URL(URL);
    }

    @Override
    protected void getPostData(JSONObject json) throws JSONException
    {
        json.put("method", METHOD);
        json.put("appId", appId);
        json.put("cpId", cpId);
        json.put("playerId", playerId);
        json.put("playerLevel", playerLevel);
        json.put("playerSSign", playerSSign);
        json.put("ts", ts);
        json.put("cpSign", cpSign);
        return;
    }

    @Override
    public AbstractHttpResponse createResponse()
    {
        return new CheckPlayerSignResponse();
    }
}
