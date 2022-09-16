package com.tang.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tang.seckill.pojo.SeckillOrder;

import java.sql.Wrapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
    default SeckillOrder selectOrderByGoodsId (Long userId, Long goodsId) {
        return this.selectOne(Wrappers.<SeckillOrder>lambdaQuery()
                .eq(SeckillOrder::getGoodsId, goodsId)
                .eq(SeckillOrder::getUserId, userId));
    }

}
