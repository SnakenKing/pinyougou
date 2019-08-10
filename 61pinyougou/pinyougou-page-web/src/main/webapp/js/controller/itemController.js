var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
		sku:skuList[0],//用于绑定页面中的SKU的数据
        list: [],
		specificationItems:JSON.parse(JSON.stringify(skuList[0].spec)),//用于存储当前的规格的对象 深克隆
        entity: {},
        ids: [],
        num:1,//购买的数量
        searchEntity: {}
    },
    methods: {
        //当点击+ - 调用 影响变量num的值
        addNum:function (num) {
            
            this.num+=num;

            if(this.num<=1){
                this.num=1;
            }

        },
		selectSpecifcation:function(specName,specValue){
			//this.specificationItems[specName]=specValue;
			this.$set(this.specificationItems,specName,specValue)
			console.log(specName);
			console.log(specValue);
			console.log(this.specificationItems);

			this.search();
		},
		isSelected:function(specName,specValue){

			 if(this.specificationItems[specName]==specValue){
			 return true
				 }

			return false;
		
		},
	
		//循环遍历sku的列表  判断 点击到的规格的对象是否在SKU的列表中存在,如果是 找到SKU对象 赋值给变量
		search:function(){
			   for(var i=0;i<skuList.length;i++){  //[{spec:{}},]
				   	var obj = skuList[i];//{spec:{}}
					
					if(JSON.stringify(this.specificationItems)==JSON.stringify(obj.spec)){
						this.sku= obj;
						break;
					}

			   }
		},
		//点击加入购物车 发送请求 传递商品的ID 和购买的数量 添加购物车
        addGoodsToCartList:function () {
            axios.get('http://localhost:9107/cart/addGoodsToCartList.shtml?itemId='+this.sku.id+'&num='+this.num,{withCredentials:true}).then(
                function (response) {//response.data=result
                    if(response.data.success){//添加购物车成功
                        window.location.href="http://localhost:9107/cart.html";
                    }
                }
            )
        }


    },
    created: function () {

    }
});