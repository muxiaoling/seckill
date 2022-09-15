package com.tang.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.seckill.pojo.SeckillOrder;
import com.tang.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {
    //获取秒杀结果
    Long getResult(User user, Long goodsId);
}
