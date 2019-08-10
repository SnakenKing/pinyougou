var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        selected1:[],//绑定选中的下拉的数据变量 品牌
        selected2:[],//绑定选中的下拉的数据变量 规格
        brandOptions:[],//绑定显示所有的品牌的数据列表
        specOptions:[],//绑定显示所有的规格的数据列表  [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        entity:{customAttributeItems:[]},
        ids:[],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/typeTemplate/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/typeTemplate/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/typeTemplate/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/typeTemplate/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/typeTemplate/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/typeTemplate/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;

                //获取到规格的列表 转成JSON对象([{}])
                app.entity.specIds = JSON.parse(app.entity.specIds);

                let stringify = JSON.stringify(app.entity.specIds);
                console.log(stringify);


                //获取到品牌的列表 转成JSON对象([{}])
                app.entity.brandIds = JSON.parse(app.entity.brandIds);
                //获取到扩展属性   转成JSON对象([{}])
                app.entity.customAttributeItems = JSON.parse(app.entity.customAttributeItems);

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/typeTemplate/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //页面加载的时候调用 获取所有的品牌列表 数据转成要的格式 赋值给变量
        findBrandIds:function () {
            axios.get('/brand/findAll.shtml').then(
                function (response) {//response.data=List<tbrand>
                    //response.data = [{id:1,name:"联想",firstChar:"L"}]

                    // brandOptions:[],
                    //要求的格式: [{id:1,"text":"联想"},{}]
                    for(var i=0;i<response.data.length;i++){
                        var obj = response.data[i];//{id:1,name:"联想",firstChar:"L"
                        app.brandOptions.push({"id":obj.id,"text":obj.name});
                    }
                }
            )
        },
        //页面加载的时候调用 获取所有的品牌列表 数据转成要的格式 赋值给变量 specOptions
        findSpecIds:function () {
            axios.get('/specification/findAll.shtml').then(
                function (response) {//response.data=List<Tbspecification>====>[{id:1,"specName":"aaaa"}]
                    for(var i=0;i<response.data.length;i++){
                        var obj = response.data[i];// {id:1,"specName":"aaaa"}
                        app.specOptions.push({"id":obj.id,"text":obj.specName});
                    }
                }
            )
        },

        addTableRow:function () {
            this.entity.customAttributeItems.push({});
        },
        removeTableRow:function (index) {
            //第一个参数:下标值
            //第二个参数:要删除个元素的个数
            this.entity.customAttributeItems.splice(index,1);
        },

        //var obj = {id:1}

        //obj.id=2 obj.name=222
        //var abc = obj['id']

        //json字符串中的text的值 通过逗号拼接返回字符串
        jsonToString:function (list,key) {//key 可以使text  也可以是任意的属性的名称
            //1.将JSON的字符串转成对象 [{}]
            var jsonobj = JSON.parse(list);//[{"id":1,"text":"联想"}]
            //2.循环遍历 对象
            var str="";

            for(var i=0;i<jsonobj.length;i++){
                //3.获取对象中的text属性的值 拼接字符串 返回
                var obj = jsonobj[i];//{"id":1,"text":"联想"}
                str+=obj[key]+",";
            }

            if(str.length>0){
                str = str.substring(0,str.length-1);
            }
            return str;
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);
        this.findBrandIds();
        this.findSpecIds();

    }

})
