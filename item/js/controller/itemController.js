app.controller("itemController",function($scope,$http){



	
	$scope.specificationItems={};//存储用户选择的规格
	
	//数量加减
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}		
	}
	
	//用户选择规格
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;
        $scope.searchSku(); //读取sku
	}
	
	//判断某规格是否被选中
	$scope.isSelected = function(name,value) {

	    if($scope.specificationItems[name]==value){
            return true;
        }else{
            return false;
        }
    };
	
	$scope.sku={};//当前选择的SKU
	
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}



	
	//匹配两个对象是否相等
	matchObject=function(map1,map2){
	    if(Object.keys(map1).length != Object.keys(map2).length){
            return false;
        }
		for(var k in map1){
			if(map1[k] != map2[k]){
				return false;
			}			
		}		
		return true;
		
	}
    //根据规格查询sku
    $scope.searchSku=function(){
        for(var i=0;i<skuList.length;i++){
            if(matchObject(skuList[i].spec, $scope.specificationItems)){
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku={id:0,title:'--------',price:0};//如果没有匹配的

    }
	

	
	//添加商品到购物车
	//添加商品到购物车
	$scope.addToCart=function(){
        location.href = "http://192.168.200.1:9103/cart.html#?itemId="+$scope.sku.id+"&num="+$scope.num;
	}
	
});