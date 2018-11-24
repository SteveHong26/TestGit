package cn.itcast.core.controller.seller;



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

    @RequestMapping("/add.do")
    public Result add(@RequestBody Seller seller) throws Exception {

        try {
            sellerService.add(seller);
            return new Result(true,"Register successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"Failed to register!");
        }

    }
}
