package cn.itcast.core.service.seckill;

import cn.itcast.core.pojo.seckill.SeckillOrder;

public interface SeckillOrderService {

    void submitOrder(Long id,String userId);

    SeckillOrder searchOrderFromRedis(String userId);

    void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);

    void deleteOrderFromRedis(String userId,Long orderId);
}
