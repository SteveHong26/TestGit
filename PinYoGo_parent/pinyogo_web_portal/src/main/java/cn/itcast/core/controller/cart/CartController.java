package cn.itcast.core.controller.cart;

import cn.itcast.core.service.cart.CartService;
import cn.itcast.core.vo.Cart;
import cn.itcast.core.vo.LoginResult;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    //为避免远程服务调用超时,将超时事件改为10秒,而默认为1秒
    @Reference(timeout = 6000)
    private CartService cartService;


    /**
     * 这个方法是用于搜索购物车的数据
     * 当前端未传递任何商品参数,就意味着浏览器需要根据条件显示数据
     * 如果用户已经登陆了,就需要根据用户登陆的ID也就是用户名来查询
     * 缓存中的购物车数据并显示
     * 反之
     * 则查询本地数据
     * @param cartList
     * @return
     * @throws Exception
     */
    @RequestMapping("/findCartList.do")
    public LoginResult findCartList(@RequestBody List<Cart> cartList) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登陆用户名============================>>>>>>>>>>>>>:" + username);
        //如果用户未登陆
        if ("anonymousUser".equals(username)) {
            return new LoginResult(true,"",cartList);
        }else{
            //如果已经登陆了
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if(cartList.size() > 0){
                cartList_redis = cartService.mergeRedisAndLocalStorage(cartList, cartList_redis);
                cartService.saveCartListToRedis(cartList_redis,username);
            }
        return new LoginResult(true,username,cartList_redis);
        }
    }


    /**
     *  添加购物车等相关功能的实现
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList.do")
    public LoginResult addGoodsToCartList(@RequestBody List<Cart> cartList,Long itemId,Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登陆用户名:" + username);
        if ("anonymousUser".equals(username)) {
            username = "";
        }
        try {
            //如果登陆
            if (!"".equals(username)) {
                List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
                List<Cart> carts = cartService.addGoodsToCartList(cartList_redis,itemId,num);
                cartService.saveCartListToRedis(carts,username);
                return new LoginResult(true, username, carts);
            }else{
                List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);
                return new LoginResult(true, username, carts);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false,username,"添加失败!");
        }
    }
}
