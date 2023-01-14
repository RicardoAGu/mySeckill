package com.intern.seckill.config;

import com.intern.seckill.pojo.User;

/**
 * 使用TheadLocal本地线程存放当前请求的用户对象。
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }

}
