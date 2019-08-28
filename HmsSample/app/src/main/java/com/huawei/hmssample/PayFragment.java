package com.huawei.hmssample;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.pay.HwPayConstant;
import com.huawei.hms.support.api.entity.pay.OrderRequest;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.OrderResult;
import com.huawei.hms.support.api.pay.PayResult;
import com.huawei.hms.support.api.pay.PayResultInfo;
import com.huawei.logger.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * {类描述}
 *
 * @author #user
 * @version #time
 */
public class PayFragment extends Fragment implements View.OnClickListener, OnFragmentResultListener {

    public static final String TAG = "PayFragment";

    //以下常量用于测试
    //The following constants are used to test
    private final static String PRODUCT_NAME = "TestProductName";
    private final static String PRODUCT_DESC = "TestProductDescription";
    private final static double PRODUCT_PRICE = 0.01;

    private HuaweiApiClient client;

    //支付相关信息map
    //Payment related Information map
    private Map<String, Object> params;

    private View view;

    private PayReq payReq = null;

    private final int REQ_CODE_PAY = 3001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pay, container, false);
        view.findViewById(R.id.hwpay_buy).setOnClickListener(this);
        view.findViewById(R.id.hwpay_query).setOnClickListener(this);

        ((TextView)view.findViewById(R.id.ware_name)).setText("Product Name: "+PRODUCT_NAME);
        ((TextView)view.findViewById(R.id.ware_desc)).setText("Product Description: "+PRODUCT_DESC);
        ((TextView)view.findViewById(R.id.ware_price)).setText("Product Price: "+PRODUCT_PRICE);
        ((EditText)view.findViewById(R.id.ware_num_edit)).setText("1");

        if (getActivity() instanceof ISetHuaweiClient) {
            client = ((ISetHuaweiClient)getActivity()).getHuaweiClient();
        }
        return view;
    }


    /**
     * 支付接口调用方式
     * How the payment interface is invoked
     */
    private void pay() {
        if(!client.isConnected()) {
            Log.i(TAG, "Payment failed because: Huaweiapiclient not connected!");
            client.connect(getActivity());
            return;
        }

        PendingResult<PayResult> payResult = HuaweiPay.HuaweiPayApi.pay(client, createPayReq());
        payResult.setResultCallback(new PayResultCallback(REQ_CODE_PAY));
    }


    /**
     * 支付接口调用的回调处理
     * Callback of pay
     * 只有当处理结果中的返回码为 PayStatusCodes.PAY_STATE_SUCCESS的时候，开发者需要继续调用支付否则就需要处理支付失败结果
     * Only if the return code in the processing result is paystatuscodes.pay_state_success,
     * the developer will need to continue to call the payment or it will need to process the payment failure result
     */
    private class PayResultCallback implements ResultCallback<PayResult> {
        private int requestCode;
        PayResultCallback(int requestCode) {
            this.requestCode = requestCode;
        }
        @Override
        public void onResult(PayResult result) {
            if(result == null) {
                //异常场景，接口调用失败
                //Abnormal scenarios, Failed to invoke the interface
                return;
            }
            Status status = result.getStatus();
            if (PayStatusCodes.PAY_STATE_SUCCESS == status.getStatusCode()) {
                //当支付回调 返回码为0的时候，表明支付流程正确，开发者需要调用startResolutionForResult接口来进来后续处理
                //When the payment callback return code is 0, indicating that the payment process is correct,
                //the developer needs to invoke the Startresolutionforresult interface to come in for subsequent processing
                //支付会先判断华为帐号是否登录，如果未登录，会先提示用户登录帐号。之后才会进行支付流程
                //Payment will first determine whether the Huawei account is logged in,
                // if not logged in, will first prompt the user login account. Before the payment process is made.
                try {
                    status.startResolutionForResult(getActivity(), requestCode);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Start payment failed:"+e.getMessage());
                }
            } else {
                Log.i(TAG, "Start payment failed，Error Code :" + status.getStatusCode());
            }
        }
    }

    /**
     * 生成PayReq对象，用来在进行支付请求的时候携带支付相关信息,订单参数需要商户使用在华为开发者联盟申请的RSA私钥进行签名，强烈建议将签名操作在商户服务端处理，避免私钥泄露
     * Generate Payreq object to carry the payment related information when making the payment request,
     * the order parameter requires the merchant to sign with the RSA private key requested by the Huawei Developer Alliance,
     * it is highly recommended to handle the signature operation on the merchant service side to avoid the private key leaking
     */
    private PayReq createPayReq() {
        getPaySignInfo();
        payReq = new PayReq();

        //商品名称
        //Product Name
        payReq.productName = (String) params.get(HwPayConstant.KEY_PRODUCTNAME);
        //商品描述
        //Product Description
        payReq.productDesc = (String) params.get(HwPayConstant.KEY_PRODUCTDESC);
        //商户ID
        //Merchant ID
        payReq.merchantId = (String)params.get(HwPayConstant.KEY_MERCHANTID);
        //应用ID
        //Application ID
        payReq.applicationID = (String) params.get(HwPayConstant.KEY_APPLICATIONID);
        // 支付金额
        //Amount paid
        payReq.amount = String.valueOf(params.get(HwPayConstant.KEY_AMOUNT));
        // 支付订单号
        //Payment order Number
        payReq.requestId = (String) params.get(HwPayConstant.KEY_REQUESTID);
        // 国家码
        //Country code
        payReq.country = (String)params.get(HwPayConstant.KEY_COUNTRY);
        //币种
        //Currency
        payReq.currency = (String)params.get(HwPayConstant.KEY_CURRENCY);
        // 渠道号
        //Channel number
        payReq.sdkChannel = (Integer) params.get(HwPayConstant.KEY_SDKCHANNEL);
        // 回调接口版本号
        //Callback Interface Version number
        payReq.urlVer = (String) params.get(HwPayConstant.KEY_URLVER);

        //以上信息按照一定规则进行签名,建议开发者在服务器端储存签名私钥，并在服务器端进行签名操作。
        //The above information is signed according to certain rules,
        // and it is recommended that the developer store the signature private key on the server side and sign the operation on the server side.
        payReq.sign = Utils.getSign(params);

        // 商户名称，必填，不参与签名。会显示在支付结果页面
        //Merchant name, must be filled out, do not participate in the signature. will appear on the Pay results page
        payReq.merchantName = "TestMerchantName";
        // 分类，必填，不参与签名。该字段会影响风控策略,X4：主题,X5：应用商店,	X6：游戏,X7：天际通,X8：云空间,X9：电子书,X10：华为学习,X11：音乐,X12 视频,
        // X31 话费充值,X32 机票/酒店,X33 电影票,X34 团购,X35 手机预购,X36 公共缴费,X39 流量充值
        //Categories, required, do not participate in the signature. This field affects wind control strategies,
        // X4: Themes, X5: App Store, X6: Games, X7: Sky Pass, X8: Cloud Space, X9: ebook, X10: Huawei Learning, X11: Music, X12 video,
        //X31, X32 air tickets/hotels, X33 movie tickets, X34 Group purchase, X35 mobile phone advance, X36 public fees, X39 flow Recharge
        payReq.serviceCatalog = "X6";
        //商户保留信息，选填不参与签名，支付成功后会华为支付平台会原样 回调开发者服务端
        //The merchant retains the information, chooses not to participate in the signature, and after the payment succeeds,
        //the Huawei payment platform will be callback to the developer server.
        payReq.extReserved = "Test ext reserved info!";

        return payReq;
    }

    /**
     * 生成支付信息map 包含一下信息，该信息需要参与签名
     * Generate Payment information map contains information that requires participation in signing
     * HwPayConstant.KEY_MERCHANTID  必选参数 商户id，开发者联盟网站生成的商户ID/支付ID
     *                               Required Parameters ,Merchant ID, developer affiliate site generated merchant ID payment ID
     * HwPayConstant.KEY_APPLICATIONID 必选参数 应用的appid，开发者联盟网站生成
     *                                 Required Parameters, AppID for application ,Developer Alliance site Generation
     * HwPayConstant.KEY_PRODUCTNAME 必选参数 商品名称 此名称将会在支付时显示给用户确认 注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > ,
     *                               Required Parameters,Product Name This name will be displayed to the user at the time of payment confirmation
     *                               Note: This field cannot contain special characters, including # "&/? $ ^ *:) \ < >,
     * HwPayConstant.KEY_PRODUCTDESC 必选参数 商品描述 注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , |
     *                               Required Parameters,Product Description Note: This field cannot contain special characters, including # "&/? $ ^ *:) \ < >, |
     * HwPayConstant.KEY_REQUESTID  必选参数 请求订单号。其值由商户定义生成，用于标识一次支付请求，每次请求需唯一，不可重复。
     * 					            支付平台在服务器回调接口中会原样返回requestId的值。注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , .以及中文字符
     * 					            Required Parameters,Request order number.     The value is generated by the merchant definition and is used to identify a payment request
     * 					            that is unique and not repeatable on each request. * The payment platform returns the RequestID value as is in the server callback interface.
     * 					            Note: This field cannot contain special characters, including # "&/? $ ^ *:) \ < >,. and Chinese characters
     * HwPayConstant.KEY_AMOUNT 必选参数 支付金额 string类型，精确到小数点后2位 比如 20.00
     *                          Required Parameters,Payment amount String type, accurate to 2 digits after decimal point such as 20.00
     * HwPayConstant.KEY_CURRENCY 必选参数 币种 币种，用于支付的币种，如USD、CNY、MYR。符合ISO 4217，默认CNY，参见： http://www.iso.org/iso/home/standards/currency_codes.htm
     *                            Required Parameters, Currency currency for the currency to be paid, such as USD, CNY, MYR. Complies with ISO 4217, default CNY,
     *                            see: http://www.iso.org/iso/home/standards/currency_codes.htm
     * HwPayConstant.KEY_COUNTRY 必选参数 国家码.用于区分国家信息，如US、CN、MY，符合ISO 3166标准，参见：http://www.iso.org/iso/home/standards/country_codes.htm
     *                           Required Parameters,Country code. Used to differentiate national information, such as us, CN, my, in accordance with ISO 3166 standards,
     *                           see: http://www.iso.org/iso/home/standards/country_codes.htm
     * HwPayConstant.KEY_URL 可选参数 支付结果回调URL. 华为服务器收到后检查该应用有无在开发者联盟配置回调URL，如果配置了则使用应用配置的URL，否则使用此url
     *   					 作为该次支付的回调URL,建议直接 以配置在 华为开发者联盟的回调URL为准
     *   				    Optional parameters,The payment result callback URL. Huawei Server to check if the application has a callback URL in the developer Federation configuration,
     *   				    if configured then use the URL of the application configuration.Otherwise, use this URL as the callback URL for the payment,
     *   				    which is recommended directly to the callback URL configured in the Huawei Developer Federation
     * HwPayConstant.KEY_URLVER 可选参数  回调接口版本号。如果传值则必须传2， 额外回调信息，具体参考接口文档
     *                          Optional parameters,The callback interface version number. If the value must pass 2, additional callback information, specific reference interface document
     * HwPayConstant.KEY_SDKCHANNEL 必选参数 渠道信息。 取值如下：0 代表自有应用，无渠道 1 代表应用市场渠道 2 代表预装渠道 3 代表游戏中心渠道
     *                              Required Parameters,Channel information. Values are as follows: 0 stands for own application,
     *                              Channel 1 represents the application market Channel 2 represents the pre-installed channel 3 represents the game center channel
     */
    private void getPaySignInfo() {
        if(params != null) {
            params.clear();
        } else {
            params = new HashMap<>();
        }
        params.put(HwPayConstant.KEY_MERCHANTID, PayContasts.cpId);
        params.put(HwPayConstant.KEY_APPLICATIONID, PayContasts.appId);

        //以下数据以实际商品信息为准
        //The following data is subject to actual commodity information
        params.put(HwPayConstant.KEY_PRODUCTNAME, PRODUCT_NAME);
        params.put(HwPayConstant.KEY_PRODUCTDESC, PRODUCT_DESC);

        DateFormat format = new java.text.SimpleDateFormat("yyyyMMddhhmmssSSS");
        String requestId = format.format(new Date());
        int random=(int) ((Math.random()+1)*100000);
        requestId = requestId+random;
        params.put(HwPayConstant.KEY_REQUESTID, requestId);

        int num = Integer.valueOf(((EditText) view.findViewById(R.id.ware_num_edit)).getText().toString());
        String amount = String.format("%.2f", PRODUCT_PRICE * num);
        params.put(HwPayConstant.KEY_AMOUNT, amount);

        params.put(HwPayConstant.KEY_CURRENCY, "CNY");
        params.put(HwPayConstant.KEY_COUNTRY, "CN");

        params.put(HwPayConstant.KEY_URLVER, "2");
        params.put(HwPayConstant.KEY_SDKCHANNEL, 1);
    }

    /**
     * 查询订单接口
     * 如果应用存在业务服务器，那么订单状态请以到业务服务器的查询结果为准。
     * 建议没有业务服务器的应用在以下场景调用该查询订单接口以确认订单支付状态，解决支付掉单情况。
     * 1单机游戏或应用在支付过程异常中断（关机、程序奔溃等）
     * 2支付结果码为30005、30002
     * 3支付成功但是验签失败。
     *
     * Query Order Interface
     * If the application has a business Server, the order status is based on the query results to the Business Server.
     * It is recommended that no Business Server application call the query order interface in the following scenario to confirm the order payment status and resolve the payment slip.
     * 1 single game or application in the payment process abnormal interruption (shutdown, program crash, etc.)
     * 2 Payment result code is 30005, 30002
     * 3 Payment succeeded but the verification failed.
     * @param orderRequest
     */
    private void checkPayResult(final OrderRequest orderRequest) {
        if (!client.isConnected()) {
            Log.i(TAG, "Query order failed because: Huaweiapiclient not connected!");
            client.connect(getActivity());
            return;
        }
        PendingResult<OrderResult> checkPayResult = HuaweiPay.HuaweiPayApi.getOrderDetail(client, orderRequest);
        checkPayResult.setResultCallback(new GetOrderDetailCallback());
    }

    /**
     * 查询订单接口结果回调
     * Query Order Interface Result callback
     */
    private class GetOrderDetailCallback implements ResultCallback<OrderResult> {
        @Override
        public void onResult(OrderResult result) {

            if (result == null) return;

            Map<String, Object> paramsa = new HashMap<>();
            paramsa.put("returnCode", result.getReturnCode());

            String tmp = result.getRequestId();
            if (!TextUtils.isEmpty(tmp)) {
                paramsa.put("requestId", tmp);
            }

            tmp = result.getOrderID();
            if (!TextUtils.isEmpty(tmp)) {
                paramsa.put("orderID", tmp);
            }

            tmp = result.getOrderStatus();
            if (!TextUtils.isEmpty(tmp)) {
                paramsa.put("status", tmp);
            }

            tmp = result.getOrderTime();
            if (!TextUtils.isEmpty(tmp)) {
                paramsa.put("orderTime", tmp);
            }

            tmp = result.getTradeTime();
            if (!TextUtils.isEmpty(tmp)) {
                paramsa.put("tradeTime", tmp);
            }

            tmp = result.getReturnDesc();
            if (!TextUtils.isEmpty(tmp)) {
                paramsa.put("returnDesc", tmp);
            }

            String noSigna = Utils.getNoSign(paramsa);

            if (result.getReturnCode() == 0) {
                boolean checkSuccess = Utils.doCheck(noSigna, result.getSign());
                if (checkSuccess) {
                    Log.i(TAG, "Payment Query Results: Successful, signature verification OK.)");
                } else {
                    Log.i(TAG, "Payment Query Result: Successful, signature verification failed. If this is a test process, check the relevant parameters for the signature (public key, AppID, etc.)");
                }
            } else if (result.getReturnCode() == PayStatusCodes.ORDER_STATUS_HANDLING
                    || result.getReturnCode() == PayStatusCodes.ORDER_STATUS_UNTREATED
                    || result.getReturnCode() == PayStatusCodes.PAY_STATE_TIME_OUT) {
                Log.i(TAG, "Payment Query Results: The order has not finished processing, over time to query again. such as 30 minutes after the query again. If it is more than 24 hours, treat as failed, delete reqid");
            } else if (result.getReturnCode() == PayStatusCodes.PAY_STATE_NET_ERROR) {
                Log.i(TAG, "Payment Query results: Network problems lead to failure, please make sure the network is connected before querying.");
            } else {
                Log.i(TAG, "Payment Query Result: failed. If you are in the process of testing, carefully review request parameters and signature parameters.");
            }
        }
    }

    @Override
    public void OnFragmentResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_PAY) {
            //当返回值是-1的时候表明用户支付调用调用成功
            //When the return value is-1 indicates that the user pays the call successfully
            if (resultCode == Activity.RESULT_OK) {
                PayResultInfo payResultInfo = HuaweiPay.HuaweiPayApi.getPayResultInfoFromIntent(data);
                if (payResultInfo != null) {
                    Map<String, Object> paramsa = new HashMap<>();
                    if (PayStatusCodes.PAY_STATE_SUCCESS == payResultInfo.getReturnCode()) {

                        paramsa.put("returnCode", payResultInfo.getReturnCode());
                        paramsa.put("userName", payResultInfo.getUserName());
                        paramsa.put("requestId", payResultInfo.getRequestId());
                        paramsa.put("amount", payResultInfo.getAmount());
                        paramsa.put("time", payResultInfo.getTime());

                        String conntry = payResultInfo.getCountry();
                        if (!TextUtils.isEmpty(conntry)) {
                            paramsa.put("country", conntry);
                        }

                        String currency = payResultInfo.getCurrency();
                        if (!TextUtils.isEmpty(currency)) {
                            paramsa.put("currency", currency);
                        }

                        String orderId = payResultInfo.getOrderID();
                        if (!TextUtils.isEmpty(orderId)) {
                            paramsa.put("orderID", orderId);
                        }

                        String errMsg = payResultInfo.getErrMsg();
                        if (!TextUtils.isEmpty(errMsg)) {
                            paramsa.put("errMsg", errMsg);
                        }

                        String noSigna = Utils.getNoSign(paramsa);
                        boolean success = Utils.doCheck(noSigna, payResultInfo.getSign());

                        if (success) {
                            Log.i(TAG, "Pay success.");
                        } else {
                            //支付成功，但是签名校验失败。开发者需要到服务器上查询该次支付的情况，然后再进行处理。
                            //The payment was successful, but the signature checksum failed. The developer needs to check the payment on the server before processing.
                            Log.i(TAG, "Payment succeeded, but signature verification failed.");
                        }

                        Log.i(TAG, "Merchant Name: " + payResultInfo.getUserName());
                        if (!TextUtils.isEmpty(orderId)) {
                            Log.i(TAG, "Order number: " + orderId);
                        }
                        Log.i(TAG, "Amount paid: " + payResultInfo.getAmount());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = payResultInfo.getTime();
                        if (time != null) {
                            try {
                                Date curDate = new Date(Long.valueOf(time));
                                String str = formatter.format(curDate);
                                Log.i(TAG, "Trading Time: " + str);
                            } catch (NumberFormatException e) {
                                Log.i(TAG, "Parsing error! time: " + time);
                            }
                        }
                        Log.i(TAG, "Merchant Order Number: " + payResultInfo.getRequestId());
                    } else if (PayStatusCodes.PAY_STATE_CANCEL == payResultInfo.getReturnCode()) {
                        //支付失败，原因是用户取消了支付，可能是用户取消登录，或者取消支付
                        //Payment failed because the user canceled the payment, the user may cancel the login, or cancel the payment
                        Log.i(TAG, "Payment failed: User canceled." + payResultInfo.getErrMsg());
                    } else {
                        //异常场景，接口调用失败
                        //Exception scenario, interface call failed
                        Log.i(TAG, "Payment failed! Error Code：" + payResultInfo.getReturnCode() + "->" + payResultInfo.getErrMsg());
                    }
                } else {
                    //异常场景，接口调用失败
                    //Exception scenario, interface call failed
                }
            } else {
                //异常场景，接口调用失败
                //Exception scenario, interface call failed
                Log.i(TAG, "Payment failed.");
            }
        }
    }

    /**
     * 创建查询订单请求参数
     * Create a query Order request parameter
     * @param requestId 请求id / Request ID
     * @return 查询订单请求参数对象 /Query Order Request Parameter Object
     */
    private OrderRequest createGetOrderDetailReq(String requestId) {
        Map<String, Object> paramcheck = new HashMap<>();
        paramcheck.put("merchantId", PayContasts.cpId);
        paramcheck.put("requestId", requestId);
        paramcheck.put("keyType", "1");
        paramcheck.put("time", String.valueOf(System.currentTimeMillis()));

        OrderRequest request = new OrderRequest();
        request.setRequestId((String)paramcheck.get("requestId"));
        request.setMerchantId((String)paramcheck.get("merchantId"));
        request.setKeyType((String)paramcheck.get("keyType"));
        request.setTime((String)paramcheck.get("time"));
        request.setSign(Utils.getSign(paramcheck));

        return request;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hwpay_buy:
			    pay();
			    break;

            case R.id.hwpay_query:
                //订单编号是调用pay接口的时候返回的orderId，示例代码中需要手动输入，开发者需要在合适时机和合适位置保存需要查询的订单编号
                //The order number is the OrderID that is returned when the pay interface is invoked, and the sample code needs to be entered manually,
                // and the developer needs to keep the order number for the query at the right time and in the right place.
                String requestId = ((EditText) view.findViewById(R.id.request_id_edit)).getText().toString();
                OrderRequest orderRequest = createGetOrderDetailReq(requestId);
                checkPayResult(orderRequest);
                break;

            default:
                break;
        }
    }
}
