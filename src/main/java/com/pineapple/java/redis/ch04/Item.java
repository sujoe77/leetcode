package com.pineapple.java.redis.ch04;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Item {
    public static final String MARKET = "market:";
    public static final String USERS = "users:";
    public static final String INVENTORY = "inventory:";
    public static final String SELLER = "userX";
    public static final String BUYER = "userY";

    public void testListItem(Jedis conn, boolean nested) {
        if (!nested) {
            System.out.println("\n----- testListItem -----");
        }

        System.out.println("We need to set up just enough state so that a user can list an item");
        String seller = SELLER;
        String item = "itemX";
        conn.sadd("inventory:" + seller, item);

        listInventory(conn.smembers("inventory:" + seller));

        System.out.println("Listing the item...");
        boolean l = listItem(conn, item, seller, 10);
        System.out.println("Listing the item succeeded? " + l);
        assert l;

        listMarket(conn);
    }

    private void listMarket(Jedis conn) {
        Set<Tuple> r = conn.zrangeWithScores(MARKET, 0, -1);
        System.out.println("The market contains:");
        for (Tuple tuple : r) {
            System.out.println("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert r.size() > 0;
    }

    public boolean listItem(Jedis conn, String itemId, String sellerId, double price) {
        String inventory = INVENTORY + sellerId;
        String item = itemId + '.' + sellerId;
        long end = System.currentTimeMillis() + 5000;

        while (System.currentTimeMillis() < end) {
            conn.watch(inventory);
            if (!conn.sismember(inventory, itemId)) {
                conn.unwatch();
                return false;
            }

            List<Object> results = moveFromInventoryToMarket(conn, itemId, price, inventory, item);
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            if (results == null) {
                continue;
            }
            return true;
        }
        return false;
    }

    private List<Object> moveFromInventoryToMarket(Jedis conn, String itemId, double price, String inventory, String item) {
        Transaction trans = conn.multi();
        trans.zadd(MARKET, price, item);
        trans.srem(inventory, itemId);
        return trans.exec();
    }

    public void testPurchaseItem(Jedis conn) {
        System.out.println("\n----- testPurchaseItem -----");
        testListItem(conn, true);

        String userY = prepareUserY(conn);
        Map<String, String> r;

        System.out.println("Let's purchase an item");
        boolean p = purchaseItem(conn, BUYER, "itemX", SELLER, 10);
        System.out.println("Purchasing an item succeeded? " + p);
        assert p;

        checkAfter(conn, userY);
    }

    private void checkAfter(Jedis conn, String userY) {
        Map<String, String> r;
        r = conn.hgetAll(userY);
        System.out.println("Their money is now:");
        for (Map.Entry<String, String> entry : r.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        assert r.size() > 0;

        String buyer = BUYER;
        Set<String> i = conn.smembers("inventory:" + buyer);
        System.out.println("Their inventory is now:");
        for (String member : i) {
            System.out.println("  " + member);
        }
        assert i.size() > 0;
        assert i.contains("itemX");
        assert conn.zscore(MARKET, "itemX.userX") == null;
    }

    private String prepareUserY(Jedis conn) {
        System.out.println("We need to set up just enough state so a user can buy an item");
        String buyerKey = "users:buyerKey";
        conn.hset(buyerKey, "funds", "125");
        Map<String, String> r = conn.hgetAll(buyerKey);
        System.out.println("The user has some money:");
        for (Map.Entry<String, String> entry : r.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        assert r.size() > 0;
        assert r.get("funds") != null;
        System.out.println();
        return buyerKey;
    }


    public boolean purchaseItem(Jedis conn, String buyerId, String itemId, String sellerId, double lprice) {
        String buyerKey = USERS + buyerId;
        String sellerKey = USERS + sellerId;
        String item = itemId + '.' + sellerId;
        String buyerInventory = INVENTORY + buyerId;
        long end = System.currentTimeMillis() + 10000;

        while (System.currentTimeMillis() < end) {
            conn.watch(MARKET, buyerKey);

            double price = conn.zscore(MARKET, item);
            double funds = Double.parseDouble(conn.hget(buyerKey, "funds"));
            if (price != lprice || price > funds) {
                conn.unwatch();
                return false;
            }

            List<Object> results = doPurchase(conn, itemId, buyerKey, sellerKey, item, buyerInventory, price);
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            if (results == null) {
                continue;
            }
            return true;
        }

        return false;
    }

    private List<Object> doPurchase(Jedis conn, String itemId, String buyerKey, String sellerKey, String item, String buyerInventory, double price) {
        Transaction trans = conn.multi();
        trans.hincrBy(sellerKey, "funds", (int) price);
        trans.hincrBy(buyerKey, "funds", (int) -price);
        trans.sadd(buyerInventory, itemId);
        trans.zrem(MARKET, item);
        return trans.exec();
    }

    private void listInventory(Set<String> items) {
        System.out.println("The user's inventory has:");
        for (String item : items) {
            System.out.println("  " + item);
        }
        assert items.size() > 0;
        System.out.println();
    }
}
