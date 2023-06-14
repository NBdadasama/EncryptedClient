package com.middleware.demo.service;

import com.middleware.demo.entity.Rsapublickey;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86185
* @description 针对表【rsapublickey】的数据库操作Service
* @createDate 2023-06-13 09:35:38
*/
public interface RsapublickeyService extends IService<Rsapublickey> {
    public Rsapublickey selectRsapublickeyByUsername(String username);
}
