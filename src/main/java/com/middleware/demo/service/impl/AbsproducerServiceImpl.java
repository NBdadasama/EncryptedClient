package com.middleware.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.middleware.demo.entity.Absproducer;
import com.middleware.demo.service.AbsproducerService;
import com.middleware.demo.mapper.AbsproducerMapper;
import org.springframework.stereotype.Service;

/**
* @author 86185
* @description 针对表【absproducer】的数据库操作Service实现
* @createDate 2023-06-12 23:48:02
*/
@Service
public class AbsproducerServiceImpl extends ServiceImpl<AbsproducerMapper, Absproducer>
    implements AbsproducerService{

    @Override
    public Absproducer selectAbsproducerByConsumerName(String consumerName) {
        QueryWrapper<Absproducer> wrapper = new QueryWrapper<Absproducer>();
        wrapper.eq("consumername",consumerName);
        return this.getOne(wrapper);
    }
}




