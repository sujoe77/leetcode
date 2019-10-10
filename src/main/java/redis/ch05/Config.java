package redis.ch05;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * save config as json in string, key as: "config:" + type + ':' + component
 *
 * get config from string
 */
public class Config {
    public static final Map<String, Jedis> REDIS_CONNECTIONS = new HashMap<String, Jedis>();
    private static final Map<String, Map<String, Object>> CONFIGS = new HashMap<String, Map<String, Object>>();
    private static final Map<String, Long> CHECKED = new HashMap<String, Long>();

    public void testConfig(Jedis conn) {
        System.out.println("\n----- testConfig -----");
        System.out.println("Let's set a config and then get a connection from that config...");
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("db", 15);
        setConfig(conn, "redis", "test", config);

        Jedis conn2 = redisConnection("test");
        System.out.println("We can run commands from the configured connection: " + (conn2.info() != null));
    }

    public void setConfig(Jedis conn, String type, String component, Map<String, Object> config) {
        Gson gson = new Gson();
        conn.set("config:" + type + ':' + component, gson.toJson(config));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getConfig(Jedis conn, String type, String component) {
        int wait = 1000;
        String key = "config:" + type + ':' + component;

        Long lastChecked = CHECKED.get(key);
        if (lastChecked == null || lastChecked < System.currentTimeMillis() - wait) {
            CHECKED.put(key, System.currentTimeMillis());

            String value = conn.get(key);
            Map<String, Object> config = null;
            if (value != null) {
                Gson gson = new Gson();
                config = (Map<String, Object>) gson.fromJson(
                        value, new TypeToken<Map<String, Object>>() {
                        }.getType());
            } else {
                config = new HashMap<String, Object>();
            }

            CONFIGS.put(key, config);
        }

        return CONFIGS.get(key);
    }

    public Jedis redisConnection(String component) {
        Jedis configConn = REDIS_CONNECTIONS.get("config");
        if (configConn == null) {
            configConn = new Jedis("localhost");
            configConn.select(15);
            REDIS_CONNECTIONS.put("config", configConn);
        }

        String key = "config:redis:" + component;
        Map<String, Object> oldConfig = CONFIGS.get(key);
        Map<String, Object> config = getConfig(configConn, "redis", component);

        if (!config.equals(oldConfig)) {
            Jedis conn = new Jedis("localhost");
            if (config.containsKey("db")) {
                conn.select(((Double) config.get("db")).intValue());
            }
            REDIS_CONNECTIONS.put(key, conn);
        }

        return REDIS_CONNECTIONS.get(key);
    }

}
