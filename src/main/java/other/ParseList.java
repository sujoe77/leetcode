package other;

import com.pineapple.java.redis.ch07.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ParseList {
    public static void main(String[] args) throws IOException {
        List<String> lines = FileUtils.readLines(new File("/media/zhou/DATA/zhousu/git/gitLab/myProject/docs/notes/design_list.txt"), StandardCharsets.UTF_8);
        int i = 0;
        Map<String, Object> countMap = new TreeMap<>();
        for (String line : lines) {
            if (i % 6 == 4) {
                processLine(i / 6 +1, countMap, line);
            }
            i++;
        }
        printMap(countMap);
    }

    private static void processLine(int i, Map<String, Object> countMap, String line) {
        String[] symbol = new String[]{"\"", "(", ")", "|", ",", "/", "-", "&", "#", ":", "'", "*", ".", "]"};
        line = line.toLowerCase();
        for(String s : symbol){
            line = line.replace(s, "");
        }
        String[] words = line.split(" ");
        for(String w : words){
            if(w.length() == 0 || StringUtils.isNumeric(w) || w.length() < 3){
                continue;
            }
            if(countMap.keySet().contains(w)){
                ( (List<Integer>)(countMap.get(w))).add(i);
            } else {
                countMap.put(w, new ArrayList(){{
                    add(i);
                }});
            }
        }
    }

    private static void printMap(Map<String, Object> countMap) {
        for(String key : countMap.keySet()){
            System.out.println(key + ":" + ((List)countMap.get(key)).size());
        }
    }
}
