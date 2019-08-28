package com.huawei.hmssample.server;

import org.json.JSONObject;

/**
 * Created by l00216976 on 2017/7/11.
 */

public class CheckPlayerSignResponse extends  AbstractHttpResponse {

    private String rtnCode;
    private String ts;
    private String rtnSign;

    public String getRtnCode() {
        return rtnCode;
    }

    public String getTs() {
        return ts;
    }

    public String getRtnSign() {
        return rtnSign;
    }

    @Override
    protected void parse(JSONObject jsonObject)
    {
        rtnCode = jsonObject.optString("rtnCode");
        ts = jsonObject.optString("ts");
        rtnSign = jsonObject.optString("rtnSign");
    }
}
