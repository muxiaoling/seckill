package com.tang.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.seckill.exception.GlobalException;
import com.tang.seckill.mapper.OrderMapper;
import com.tang.seckill.pojo.Order;
import com.tang.seckill.pojo.SeckillGoods;
import com.tang.seckill.pojo.SeckillOrder;
import com.tang.seckill.pojo.User;
import com.tang.seckill.service.IGoodsService;
import com.tang.seckill.service.IOrderService;
import com.tang.seckill.service.ISeckillGoodsService;
import com.tang.seckill.service.ISeckillOrderService;
import com.tang.seckill.utils.JsonUtil;
import com.tang.seckill.utils.MD5Utils;
import com.tang.seckill.vo.GoodsVo;
import com.tang.seckill.vo.OrderDetailVo;
import com.tang.seckill.vo.RespBeanEnum;
import com.tang.seckill.vo.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 下单操作  上游是消息队列消费
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id",
                goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1")
                .eq("goods_id", goods.getId()).gt("stock_count", 0));
        if (seckillGoods.getStockCount() < 1) {
            redisTemplate.opsForValue().set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), JsonUtil.object2JsonStr(seckillOrder));
        return order;
    }

    /**
     * 获取订单详情
     * @param orderId 订单id号
     * @return 订单详情
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }

    //获取秒杀地址
    @Override
    public String creatPath(User user, Long goodsId) {
        String str = MD5Utils.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" +
                goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    //校验秒杀地址
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || StringUtils.isEmpty(path)) return false;

        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() +
                ":" + goodsId);
        return path.equals(redisPath);
    }
}

