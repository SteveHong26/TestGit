app.service('seckillGoodsService',function($http){

    this.findSeckillGoodsList = function() {
        return $http.get("seckillGoods/findSeckillGoodsList.do");
    };

    this.findOne = function(id) {

        return $http.get("../seckillGoods/findOne.do?id="+id);

    };
});