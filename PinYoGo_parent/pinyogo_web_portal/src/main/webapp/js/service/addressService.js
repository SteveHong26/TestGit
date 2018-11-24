//地址服务层
app.service('addressService',function($http){

    //根据当前用户查询地址列表
    this.findProfileByUser=function () {
        return $http.get("../user/findProfileByUser.do");
    }

});