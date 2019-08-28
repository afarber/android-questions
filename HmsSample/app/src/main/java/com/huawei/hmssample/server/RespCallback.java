package com.huawei.hmssample.server;

/**
 * 异步请求服务器回调结果接口
 * <p>
 * Created by p00360450 on 2017/6/8.
 */
public interface RespCallback
{
    void notifyResult(AbstractHttpResponse response);
}
