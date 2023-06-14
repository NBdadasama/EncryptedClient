package com.middleware.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.middleware.demo.entity.Absproducer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 86185
* @description 针对表【absproducer】的数据库操作Mapper
* @createDate 2023-06-12 23:48:02
* @Entity com.middleware.demo.entity.Absproducer
*/
@Mapper
public interface AbsproducerMapper extends BaseMapper<Absproducer> {
    Absproducer selectOneByUsername(@Param("username") String username);

    List<Absproducer> selectAllByUsername(@Param("username") String username);

    Absproducer selectOneByConsumername(@Param("consumername") String consumername);
}




