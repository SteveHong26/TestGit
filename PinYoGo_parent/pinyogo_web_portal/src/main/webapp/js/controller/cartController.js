// 购物车控制层//购物车控制层
// app.controller('cartController',function($scope,$location,cartService){
//
//     /**
//      * 创建页面初始化方法,当页面加载,将用户添加到购物车的数据,显示到cart.html页面
//      */
//     $scope.init = function() {
//         //调用getCartList为cartList赋值
//         $scope.cartList = cartService.getCartList();
//         //获取url地址当中传递的itemId和num的值,使用$location.search()["属性名"];
//         var itemId = $location.search()['itemId'];
//         var num = $location.search()['num'];
//         if(itemId != null && num != null){
//             $scope.addGoodsToCartList(itemId,num);
//         }
//     }
//
//
//     /**
//      * controller中定义addGoodsToCartList方法
//      * @param itemId
//      * @param num
//      */
//     $scope.addGoodsToCartList = function(itemId,num) {
//         cartService.addGoodsToCartList($scope.cartList, itemId, num).success(
//             function(response){
//                if(response.success){
//                    cartService.setCartList(response.data);//保存购物车
//                    $scope.cartList = response.data;
//                }
//             });
//     };
//
//     $scope.$watch("cartList",function(newValue,oldValue){
//         $scope.totalValues = cartService.sum($scope.cartList);
//     });
// });

//购物车控制层
app.controller('cartController',function($scope,$location,cartService,addressService,orderService){


    $scope.cartList = []; //定义购物车数组



    //页面初始化
    $scope.init=function () {
        $scope.cartList= cartService.getCartList();
        var itemId= $location.search()["itemId"];//商品ID
        var num= $location.search()["num"]; // 数量
        if(itemId!=null && num!=null){ //如果有参数
            $scope.addGoodsToCartList(itemId,num);//添加商品到购物车
        }else{
            //如果没有就查询
            $scope.findCartList();
        }
    }


    $scope.findCartList = function() {
        $scope.cartList = cartService.getCartList();
        cartService.findCartList($scope.cartList).success(
            function(response){
                $scope.cartList = response.data;
                //如果用户登陆则清楚localStorage中的数据
                if(response.loginName != ""){
                    cartService.removeCartList();
                }
                $scope.loginName = response.loginName;
            });
    };

    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList( $scope.cartList ,itemId, num ).success(
            function (response) {
                if(response.success){
         /*             cartService.setCartList(response.data);*/
                      $scope.cartList=response.data;
                    //判断用户是否登陆
                    if(response.loginName != ""){
                        //如果登陆就查找购物车信息
                        $scope.findCartList();
                    }else {
                        //如果没有登陆就将数据保存到本地localStorage
                        cartService.setCartList(response.data);
                    }
                    /*$scope.cartList = response.data;*/
                    $scope.loginName = response.loginName;
                }
            }
        )
    }

    //当购物车发生变化，计算合计
    $scope.$watch("cartList",function (newValue,oldValue) {
        //alert("1");
        $scope.totalValues=  cartService.sum($scope.cartList);
    })



    //查询当前用户的地址列表
    $scope.findAddressList=function () {
        addressService.findProfileByUser().success(
            function (response) {
                $scope.addrList=response;
                //默认地址选择
                for (var i=0; i<$scope.addrList.length;i++) {
                    if($scope.addrList[i].isDefault=='1'){
                        $scope.address = $scope.addrList[i];
                        break;
                    }
                }
            }
        )
    }
    $scope.isSelected = function(address) {
        if($scope.address==address){
            return true;
        }else{
            return false;
        }
    };

    $scope.order = {paymentType:"1"}
    $scope.selectPaymentType = function(type) {
        $scope.order.paymentType = type;
    };

    $scope.selectAddress = function(address) {
        $scope.address = address;
    };


    $scope.submitOrder = function() {
        //将address中的部分收货人信息在页面赋值给要传递给后台进行处理的order中
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;

        orderService.submitOrder($scope.order).success(
            function(response){
                if(response.flag){
                    //页面跳转
                    if($scope.order.paymentType=='1'){ //如果是微信支付就跳转到支付页面
                        location.href = "pay.html";
                    }else{//如果是货到付款就跳转到提示页面
                        location.href = "paysuccess.html";
                    }
                }else{
                    alert(response.message); //也可以跳转到提示页面
                }
            }
        )
    };
});