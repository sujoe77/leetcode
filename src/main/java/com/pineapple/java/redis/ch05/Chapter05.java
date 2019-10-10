package com.pineapple.java.redis.ch05;

import com.pineapple.java.redis.ch05.stat.Statistics;
import redis.clients.jedis.Jedis;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Chapter05 {
    public static final Collator COLLATOR = Collator.getInstance();
    public static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00");

    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final void main(String[] args)
            throws InterruptedException {
        new Chapter05().run();
    }

    public void run() throws InterruptedException {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        Logger logger = new Logger();
        logger.testLogRecent(conn);
        logger.testLogCommon(conn);

        Statistics statistics = new Statistics();
        statistics.testCounters(conn);
        statistics.testStats(conn);
        statistics.testAccessTime(conn);

        IPTable table = new IPTable();
        table.testIpLookup(conn);

        new Maintain().testIsUnderMaintenance(conn);

        Config config = new Config();
        config.testConfig(conn);
    }
}
