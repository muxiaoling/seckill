package com.tang.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.seckill.mapper.OrderMapper;
import com.tang.seckill.mapper.SeckillOrderMapper;
import com.tang.seckill.pojo.Order;
import com.tang.seckill.pojo.SeckillOrder;
import com.tang.seckill.pojo.User;
import com.tang.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOrderByGoodsId(user.getId(), goodsId);
        if (null != seckillOrder) {
            return seckillOrder.getOrderId();
        } else if(redisTemplate.hasKey("isStockEmpty:" + goodsId)) {
            return -1L;
        } else {
            return 0L;
        }
    }
}
