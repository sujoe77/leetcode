package com.pineapple.java.algo.search.tries;

import java.util.*;

public class Tries {
    private static final Map<Character, Node> firstChars = new HashMap<>();

    public static Map<String, List<Integer>> search(String article) {
        int index = 0;
        Map<String, List<Integer>> ret = new TreeMap<>();
        Node node = null;
        StringBuffer sb = new StringBuffer();
        article = article + Node.END_NODE.getValue();
        for (char c : article.toCharArray()) {
            if (sb.length() == 0) {
                if ((node = firstChars.get(c)) != null) {
                    sb.append(c);
                }
            } else if (node != null) {
                Map<Character, Node> nextCharSet = node.getNextCharSet();
                if (nextCharSet.values().contains(Node.END_NODE)) {
                    addWordIndex(ret, index - sb.length(), sb.toString());
                }
                if ((node = nextCharSet.get(c)) != null) {
                    sb.append(c);
                } else {
                    sb.delete(0, sb.length());
                }
            }
            index++;
        }
        return ret;
    }

    private static void addWordIndex(Map<String, List<Integer>> ret, int index, String word) {
        if (ret.get(word) == null) {
            ret.put(word, new ArrayList<>());
        }
        ret.get(word).add(index);
    }

    public static void addWord(Collection<String> words) {
        addWord(words.toArray(new String[]{}));
    }

    public static void addWord(String... words) {
        for (String word : words) {
            addWord(word);
        }
    }

    public static void addWord(String word) {
        int i = 0;
        Node last = null;
        Node node = null;
        for (char c : word.toCharArray()) {
            node = new Node(c);
            if (i == 0) {
                if (firstChars.get(c) == null) {
                    firstChars.put(c, node);
                } else {
                    node = firstChars.get(c);
                }
            }
            if (last != null) {
                Node node1 = last.getNextCharSet().get(c);
                if(node1 == null)
                    last.append(node);
                else
                    node = node1;
            }
            last = node;
            i++;
        }
        if (node != null) {
            node.append(Node.END_NODE);
        }
    }
}
