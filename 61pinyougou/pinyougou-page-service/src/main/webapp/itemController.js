var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        num:1,//购买的数量
        searchEntity: {}
    },
    methods: {
        //当点击+ - 调用 影响变量num的值
        addNum:function (num) {
            alert(num);
            this.num+=num;

            if(this.num<=1){
                this.num=1;
            }

        }

    },
    created: function () {

    }
});