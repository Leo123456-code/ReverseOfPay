package com.imooc.pay.service.serviceImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imooc.pay.dao.PayInfoMapper;
import com.imooc.pay.enums.PayPlatformEnum;
import com.imooc.pay.pojo.PayInfo;
import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * created by Leo徐忠春
 * created Time 2020/2/4-16:58
 * email 1437665365@qq.com
 */
@Slf4j
@Service
public class PayServiceImpl implements IPayService {

    @Autowired
    private BestPayService bestPayService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final String QUEUES_PAY_NOTIFY= "payNotify" ;
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 创建支付实现类
     * @param orderId  商品ID
     * @param amount   价格
     */
    @Override
    public PayResponse create(String orderId, BigDecimal amount,
                              BestPayTypeEnum bestPayTypeEnum) {

        if(bestPayTypeEnum != BestPayTypeEnum.ALIPAY_PC
        && bestPayTypeEnum != BestPayTypeEnum.WXPAY_NATIVE){
            throw new RuntimeException("暂不支持的支付类型");
        }
        //把支付订单信息写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),amount);

        payInfoMapper.insertSelective(payInfo);//订单入库

        PayRequest payRequest = new PayRequest();
        payRequest.setOrderName("7769201-徐忠春二次开发");
        payRequest.setOrderId(orderId);
        payRequest.setOrderAmount(amount.doubleValue());
        payRequest.setPayTypeEnum(bestPayTypeEnum);//支付类型

        PayResponse response = bestPayService.pay(payRequest);
        log.info("response={}",gson.toJson(response));

        return response;

    }

    @Override
    public String asyncNotify(String notifyData) {
        //1.签名效验
        PayResponse response = bestPayService.asyncNotify(notifyData);
        log.info("异步通知={}",response);
        //2.金额效验（从数据库查订单）
        //orderNO=orderId
        PayInfo payInfo = payInfoMapper.
                selectByOrderNo(Long.parseLong(response.getOrderId()));
        if(payInfo==null){
            throw new RuntimeException("通过orderNo查出的结果是null");
        }
        //判断状态
        //如果支付不成功
        if(!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){
            //如果支付的金额不相等
            if(payInfo.getPayAmount().compareTo(BigDecimal.valueOf(response.getOrderAmount()))!=0){
                //告警
                throw new RuntimeException("异步通知中的金额和数据库的不一致,orderNo="
                        +response.getOrderId());
            }
        }
        //3.修改订单支付状态
        payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
        payInfo.setPlatformNumber(response.getOutTradeNo());//支付流水号
        payInfo.setUpdateTime(new Date());//修改时间
        payInfoMapper.updateByPrimaryKeySelective(payInfo);//修改数据库的支付状态
        //TODO rabbitMQ  pay项目发送MQ消息,mall接受MQ消息
        amqpTemplate.convertAndSend(QUEUES_PAY_NOTIFY,new Gson().toJson(payInfo));
        //4.告诉微信和支付宝不要再通知
        //如果支付平台是阿里
        if(response.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY){
            return "success";
        }else if(response.getPayPlatformEnum() == BestPayPlatformEnum.WX){
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }
        throw new RuntimeException("暂不支持的支付平台...");

    }

    /**
     * 通过订单号查询支付记录
     * @param orderId
     * @return
     */
    @Override
    public PayInfo queryByOrderId(String orderId) {
        PayInfo payInfo =
                payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
        return payInfo;
    }
}
