package com.imooc.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * created by Leo徐忠春
 * created Time 2020/2/4-17:16
 * email 1437665365@qq.com
 */
@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayAccountConfig {
    //支付宝
    private String appId;
    private String privateKey;
    private String payPublicKey;
    private String notifyUrl;
    private String returnUrl;
}
