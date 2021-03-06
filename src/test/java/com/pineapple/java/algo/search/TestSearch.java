package com.pineapple.java.algo.search;

import com.pineapple.java.algo.search.tries.Tries;
import com.pineapple.java.algo.search.tries.Words;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestSearch {
    @Test
    public void testSearch() throws IOException {
//        Tries.addWord("hello");
//        Tries.addWord("hehe");
//        Tries.addWord("am");
//        Tries.addWord("and");
//        Tries.addWord("I");
//        Tries.addWord("The", "could", "to", "book", "all", "that", "soul", "history", "action", "Anna", "Petersburg");
//
//        Tries.addWord("book");
        String article = getArticle();
        Set<String> words = Words.getWords(article);
        Tries.addWord(words);
//        testWords(words);
        long start = System.currentTimeMillis();
        Map<String, List<Integer>> ret = Tries.search(article);
        long end = System.currentTimeMillis();
        System.out.println("words size: " + words.size());
        System.out.println("entry size: " + ret.size());
        System.out.println("time cost: " + (end - start));
        for(Map.Entry<String, List<Integer>> entry : ret.entrySet()){
            if(entry.getValue().size() < 500){
                continue;
            }
            System.out.print(entry.getKey() + ":");
            System.out.print(entry.getValue().size() + ":");
//            for(Integer i : entry.getValue()){
//                System.out.print(i + ",");
//            }
            System.out.println("");
        }
    }

    private void testWords(Set<String> words) {
        for(String word : words){
            Set<String> keys = Tries.search(word).keySet();
            if(!keys.contains(word)){
                System.out.println(word + " not found!");
            }
        }
    }

    @Test
    public void  testGetWords() throws IOException {
        Set<String> ret = Words.getWords(getArticle());
        System.out.println(ret.size());
    }

    private String getArticle() throws IOException {
        return FileUtils.readFileToString(new File("/home/zhou/下载/warAndPeace.txt"), StandardCharsets.UTF_8);
    }
}