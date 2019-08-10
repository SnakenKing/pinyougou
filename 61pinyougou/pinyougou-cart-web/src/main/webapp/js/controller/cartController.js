var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        cartList: [],//购物车的列表
        totalMoney: 0,//总金额
        addressList: [],//地址的列表
        address: {},//绑定当前的被点击到的地址对象
        totalNum: 0,//总数量
        order: {paymentType:'1'},//绑定订单的对象
        searchEntity: {}
    },
    methods: {
        //方法 当页面加载的时候调用获取所有的购物车数据赋值给变量cartList
        findCartList: function () {


            axios.get('/cart/findCartList.shtml').then(
                function (response) {
                    //response.data=list
                    app.cartList = response.data;//[{},{num,totalfee},{}]

                    app.totalMoney = 0;
                    app.totalNum = 0

                    for (var i = 0; i < app.cartList.length; i++) {
                        var obj = app.cartList[i];//cart={sellerID,sellerName ,List[{}]}

                        for (var n = 0; n < obj.orderItemList.length; n++) {
                            var objorderitem = obj.orderItemList[n];//{totalFee,num}
                            app.totalMoney += objorderitem.totalFee;
                            app.totalNum += objorderitem.num;
                        }

                    }

                }
            )
        },
        //添加购物车 点击+ - 调用
        addGoodsToCartList: function (itemId, num) {
            axios.get('/cart/addGoodsToCartList.shtml?itemId=' + itemId + '&num=' + num).then(
                function (response) {//response.data=result
                    if (response.data.success) {
                        app.findCartList();
                    }
                }
            )
        },
        //页面加载的时候调用获取当前登录的用户的地址列表 赋值给一个变量

        findAddressList: function () {
            axios.get('/address/findAddressListByUserId.shtml').then(
                function (response) {
                    app.addressList = response.data;//所有的地址

                    for (var i = 0; i < app.addressList.length; i++) {
                        var obj = app.addressList[i];//{isDefault}
                        if(obj.isDefault=='1'){
                            app.address=obj;
                            break;
                        }
                    }


                }
            )
        },

        //选择支付的类型()
        selectType:function(type){
            this.order.paymentType=type;
        },
        //判断 循环到的地址对象是否和当前的地址对象一致,如果一致返回true,否则返回false
        isSelected: function (address) {
            if (address == this.address) {
                return true;
            }
            return false;
        },
        //点击地址的时候调用 被点击到的地址对象赋值给变量address
        selectAddress: function (address) {
            this.address = address;
        },
        //点击提交订单的时候调用 提交订单 (1.获取当前地址对象中的数据赋值给变量order.2发送请求提交订单)
        submitOrder:function () {
            this.order.receiverAreaName=this.address.address;//
            this.order.receiverMobile=this.address.mobile;//
            this.order.receiver=this.address.contact;//


            axios.post('/order/submitOrder.shtml',this.order).then(
                function (response) {//response.data =result
                    if(response.data.success){
                        window.location.href="pay.html";
                    }
                }
            )
        }
    },
    created: function () {
        this.findCartList();
        var url = window.location.href;//获取当前浏览器中的URL的地址数据
        //如果 url中有结算页
        if(url.indexOf("getOrderInfo.html")!=-1){
            this.findAddressList();
        }

    }
});