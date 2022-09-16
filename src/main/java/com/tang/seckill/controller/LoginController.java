package com.tang.seckill.controller;


import com.tang.seckill.service.IUserService;
import com.tang.seckill.vo.LoginVo;
import com.tang.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private IUserService userService;

    /**
     * 跳转登录页面
     * admin 123456
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    /**
     * 登录功能
     * @param loginVo
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin (@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        return userService.doLogin(loginVo, request, response);
    }
}
