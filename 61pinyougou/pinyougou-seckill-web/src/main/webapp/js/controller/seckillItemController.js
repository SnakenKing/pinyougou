var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],//所有的商品的列表数据
        entity: {},
        ids: [],
        messageInfo:'',//提示信息
        seckillId: 0,
        count:0,//库存
        timeString:'',//倒计时变量
        searchEntity: {}
    },
    methods: {
        //下秒杀订单
        submitOrder: function () {
            axios.get('/seckillOrder/submitOrder.shtml?id=' + this.seckillId).then(
                function (response) {
                    if (response.data.success) {
                        //跳转到支付的页面
                       app.messageInfo=response.data.message;
                    } else {

                        if (response.data.message == 403) {
                            //要登录
                            var url = window.location.href;
                            window.location.href = "/page/login.shtml?url=" + url;
                        }
                    }
                }
            )
        },

        //在点击立即抢购之后 不停的 发送请求 查询状态
        queryStatus:function(){

            var count=0;

            window.setInterval(function () {
                count+=3;
                //发送请求
                axios.get('/seckillOrder/queryOrderStatus.shtml').then(
                    function (response) {//response.data =result
                        if(response.data.success){
                            //下单成功
                            window.location.href="pay/pay.html";

                        }else{
                           app.messageInfo=response.data.message+"....."+count;
                        }
                    }
                )
            },3000)
        },

            //将时间(毫秒) 转成 天  时  分 秒 格式
        convertTimeString:function(alltime){
            var allsecond=Math.floor(alltime/1000);//毫秒数转成 秒数。
            var days= Math.floor( allsecond/(60*60*24));//天数
            var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小时数
            var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
            var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
            if(days>0){
                days=days+"天 ";
            }
            if(hours<10){
                hours="0"+hours;
            }
            if(minutes<10){
                minutes="0"+minutes;
            }
            if(seconds<10){
                seconds="0"+seconds;
            }
            return days+hours+":"+minutes+":"+seconds;
        },
        //倒计时
        caculate: function (alltime) {
            //倒计时的时间  1000000000毫秒
            //每次隔一秒钟执行一次方法:时间-1000
            //赋值给一个变量  绑定到倒计时的html中


           var clock= window.setInterval(function () {
                alltime=alltime-1000;

                app.timeString =app.convertTimeString(alltime);
                if(alltime<=0){
                    //清除倒计时
                    window.clearInterval(clock);
                }
                console.log(alltime);
            },1000);

        },
        getGoodsById:function (id) {
            axios.get('/seckillGoods/getGoodsById.shtml?id='+id).then(
                function (response) {
                    app.count=response.data.count;
                    app.caculate(response.data.time);
                }
            )
        }
    },
    created: function () {
        //获取URL中的参数的值
        let urlObj = this.getUrlParam();
        //赋值给变量
        this.seckillId = urlObj.id;
        console.log(this.seckillId);

        //页面加载的时候 发送请求 获取到距离结束的时间  进行倒计时
        this.getGoodsById(this.seckillId);
        //this.caculate(1000000000);

    }
});