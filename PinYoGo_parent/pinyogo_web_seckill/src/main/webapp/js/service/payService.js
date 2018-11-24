app.service('payService',function($http){
    //创建本地支付的方法,生成本地二维码
    this.createNative = function() {
        return $http.get("../pay/createNative.do");
    };

    this.queryPayStatus = function(out_trade_no) {

        return $http.get("../pay/queryPayStatus.do?out_trade_no="+out_trade_no);
    };
});