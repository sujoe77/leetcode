package com.pineapple.java.redis.ch07;

import com.pineapple.java.redis.ch07.search.Index;
import com.pineapple.java.redis.ch07.util.StringUtil;
import com.pineapple.java.redis.ch07.util.set.SetOperation;
import com.pineapple.java.redis.clients.jedis.Jedis;

public class Chapter07 {
    public static String CONTENT = "this is some random content, look at how it is indexed.";

    static {
        for (String word :
                ("able about across after all almost also am among " +
                        "an and any are as at be because been but by can " +
                        "cannot could dear did do does either else ever " +
                        "every for from get got had has have he her hers " +
                        "him his how however if in into is it its just " +
                        "least let like likely may me might most must my " +
                        "neither no nor not of off often on only or other " +
                        "our own rather said say says she should since so " +
                        "some than that the their them then there these " +
                        "they this tis to too twas us wants was we were " +
                        "what when where which while who whom why will " +
                        "with would yet you your").split(" ")) {
            StringUtil.STOP_WORDS.add(word);
        }
    }

    public static final void main(String[] args) {
        Jedis conn = new Jedis("localhost");
        conn.select(15);
        conn.flushDB();

        new Index().testIndexDocument(conn);
        new SetOperation().testSetOperations(conn);
        Search search = new Search();
        search.testParseQuery(conn);
        search.testParseAndSearch(conn);
        search.testSearchWithSort(conn);
        search.testSearchWithZsort(conn);
        conn.flushDB();

        search.testStringToScore(conn);
        new Ad().testIndexAndTargetAds(conn);
        Job job = new Job();
        job.testIsQualifiedForJob(conn);
        job.testIndexAndFindJobs(conn);
    }
}
