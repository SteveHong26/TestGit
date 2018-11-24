app.service('seckillOrderService',function($http){

    this.submitOrder = function(id) {
        return $http.get("../seckillOrder/submitOrder.do?seckillId=" + id);
    };

});