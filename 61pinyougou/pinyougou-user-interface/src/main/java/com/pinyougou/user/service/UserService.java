package com.pinyougou.user.service;
import java.util.List;
import com.pinyougou.pojo.TbUser;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService extends CoreService<TbUser> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser User);

	/**
	 * 生成验证码发送消息 发短信
	 * @param phone
	 */
    void createSmsCode(String phone);

	/**
	 * 判断 手机号对应的验证码是否正确
	 * @param phone
	 * @param code
	 * @return
	 */
    boolean isChecked(String phone, String code);
}
