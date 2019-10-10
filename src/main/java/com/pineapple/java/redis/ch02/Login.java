package com.pineapple.java.redis.ch02;

import redis.clients.jedis.Jedis;

import java.util.UUID;

public class Login {

    public void testLoginCookies(Jedis conn) throws InterruptedException {
        System.out.println("\n----- testLoginCookies -----");
        String token = UUID.randomUUID().toString();

        Token theToken = new Token();
        theToken.updateToken(conn, token, "username", "itemX");
        System.out.println("We just logged-in/updated token: " + token);
        System.out.println("For user: 'username'");
        System.out.println();

        System.out.println("What username do we get when we look-up that token?");
        String r = theToken.checkToken(conn, token);
        System.out.println(r);
        System.out.println();
        assert r != null;

        System.out.println("Let's drop the maximum number of cookies to 0 to clean them out");
        System.out.println("We will start a thread to do the cleaning, while we stop it later");

        theToken.cleanSession();

        long s = theToken.getLoginCount(conn);
        System.out.println("The current number of sessions still available is: " + s);
        assert s == 0;
    }
}
