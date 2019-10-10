package redis.ch07.util.set;

import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;

import java.util.UUID;

public class ZSetOperation {
    public static String zintersect(Transaction trans, int ttl, ZParams params, String... sets) {
        return zsetCommon(trans, "zinterstore", ttl, params, sets);
    }

    public static String zunion(Transaction trans, int ttl, ZParams params, String... sets) {
        return zsetCommon(trans, "zunionstore", ttl, params, sets);
    }

    private static String zsetCommon(Transaction trans, String method, int ttl, ZParams params, String... sets) {
        String[] keys = new String[sets.length];
        for (int i = 0; i < sets.length; i++) {
            keys[i] = "idx:" + sets[i];
        }

        String id = UUID.randomUUID().toString();
        try {
            trans.getClass()
                    .getDeclaredMethod(method, String.class, ZParams.class, String[].class)
                    .invoke(trans, "idx:" + id, params, keys);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        trans.expire("idx:" + id, ttl);
        return id;
    }
}
