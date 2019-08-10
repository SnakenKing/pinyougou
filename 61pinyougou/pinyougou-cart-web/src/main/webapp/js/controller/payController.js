var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        payObject:{total_fee:0,out_trade_no:''},//支付的信息对象
        ids: [],
        searchEntity: {}
    },
    methods: {
        //生成支付二维码 页面加载的时候调用
        createNative:function () {
            axios.get('/pay/createNative.shtml').then(
                function (response) {//response.data =map
                    //获取到金额
                    app.payObject.total_fee=response.data.total_fee/100;//原
                    //获取到订单号
                    app.payObject.out_trade_no=response.data.out_trade_no;
                    //获取二维码的连接地址
                    var code_url = response.data.code_url;

                    //生成二维码

                    var qrious = new QRious({
                        element:document.getElementById("qrious"),
                        level:"H",
                        size:250,
                        value:code_url
                    });

                    //如果对象存在
                    if(qrious){
                        app.queryStatus();
                    }


                }
            )
        },
        //方法 是页面加载之后,生成了二维码之后去调用.查询该支付的订单的支付的状态.
        queryStatus:function () {
            axios.get('/pay/queryPayStatus.shtml?out_trade_no='+this.payObject.out_trade_no).then(
                function (response) {//result
                    if(response.data.success){
                        //支付成功
                        window.location.href="paysuccess.html?money=0.01";
                    }else{
                        //1.失败
                        //2.超时

                        //支付超时
                        if(response.data.message=="支付超时"){
                            //自动的重新生成二维码连接
                            app.createNative();
                        }else{
                            //支付失败
                            window.location.href="payfail.html";
                        }
                    }
                }
            )
        }
    },
    created: function () {
        this.createNative();
    }
});