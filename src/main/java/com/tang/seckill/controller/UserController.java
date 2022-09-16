package com.tang.seckill.controller;

import com.tang.seckill.pojo.User;
import com.tang.seckill.rabbitmq.MQSender;
import com.tang.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
//    @Autowired
//    private MQSender mqSender;


    @RequestMapping("/info")
    @ResponseBody
    public RespBean info (User user) {
        return RespBean.success(user);
    }


    /**
     * 测试发送消息
     */
    /**
     * fanout模式
     */
   /* @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq01() {
        mqSender.send("hello, fanout");
    }

    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq02() {
        mqSender.send01("hello, direct_red");
    }

    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq03() {
        mqSender.send02("hello, direct_green");
    }

    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void mq04() {
        mqSender.send03("hello, topic_01");
    }

    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void mq05() {
        mqSender.send04("hello, topic02");
    }*/
}
