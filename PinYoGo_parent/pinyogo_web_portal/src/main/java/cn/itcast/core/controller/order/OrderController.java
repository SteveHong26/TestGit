package cn.itcast.core.controller.order;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.service.order.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/add.do")
    public Result submitOrder(@RequestBody Order order) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUserId(username);
        try {
            orderService.saveOrderToMysql(order);
            return new Result(true,"We have received your orders and as soon as possible to deliver them to your hand. Thanks for supports");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"Something could be wrong");
        }

    }

}
