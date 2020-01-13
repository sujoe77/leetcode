package other;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ToMD {
    public static void main(String[] args) throws IOException {
        String content = FileUtils.readFileToString(new File("/media/zhou/DATA/zhousu/git/github_new/DataLab/highscailability.md.html"), StandardCharsets.UTF_8);
        String temp = content;
        StringBuffer sb = new StringBuffer("");
        while (true) {
            int titlePos = temp.indexOf("# ");
            int linkPos = temp.indexOf("<a ");
            if (titlePos < 0 && linkPos < 0) break;
            System.out.println("titlepos : " + titlePos);
            System.out.println("linkPos : " + linkPos);
            if (titlePos >= 0 && linkPos >= 0 && titlePos < linkPos) {
                temp = temp.substring(titlePos);
                int endIndex = temp.indexOf("<");
                sb.append(temp, 0, endIndex).append("<br/>\n");
                temp = temp.substring(endIndex);
            } else if (linkPos >= 0 && (titlePos > linkPos || titlePos < 0)) {
                temp = temp.substring(linkPos);
                temp = temp.substring(temp.indexOf("href") + 6);
                String link = temp.substring(0, temp.indexOf("\""));
                temp = temp.substring(temp.indexOf(">") + 1);
                int endIndex = temp.indexOf("<");
                String name = temp.substring(0, endIndex);
                temp = temp.substring(endIndex);
                sb.append("[").append(name).append("]").append("(").append(link).append(")<br/>\n");
                System.out.println("name is: " + name);
                System.out.println("like is: " + link);
            }
        }
        System.out.println(sb.toString());
        FileUtils.write(new File("/media/zhou/DATA/zhousu/git/github_new/DataLab/highscailability.md"), sb.toString(), StandardCharsets.UTF_8);
    }
}
