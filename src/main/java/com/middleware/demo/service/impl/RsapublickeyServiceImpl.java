package com.middleware.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.middleware.demo.entity.Rsapublickey;
import com.middleware.demo.mapper.RsapublickeyMapper;
import com.middleware.demo.service.RsapublickeyService;
import org.springframework.stereotype.Service;

/**
* @author 86185
* @description 针对表【rsapublickey】的数据库操作Service实现
* @createDate 2023-06-13 09:35:38
*/
@Service
public class RsapublickeyServiceImpl extends ServiceImpl<RsapublickeyMapper, Rsapublickey>
    implements RsapublickeyService{

    @Override
    public Rsapublickey selectRsapublickeyByUsername(String username) {
        QueryWrapper<Rsapublickey> wrapper = new QueryWrapper<Rsapublickey>();
        wrapper.eq("username",username);
        return this.getOne(wrapper);
    }
}




