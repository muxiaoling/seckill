package com.tang.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.seckill.pojo.Goods;
import com.tang.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
