package com.imooc.pay.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * created by Leo徐忠春
 * created Time 2020/2/4-22:19
 * email 1437665365@qq.com
 */
@Component
public class BestPayConfig {


    @Autowired
    private WxAccountConfig wx;
    @Autowired
    private AlipayAccountConfig alipay;
    @Bean
    public BestPayService bestPayService(AliPayConfig aliPayConfig){
        //微信native支付
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wx.getAppId());//公众号ID
        wxPayConfig.setMchId(wx.getMchId());//商户号ID
        wxPayConfig.setMchKey(wx.getMchKey());//商户秘钥
        wxPayConfig.setNotifyUrl(wx.getNotifyUrl());//支付完成后的异步通知地址
        wxPayConfig.setReturnUrl(wx.getReturnUrl());//支付完成后的同步返回地址

        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        return bestPayService;
    }

    @Bean
    public AliPayConfig aliPayConfig(){
        //阿里网页支付
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppId(alipay.getAppId());//公众号ID
        aliPayConfig.setPrivateKey(alipay.getPrivateKey());//商户私钥
        aliPayConfig.setAliPayPublicKey(alipay.getPayPublicKey());//支付宝公钥
        aliPayConfig.setNotifyUrl(alipay.getNotifyUrl());//支付完成后的异步通知地址
        aliPayConfig.setReturnUrl(alipay.getReturnUrl());//支付完成后的同步返回地址
        return aliPayConfig;

    }
}
