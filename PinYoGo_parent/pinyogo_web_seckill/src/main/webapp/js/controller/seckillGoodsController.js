app.controller('seckillGoodsController',function($scope,$interval,$location,seckillOrderService,seckillGoodsService){

    $scope.findSeckillGoodsList = function() {
        seckillGoodsService.findSeckillGoodsList().success(
            function(response){
                $scope.goodsList = response;
            }
        );
    }
    $scope.findOne = function()   {
        seckillGoodsService.findOne($location.search()['id']).success(

            function(response){
                $scope.seckillGood = response;
                allSeconds = Math.floor(((new Date($scope.seckillGood.endTime)).getTime() - (new Date($scope.seckillGood.startTime)).getTime()) / 1000);
                time = $interval(function () {
                    allSeconds--;
                    $scope.timeStr = convertTimeToStr(allSeconds);
                    if(allSeconds == 0){
                        $interval.cancel(time);
                    }
                },1000);
            });
    };
    convertTimeToStr = function (allSeconds) {
        var days = Math.floor(allSeconds / (86400)); //天数 86400 = 24h * 60m * 60m
        var hours = Math.floor((allSeconds - days * 86400) / (3600));//小时 3600 = 60m * 60s
        var minutes = Math.floor((allSeconds - days * 86400 - hours * 3600) / 60);//分钟数
        var seconds = allSeconds - days * 86400 - hours * 3600 - minutes * 60;//秒数
        var timeStr = "";
        timeStr += (days==0?"":days) + "天 " + (hours==0?"":hours) + ":" + (minutes==0?"":minutes) + ":" + seconds;

/*
     if(days > 0){
         timeStr += days + "天";
     }
        timeStr += hours + ":" + minutes + ":" + seconds;
*/

        return timeStr;
    };

    //提交订单
    $scope.submitOrder=function () {
        seckillOrderService.submitOrder($scope.seckillGood.id).success(
            function (response) {
                if(response.flag){
                    //成功
                    alert("下单成功！");
                    location.href="pay.html";
                }else{
                    alert(response.message);
                }
            }
        )
    }

});