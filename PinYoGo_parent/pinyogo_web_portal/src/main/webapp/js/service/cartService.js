//购物车服务层
app.service('cartService',function($http){


    //定义清空本地localStorage中数据的方法
    this.removeCartList = function() {
        localStorage.removeItem("cartList");
    };

    //添加findCartList方法
    this.findCartList = function(cartList) {
        return $http.post("../cart/findCartList.do",cartList);
    };

    /**
     * 使用 localStorage
     * 使用localStorage注意的规范: 如果用户未登录使用localStorage,反之使用redis缓存
     */
    //setCartList==向localStorage中添加值,api为localStorage.setItem(key,value);
    this.setCartList = function(cartList) {
        localStorage.setItem("cartList", JSON.stringify(cartList));
    };

    //setCartList==从localStorage中取值,api为localStorage.getItem(key,value);
    this.getCartList = function() {
        var cartListStr = localStorage.getItem("cartList");
        //对carListStr对象进行判断,如果为空,就返回空
        if(cartListStr == null){
            return [];
        }else {
            //如果非空,就返回json对象
            return JSON.parse(cartListStr);
        }
    };

    //声明addGoodsToCartList方法,向后台传递参数
    this.addGoodsToCartList = function(cartList,itemId,num) {
        return $http.post("../cart/addGoodsToCartList.do?itemId=" + itemId + "&num=" + num, cartList);
    };

    //求商品总计的函数
    this.sum = function(cartList) {
        var totalValues = {totalNum:0, totalMoney:0};
        for (var i = 0;i < cartList.length;i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];
                totalValues.totalNum += orderItem.num;
                totalValues.totalMoney += orderItem.totalFee;
            }
        }
        return totalValues;
    };
});

