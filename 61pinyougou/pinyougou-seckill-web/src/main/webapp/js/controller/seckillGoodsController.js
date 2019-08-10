var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],//所有的商品的列表数据
        entity: {},
        ids: [],
        searchEntity: {}
    },
    methods: {
        //在加载的调用
        findAllFromRedis: function () {
            axios.get('/seckillGoods/findAllFromRedis.shtml').then(
                function (response) {//List
                    app.list = response.data;
                }
            )
        }
    },
    created: function () {
        this.findAllFromRedis();
    }
});