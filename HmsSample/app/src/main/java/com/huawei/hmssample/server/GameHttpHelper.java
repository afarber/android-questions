package com.huawei.hmssample.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;

import android.os.Build;
import android.util.Log;

/**
 * 调用HMSCore的connection执行HTTP请求
 * Created by j00161244 on 2017/6/23.
 */
public class GameHttpHelper
{
    private final static  String TAG = "GameHttpHelper";
    /**
     * 默认HTTP超时，单位：milliseconds。
     */
    private static final int CONN_TIMEOUT = 30 * 1000;
    private static final int READ_TIMEOUT = 30 * 1000;

    public AbstractHttpResponse send(AbstractHttpRequest request) throws IOException {
        if(request != null && request.getURL() != null) {
            try {
                AbstractHttpResponse e = this.send2(request);
                return e;

            } catch (IOException var5) {
                return null;
            }
        } else {
            throw new NullPointerException("request or request.getURL() must not be null.");
        }
    }

    private AbstractHttpResponse send2(AbstractHttpRequest request) throws IOException {
        HttpURLConnection conn = null;

        AbstractHttpResponse var6;
        try {
            URL url = request.getURL();
            conn = (HttpURLConnection)url.openConnection();

            AbstractHttpResponse response = this.execute(conn, request);
            var6 = response;
        } finally {
            if(conn != null) {
                conn.disconnect();
            }

        }

        return var6;
    }

    protected AbstractHttpResponse execute(HttpURLConnection conn, AbstractHttpRequest request) throws IOException
    {
        // 设置SSLSocketFactory
//        if (conn instanceof HttpsURLConnection)
//        {
//            ((HttpsURLConnection) conn).setSSLSocketFactory(GameSSL.getSSLSocketFactory());
//        }

        // 设置HttpURLConnection选项
        conn.setRequestMethod("POST");

        conn.setConnectTimeout(CONN_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        conn.setUseCaches(false);

        Log.i(TAG, "http req method:" + CheckPlayerSignRequest.METHOD + ", url:" + request.getURL());
        Log.i(TAG, "http req method:" + CheckPlayerSignRequest.METHOD + ", content:" + new String(request.body(), "UTF-8"));

        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        long contentLength = request.contentLength();
        if (contentLength != -1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                conn.setFixedLengthStreamingMode(contentLength);
            }
            else
            {
                // API 19以下的版本，参数是int型
                conn.setFixedLengthStreamingMode((int) contentLength);
            }
        }
        else
        {
            conn.setChunkedStreamingMode(0);
        }

        byte[] data = request.body();
        if (data != null)
        {
            OutputStream out = null;
            try
            {
                out = conn.getOutputStream();
                out.write(data);
                out.flush();
            }
            finally
            {
                //IOUtils.closeQuietly(out);
            }
        }
        // 解析服务端响应
        AbstractHttpResponse response = request.createResponse();

        try
        {
            response.parseHttpResponse(conn);
            Log.i(TAG, "http resp method:" + CheckPlayerSignRequest.METHOD + ", content:" + response.getUTF8String());
        }
        catch (JSONException e)
        {
            throw new IllegalArgumentException("JSONException");
        }

        return response;
    }
}
