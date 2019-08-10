package com.pinyougou.user.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.pinyougou.user.service.UserService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {


    private TbUserMapper userMapper;

    @Autowired
    public UserServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if (user != null) {
            if (StringUtils.isNotBlank(user.getUsername())) {
                criteria.andLike("username", "%" + user.getUsername() + "%");
                //criteria.andUsernameLike("%"+user.getUsername()+"%");
            }
            if (StringUtils.isNotBlank(user.getPassword())) {
                criteria.andLike("password", "%" + user.getPassword() + "%");
                //criteria.andPasswordLike("%"+user.getPassword()+"%");
            }
            if (StringUtils.isNotBlank(user.getPhone())) {
                criteria.andLike("phone", "%" + user.getPhone() + "%");
                //criteria.andPhoneLike("%"+user.getPhone()+"%");
            }
            if (StringUtils.isNotBlank(user.getEmail())) {
                criteria.andLike("email", "%" + user.getEmail() + "%");
                //criteria.andEmailLike("%"+user.getEmail()+"%");
            }
            if (StringUtils.isNotBlank(user.getSourceType())) {
                criteria.andLike("sourceType", "%" + user.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(user.getNickName())) {
                criteria.andLike("nickName", "%" + user.getNickName() + "%");
                //criteria.andNickNameLike("%"+user.getNickName()+"%");
            }
            if (StringUtils.isNotBlank(user.getName())) {
                criteria.andLike("name", "%" + user.getName() + "%");
                //criteria.andNameLike("%"+user.getName()+"%");
            }
            if (StringUtils.isNotBlank(user.getStatus())) {
                criteria.andLike("status", "%" + user.getStatus() + "%");
                //criteria.andStatusLike("%"+user.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(user.getHeadPic())) {
                criteria.andLike("headPic", "%" + user.getHeadPic() + "%");
                //criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }
            if (StringUtils.isNotBlank(user.getQq())) {
                criteria.andLike("qq", "%" + user.getQq() + "%");
                //criteria.andQqLike("%"+user.getQq()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsMobileCheck())) {
                criteria.andLike("isMobileCheck", "%" + user.getIsMobileCheck() + "%");
                //criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsEmailCheck())) {
                criteria.andLike("isEmailCheck", "%" + user.getIsEmailCheck() + "%");
                //criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getSex())) {
                criteria.andLike("sex", "%" + user.getSex() + "%");
                //criteria.andSexLike("%"+user.getSex()+"%");
            }

        }
        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DefaultMQProducer producer;

    @Value("${template_code}")
    private String templateCode;
    @Value("${sign_name}")
    private String signName;

    @Override
    public void createSmsCode(String phone) {
        /**
         *
         + 点击按钮的时候 发送请求 传递手机号
         + 生成6位数验证码
         + 存储到redis中
         + 发送消息给 (mq)




         +    ===========接收方法================
         + 短信服务 监听消息
         + 获取消息本身(1.手机号,2.模板,3.签名,4 验证码) map:{}
         + 调用阿里大鱼的API 实现发送短信
         */
        try {
            //1.生成随机6位数字的验证码
            String code = (long) ((Math.random() * 9 + 1) * 100000) + "";
            //2.存储到redis中 1.依赖2.配置文件3.redistempalte
            redisTemplate.boundValueOps("USER_REGETER_PREFIX_" + phone).set(code);// set key value
            //3.设置有效期 (expire key second)
            redisTemplate.boundValueOps("USER_REGETER_PREFIX_" + phone).expire(24, TimeUnit.HOURS);//

            //4.发送消息给 (mq) 1.依赖 2.配置文件 3.producer,发送消息
            // 1.手机号,2.模板,3.签名,4 template_param 验证码)
            Map<String, String> map = new HashMap<>();//封装消息的内容的
            map.put("mobile", phone);
            map.put("template_code", templateCode);
            map.put("sign_name", signName);

           /* Map<String,String> map1 = new HashMap<>();
            map1.put("code","1231231");
            JSON.toJSONString(map1);*/
            map.put("param", "{\"code\":\"" + code + "\"}");

            Message message = new Message("SMS_TOPIC", "SEND_MESSAGE_TAG", "createSmsCode", JSON.toJSONString(map).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult send = producer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean isChecked(String phone, String code) {
        //1.判断是否为空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            return false;
        }
        //2.判断是否正确
        String codeFromredis = (String) redisTemplate.boundValueOps("USER_REGETER_PREFIX_" + phone).get();
        if(!code.equals(codeFromredis)){
            return false;
        }
        //3.返回
        return true;
    }

    public static void main(String[] args) {
        System.out.println(Math.random());
        // 1000000 -1
        // 999999
        String s = (long) ((Math.random() * 9 + 1) * 100000) + "";
    }

}
