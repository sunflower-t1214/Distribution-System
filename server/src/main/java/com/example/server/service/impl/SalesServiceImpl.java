package com.example.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.entity.Sales;
import com.example.server.mapper.SalesMapper;
import com.example.server.service.SalesService;
import org.springframework.stereotype.Service;

@Service
public class SalesServiceImpl extends ServiceImpl<SalesMapper, Sales> implements SalesService {
}