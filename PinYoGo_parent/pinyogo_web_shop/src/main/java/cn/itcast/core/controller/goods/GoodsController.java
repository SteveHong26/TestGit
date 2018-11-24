package cn.itcast.core.controller.goods;


import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.service.goods.GoodsService;

import cn.itcast.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;


    @RequestMapping("/findOne.do")
    public GoodsVo findOne(Long id) {
        return goodsService.findOne(id);
    }


    @RequestMapping("/update.do")
    public Result update(@RequestBody GoodsVo goodsVo) {
        try {
            goodsService.update(goodsVo);
            return new Result(true,"😊 Successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😅 Something could be wrong!");
        }
    }


    @RequestMapping("/search.do")
    public PageResult search(Integer page,Integer rows,@RequestBody Goods goods) throws Exception {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page,rows,goods);
    }


    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) throws Exception {
        try {
            goodsService.delete(ids);
            return new Result(true,"😊 successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭 Oops! Something could be wrong!");
        }

    }



    @RequestMapping("/add.do")
    public Result add(@RequestBody GoodsVo goodsVo) throws Exception {

        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(sellerId);
            goodsService.add(goodsVo);
            return new Result(true,"Add product information successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"Failed to add product information!");
        }
    }

}
