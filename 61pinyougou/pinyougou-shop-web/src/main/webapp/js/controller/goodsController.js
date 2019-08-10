var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        specList: [],//规格的数据
        typeTemplate: {},//模板对象
        brandTextList: [],//品牌的列表数据
        entity: {
            goods: {},
            goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []},
            itemList: []
        },//组合对象
        ids: [],
        image_entity: {color: '', url: ''},//图片的对象
        imagurl: '',//图片路径
        itemCat1List: [],//一级分类列表数组
        itemCat2List: [],//2级分类列表数组
        itemCat3List: [],//3级分类列表数组
        searchEntity: {}
    },
    methods: {
        searchList: function (curPage) {
            axios.post('/goods/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/goods/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },
        //添加商品
        add: function () {
            //获取富文本编辑器中的内容 赋值给一个变量
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/add.shtml', this.entity).then(function (response) {
                if (response.data.success) {
                    alert("成功");
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    alert("成功");
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.goods.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function () {
            //1.从url中获取ID的值
            var id = this.getUrlParam().id;
            //2.传递id的值到后台
            axios.get('/goods/findOne/' + id + '.shtml').then(function (response) {
                //3.获取到组合对象 赋值给变量entity
                app.entity = response.data;
                //3.1 设置富文本编辑器的值
                editor.html(app.entity.goodsDesc.introduction);
                //3.2 转成JSON
                app.entity.goodsDesc.itemImages=JSON.parse( app.entity.goodsDesc.itemImages);
                app.entity.goodsDesc.customAttributeItems=JSON.parse( app.entity.goodsDesc.customAttributeItems);
                app.entity.goodsDesc.specificationItems=JSON.parse( app.entity.goodsDesc.specificationItems);
                for(var i=0;i<app.entity.itemList.length;i++){
                    var objx = app.entity.itemList[i];//{id,title,spec:\{\}}
                    objx.spec=JSON.parse(objx.spec);
                }


            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/goods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //1.模拟创建一个表单
        //2.向表单中添加一个文件
        //3.设置头信息(设置成mutippart-formdata)
        //4.支持跨域
        //5.发送请求  获取后台传递过来的数据  获取里面的message 的值就是URL地址
        upload: function () {


            var formData = new FormData();// FormData 是JS的对象 表单对象


            //第一个file 应该要和controller里面的接收文件的参数名称保持一致
            //第二个file 是js的文件对象 和页面中的ID 保持一致.

            //file.files[0] 获取文件数组中第一个文件对象
            formData.append('file', file.files[0]);

            axios({
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                data: formData,//数据
                method: 'post',
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials: true
            }).then(function (response) {
                if (response.data.success) {
                    //上传成功
                    app.image_entity.url = response.data.message;//赋值图片路径
                }
            })


        },
        //点击保存的时候调用
        addImageEntity: function () {
            //目的就是将图片对象 存储到数组中
            this.entity.goodsDesc.itemImages.push(this.image_entity);

        },
        //页面加载的时候查询一级分类的列表
        findItemCat1List: function () {
            axios.get('/itemCat/findByParentId/0.shtml').then(
                function (response) {
                    app.itemCat1List = response.data;
                }
            )
        },
        /**
         * [
         {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"},
         {"attributeValue":["16G","32G"],"attributeName":"机身内存"}
         ]
         */

        //点击复选框的时候调用 影响变量:specificationItems的值
        // specName :网络
        // specValue:移动4G
        updateChecked: function ($event, specName, specValue) {
            var specificationItems = this.entity.goodsDesc.specificationItems;
            var obj = this.searchObjectByKey(specificationItems, specName, 'attributeName');//从变量specificationItems 根据 规格名 中获取对象
            if (obj != null) {
                //{"attributeValue":["移动3G"],"attributeName":"网络"}
                //添加选项的值
                if ($event.target.checked) {
                    obj.attributeValue.push(specValue);
                } else {
                    obj.attributeValue.splice(obj.attributeValue.indexOf(specValue), 1);

                    //判断如果没有选项值 删除对象
                    if (obj.attributeValue.length == 0) {
                        this.entity.goodsDesc.specificationItems.splice(this.entity.goodsDesc.specificationItems.indexOf(obj), 1);
                    }

                }


            } else {
                //直接添加对象
                this.entity.goodsDesc.specificationItems.push({
                    "attributeValue": [specValue],
                    "attributeName": specName
                });
            }
        },
        searchObjectByKey: function (list, specName, key) {// key 指定是text attributeName a  b c
            for (var i = 0; i < list.length; i++) {
                //{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}
                var objx = this.entity.goodsDesc.specificationItems[i];
                if (objx[key] == specName) {
                    return objx;
                }
            }
            return null;
        },

        //点击复选框的时候调用 目的:循环遍历 specificationItems变量 获取里面的规格名和规格的值 重头到尾重新生成sku的变量的值


        createList: function () {
            this.entity.itemList = [{spec: {}, 'price': 0, 'num': 0, 'status': '0', isDefault: '0'}];

            //[{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}]
            var specificationItemsx = this.entity.goodsDesc.specificationItems;
            for (var i = 0; i < specificationItemsx.length; i++) {

                this.entity.itemList = this.addColumn(this.entity.itemList, specificationItemsx[i].attributeName, specificationItemsx[i].attributeValue);
            }
        },
        /**
         *
         * @param list
         * @param columnName "网络"
         * @param columnValues  ["移动3G","移动4G"]
         * @returns {Array}
         */
        addColumn: function (list, columnName, columnValues) {
            var newList = [];

            for (var i = 0; i < list.length; i++) {
                var oldRow = list[i];//{spec:{},'price':0,'num':0,'status':'0',isDefault:'0'}

                for (var j = 0; j < columnValues.length; j++) {  //["移动3G","移动4G"]
                    var newRow = JSON.parse(JSON.stringify(oldRow));// {spec:{},'price':0,'num':0,'status':'0',isDefault:'0'}
                    newRow.spec[columnName] = columnValues[j];//深克隆  obj.id=12311

                    newList.push(newRow);
                }

            }

            return newList;
        },
        //页面加载的时候调用获取url中的参数的值返回一个JSON对象
        test: function () {
            let jsonobj = this.getUrlParam();

            console.log("id的值为:" + jsonobj.id);
            console.log("name:" + jsonobj.name);

        },
        isChecked:function (specName,specValue) {

            //判断 循环到的规格的选项是否在已有的变量中的存在,如果存在,返回true 否则返回false

            //1.根据规格的名称 查询对象  如果对象不不存在 返回false
            var obj = this.searchObjectByKey(this.entity.goodsDesc.specificationItems,specName,'attributeName');
            //2.如果存在 查询 对象中的数组中是否包含 规格的选项
            if(obj!=null){
                if(obj.attributeValue.indexOf(specValue)!=-1){
                    return true;
                }
            }

            return false;
        }

    },
    watch: {
        //监听某一个变量,并触发函数,发送请求 根据一级分类的ID 查询二级分类的列表数据 赋值给变量 循环遍历展示即可
        'entity.goods.category1Id': function (newVal, oldVal) {
            if (newVal != undefined) {
                axios.get('/itemCat/findByParentId/' + newVal + '.shtml').then(
                    function (response) {
                        app.itemCat2List = response.data;
                    }
                )
            }
        },
        //监听二级分类的变化 触发函数 根据二级分类的ID 查询二级分类下的三级分类列表数据
        'entity.goods.category2Id': function (newVal, oldVal) {
            if (newVal != undefined) {
                axios.get('/itemCat/findByParentId/' + newVal + '.shtml').then(
                    function (response) {
                        app.itemCat3List = response.data;
                    }
                )
            }
        },
        'entity.goods.category3Id': function (newVal, oldVal) {
            if (newVal != undefined) {
                axios.get('/itemCat/findOne/' + newVal + '.shtml').then(
                    function (response) {

                        console.log(response.data.typeId);
                        //app.entity.goods.typeTemplateId=response.data.typeId;//商品分类的对象本身里面的typeId的值
                        //直接给变量赋值的方式 值是改了,但是页面不会渲染.
                        console.log(app.entity.goods.typeTemplateId);//

                        /**
                         * target 指定的是给哪一个对象设置新的属性的值 对应的对象
                         * key 对象中的属性
                         * value 对象中属性的对应的值
                         */
                        app.$set(app.entity.goods, 'typeTemplateId', response.data.typeId);
                    }
                )
            }
        },
        //监听模板的ID的变化 根据ID 获取模板的对象 ,获取对象中的品牌的列表  展示到页面中
        'entity.goods.typeTemplateId': function (newVal, oldVal) {
            if (newVal != undefined) {

                axios.get('/typeTemplate/findOne/' + newVal + '.shtml').then(
                    function (response) {//response.data=typetemplate
                        app.typeTemplate = response.data;//
                        //获取品牌列表 需要转成JSON对象
                        app.brandTextList = JSON.parse(app.typeTemplate.brandIds);
                        //获取模板中的扩展属性的值赋值给变量即可
                        if(app.entity.goods.id!=null){
                            //要更新
                        }else{
                            app.entity.goodsDesc.customAttributeItems = JSON.parse(app.typeTemplate.customAttributeItems);
                        }

                    }
                );

                axios.get('/typeTemplate/findSpecList/' + newVal + '.shtml').then(
                    function (response) {
                        app.specList = response.data;
                    }
                )
            }
        }
    },
    //钩子函数 初始化了事件和
    created: function () {
        this.findItemCat1List();
        this.findOne();
    }

})
