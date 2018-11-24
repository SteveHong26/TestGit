package cn.itcast.core.service.cart;

import cn.itcast.core.vo.Cart;

import java.util.List;

public interface CartService {

    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) throws Exception;

    List<Cart>findCartListFromRedis(String username) throws Exception;

    void saveCartListToRedis(List<Cart> cartList,String username) throws Exception;

    List<Cart> mergeRedisAndLocalStorage(List<Cart> cartList1, List<Cart> cartList2) throws Exception;
}
