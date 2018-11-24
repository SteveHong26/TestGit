package cn.itcast.core.service.order;

import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;

public interface OrderService {

    void saveOrderToMysql(Order order) throws Exception;

    PayLog searchPayLogFromRedis(String userId) throws Exception;

    void updateOrderStatus(String out_trade_no,String transaction_id);

}
