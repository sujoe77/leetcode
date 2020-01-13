package other;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DesignTopics {
    public static void main(String[] args) throws IOException {
        List<String> content = FileUtils.readLines(new File("/media/zhou/DATA/zhousu/git/github_new/DataLab/archi/topics.txt"), StandardCharsets.UTF_8);
        PriorityQueue<Topic> queue = new PriorityQueue((Comparator<Topic>) (o1, o2) -> o1.compareTo(o2));
        System.out.println(content.size());
        for (String entry : content) {
            String key = entry.substring(0, entry.indexOf("("));
            key = key.trim();
            String value = entry.substring(entry.indexOf("(") + 1, entry.indexOf(")"));
            queue.add(new Topic(key, Integer.valueOf(value)));
//            System.out.println("putting: " + key + ", " + value);
        }
        System.out.println(content.size());
//        NavigableSet<Topic> set = queue.navigableKeySet();
        while(!queue.isEmpty()){
//            Map.Entry<Topic, Integer> entry = queue.pollFirstEntry();
            Topic topic = queue.poll();
            System.out.println(topic.topic + ", " + topic.count);
//            System.out.println("polling: " + entry.getKey().topic);
        }
    }

    public static class Topic implements Comparable<Topic> {
        private final String topic;
        private final int count;

        public Topic(String topic, int count) {
            this.topic = topic;
            this.count = count;
        }



        @Override
        public int compareTo(Topic o) {
            return o.count - this.count;
        }
    }
}
