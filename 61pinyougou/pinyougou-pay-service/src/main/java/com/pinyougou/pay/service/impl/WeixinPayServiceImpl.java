package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.pay.service.impl *
 * @since 1.0
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            //1.创建map对象 组装参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", "wx8397f8696b538317");
            paramMap.put("mch_id", "1473426802");
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            paramMap.put("body", "品优购");

            paramMap.put("out_trade_no", out_trade_no);
            paramMap.put("total_fee", total_fee);//单位是分
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://a31ef7db.ngrok.io/WeChatPay/WeChatPayNotify");//通知的地址
            paramMap.put("trade_type", "NATIVE");//通知的地址

            //将MAP转成XML 自动添加签名

            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");


            //2.使用httpclient 模拟浏览器发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);//HTTPS请求
            httpClient.setXmlParam(xmlParam);//设置请求体(携带xml数据)
            httpClient.post();

            //3.使用httpclient 模拟浏览器获取响应(XML)
            String resultString = httpClient.getContent();

            System.out.println(resultString);


            //4.将XML 转成MAP
            Map<String, String> map = WXPayUtil.xmlToMap(resultString);

            Map<String, String> resultMap = new HashMap<>();

            resultMap.put("out_trade_no", out_trade_no);
            resultMap.put("total_fee", total_fee);//分
            resultMap.put("code_url", map.get("code_url"));//连接地址

            //5.返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();//空数据
        }
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {

        try {
            //1.创建map对象 组装参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", "wx8397f8696b538317");
            paramMap.put("mch_id", "1473426802");
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no", out_trade_no);

            //将MAP转成XML 自动添加签名

            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");


            //2模拟浏览器发送请求(查询订单的API )
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);//HTTPS请求
            httpClient.setXmlParam(xmlParam);//设置请求体(携带xml数据)
            httpClient.post();

            //3.使用httpclient 模拟浏览器获取响应(XML)
            String resultString = httpClient.getContent();

            System.out.println(resultString);


            //4.将XML 转成MAP
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultString);

            //5.返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();//空数据
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        try {
            //1.创建map对象 组装参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", "wx8397f8696b538317");
            paramMap.put("mch_id", "1473426802");
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no", out_trade_no);

            //将MAP转成XML 自动添加签名

            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");


            //2模拟浏览器发送请求(查询订单的API )
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);//HTTPS请求
            httpClient.setXmlParam(xmlParam);//设置请求体(携带xml数据)
            httpClient.post();

            //3.使用httpclient 模拟浏览器获取响应(XML)
            String resultString = httpClient.getContent();

            System.out.println(resultString);


            //4.将XML 转成MAP
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultString);

            //5.返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();//空数据
        }

    }
}
