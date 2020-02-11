package com.imooc.pay.service.serviceImpl;

import com.imooc.pay.PayApplicationTests;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;

/**
 * created by Leo徐忠春
 * created Time 2020/2/4-17:30
 * email 1437665365@qq.com
 */
public class PayServiceImplTest extends PayApplicationTests {
    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void create() {
        //生成一个二维码
        payService.create("1258665445878",new BigDecimal(0.01),
                BestPayTypeEnum.ALIPAY_PC);
    }

    //测试MQ发送消息
    @Test
    public void sendMsg(){
        amqpTemplate.convertAndSend("payNotify",
                "这是pay队列发送的第二条消息");

    }



}