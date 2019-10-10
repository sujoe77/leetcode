package com.pineapple.java.redis.ch07;

import com.pineapple.java.redis.ch07.util.set.ZSetOperation;
import com.pineapple.java.redis.clients.jedis.Jedis;
import com.pineapple.java.redis.clients.jedis.Transaction;
import com.pineapple.java.redis.clients.jedis.ZParams;

import java.util.*;

public class Job {
    public void testIsQualifiedForJob(Jedis conn) {
        System.out.println("\n----- testIsQualifiedForJob -----");
        Job job = new Job();
        job.addJob(conn, "test", "q1", "q2", "q3");
        assert job.isQualified(conn, "test", "q1", "q3", "q2");
        assert !job.isQualified(conn, "test", "q1", "q2");
    }

    public void testIndexAndFindJobs(Jedis conn) {
        System.out.println("\n----- testIndexAndFindJobs -----");
        Job job = new Job();
        job.indexJob(conn, "test1", "q1", "q2", "q3");
        job.indexJob(conn, "test2", "q1", "q3", "q4");
        job.indexJob(conn, "test3", "q1", "q3", "q5");

        assert findJobs(conn, "q1").size() == 0;

        Iterator<String> result = findJobs(conn, "q1", "q3", "q4").iterator();
        assert "test2".equals(result.next());

        result = findJobs(conn, "q1", "q3", "q5").iterator();
        assert "test3".equals(result.next());

        result = findJobs(conn, "q1", "q2", "q3", "q4", "q5").iterator();
        assert "test1".equals(result.next());
        assert "test2".equals(result.next());
        assert "test3".equals(result.next());
    }

    public void addJob(Jedis conn, String jobId, String... requiredSkills) {
        conn.sadd("job:" + jobId, requiredSkills);
    }

    public boolean isQualified(Jedis conn, String jobId, String... candidateSkills) {
        String temp = UUID.randomUUID().toString();
        Transaction trans = conn.multi();
        for (String skill : candidateSkills) {
            trans.sadd(temp, skill);
        }
        trans.expire(temp, 5);
        trans.sdiff("job:" + jobId, temp);

        List<Object> response = trans.exec();
        Set<String> diff = (Set<String>) response.get(response.size() - 1);
        return diff.size() == 0;
    }

    public void indexJob(Jedis conn, String jobId, String... skills) {
        Transaction trans = conn.multi();
        Set<String> unique = new HashSet<String>();
        for (String skill : skills) {
            trans.sadd("idx:skill:" + skill, jobId);
            unique.add(skill);
        }
        trans.zadd("idx:jobs:req", unique.size(), jobId);
        trans.exec();
    }

    public Set<String> findJobs(Jedis conn, String... candidateSkills) {
        String[] keys = new String[candidateSkills.length];
        int[] weights = new int[candidateSkills.length];
        for (int i = 0; i < candidateSkills.length; i++) {
            keys[i] = "skill:" + candidateSkills[i];
            weights[i] = 1;
        }

        Transaction trans = conn.multi();
        String jobScores = ZSetOperation.zunion(trans, 30, new ZParams().weights(weights), keys);
        String finalResult = ZSetOperation.zintersect(trans, 30, new ZParams().weights(-1, 1), jobScores, "jobs:req");
        trans.exec();

        return conn.zrangeByScore("idx:" + finalResult, 0, 0);
    }
}
