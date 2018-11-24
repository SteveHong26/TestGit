package cn.itcast.core.service.pay;



import java.util.HashMap;
import java.util.Map;

public interface WeChatPayService {
    /**
     *   配置本地二维码生成功能
     * @param out_trade_no 支付订单号
     * @param total_fee    总金额 用于支付
     * @return HashMap
     * @throws Exception
     */
    HashMap createNative(String out_trade_no,String total_fee);

    Map<String,String> queryPayStatus(String out_trade_no);

    Map queryPayStatusWhile(String out_trade_no) throws Exception;


}
