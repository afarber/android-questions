package com.huawei.hmssample.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 实现HMScore的Http响应
 * Created by j00161244 on 2017/6/23.
 */
public abstract class AbstractHttpResponse
{
    private final static  String TAG = "AbstractHttpResponse";
    // 服务端返回的数据
    private byte[] bodyBytes;
    // http响应消息，如 OK
    private String httpMessage;
    // http 响应码，如 200
    private int httpCode;
    // http 响应的长度
    private long httpContentLength;
    // http 响应的数据类型
    private String httpContentType;
    /**
     * 缓存的json格式响应数据
     */
    private JSONObject jsonContent;


    /**
     * 解析服务端返回的数据
     */
    protected abstract void parse(JSONObject json) throws JSONException;

    /**
     * 构建并解析Http的响应数据
     *
     * @param conn 执行完请求的connection
     */
    public final void parseHttpResponse(HttpURLConnection conn) throws IOException, JSONException
    {
        httpCode = conn.getResponseCode();
        httpMessage = conn.getResponseMessage();

        if (httpCode == HttpURLConnection.HTTP_OK)
        {
            // 读取服务端返回的ContentLength，失败返回-1
            httpContentLength = stringToLong(conn.getHeaderField("Content-Length"));

            httpContentType = conn.getContentType();
            bodyBytes = read(conn.getInputStream());

            // 调用子类解析函数
            parse();
        }
    }

    protected void parse() throws JSONException
    {
        // 所有GSS接口返回的应该都是json串
        jsonContent = getJsonContent();

        // 调用子类解析接口
        if (jsonContent != null)
        {
            // 在调用子类解析详细业务数据
            parse(jsonContent);
        }
    }

    /**
     * 获取服务器返回的内容
     *
     * @return JSON对象数据
     * @throws JSONException
     */
    private JSONObject getJsonContent() throws JSONException
    {
        JSONObject json = null;
        String str = getUTF8String();
        if (str != null)
        {
            json = new JSONObject(str);
        }

        return json;
    }


    // 读取服务端响应数据
    private byte[] read(InputStream content) throws IOException
    {
        long contentLength = contentLength();
        if (contentLength > Integer.MAX_VALUE)
        {
            throw new IOException("Cannot buffer entire body for content length: " + contentLength);
        }

        byte[] bytes;
        try
        {
            bytes = toByteArray(content);
        }
        finally
        {
            closeQuietly(content);
        }

        if (contentLength != -1 && contentLength != bytes.length)
        {
            throw new IOException("Content-Length and stream length disagree");
        }

        return bytes;
    }

    /**
     * 获取响应的长度
     */
    public final long contentLength()
    {
        return httpContentLength;
    }

    /**
     * 获取服务端返回的消息体
     */
    public final byte[] bytes()
    {
        if (bodyBytes != null)
        {
            // fix findbugs
            return bodyBytes.clone();
        }
        else
        {
            return new byte[0];
        }
    }

    /**
     * 把响应消息体以UTF8编码为字符串返回
     */
    public final String getUTF8String()
    {
        String str = null;
        try
        {
            byte[] b = bytes();
            if (b.length > 0)
            {
                str = new String(b, "UTF-8");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("response", "make utf-8 string failed, unsupported encoding.");
        }

        return str;
    }

    // 字符串转换成long值
    private static long stringToLong(String s)
    {
        if (s == null)
        {
            return -1;
        }
        try
        {
            return Long.parseLong(s);
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, new byte[4096]);
    }

    public static long copy(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0L;

        int n1;
        for(boolean n = false; -1 != (n1 = input.read(buffer)); count += (long)n1) {
            output.write(buffer, 0, n1);
        }

        return count;
    }

    public static void closeQuietly(InputStream input) {
        if(input != null) {
            try {
                input.close();
            } catch (IOException e) {
                Log.e(TAG, "closeQuietly IOException", e);
            }
        }
    }
}
