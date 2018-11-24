package cn.itcast.core.service.seckill;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> findSeckillGoodsList() {

        List<SeckillGoods> seckillGoodList = redisTemplate.boundHashOps("seckillGoods").values();

        if (seckillGoodList.size() == 0 || seckillGoodList == null) {
            System.out.println("从数据库中取出记录");
            SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
            SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();
            criteria.andNumGreaterThan(0);
            criteria.andStatusEqualTo("1");
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            criteria.andEndTimeGreaterThan(new Date());
            seckillGoodList = seckillGoodsDao.selectByExample(seckillGoodsQuery);
            //存入Redis当中
            for (SeckillGoods seckillGoods : seckillGoodList) {
                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            }
            return seckillGoodList;
        }else{
            System.out.println("从缓存中取出记录");
            return seckillGoodList;
        }
    }


    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        //从Redis中获取
        return (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
    }
}
