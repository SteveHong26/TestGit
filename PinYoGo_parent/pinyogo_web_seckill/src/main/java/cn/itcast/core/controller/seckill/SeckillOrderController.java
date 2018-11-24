package cn.itcast.core.controller.seckill;

import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.service.seckill.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/submitOrder.do")
    public Result submitOrder(Long seckillId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if("anonymousUser".equals(userId)){
            return new Result(false,"用户未登录");
        }
        try {
                seckillOrderService.submitOrder(seckillId, userId);
                return new Result(true,"添加成功");
        }catch (RuntimeException r){
            return new Result(false, r.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
}
