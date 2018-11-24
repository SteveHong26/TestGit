package cn.itcast.core.service.pay;

import cn.itcast.core.utils.pay.HttpClient;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeChatPayServiceImpl implements WeChatPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;



    /**
     * 创建本地二维码生成功能用于微信支付
     * @param out_trade_no 支付订单号
     * @param total_fee    总金额 用于支付
     * @return
     * @throws Exception
     */
    @Override
    public HashMap createNative(String out_trade_no, String total_fee) {
        //注入properties配置文件中的属性
        //配置参数

        HashMap map = new HashMap();
        map.put("appid", appid);
        map.put("mch_id", partner);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        //签名 省略,因为微信API会自动生成签名
        map.put("body","品优购");
        map.put("out_trade_no", out_trade_no);
        map.put("total_fee", total_fee);
        map.put("spbill_create_ip","127.0.0.1");
        map.put("notify_url", "https://www.baidu.com");
        map.put("trade_type","NATIVE");
        //发送请求
        try {
            String xmlParam = WXPayUtil.generateSignedXml(map,partnerkey);
            System.out.println("请求参数:" + xmlParam);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //获取结果
            //获取结果的目的在于,要向前端返回一些参数以便为生成二维码提供条件
            //返回的url地址就是生成二维码的条件
            //传递out_trade_no和total_fee是后续的业务之需
            String resultXML = httpClient.getContent();
            System.out.println("返回结果:" + resultXML);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXML);
            HashMap resultHashMap = new HashMap();
            if ("SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"))) {
                resultHashMap.put("code_url", resultMap.get("code_url"));
                resultHashMap.put("out_trade_no", out_trade_no);
                resultHashMap.put("total_fee", total_fee);
            } else {
                System.out.println("出错了");
            }
            return resultHashMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 根据支付订单号检测订单的支付状态
     * @param out_trade_no
     * @return
     * @throws Exception
     */
    @Override
    public Map queryPayStatus(String out_trade_no){
        //创建一个HashMap,来配置参数
        HashMap paramMap = new HashMap();
        paramMap.put("appid", appid); //公众号ID
        paramMap.put("mch_id", partner); //商户ID
        paramMap.put("out_trade_no", out_trade_no);//支付订单号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String queryUrl = "https://api.mch.weixin.qq.com/pay/orderquery";//接口链接的地址
        try {
            //生成签名,传递刚才配置好的Map集合和商家密钥,来获得一个String引用类型的XML内容
            String xmlMap = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发送请求,创建操作请求的对象
            HttpClient httpClient = new HttpClient(queryUrl);
            //设置是否是加密协议类型
            httpClient.setHttps(true);
            //注入到HttpClient刚才生成的带有签名的的xml
            httpClient.setXmlParam(xmlMap);
            //发送post请求
            httpClient.post();
            //获取返回的结果
            String resultStr = httpClient.getContent();
            //将结果转换成Map并返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultStr);
            System.out.println(resultMap);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public Map queryPayStatusWhile(String out_trade_no) throws Exception{
        int x = 0;

        Map map = null;
        while (true) {
            x++;
            if (x >= 10) {
                break;
            }

            map = queryPayStatus(out_trade_no);

            if (map == null) {
                break;
            }

            if ("SUCCESS".equals(map.get("trade_state"))) {
                break;
            }

            Thread.sleep(3000);

        }
        return map;
    }
}
