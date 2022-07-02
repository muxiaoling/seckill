package com.tang.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tang.seckill.pojo.User;
import com.tang.seckill.vo.LoginVo;
import com.tang.seckill.vo.RespBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 * <p>
 * 乐字节：专注线上IT培训
 * 答疑老师微信：lezijie
 *
 * @author zhoubin
 */
@Service
public interface IUserService extends IService<User> {
	/**
	 * 登录
	 * @param loginVo
	 * @param request
     * @param response
     * @return
	 */
	RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 根据cookie获取用户
	 * @param userTicket
	 * @return
	 */
	User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

	/**
	更改密码
	 */
	RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
