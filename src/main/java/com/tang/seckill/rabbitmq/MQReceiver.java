package com.tang.seckill.rabbitmq;

import com.tang.seckill.pojo.SeckillMessage;
import com.tang.seckill.pojo.SeckillOrder;
import com.tang.seckill.pojo.User;
import com.tang.seckill.service.IGoodsService;
import com.tang.seckill.service.IOrderService;
import com.tang.seckill.utils.JsonUtil;
import com.tang.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;
    //下单操作
    @RabbitListener(queues = "seckillQueue")
    public void receiver(String message) {
        log.info("接收到的消息：" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodId);
        if (goodsVo.getStockCount() < 1) {
            return;
        }
        //是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodId);
        if (seckillOrder != null) {
            return;
        }
        //下单操作
        orderService.seckill(user, goodsVo);
    }



//
//    @RabbitListener(queues = "queue_fanout001")
//    public void receiver01(Object msg) {
//        log.info("QUEUE001接收消息" + msg);
//    }
//    @RabbitListener(queues = "queue_fanout002")
//    public void receiver02(Object msg) {
//        log.info("QUEUE002接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receiver03(Object msg) {
//        log.info("QUEUE01接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receiver04(Object msg) {
//        log.info("QUEUE02接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receiver05(Object msg) {
//        log.info("QUEUE_01接收消息" + msg);
//    }
//    @RabbitListener(queues = "queue_topic02")
//    public void receiver06(Object msg) {
//        log.info("QUEUE_02接收消息" + msg);
//    }
}
