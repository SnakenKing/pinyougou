var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        keywords:'',//搜索的关键字
        contentList:[],//广告的列表数据
        ids: [],
        searchEntity: {}
    },
    methods: {
        //在页面加载的调用 根据分类的ID 获取广告的列表数据赋值一个变量 页面循环遍历即可
        findByCategoryId:function (categoryId) {
            axios.get('/content/findByCategoryId/'+categoryId+'.shtml').then(
                function (response) {//response.data=list<contenet>
                    app.contentList=response.data;
                }
            )
        },
        //1.获取u关键字的值 跳转到搜索的页面 携带参数
        doSearch:function () {


            //要转码
            window.location.href="http://localhost:9104/search.html?keywords="+encodeURIComponent(this.keywords);

        }
    },
    created: function () {
            this.findByCategoryId(1);
    }
});