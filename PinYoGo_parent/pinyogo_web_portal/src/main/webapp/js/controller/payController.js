app.controller('payController',function($scope,$location,payService){
    $scope.createNative = function() {
        payService.createNative().success(
            function(response){
               //显示订单号和金额
                $scope.money = (response.total_fee/100).toFixed(2);
                $scope.out_trade_no = response.out_trade_no;

                //生成二维码
                var qrCode=new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    value:response.code_url,
                    level:'H'
                });
                queryPayStatus(); //查询支付状态
            });
        //调用查询
        queryPayStatus = function() {
            payService.queryPayStatus($scope.out_trade_no).success(
                function (response) {
                    if(response.flag) {
                        location.href = "paysuccess.html#?money="+$scope.money;

                    }else{
                        if(response.message='二维码响应超时') {
                            $scope.createNative();
                        }else{
                            location.href = "payfail.html";
                        }

                    }
                });
        };
    };

    $scope.getMoney = function() {
        return $location.search()["money"];
    };
})