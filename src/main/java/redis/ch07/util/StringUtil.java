package redis.ch07.util;

import redis.ch07.entity.Query;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static final Set<String> STOP_WORDS = new HashSet<String>();
    public static final Pattern WORDS_RE = Pattern.compile("[a-z']{2,}");
    public static final Pattern QUERY_RE = Pattern.compile("[+-]?[a-z']{2,}");

    public static Set<String> tokenize(String content) {
        Set<String> words = new HashSet<String>();
        Matcher matcher = WORDS_RE.matcher(content);
        while (matcher.find()) {
            String word = matcher.group().trim();
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                words.add(word);
            }
        }
        return words;
    }

    public static Query parse(String queryString) {
        Query query = new Query();
        Set<String> current = new HashSet<String>();
        Matcher matcher = QUERY_RE.matcher(queryString.toLowerCase());
        while (matcher.find()) {
            String word = matcher.group().trim();
            char prefix = word.charAt(0);
            if (prefix == '+' || prefix == '-') {
                word = word.substring(1);
            }

            if (word.length() < 2 || STOP_WORDS.contains(word)) {
                continue;
            }

            if (prefix == '-') {
                query.unwanted.add(word);
                continue;
            }

            if (!current.isEmpty() && prefix != '+') {
                query.all.add(new ArrayList<String>(current));
                current.clear();
            }
            current.add(word);
        }

        if (!current.isEmpty()) {
            query.all.add(new ArrayList<String>(current));
        }
        return query;
    }

    public static long stringToScore(String string) {
        return stringToScore(string, false);
    }

    public static long stringToScore(String string, boolean ignoreCase) {
        if (ignoreCase) {
            string = string.toLowerCase();
        }

        List<Integer> pieces = new ArrayList<Integer>();
        for (int i = 0; i < Math.min(string.length(), 6); i++) {
            pieces.add((int) string.charAt(i));
        }
        while (pieces.size() < 6) {
            pieces.add(-1);
        }

        long score = 0;
        for (int piece : pieces) {
            score = score * 257 + piece + 1;
        }

        return score * 2 + (string.length() > 6 ? 1 : 0);
    }

    public static long stringToScoreGeneric(String string, Map<Integer, Integer> mapping) {
        int length = (int) (52 / (Math.log(mapping.size()) / Math.log(2)));

        List<Integer> pieces = new ArrayList<Integer>();
        for (int i = 0; i < Math.min(string.length(), length); i++) {
            pieces.add((int) string.charAt(i));
        }
        while (pieces.size() < 6) {
            pieces.add(-1);
        }

        long score = 0;
        for (int piece : pieces) {
            int value = mapping.get(piece);
            score = score * mapping.size() + value + 1;
        }

        return score * 2 + (string.length() > 6 ? 1 : 0);
    }
}
