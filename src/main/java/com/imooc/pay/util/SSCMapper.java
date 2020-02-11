package com.imooc.pay.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * created by Leo徐忠春
 * created Time 2020/2/3-21:43
 * email 1437665365@qq.com
 */
public interface SSCMapper<T> extends Mapper<T>, MySqlMapper<T> {
    //TODO
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
}
