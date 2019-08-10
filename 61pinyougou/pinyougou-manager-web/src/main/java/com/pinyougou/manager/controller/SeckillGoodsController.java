package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import entity.Result;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsService.findAll();
    }

    @RequestMapping("/findAllFromRedis")
    public List<TbSeckillGoods> findAllFromRedis() {
        return seckillGoodsService.findAllFromRedis();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbSeckillGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                             @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return seckillGoodsService.findPage(pageNo, pageSize);
    }

    /**
     * 增加
     *
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.add(seckillGoods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.update(seckillGoods);
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
    public TbSeckillGoods findOne(@PathVariable(value = "id") Long id) {
        return seckillGoodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            seckillGoodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    /**
     * 条件的分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/search")
    public PageInfo<TbSeckillGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                             @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                             @RequestBody TbSeckillGoods seckillGoods) {
        return seckillGoodsService.findPage(pageNo, pageSize, seckillGoods);
    }


    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * 秒杀商品的审核
     *
     * @param ids
     * @param status 更新的状态
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestBody Long[] ids,String status) {
        try {
            for (Long id : ids) {
                TbSeckillGoods goods = new TbSeckillGoods();
                goods.setId(id);
                goods.setStatus(status);
                //update tb-seckill set stats=1 where id = 1
                seckillGoodsService.updateByPrimaryKeySelective(goods);
            }

            //审核之后 发送消息

            MessageInfo messageInfo = new MessageInfo("TOPIC_SECKILL","Tags_genHtml","seckillGoods_updateStatus",ids, MessageInfo.METHOD_ADD);
            //设置字符编码 UTF-8
            defaultMQProducer.send(new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes(RemotingHelper.DEFAULT_CHARSET)));


            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

}
