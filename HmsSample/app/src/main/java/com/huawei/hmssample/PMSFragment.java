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

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.pay.HwPayConstant;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.entity.pay.ProductDetail;
import com.huawei.hms.support.api.entity.pay.ProductDetailRequest;
import com.huawei.hms.support.api.entity.pay.ProductFailObject;
import com.huawei.hms.support.api.entity.pay.ProductPayRequest;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.PayResult;
import com.huawei.hms.support.api.pay.ProductDetailResult;
import com.huawei.hms.support.api.pay.ProductPayResultInfo;
import com.huawei.logger.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {类描述}
 *
 * @author #user
 * @version #time
 */
public class PMSFragment extends Fragment implements View.OnClickListener, OnFragmentResultListener{
    public static final String TAG = "PMSFragment";

    private HuaweiApiClient client;

    private View view;

    private final int REQ_CODE_PMSPAY = 3002;

    //支付相关信息map
    //Payment related Information map
    private Map<String, Object> params;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pms, container, false);
        view.findViewById(R.id.hwpay_pms).setOnClickListener(this);
        view.findViewById(R.id.hwpay_pms_pay).setOnClickListener(this);

        if (getActivity() instanceof ISetHuaweiClient) {
            client = ((ISetHuaweiClient)getActivity()).getHuaweiClient();
        }

        return view;
    }

    /**
     * The PMS obtains the commodity information interface
     * 为商品提供全球定价体系。根据传入的商品ID信息，返回商品的定价信息。包含特定的币种以及对应的数值。
     * PMS Payment Interface
     * Provide a global pricing system for commodities. Returns the pricing information for the product,
     * based on the incoming Product ID information. Contains a specific currency and the corresponding numeric value.
     */
    private void getProductDetails() {
        if(!client.isConnected()) {
            Log.i(TAG, "Get Product detail failed, Reason: Huaweiapiclient not connected.");
            client.connect(getActivity());
            return;
        }

        PendingResult<ProductDetailResult> payResult = HuaweiPay.HuaweiPayApi.getProductDetails(client, createDetailReq());
        payResult.setResultCallback(new PayDetailResultCallback());
    }


    /**
     * 获取商品详情回调
     * 从该接口中获取的商品的国家 币种 和价格，用于展示给用户
     * Get Product Details callback
     * The national currency and price of the commodity obtained from this interface for display to the user
     */
    private class PayDetailResultCallback implements ResultCallback<ProductDetailResult> {
        @Override
        public void onResult(ProductDetailResult result) {
            if(result == null) {
                //异常场景，接口调用失败
                //Exception scenario, interface call failed
                return;
            }
            //请求流水号。原样返回请求中的值
            //Request flow number. Returns the value in the request as is
            String requestId = result.getRequestId();
            if(TextUtils.isEmpty(requestId)) {
                return;
            }

            List<ProductDetail> productList = result.getProductList();
            if(!productList.isEmpty()) {
                for(ProductDetail detail : productList) {
                    Log.i(TAG, "Product No:"+ detail.getProductNo());
                    Log.i(TAG, "Micro Unit Price:"+ detail.getMicrosPrice());
                    Log.i(TAG, "Product Display Price:"+ detail.getPrice());
                    Log.i(TAG, "Product Currency:"+ detail.getCurrency());
                    Log.i(TAG, "Product Country Code:"+ detail.getCountry());
                    Log.i(TAG, "Product Name:"+ detail.getProductName());
                    Log.i(TAG, "Product Description:"+ detail.getProductDesc());
                }
            }
            List<ProductFailObject> failList = result.getFailList();
        }
    }

    /**
     * 创建PMS请求实例
     * 需要传入的参数有
     * merchantId 必选 商户ID。在开发者联盟上获取的支付 ID
     * applicationID 必选 应用ID。在开发者联盟上获取的APP ID.
     * requestId 必选 请求流水号。其值由商户定义生成，用于标识一次请求，每次请求需唯一，不可重复。注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , |
     * productNos 必选 商品ID。需要获取价格信息的商品No列表，多个ID以竖线分割，一次查询最多支持20个No。
     *              注意：所查询的productNo必须属于对应的packageName,且在应用内唯一
     *
     * Create a PMS Request instance
     * The parameters that need to be passed in are
     * merchantId :Required, Merchant ID. Payment IDs obtained on the developer Consortium
     * applicationID : Required, Application ID. The app ID obtained on the developer consortium.
     * requestId : Required,  request flow number. The value is generated by the merchant definition
     *              and is used to identify a request that is unique and not repeatable on each request.
     *              Note: This field cannot contain special characters, including # "&/? $ ^ *:) \ < >, |
     * productNos : Required, item ID. A list of items that need to get price information, multiple IDs separated by a vertical bar, and a query that supports up to 20 No.
     *              Note: The productno to be queried must belong to the corresponding PackageName and only in the application
     * @return {@link com.huawei.hms.support.api.entity.pay.ProductDetailRequest}
     */
    private ProductDetailRequest createDetailReq() {
        ProductDetailRequest detailReq = new ProductDetailRequest();
        // 商户ID
        // Merchant ID
        detailReq.merchantId = PayContasts.cpId;
        // 应用ID
        // Application ID
        detailReq.applicationID = PayContasts.appId;
        // 请求流水号
        // request flow number.
        DateFormat format = new java.text.SimpleDateFormat("yyyyMMddhhmmssSSS");
        String requestId = format.format(new Date());
        int random=(int) ((Math.random()+1)*100000);
        detailReq.requestId = requestId+random;

        // 商品ID。
        // item ID
        detailReq.productNos = ((EditText)view.findViewById(R.id.product_no_edit)).getText().toString();

        return detailReq;
    }




    /**
     * PMS支付接口
     * 该接口区别于pay接口的地方在于，不用传入商品的价格，货币，国家，取而传入商品的PMS编码。
     *
     * PMS Payment Interface
     * The interface differs from the pay interface in that it does not use the price of the incoming commodity,
     * the currency, the country, and the PMS code of the incoming commodity.
     */
    private void pmsPay() {
        if(!client.isConnected()) {
            Log.i(TAG, "Payment failed because: Huaweiapiclient not connected.");
            client.connect(getActivity());
            return;
        }

        PendingResult<PayResult> payResult = HuaweiPay.HuaweiPayApi.productPay(client, createPmsPayReq());
        payResult.setResultCallback(new PmsPayResultCallback(REQ_CODE_PMSPAY));
    }


    /**
     * 支付接口调用的回调处理
     * 只有当处理结果中的返回码为 PayStatusCodes.PAY_STATE_SUCCESS的时候，开发者需要继续调用支付
     * 否则就需要处理支付失败结果
     *
     * Callback processing for payment interface calls
     * Only when the return code in the processing result is paystatuscodes.pay_state_success, the developer needs to continue calling the payment
     * Otherwise, you will need to deal with payment failure results
     */
    private class PmsPayResultCallback implements ResultCallback<PayResult> {
        private int requestCode;
        public PmsPayResultCallback(int requestCode) {
            this.requestCode = requestCode;
        }
        @Override
        public void onResult(PayResult result) {
            Status status = result.getStatus();
            if (PayStatusCodes.PAY_STATE_SUCCESS == status.getStatusCode()) {
                try {
                    status.startResolutionForResult(getActivity(), requestCode);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Start payment failed"+e.getMessage());
                }
            } else {
                Log.i(TAG, "Payment failed, error code :" + status.getStatusCode());
            }
        }
    }



    /**
     * 生成PayReq对象，用来在进行支付请求的时候携带支付相关信息
     * payReq订单参数需要商户使用在华为开发者联盟申请的RSA私钥进行签名，强烈建议将签名操作在商户服务端处理，避免私钥泄露
     *
     * Generate Payreq object to carry the payment information when making the payment request
     * Payreq order parameters require the merchant to sign with the RSA private key requested by the Huawei Developer Consortium,
     * it is highly recommended that the signature operation be handled at the merchant service side to avoid the private key disclosure
     */
    private ProductPayRequest createPmsPayReq() {
        getPmsSignInfo();
        ProductPayRequest payReq = new ProductPayRequest();

        // 商户ID
        // Merchant ID
        payReq.merchantId = (String)params.get(HwPayConstant.KEY_MERCHANTID);
        // 应用ID
        // Application ID
        payReq.applicationID = (String) params.get(HwPayConstant.KEY_APPLICATIONID);
        // 商品的PMS编码
        // PMS coding of commodities
        payReq.productNo = (String) params.get(HwPayConstant.KEY_PRODUCT_NO);
        // 支付订单号
        // Payment order Number
        payReq.requestId = (String) params.get(HwPayConstant.KEY_REQUESTID);
        // 渠道号
        // Channel number
        payReq.sdkChannel = (Integer) params.get(HwPayConstant.KEY_SDKCHANNEL);
        // 回调接口版本号
        // Callback Interface Version number
        payReq.urlVer = (String) params.get(HwPayConstant.KEY_URLVER);

        //以上信息按照一定规则进行签名,建议开发者在服务器端储存签名私钥，并在服务器端进行签名操作。
        //The above information is signed according to certain rules,
        // and it is recommended that the developer store the signature private key on the server side and sign the operation on the server side.
        payReq.sign = Utils.getSign(params);

        // 商户名称，必填，不参与签名。会显示在支付结果页面
        // Merchant name, must be filled out, do not participate in the signature. will appear on the Pay results page
        payReq.merchantName = "TestMerchantName";
        //分类，必填，不参与签名。该字段会影响风控策略
        // X4：主题,X5：应用商店,	X6：游戏,X7：天际通,X8：云空间,X9：电子书,X10：华为学习,X11：音乐,X12 视频,
        // X31 话费充值,X32 机票/酒店,X33 电影票,X34 团购,X35 手机预购,X36 公共缴费,X39 流量充值
        //Categories, required, do not participate in the signature. This field affects wind control strategies,
        // X4: Themes, X5: App Store, X6: Games, X7: Sky Pass, X8: Cloud Space, X9: ebook, X10: Huawei Learning, X11: Music, X12 video,
        //X31, X32 air tickets/hotels, X33 movie tickets, X34 Group purchase, X35 mobile phone advance, X36 public fees, X39 flow Recharge
        payReq.serviceCatalog = "X6";
        //商户保留信息，选填不参与签名，支付成功后会华为支付平台会原样 回调开发者服务端
        //The merchant retains the information, chooses not to participate in the signature, and after the payment succeeds,
        //the Huawei payment platform will be callback to the developer server.
        payReq.extReserved = "TestExtReserved";

        return payReq;
    }

    /**
     * 生成支付信息map 包含一下信息，该信息需要参与签名
     * HwPayConstant.KEY_MERCHANTID  必选参数 商户id，开发者联盟网站生成的商户ID/支付ID
     * HwPayConstant.KEY_APPLICATIONID 必选参数 应用的appid，开发者联盟网站生成
     * HwPayConstant.KEY_PRODUCTNO PMS支付接口必选参数 所购买商品的PMS编码，用于从PMS系统获取商品信息和价格,此处填写查询到的产品编码
     * HwPayConstant.KEY_REQUESTID  必选参数 请求订单号。其值由商户定义生成，用于标识一次支付请求，每次请求需唯一，不可重复。
     * 					支付平台在服务器回调接口中会原样返回requestId的值。注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , .以及中文字符
     * HwPayConstant.KEY_URL 可选参数 支付结果回调URL. 华为服务器收到后检查该应用有无在开发者联盟配置回调URL，如果配置了则使用应用配置的URL，否则使用此url
     *   					作为该次支付的回调URL,建议直接 以配置在 华为开发者联盟的回调URL为准
     * HwPayConstant.KEY_URLVER 可选参数  回调接口版本号。如果传值则必须传2， 额外回调信息，具体参考接口文档
     * HwPayConstant.KEY_SDKCHANNEL 必选参数 渠道信息。 取值如下：0 代表自有应用，无渠道 1 代表应用市场渠道 2 代表预装渠道 3 代表游戏中心渠道
     *
     * Generate Payment information map contains information that requires participation in signing
     * HwPayConstant.KEY_MERCHANTID  Required Parameters ,Merchant ID, developer affiliate site generated merchant ID payment ID
     * HwPayConstant.KEY_APPLICATIONID Required Parameters, AppID for application ,Developer Alliance site Generation
     * HwPayConstant.KEY_PRODUCTNO  Required Parameters, The PMS code for the goods purchased, used to obtain commodity information and prices from the PMS system,
     *                              where the product code for the query is filled in
     * HwPayConstant.KEY_REQUESTID  Required Parameters,Request order number.     The value is generated by the merchant definition and is used to identify a payment request
     * 					            that is unique and not repeatable on each request. * The payment platform returns the RequestID value as is in the server callback interface.
     * 					            Note: This field cannot contain special characters, including # "&/? $ ^ *:) \ < >,. and Chinese characters
     * HwPayConstant.KEY_URL Optional parameters,The payment result callback URL. Huawei Server to check if the application has a callback URL in the developer Federation configuration,
     *   				    if configured then use the URL of the application configuration.Otherwise, use this URL as the callback URL for the payment,
     *   				    which is recommended directly to the callback URL configured in the Huawei Developer Federation
     * HwPayConstant.KEY_URLVER  Optional parameters,The callback interface version number. If the value must pass 2, additional callback information, specific reference interface document
     * HwPayConstant.KEY_SDKCHANNEL Required Parameters,Channel information. Values are as follows: 0 stands for own application,
     *                              Channel 1 represents the application market Channel 2 represents the pre-installed channel 3 represents the game center channel
     */
    private void getPmsSignInfo() {
        if(params != null) {
            params.clear();
        } else {
            params = new HashMap<String, Object>();
        }
        params.put(HwPayConstant.KEY_MERCHANTID, PayContasts.cpId);
        params.put(HwPayConstant.KEY_APPLICATIONID, PayContasts.appId);
        params.put(HwPayConstant.KEY_PRODUCT_NO, ((EditText)view.findViewById(R.id.product_no_edit)).getText().toString());

        DateFormat format = new java.text.SimpleDateFormat("yyyyMMddhhmmssSSS");
        String requestId = format.format(new Date());
        int random=(int) ((Math.random()+1)*100000);
        requestId = requestId+random;
        params.put(HwPayConstant.KEY_REQUESTID, requestId);
        params.put(HwPayConstant.KEY_URLVER, "2");
        params.put(HwPayConstant.KEY_SDKCHANNEL, 1);
    }

    @Override
    public void OnFragmentResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_PMSPAY){
            //当返回值是-1的时候表明用户支付调用调用成功
            //When the return value is-1 indicates that the user pays the call successfully
            if(resultCode == Activity.RESULT_OK) {
                ProductPayResultInfo payResultInfo = HuaweiPay.HuaweiPayApi.getProductPayResultFromIntent(data);
                if(payResultInfo != null) {

                    Log.i(TAG, "Payment Results ：" + payResultInfo.toString());

                    Map<String, Object> paramsa = new HashMap<String, Object>();
                    if (PayStatusCodes.PAY_STATE_SUCCESS == payResultInfo.getReturnCode()) {

                        paramsa.put("returnCode", payResultInfo.getReturnCode());
                        paramsa.put("orderID", payResultInfo.getOrderID());
                        paramsa.put("microsAmount", payResultInfo.getMicrosAmount());
                        paramsa.put("errMsg", payResultInfo.getErrMsg());
                        paramsa.put("time", payResultInfo.getTime());
                        paramsa.put("requestId", payResultInfo.getRequestId());
                        paramsa.put("productNo", payResultInfo.getProductNo());
                        paramsa.put("currency", payResultInfo.getCurrency());
                        paramsa.put("country", payResultInfo.getCountry());
                        paramsa.put("merchantId", payResultInfo.getMerchantId());

                        String noSigna = Utils.getNoSign(paramsa);
                        boolean success = Utils.doCheck(noSigna, payResultInfo.getSign());

                        if (success) {
                            Log.i(TAG, "Pay success.");
                        } else {
                            //支付成功，但是签名校验失败。开发者需要到服务器上查询该次支付的情况，然后再进行处理。
                            //The payment was successful, but the signature checksum failed. The developer needs to check the payment on the server before processing.
                            Log.i(TAG, "Payment succeeded, but signature verification failed.");
                        }

                        Log.i(TAG, "Order number: " + payResultInfo.getOrderID());
                        Log.i(TAG, "Amount paid: " + payResultInfo.getMicrosAmount());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = payResultInfo.getTime();
                        if(time != null) {
                            try {
                                Date curDate = new Date(Long.valueOf(time));
                                String str = formatter.format(curDate);
                                Log.i(TAG, "Trading Time: " + str);
                            } catch (NumberFormatException e) {
                                Log.i(TAG, "Parse error. time: " + time);
                            }
                        }
                        Log.i(TAG, "Merchant Order Number: " + payResultInfo.getRequestId());
                    } else {
                        Log.i(TAG, "Payment failed：" + payResultInfo.getErrMsg());
                    }
                } else {
                }
            } else {
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hwpay_pms:
                getProductDetails();
                break;
            case R.id.hwpay_pms_pay:
                pmsPay();
                break;
            default:
                break;
        }

    }
}
