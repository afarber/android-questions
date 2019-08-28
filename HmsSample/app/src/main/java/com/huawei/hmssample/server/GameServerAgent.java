package com.huawei.hmssample.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.huawei.logger.Log;

/**
 * 与服务器交互代理
 * <p>
 * Created by p00360450 on 2017/6/7.
 */
public final class GameServerAgent
{
    /**
     * 日志标签
     */
    private static final String TAG = "GameServerAgent";

    /**
     * HTTP请求线程池
     */
    private static final ExecutorService httpExecutor = Executors.newFixedThreadPool(4);

    @SuppressWarnings("unchecked")
    public static <Q extends AbstractHttpRequest, P extends AbstractHttpResponse> P call(Q request) throws IOException
    {
        return (P) new GameHttpHelper().send(request);
    }

    /**
     * 请求服务器数据
     *
     * @param request
     * @return
     */
    public static AbstractHttpResponse syncAckServer(AbstractHttpRequest request)
    {
        AbstractHttpResponse r = request.createResponse();
        try
        {
            r = call(request);
        }
        catch (ConnectException e)
        {
            Log.e(TAG, "ack server meet ConnectException.");
        }
        catch (SocketTimeoutException e)
        {
            Log.e(TAG, "ack server meet SocketTimeoutException.");
        }
        catch (IOException e)
        {
            Log.e(TAG, "ack server meet IOException.");
        }
        catch (IllegalArgumentException e)
        {
            Log.e(TAG, "ack server meet IllegalArgumentException.");
            // 服务器下发参数异常
        }
        catch (Exception ex)
        {
            Log.e(TAG, "ack server meet Exception.");
            // 请求服务器失败
        }

        return r;
    }

    /**
     * 异步请求服务器数据
     *
     * @param request
     */
    public static void asyncAckServer(final AbstractHttpRequest request, final RespCallback callback)
    {
        httpExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.notifyResult(syncAckServer(request));
                }
            }
        });
    }
}
