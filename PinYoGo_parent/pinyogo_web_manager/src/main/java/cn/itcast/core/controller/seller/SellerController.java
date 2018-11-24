package cn.itcast.core.controller.seller;

import cn.itcast.core.pojo.page.PageResult;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.seller.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller) throws Exception {
        return sellerService.search(page,rows,seller);
    }

    @RequestMapping("/findOne.do")
    public Seller findOne(String id) throws Exception {

        return sellerService.findOne(id);
    }

    @RequestMapping("/updateStatus.do")
    public Result updateStatus(String sellerId,String status) {
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"Update successful");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"Failed to update");
        }
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) throws Exception {
        try {
            sellerService.delete(ids);
            return new Result(true,"Delete successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"Failed to delete!");
        }

    }

}
