package com.pineapple.java.reflection.proxy.dynamic.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

public class CgLibTest {
    public static void main(String[] args) {
        LogInterceptor logInterceptor = new LogInterceptor();
        LogInterceptor2 logInterceptor2 = new LogInterceptor2();
        Enhancer enhancer = new Enhancer();

        //设置超类，cglib是通过继承来实现的
        enhancer.setSuperclass(UserDao.class);
        // 设置多个拦截器，NoOp.INSTANCE是一个空拦截器，不做任何处理
        enhancer.setCallbacks(new Callback[]{logInterceptor, logInterceptor2, NoOp.INSTANCE});
        enhancer.setCallbackFilter(new DaoFilter());

        // 创建代理类
        UserDao proxy = (UserDao) enhancer.create();
        proxy.select();
        proxy.update();
    }
}

