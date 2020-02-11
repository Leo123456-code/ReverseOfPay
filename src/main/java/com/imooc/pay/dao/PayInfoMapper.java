package com.imooc.pay.dao;

import com.imooc.pay.pojo.PayInfo;
import com.imooc.pay.util.SSCMapper;

public interface PayInfoMapper extends SSCMapper<PayInfo> {
    //根据订单号查找
    PayInfo selectByOrderNo(Long orderNo);

}