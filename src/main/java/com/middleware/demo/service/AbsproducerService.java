package com.middleware.demo.service;

import com.middleware.demo.entity.Absproducer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86185
* @description 针对表【absproducer】的数据库操作Service
* @createDate 2023-06-12 23:48:02
*/
public interface AbsproducerService extends IService<Absproducer> {
    public Absproducer selectAbsproducerByConsumerName(String consumerName);


}
