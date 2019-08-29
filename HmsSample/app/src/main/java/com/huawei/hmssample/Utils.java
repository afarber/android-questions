package com.huawei.hmssample;

import android.util.Base64;

import com.huawei.logger.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {类描述}
 *
 * @author #user
 * @version #time
 */
public class Utils {

    public static final String TAG = "Utils";

    //使用加密算法规则
    //cryptographic algorithm
    private static String SIGN_ALGORITHMS = "SHA256WithRSA";

    private  static String charset = "UTF-8";
    /**
     * 使用开发者联盟提供的支付公钥对支付成功结果中的签名信息进行验证
     * 如果签名验证成功，则表明支付流程正确
     * 如果签名验证不成功，那么支付已经成功，但是签名有误，开发者需要到服务器上查询支付情况
     * Use the payment public key provided by the developer consortium to verify the signature information in the payment success result
     * If the signature verification succeeds, the payment process is correct
     * If the signature verification is unsuccessful, but the payment has been successful, the developer needs to check the payment on the server
     * @param content 验签的文本 / sign text
     * @param sign 签名 / signature
     * @return 验签结果 / Verification results
     */
    static boolean doCheck(String content, String sign) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(PayConstants.publicKey, Base64.DEFAULT);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(charset));

            return signature.verify(Base64.decode(sign, Base64.DEFAULT));

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "doCheck NoSuchAlgorithmException" + e);
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "doCheck InvalidKeySpecException" + e);
        } catch (InvalidKeyException e) {
            Log.e(TAG, "doCheck InvalidKeyException" + e);
        } catch (SignatureException e) {
            Log.e(TAG, "doCheck SignatureException" + e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "doCheck UnsupportedEncodingException" + e);
        }
        return false;
    }

    /**
     * 使用开发者联盟网站分配的支付私钥对支付信息进行签名
     * 强烈建议在 商户服务端做签名处理，且私钥存储在服务端，防止信息泄露
     * 开发者通过服务器获取服务器端的签名之后，再进行支付请求
     * Signing the payment information using the payment private key assigned by the Developer affiliate Web site
     * strongly recommends that the Merchant service to do signature processing, and the private key is stored on the server side to prevent information disclosure
     * The developer makes the server-side signature and then pays the request
     * @param content 需要签名的文本 Text to be signed
     * @return 签名后的文本 Signed text
     */
    private static String rsaSign(String content) {

        if (null == content) {
            return null;
        }

        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(PayConstants.privateKey, Base64.DEFAULT));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            byte[] signed = signature.sign();
            return Base64.encodeToString(signed, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "sign NoSuchAlgorithmException");
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "sign InvalidKeySpecException");
        } catch (InvalidKeyException e) {
            Log.e(TAG, "sign InvalidKeyException");
        } catch (SignatureException e) {
            Log.e(TAG, "sign SignatureException");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "sign UnsupportedEncodingException");
        }
        return null;
    }

    /**
     * 将商户id，应用id, 商品名称，商品说明，支付金额，订单号，渠道号，回调地址版本号等信息按照key值升序排列后
     * 以key=value并以&的方式连接起来生成待签名的字符串
     * First sort the following fields in ascending order of key values,Merchant ID, application ID, product name, description of goods,
     * payment amount, order number, channel number, callback address version number,
     * Then together them with & as "Key=value" to generate a string to be signed
     * @return 返回排序后的待签名字符串/Returns the sorted string to be signed
     */
    static String getNoSign(Map<String, Object> params) {
        //对参数按照key做升序排序，对map的所有value进行处理，转化成string类型
        //拼接成key=value&key=value&....格式的字符串
        //The parameters are sorted in ascending order by key, and all the value of the map is processed to convert to string type
        //Stitching into key=value&key=value& ... format string
        StringBuffer content = new StringBuffer();
        // 按照key做排序
        // Sort by key
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        String value;
        Object object;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            object = params.get(key);
            if (object instanceof String) {
                value = (String) object;
            } else {
                value = String.valueOf(object);
            }

            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }
        }

        return content.toString();
    }

    /**
     * 将商户id，应用id, 商品名称，商品说明，支付金额，订单号，渠道号，回调地址版本号等信息按照key值升序排列后
     * 以key=value并以&的方式连接起来生成待签名的字符串，将生成的代签名字符串使用开发者联盟网站提供的应用支付私钥
     * 进行签名
     *
     * First sort the following fields in ascending order of key values,Merchant ID, application ID, product name, description of goods,
     * payment amount, order number, channel number, callback address version number,Then together them with & as "Key=value" to generate a string to be signed.
     * Sign the signature string using the private key provided by the Developer Federated Web site
     * @return 获取签名字符串/Get signature string
     */
    static String getSign(Map<String, Object> params) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        String value;
        Object object;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            object = params.get(key);
            if (object instanceof String) {
                value = (String) object;
            } else {
                value = String.valueOf(object);
            }

            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }
        }

        String signOri = content.toString();
        return rsaSign(signOri);
    }
}
