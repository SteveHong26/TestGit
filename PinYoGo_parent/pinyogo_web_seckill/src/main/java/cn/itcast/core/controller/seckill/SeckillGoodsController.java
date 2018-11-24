package cn.itcast.core.controller.seckill;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.seckill.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;


    @RequestMapping("/findSeckillGoodsList.do")
    public List<SeckillGoods> findSeckillGoodsList() {

        try {
            return seckillGoodsService.findSeckillGoodsList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    @RequestMapping("/findOne.do")
    public SeckillGoods findOne(Long id) {
        return seckillGoodsService.findOneFromRedis(id);
    }


}
