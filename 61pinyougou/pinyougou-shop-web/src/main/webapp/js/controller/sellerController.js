var app = new Vue({
    el: "#app",
    data: {

        entity: {}

    },
    methods: {
        //入驻  当点击注册的时候调用该方法 添加一条记录
        register:function () {
            axios.post('/seller/add.shtml',this.entity).then(
                function (response) {//result
                    if(response.data.success){
                        //要登录
                        alert("要登录");
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