package com.pineapple.java.redis.ch05;

import com.google.gson.Gson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * use zset to save cityId and score
 * <p>
 * use hash to save cityId and city details as json
 * <p>
 * user ip get score, use score to get cityId, use cityId get json
 */
public class IPTable {

    public void testIpLookup(Jedis conn) {
        System.out.println("\n----- testIpLookup -----");
        String cwd = System.getProperty("user.dir");
        File blocks = new File(cwd + "/GeoLiteCity-Blocks.csv");
        File locations = new File(cwd + "/GeoLiteCity-Location.csv");
        if (checkBlock(blocks)) return;
        if (checkLocation(locations)) return;

        importIPs(conn, blocks);

        importCities(conn, locations);

        System.out.println("Let's lookup some locations!");
        for (int i = 0; i < 5; i++) {
            String ip = randomOctet(255) + '.' +
                    randomOctet(256) + '.' +
                    randomOctet(256) + '.' +
                    randomOctet(256);
            System.out.println(Arrays.toString(findCityByIp(conn, ip)));
        }
    }

    private void importCities(Jedis conn, File locations) {
        System.out.println("Importing Location lookups to Redis... (this may take a while)");
        importCitiesToRedis(conn, locations);
        long cities = conn.hlen("cityid2city:");
        System.out.println("Loaded city lookups into Redis:" + cities);
        assert cities > 1000;
        System.out.println();
    }

    private void importIPs(Jedis conn, File blocks) {
        System.out.println("Importing IP addresses to Redis... (this may take a while)");
        importIpsToRedis(conn, blocks);
        long ranges = conn.zcard("ip2cityid:");
        System.out.println("Loaded ranges into Redis: " + ranges);
        assert ranges > 1000;
        System.out.println();
    }

    private boolean checkLocation(File locations) {
        if (!locations.exists()) {
            System.out.println("********");
            System.out.println("GeoLiteCity-Location.csv not found at: " + locations);
            System.out.println("********");
            return true;
        }
        return false;
    }

    private boolean checkBlock(File blocks) {
        if (!blocks.exists()) {
            System.out.println("********");
            System.out.println("GeoLiteCity-Blocks.csv not found at: " + blocks);
            System.out.println("********");
            return true;
        }
        return false;
    }

    public String randomOctet(int max) {
        return String.valueOf((int) (Math.random() * max));
    }

    public void importIpsToRedis(Jedis conn, File file) {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
            int count = 0;
//            String[] line = null;
            List<CSVRecord> records = parser.getRecords();
            for (CSVRecord record : records) {
                String startIp = record.size() > 1 ? record.get(0) : "";
                if (startIp.toLowerCase().indexOf('i') != -1) {
                    continue;
                }
                int score = 0;
                if (startIp.indexOf('.') != -1) {
                    score = ipToScore(startIp);
                } else {
                    try {
                        score = Integer.parseInt(startIp, 10);
                    } catch (NumberFormatException nfe) {
                        continue;
                    }
                }

                String cityId = record.get(2) + '_' + count;
                conn.zadd("ip2cityid:", score, cityId);
                count++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public void importCitiesToRedis(Jedis conn, File file) {
        Gson gson = new Gson();
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
            for (CSVRecord record : parser.getRecords()) {
                if (record.size() < 4 || !Character.isDigit(record.get(0).charAt(0))) {
                    continue;
                }
                String cityId = record.get(0);
                String country = record.get(1);
                String region = record.get(2);
                String city = record.get(3);
                String json = gson.toJson(new String[]{city, region, country});
                conn.hset("cityid2city:", cityId, json);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public String[] findCityByIp(Jedis conn, String ipAddress) {
        int score = ipToScore(ipAddress);
        Set<String> results = conn.zrevrangeByScore("ip2cityid:", score, 0, 0, 1);
        if (results.size() == 0) {
            return null;
        }

        String cityId = results.iterator().next();
        cityId = cityId.substring(0, cityId.indexOf('_'));
        return new Gson().fromJson(conn.hget("cityid2city:", cityId), String[].class);
    }

    public int ipToScore(String ipAddress) {
        int score = 0;
        for (String v : ipAddress.split("\\.")) {
            score = score * 256 + Integer.parseInt(v, 10);
        }
        return score;
    }
}
