package cn.itcast.core.service.order;

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.utils.IdWorker.IdWorker;
import cn.itcast.core.vo.Cart;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private PayLogDao payLogDao;


    @Override
    @Transactional
    public void saveOrderToMysql(Order order) throws Exception {


        //获取购物车中的数据
        List<Cart> cartList =
                (List<Cart>)redisTemplate.boundHashOps("cartList").get(order.getUserId());
        String outTradeNo = idWorker.nextId() + "";

        double total_money = 0; //预定义总金额变量
        for (Cart cart : cartList) {

            //使用twitter下的snowFlake算法来获取一个ID并作为订单号
            long orderId = idWorker.nextId();
            Order tbOrder = new Order();
            tbOrder.setOrderId(orderId);
            tbOrder.setPayment(order.getPayment());
            tbOrder.setPaymentType(order.getPaymentType());
            tbOrder.setStatus(order.getStatus());
            tbOrder.setCreateTime(new Date());
            tbOrder.setUpdateTime(new Date());
            tbOrder.setUserId(order.getUserId());
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());
            tbOrder.setReceiverMobile(order.getReceiverMobile());
            tbOrder.setReceiver(order.getReceiver());
            tbOrder.setSourceType(order.getSourceType());
            tbOrder.setSellerId(cart.getSellerId());
            tbOrder.setOutTradeNo(outTradeNo);
            //循环购物车明细
            double money = 0;
            for (OrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setSellerId(cart.getSellerId());
                orderItem.setOrderId(orderId);
                money += orderItem.getPrice().doubleValue();
                orderItemDao.insert(orderItem);
            }

            tbOrder.setPayment(new BigDecimal(money));
            orderDao.insert(tbOrder);
            total_money += (long)(money*100); //所有订单的累计金额
        }
        if ("1".equals(order.getPaymentType())) { //当订单的支付方式
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(outTradeNo);
            payLog.setCreateTime(new Date());
            payLog.setTotalFee((long)total_money);
            payLog.setUserId(order.getUserId());
            payLog.setTradeState("0");//0代表支付状态
            payLog.setPayType("1"); //1代表是微信支付
            payLogDao.insert(payLog);
            redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
        }

        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

    }

    @Override
    @Transactional
    public PayLog searchPayLogFromRedis(String userId) throws Exception {

        return (PayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //修改支付日志
        PayLog payLog = payLogDao.selectByPrimaryKey(out_trade_no);
        payLog.setTransactionId(transaction_id);
        payLog.setPayTime(new Date());
        payLog.setTradeState("1"); //1代表已经支付
        payLogDao.updateByPrimaryKey(payLog);

        //修改订单状态
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.createCriteria().andOutTradeNoEqualTo(out_trade_no);
        Order order = new Order();
        order.setStatus("2"); //订单状态,2为已经支付
        order.setPaymentTime(new Date());
        orderDao.updateByExampleSelective(order,orderQuery);
        //清楚redis中的缓存
        redisTemplate.boundHashOps("payLog").get(payLog.getUserId());
    }
}
