package com.tang.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tang.seckill.config.AccessLimit;
import com.tang.seckill.lock.DistributedLocker;
import com.tang.seckill.pojo.Order;
import com.tang.seckill.pojo.SeckillMessage;
import com.tang.seckill.pojo.SeckillOrder;
import com.tang.seckill.pojo.User;
import com.tang.seckill.rabbitmq.MQSender;
import com.tang.seckill.service.IGoodsService;
import com.tang.seckill.service.IOrderService;
import com.tang.seckill.service.ISeckillOrderService;
import com.tang.seckill.utils.JsonUtil;
import com.tang.seckill.vo.GoodsVo;
import com.tang.seckill.vo.RespBean;
import com.tang.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/seckill")
@Slf4j
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> script;
    @Autowired
    private DistributedLocker distributedLocker;

    //分布式锁key
    private static final String DISTRIBUTED_LOCK_KEY = "LOCK_KEY";
    //库存为空，设置内存标记
    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /**
     * 秒杀
     * windows优化前qps 785.9
     * 缓存：1356
     * 优化: 2454
     */
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId) {
        if (user == null) return RespBean.error(RespBeanEnum.SESSION_ERROR);
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //redis判断是否重复抢购
        String seckillOrderJson =
                (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return RespBean.error(RespBeanEnum.REPEAT_BUYING_ERROR);
        }
        //内存标记商品库存不足，直接返回
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存
//        Long stock = valueOperations.decrement("seckillGoods:"+goodsId);
        RLock rLock = distributedLocker.lock(DISTRIBUTED_LOCK_KEY);
        log.info("加锁成功，开始执行减库存操作");
        //Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        try {
            int stock = (Integer) redisTemplate.opsForValue().get("seckillGoods:" + goodsId);
            if (stock <= 0) {
                EmptyStockMap.put(goodsId, true);
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
            redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId, 1L);
        }catch (Exception e) {
            e.printStackTrace();
            return RespBean.error(RespBeanEnum.DECREMENT_STOCK_ERROR);
        } finally {
            distributedLocker.unlock(rLock);
            log.info("解锁成功");
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
    }

    //-1失败，0排队
    @RequestMapping(value="/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    //获取秒杀地址

    @AccessLimit(second=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, HttpServletRequest request) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        String str = orderService.creatPath(user, goodsId);
        return RespBean.success(str);
    }


    //初始化，把商品库存数量加载到Redis里面
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
