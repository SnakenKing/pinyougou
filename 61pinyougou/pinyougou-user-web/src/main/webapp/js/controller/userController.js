var app = new Vue({
    el: "#app",
    data: {
        entity: {},
        smsCode:'',//验证码
    },
    methods: {
        //当点击完成注册的时候调用 发送请求 数据存储到ysql
        register: function () {
            axios.post('/user/add.shtml?code='+this.smsCode, this.entity).then(
                function (resposne) {//responsed.data =result
                    if (resposne.data.success) {
                        //注册成功去登录
                        alert("你要去登录了");
                    } else {
                        alert(resposne.data.message);
                    }
                }
            )
        },
        //点击获取验证码的调用
        createSmsCode:function () {
            axios.get('/user/sendCode.shtml?phone='+this.entity.phone).then(
                function (response) {
                    if(response.data.success){
                        alert("看手机");
                    }else{
                        alert(response.data.message);
                    }
                }
            )

        }
    },
    created: function () {

    }
});