package cn.itcast.core.controller.pay;

import cn.itcast.core.pojo.page.Result;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.pay.WeChatPayService;
import cn.itcast.core.service.seckill.SeckillOrderService;
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
        private SeckillOrderService seckillOrderService;


        @RequestMapping("/createNative.do")
        public HashMap createNative(){
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            SeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedis(username);
            if (seckillOrder != null) {
                long money_fen = (long) (seckillOrder.getMoney().doubleValue() * 100); //将金额转为分,微信支付的约束
                return weChatPayService.createNative(seckillOrder.getId()+"", money_fen+"");
            }else {
                return new HashMap();
            }
        }

        @RequestMapping("/queryPayStatus.do")
        public Result queryPayStatus(String out_trade_no) throws Exception {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, String> queryMap = weChatPayService.queryPayStatusWhile(out_trade_no);
            if (queryMap == null) {
                return new Result(false,"系统出错");
            }else{
                if (queryMap.get("trade_state").equals("SUCCESS")) {
                    try {
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), queryMap.get("transaction_id"));
                        return new Result(true, "支付成功");
                    } catch (RuntimeException r) {
                        r.printStackTrace();
                        return new Result(false, r.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new Result(false,"支付失败");
                    }
                }else {
                    //如果超时,则删除缓存中的订单
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                    return new Result(false,"二维码响应超时");
                }

            }
        }
    }

