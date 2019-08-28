package com.huawei.hmssample.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

/**
 * 实现HMSCore的Request
 * Created by j00161244 on 2017/6/23.
 */
public abstract class AbstractHttpRequest
{
    private final static  String TAG = "AbstractHttpRequest";

    public abstract AbstractHttpResponse createResponse();

    /**
     * 由子类实现，提交数据携带子类数据
     *
     * @param jsonObject 提交子类的JSON数据
     * @return
     */
    protected abstract void getPostData(JSONObject jsonObject) throws JSONException;

    /**
     * 返回服务器URL
     * @return
     * @throws IOException
     */
    protected abstract URL getURL() throws IOException;

    public long contentLength()
    {
        byte[] bytes = body();
        return bytes != null ? bytes.length : -1;
    }

    public byte[] body()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            getPostData(jsonObject);
        }
        catch (JSONException e)
        {
            Log.w(TAG, "Create JSON Request data meet exception.");
        }

        try
        {
            return genBody(jsonObject).getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return new byte[0];
        }
    }

    private String genBody(JSONObject jsonObject)
    {
        StringBuilder strBody = new StringBuilder();
        Iterator it = jsonObject.keys();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String value = null;
            try
            {
                value = jsonObject.getString(key);
            }
            catch (JSONException e)
            {
                Log.w(TAG, "genBody json exception.");
            }

            if (!TextUtils.isEmpty(value))
            {
                strBody.append(key).append('=').append(RSAUtil.urlEncode(value)).append('&');
            }
        }

        int length = strBody.length();
        if (length > 0 && strBody.charAt(length - 1) == '&')
        {
            strBody.deleteCharAt(length - 1);
        }

        return strBody.toString();
    }
}

