var app = new Vue({
    el: "#app",
    data: {
        loginName:''
    },
    methods: {
        //获取用户名
        getName:function () {
            axios.get('/login/getName.shtml').then(
                function (response) {
                    app.loginName=response.data;
                }
            )
        }
    },
    created: function () {
         this.getName();
    }
});