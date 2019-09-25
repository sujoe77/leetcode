package com.pineapple.java.reflection.statics;

import com.pineapple.java.reflection.UserService;
import com.pineapple.java.reflection.UserServiceImpl;

public class Client1 {
    public static void main(String[] args) {
        UserService userServiceImpl = new UserServiceImpl();
        UserService proxy = new UserServiceProxy(userServiceImpl);

        proxy.select();
        proxy.update();
    }
}