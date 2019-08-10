var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        //flag:true,
        list: [],
        entity: {},
        preDott: false,//前面的 点
        nextDott: false,//后面的 点
        searchMap: {
            'keywords': '',
            'category': '',
            'brand': '',
            'spec': {},
            'price': '',
            'pageNo': 1,
            'pageSize': 40,
            'sortField': '',
            'sortType': ''
        },//搜索的参数封装的变量
        resultMap: {brandList: []},//返回的结果集对象
        ids: [],
        pageLabels: [],//存储页码的数组
        searchEntity: {}
    },
    methods: {
        //当点击搜索的按钮的时候调用 执行搜索
        searchList: function () {
            axios.post('/itemSearch/search.shtml', this.searchMap).then(function (response) {
                app.resultMap = response.data;//Map
                app.buildPageLabel();//重新构建分页的页码标签
            });
        },

        //清空搜索条件
        clear: function () {
            this.searchMap = {
                'keywords': this.searchMap.keywords,
                'category': '',
                'brand': '',
                'spec': {},
                'price': '',
                'pageNo': 1,
                'pageSize': 40,
                'sortField': '',
                'sortType': ''
            };//搜索的参数封装的变量
        },

        //点击价格的时候调用  赋值2个变量 执行搜索
        doSort: function (sortField, sortType) {
            this.searchMap.sortField = sortField;
            this.searchMap.sortType = sortType;
            this.searchList();

        },

        //判断 关键字就是品牌  true
        keywordsIsBrand: function () {
            //循环遍历  品牌列表  //从关键字查询是否包含 品牌的数据 ,如果是 返回true,否则false
            for (var i = 0; i < this.resultMap.brandList.length; i++) {//[{id:1,"text":"三星"}]
                var obj = this.resultMap.brandList[i];//{id:1,"text":"三星"}
                //从关键字查询是否包含 品牌的数据

                if (this.searchMap.keywords.indexOf(obj.text) != -1) {

                    this.searchMap.brand = obj.text;
                    return true;
                }
            }
            return false;
        },


        //构建分页的标签页,当点击搜索的时候 调用 改变变量的值 将总页数赋值到变量中
        buildPageLabel: function () {
            this.pageLabels = [];

            var fisrtPage = 1;//开始页码
            var lastPage = this.resultMap.totalPages;//结束页码

            //如果总页数>5 展示以当前页为中心的5页
            if (this.resultMap.totalPages > 5) {
                //如果 当前页 <=3      显示前5页码
                if (this.searchMap.pageNo <= 3) {
                    fisrtPage = 1;
                    lastPage = 5;
                    //后面有点 前面没有点
                    this.preDott = false;
                    this.nextDott = true;
                } else if (this.searchMap.pageNo >= (this.resultMap.totalPages - 2)) {//如果当前页>=总页数-2  显示后5页    96 97 98 99 100
                    fisrtPage = this.resultMap.totalPages - 4;
                    lastPage = this.resultMap.totalPages;
                    // 后面没有点
                    // 前面有点
                    this.preDott = true;
                    this.nextDott = false;

                } else {//否则就是显示中间的5个页码
                    fisrtPage = this.searchMap.pageNo - 2;
                    lastPage = this.searchMap.pageNo + 2;

                    // 后面有点
                    // 前面有点

                    this.preDott = true;
                    this.nextDott = true;

                }

            } else {
                //否则显示全部   不需要点
                this.preDott = false;
                this.nextDott = false;
            }

            for (var i = fisrtPage; i <= lastPage; i++) {
                this.pageLabels.push(i);
            }
        },

        //点击的时候调用  1.把被点击到的商品分类 赋值给变量 2.执行搜索
        addSearchItem: function (key, value) {
            if (key == 'brand' || key == 'category' || key == 'price') {
                app.searchMap[key] = value;
            } else {
                app.searchMap.spec[key] = value;
            }
            app.searchList();
        },
        //点击X的调用 1.将变量 赋值给空 2.执行查询
        removeSearchItem: function (key) {
            if (key == 'brand' || key == 'category' || key == 'price') {
                app.searchMap[key] = '';
            } else {
                //删除JS对象的属性{}

                delete app.searchMap.spec[key];
            }
            app.searchList();
        },
        //根据页码进行查询
        queryByPage: function (pageNo) {
            //转成数字
            let number = parseInt(pageNo);

            if (number <= 0) {
                number = 1;
            }
            if (number > this.resultMap.totalPages) {
                number = this.resultMap.totalPages;
            }

            //1.将点击到的页码的值赋值变量
            this.searchMap.pageNo = parseInt(number);
            //2.执行搜索
            this.searchList();
        }

    },
    //钩子函数
    created: function () {


        //解码

        //页面加载的时候 获取url中的keywords的值
        let urlParam = this.getUrlParam();// {keywords:"手机"}

        if(urlParam.keywords!=undefined && urlParam.keywords!=null){
            //赋值给变量 searchmap中

            this.searchMap.keywords=decodeURIComponent(urlParam.keywords);
            //执行搜素
            this.searchList();
        }

    }

})
