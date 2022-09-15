package com.tang.seckill.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.seckill.pojo.Goods;
import com.tang.seckill.vo.GoodsVo;

import java.util.List;

/**
 *  服务类
 */
public interface IGoodsService extends IService<Goods> {
    List<GoodsVo> findGoodsVo();
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
