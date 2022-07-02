package com.tang.seckill.config;

import com.tang.seckill.pojo.User;

public class UserContext {
    //能够避免线程安全问题
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    public static void setUser(User user) {

        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }
}
