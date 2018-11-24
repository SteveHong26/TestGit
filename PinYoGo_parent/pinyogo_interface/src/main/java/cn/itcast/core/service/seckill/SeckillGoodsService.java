package cn.itcast.core.service.seckill;

import cn.itcast.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    List<SeckillGoods> findSeckillGoodsList();

    SeckillGoods findOneFromRedis(Long id);
}
