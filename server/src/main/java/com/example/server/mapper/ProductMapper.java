package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 接口名：ProductMapper
 * 作用：直接与数据库对话，继承 BaseMapper 后自动获得 CRUD 能力
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 这里暂时不需要写任何代码，BaseMapper 已经帮你写好了常用的增删改查
}