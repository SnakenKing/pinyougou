var app = new Vue({
    el: "#app",
    data: {
        userName: '',//绑定用户的名称
    },
    methods: {
        getUserInfo: function () {
            axios.get('/login/userInfo.shtml').then(
                function (response) {//response.data=Strng

                    app.userName = response.data;

                }
            )
        }
    },
    created: function () {
        this.getUserInfo();
    }
});