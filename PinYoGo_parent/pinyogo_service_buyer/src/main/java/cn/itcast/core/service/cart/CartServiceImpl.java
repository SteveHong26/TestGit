package cn.itcast.core.service.cart;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.vo.Cart;
import com.alibaba.dubbo.config.annotation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 购物车合并:
     *   说明: 遍历购物车列表2,用购物车列表1和它比较
     *   比较方法为: @addGoodsToCartList(列表1,列表2中的itemId属性,列表2中的item数量)
     *   比较流程:
     *          @searchCartBySellerId() 是比较购物车是否相等,相等则比较商品对象,
     *          使用方法@searchOrderItemByItemId(),如果商品也想等,则实现数量合并;
     *          反之: 如果购物车不相等,则通过传递的列表2中的itemId来新增购物车
     *          并将购物车添加到购物车列表集合当中
     *          如果购物车相等,但是商品不相等,则通过遍历购物车,将购物车中的商品
     *          与列表2传递商品ID进行比较,如果商品不相等,创建商品订单,添加到购物车
     *          最后将完全比较好并合并的购物车列表返回,实现合并.
     * @param cartList1
     * @param cartList2
     * @return
     * @throws Exception
     */
    @Override
    public List<Cart> mergeRedisAndLocalStorage(List<Cart> cartList1, List<Cart> cartList2) throws Exception {
        System.out.println("<<<<==================购物车合并===================>>>>");
            for (Cart cart : cartList2) {
                for (OrderItem orderItem : cart.getOrderItemList()) {
                    cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
                }
            }
            return cartList1;
    }

    /**
     * 通过用户名从redis中获取购物车集合
     * @return
     */
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("<<<<<<<<<<<<<<============从redis中获取数据==========>>>>>>>: " + username);
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(username);
        if(cartList == null){
            return new ArrayList<>();
        }
        return  cartList;

    }

    /**
     * 向redis中存储数据
     * @param cartList
     */
    public void saveCartListToRedis(List<Cart> cartList,String username) {
        System.out.println("<<<<<=========向redis中存储数据========>>>>>>: " + username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }


    /** 购物车相关操作
     * @param cartList
     * @param itemId
     * @param num
     * @return
     * @throws Exception
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) throws Exception {
        //1.根据商品 SKU ID 查询 SKU 商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //判断item的商品状态和item是否为空
        if (item == null) {
            throw new RuntimeException("该订单为空,请添加!");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品无效");
        }
        String sellerId = item.getSellerId();
        //2-3.根据商家 ID 判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4.如果购物车列表中不存在该商家的购物车
        if(cart == null){
                //4.1 新建购物车对象
                cart = new Cart();
                //获取新添加商品的商家ID,并set注入到购物车对象里
                cart.setSellerId(item.getSellerId());
                //添加商家名称
                cart.setSellerName(item.getSeller());
                //调用方法,创建新的订单对象
                OrderItem orderItem = createOrderItemIfNotExist(item, num);
                List<OrderItem> orderItemList = new ArrayList<>();
                orderItemList.add(orderItem);
                //4.2 将新建的购物车对象添加到购物车列表
                cart.setOrderItemList(orderItemList);
            cartList.add(cart);
            }else{
                //5.如果购物车列表中存在该商家的购物车
            OrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            // 查询购物车明细列表中是否存在该商品
                        //5.1. 如果没有，新增购物车明细
                        if(orderItem == null){
                            orderItem = createOrderItemIfNotExist(item, num);
                            cart.getOrderItemList().add(orderItem);
                        }else {
                            //5.2. 如果有，在原购物车明细上添加数量，更改金额
                            orderItem.setNum(orderItem.getNum() + num);
                            orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                            if (orderItem.getNum() <= 0) {
                                cart.getOrderItemList().remove(orderItem);
                            }
                            if(cart.getOrderItemList().size() == 0){
                                cartList.remove(cart);
                            }
                        }
                    }
        return cartList;
    }


    /**
     * 根据商家ID查询该商家是否存在于购物车订单当中
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }


    /**
     * 根据商品ID查询该购物车对象中是否存在该商品
     * @param orderItemList
     * @param itemId
     * @return
     */
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItemList,Long itemId) {
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }


    /**
     * 当商品在原订单中不存在时,创建新的订单对象
     * @param item
     * @param num
     * @return
     */
    private OrderItem createOrderItemIfNotExist(Item item,Integer num) {
        if (num <= 0) {
            num = 1;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        //orderItem.setOrderId(null); TODO
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    /**
     * 判断item的特定属性
     * @param item
     */
    private void exceptionHandler(Item item) {

    }
}
