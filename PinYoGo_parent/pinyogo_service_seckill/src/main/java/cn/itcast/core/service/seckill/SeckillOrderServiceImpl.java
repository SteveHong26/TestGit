package cn.itcast.core.service.seckill;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.utils.IdWorker.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Override
    @Transactional
    public void submitOrder(Long seckillId,String userId) {
        //查询该用户是否已经购买了其它秒杀商品,如果已经购买抛出异常
        if (redisTemplate.boundHashOps("seckillOrder").get(userId) != null) {
            throw new RuntimeException("请完成上次的订单");
        }
        //1 根据商品ID查询出商品对象
        SeckillGoods seckillGoods =
                (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if (seckillGoods == null || seckillGoods.getNum() <= 0) {
            throw new RuntimeException("商品已经被秒光");
        }
        //2 扣减库存
        seckillGoods.setNum(seckillGoods.getNum() - 1);
        seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);

        //2.2 如果库存为空,同步到数据库
        if (seckillGoods.getNum() == 0) {
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
            seckillGoodsDao.updateByPrimaryKey(seckillGoods);
        }

        //3. 添加订单到缓存
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setUserId(userId);
        seckillOrder.setStatus("0");
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
    }


    @Transactional
    public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId) {

        SeckillOrder seckillOrder = searchOrderFromRedis(userId);
        if (seckillOrder == null) {
            throw new RuntimeException("该订单不存在");
        }
        if (seckillOrder.getId().longValue() != orderId.longValue()) {
            throw new RuntimeException("订单号不相符");
        }
        //修改订单的属性,并将数据插入到数据库
        seckillOrder.setPayTime(new Date());
        seckillOrder.setStatus("1");
        seckillOrder.setTransactionId(transactionId);
        seckillOrderDao.insert(seckillOrder);
        //清除redis中的订单
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }

    @Override
    public SeckillOrder searchOrderFromRedis(String userId) {

        return (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        // 1.从缓存中提取订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

        // 2. 恢复库存,如果没有秒光
        if (seckillOrder != null && seckillOrder.getId().longValue() == orderId.longValue()) {
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
            if (seckillGoods != null) {//如果商品不为空
                // 2.1 修改缓存中的库存
                seckillGoods.setNum(seckillGoods.getNum() + 1);
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                // 2.2 将缓存装回
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);

            }else{// 2.3 如果为空则直接修改数据库
                // 2.4 获取根据缓存中获取的order对象,获取它的商品ID并查询数据库中的对应商品
                seckillGoods = seckillGoodsDao.selectByPrimaryKey(seckillOrder.getSeckillId());
                // 2.5 修改库存
                seckillGoods.setNum(seckillGoods.getNum() + 1);
                // 2.6 修改预发货库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                // 2.7 更新到数据库
                seckillGoodsDao.updateByPrimaryKey(seckillGoods);
                // 2.8 删除订单
                seckillOrderDao.deleteByPrimaryKey(orderId);
            }
            // 3. 删除库存中的订单
            redisTemplate.boundHashOps("seckillOrder").delete(userId);
        }
    }
}
