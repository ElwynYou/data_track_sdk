package com.elwyn.ae;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Package com.elwyn.ae
 * @Description:分析引擎sdk 服务器端数据收集
 * @Author elwyn
 * @Date 2017/8/14 21:19
 * @Email elonyong@163.com
 */
public class AnalyticesEngineSDK {
    private static final Logger LOGGER = Logger.getGlobal();
    public static final String accessUrl = "http://hadoop-senior.ibeifeng.com/BfImg.gif";
    public static final String platformName = "java_server";
    public static final String sdkname = "jdk";
    public static final String version = "1";

    /**
     * 触发订单支付成功事件,发送事件数据到服务器
     *
     * @param orderId  订单支付id
     * @param memberId 订单支付会员id
     * @return 如果发送数据成功(加入到发送队列中), 那么返回true, 否则返回false(参数异常&添加到发送队列失败)
     */
    public static boolean onChangeSuccess(String orderId, String memberId) {
        try {


            if (isEmpty(orderId) || isEmpty(memberId)) {
                LOGGER.log(Level.WARNING, "订单和会员id不能为空");
            }

            Map<String, String> data = new HashMap<>();
            data.put("u_mid", memberId);
            data.put("oid", orderId);
            data.put("c_time", String.valueOf(System.currentTimeMillis()));
            data.put("ver", version);
            data.put("en", "e_cs");
            data.put("pl", platformName);
            data.put("sdk", sdkname);
            String url = buildUrl(data);
            SendDataMonitor.addSendUrl(url);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "发送数据异常", e);
        }
        //创建url
        return false;
    }

    /**
     * 触发订单退款事件,发送退款数据到服务器
     *
     * @param orderId  退款订单Id
     * @param memberId 退款会员id
     * @return 如果发送数据成功返回true, 否则返回false
     */
    public static boolean onChargeRefund(String orderId, String memberId) {
        try {


            if (isEmpty(orderId) || isEmpty(memberId)) {
                LOGGER.log(Level.WARNING, "订单和会员id不能为空");
            }

            Map<String, String> data = new HashMap<>();
            data.put("u_mid", memberId);
            data.put("oid", orderId);
            data.put("c_time", String.valueOf(System.currentTimeMillis()));
            data.put("ver", version);
            data.put("en", "e_c");
            data.put("pl", platformName);
            data.put("sdk", sdkname);
            String url = buildUrl(data);
            SendDataMonitor.addSendUrl(url);

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "发送数据异常", e);

        }
        return false;
    }

    /**
     * 根据传入的参数构建url
     *
     * @param data
     * @return
     */
    private static String buildUrl(Map<String, String> data) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(accessUrl).append("?");
        for (Map.Entry<String, String> stringStringEntry : data.entrySet()) {
            if (isNotEmpty(stringStringEntry.getKey()) && isNotEmpty(stringStringEntry.getValue())) {
                stringBuilder.append(stringStringEntry.getKey().trim()).append("=").append(URLEncoder.encode(stringStringEntry.getValue().trim(), "utf-8")).append("&");
            }
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }
}
