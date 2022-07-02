package com.tang.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.seckill.pojo.Order;
import com.tang.seckill.pojo.User;
import com.tang.seckill.vo.GoodsVo;
import com.tang.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * 乐字节：专注线上IT培训
 * 答疑老师微信：lezijie
 *
 * @author zhoubin
 *
 */
public interface IOrderService extends IService<Order> {
    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId); //订单详情

    String creatPath(User user, Long goodsId); //获取秒杀地址

    boolean checkPath(User user, Long goodsId, String path); //校验秒杀地址
}