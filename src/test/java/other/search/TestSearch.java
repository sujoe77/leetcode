package other.search;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TestSearch {
    @Test
    public void testSearch() throws IOException {
//        Layers.addWord("hello");
//        Layers.addWord("hehe");
//        Layers.addWord("am");
//        Layers.addWord("and");
//        Layers.addWord("I");
        Layers.addWord("The", "could", "to", "book", "all", "that");

        Layers.addWord("book");
        String article = FileUtils.readFileToString(new File("/home/zhou/tmp/test.txt"), StandardCharsets.UTF_8);
        Map<String, List<Integer>> ret = Layers.search(article);
        for(Map.Entry<String, List<Integer>> entry : ret.entrySet()){
            System.out.print(entry.getKey() + ":");
            for(Integer i : entry.getValue()){
                System.out.print(i + ",");
            }
            System.out.println("");
        }
    }
}