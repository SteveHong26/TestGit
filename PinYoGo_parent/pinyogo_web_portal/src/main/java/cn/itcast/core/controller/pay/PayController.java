package cn.itcast.core.controller.pay;

        import cn.itcast.core.pojo.log.PayLog;
        import cn.itcast.core.pojo.page.Result;
        import cn.itcast.core.service.order.OrderService;
        import cn.itcast.core.service.pay.WeChatPayService;
        import com.alibaba.dubbo.config.annotation.Reference;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import java.util.HashMap;
        import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 1000*60*6)
    private WeChatPayService weChatPayService;

    @Reference
    private OrderService orderService;


    @RequestMapping("/createNative.do")
    public HashMap createNative() throws Exception{
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

            PayLog payLog = orderService.searchPayLogFromRedis(username);
            if (payLog != null) {
                return weChatPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");
            }else {
                return new HashMap();
            }


    }

    @RequestMapping("/queryPayStatus.do")
    public Result queryPayStatus(String out_trade_no) throws Exception {
        Map<String, String> queryMap = weChatPayService.queryPayStatusWhile(out_trade_no);
        if (queryMap == null) {
            return new Result(false,"二维码响应超时");
        }else{
            if (queryMap.get("trade_state").equals("SUCCESS")) {
                orderService.updateOrderStatus(out_trade_no,queryMap.get("transaction_id"));
                return new Result(true,"支付成功");
            }else {
                return new Result(false,"支付失败");
            }

        }
    }
}
