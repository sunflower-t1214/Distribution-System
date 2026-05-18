package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.entity.CommissionLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommissionLogMapper extends BaseMapper<CommissionLog> {
}
