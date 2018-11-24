package cn.itcast.core.controller.goods;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.service.goods.GoodsService;
import cn.itcast.core.service.page.ItemPageService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;


    @Reference
    private ItemPageService itemPageService;




    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods) throws Exception {
        return goodsService.searchForManager(page,rows,goods);
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) throws Exception {
        try {
            goodsService.delete(ids);
            return new Result(true,"😊 Successful");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭 Something could be wrong and contact our engineer Phone: 010-2326456!");
        }
    }

    @RequestMapping("/updateStatus.do")
    public Result updateStatus(Long[] ids,String status) {
        try {
            goodsService.updateStatus(ids, status);
            return new Result(true,"😊 Successful");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"😭 Something could be wrong and contact our engineer Phone: 010-2326456!");
        }
    }

    @RequestMapping("/genHTML.do")
    public void genHTML(Long goodsId) {
        itemPageService.getItemHTML(goodsId);
    }
}
