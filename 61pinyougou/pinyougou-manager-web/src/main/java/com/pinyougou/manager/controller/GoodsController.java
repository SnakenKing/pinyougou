package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;


    @Reference
    private ItemSearchService itemSearchService;

    @Reference
    private ItemPageService itemPageService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改 接收组合对象
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public Goods findOne(@PathVariable(value = "id") Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除SPU
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            goodsService.delete(ids);
            //删除完成

            // 删除ES中的数据
            //itemSearchService.deleteByIds(ids);

            //发送消息
            MessageInfo messageInfo = new MessageInfo("Goods_Topic","goods_delete_tag","delete",ids,MessageInfo.METHOD_DELETE);


            producer.send(new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(),JSON.toJSONString(messageInfo).getBytes(RemotingHelper.DEFAULT_CHARSET)));




            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {
        //select * from wehre seller_id = ?  admin
        //String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //goods.setSellerId(sellerId);
        return goodsService.findPage(pageNo, pageSize, goods);
    }

    @Autowired
    private DefaultMQProducer producer;

    /**
     * 审核商品 驳回商品
     *
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus/{status}")
    public Result updateStatus(@RequestBody Long[] ids, @PathVariable(name = "status") String status) {
        try {
            goodsService.updateStatus(ids, status);//审核通过了

            if ("1".equals(status)) {
                //1.先获取被审核的商品的数据( 根据SPU的ID 获取被审核到的所有的SKU的列表数据)
               /* List<TbItem> itemList = goodsService.findTbItemListByIds(ids);

                //2.调用搜索服务的方法 (更新数据到ES中)
                    //1.引入依赖
                    //2.引入远程服务
                    //3.调用方法 执行更新动作
                itemSearchService.updateIndex(itemList);*/


                //3.调用静态化服务 生成静态页面
                /*for (Long id : ids) {
                    itemPageService.genItemHtml(id);
                }*/

                //发送消息
                //1.获取到被审核的商品的SKU的列表数据
                List<TbItem> itemList = goodsService.findTbItemListByIds(ids);
                //2.发送消息 消息本身就是SKU的列表数据
                MessageInfo messageInfo = new MessageInfo("Goods_Topic","goods_update_tag","updateStatus",itemList,MessageInfo.METHOD_UPDATE);

                SendResult send = producer.send(new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes(RemotingHelper.DEFAULT_CHARSET)));

                System.out.println(send);

            }


            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

}
