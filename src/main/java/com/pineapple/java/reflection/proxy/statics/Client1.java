package com.pineapple.java.reflection.proxy.statics;

import com.pineapple.java.reflection.proxy.UserService;
import com.pineapple.java.reflection.proxy.UserServiceImpl;

public class Client1 {
    public static void main(String[] args) {
        UserService userServiceImpl = new UserServiceImpl();
        UserService proxy = new UserServiceProxy(userServiceImpl);

        proxy.select();
        proxy.update();
    }
}