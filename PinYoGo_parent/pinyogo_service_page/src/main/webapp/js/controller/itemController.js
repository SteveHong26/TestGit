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
    /*
    * 这个方法的作用在于: 使得规格选项里的属性处于默认选择状态
    * 因此,需要将该方法在页面加载时被初始化,所以需要在body标签上加上ng-init="loadSku()"
    * 效果是: 当我们点开或刷新页面时,商品的属性处于默认选择状态
    * */
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}



	
	//匹配两个对象是否相等
    /*
      下面的Object.keys方法为Angularjs中的函数
      作用: 当抽取的对象为对象时: 将抽取出对象的key
            当抽取的对象为数组时: 将抽取数组的索引
   下面的方法就是负责来判断的,先判断长度是否相等,在进行二级判断,判断属性值是否相等
        如果相等就返回true
    */
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
  /*
  方法的作用:
         实现点击后变成选中的状态,同时价格和标题的内容页发生改变
        该方法作用的标签是一个a标签
        当我们点击该标签会触发点击事件,点击事件将原本从skuList
        中获取的attributeName和value值临时赋值给specificationItems()
        这样,在另外定义一个方法来负责判断属性和值是否和skuList中的匹配,如果匹配
        就将$scope.sku重新赋值,这样标题的和价格就会$scope.sku的值发生改变而改变
        另外这一点页体现了angular双向作用的特点
    */
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
	$scope.addToCart=function(){
        location.href = "http://192.168.200.1:9103/cart.html#?itemId="+$scope.sku.id+"&num="+$scope.num;
	}
	
});