package other;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ParseRef {
    public static void main(String[] args) throws IOException {
        List<String> lines = getLines();
        System.out.println("line: " + lines.size());
        List<Ref> allRef = lines.stream().map(getRef()).collect(Collectors.toList());
        allRef.stream().forEach(System.out::println);
        List<Map<String, Integer>> temp = allRef.stream().flatMap(r -> r.getAuthors().stream().map(a -> new HashMap<String, Integer>() {
            {
                put(a, 1);
            }
        })).collect(Collectors.toList());
        Map<String, Integer> authorList = temp.stream().reduce((r, m) -> {
            String key = m.keySet().iterator().next();
            int count = r.containsKey(key) ? r.get(key) : 0;
            r.put(key, count + 1);
            return r;
        }).get();
        authorList.entrySet().stream().forEach(author -> System.out.println(author.getKey() + ":" + author.getValue()));
    }

    private static Function<String, Ref> getRef() {
        return line -> {
            System.out.println(line);
            Ref ret = getRef(line);
            return ret;
        };
    }

    private static List<String> getLines() throws IOException {
        String folder = "src/main/java/other/ref";
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            String name = "ref_" + (i + 1) + ".txt";
            File file = new File(folder + "/" + name);
            lines.addAll(FileUtils.readLines(file, StandardCharsets.UTF_8));
        }
        return lines;
    }

    private static Ref getRef(String line) {
        line = line.substring(line.indexOf("]") + 1).trim();
        String doi = line.contains("doi:") ? line.substring(line.indexOf("doi:")) : "";
        line = isNotEmpty(doi) ? line.substring(0, line.indexOf("doi")).trim() : line;
        String name = getName(line);
        String year = line.trim().matches("^.*[0-9]{4}\\.$") ? line.substring(line.length() - 5, line.length() - 1) : "";
//        System.out.println("name is: " + name + " year is: " + year);
        String org = getOrg(line, name, year);
        int yearInt = isNotEmpty(year) ? Integer.parseInt(year) : 0;
        Ref ret = new Ref(name, org, yearInt, doi);
        ret.getAuthors().addAll(getAuthors(line));
        return ret;
    }

    private static List<String> getAuthors(String line) {
        int indexOfColon = line.indexOf(":");
        int indexOfQuote = line.indexOf("\"");
        List<String> names = asList((indexOfColon >= 0 && indexOfColon < indexOfQuote ? line.substring(0, indexOfColon) : "").trim().replace(" and", ",").split(","));
        names = names.stream().map(n -> n.trim()).collect(Collectors.toList());
        return names;
    }

    private static String getOrg(String line, String name, String year) {
        int beginIndex = line.indexOf(name) + name.length() + 1;
        line = line.substring(beginIndex);
        int endIndex = line.contains(",") ? line.indexOf(",") : isNotEmpty(year) ? line.lastIndexOf(year) : line.length();
        return line.substring(0, endIndex).trim();
    }

    private static String getName(String line) {
        if (line.contains("\"")) {
            return line.substring(line.indexOf("\"") + 1, line.lastIndexOf('"'));
        } else if (line.contains(":")) {
            int beginIndex = line.indexOf(":") + 1;
            int endIndex = line.substring(beginIndex).indexOf(',');
            return line.substring(beginIndex, beginIndex + endIndex);
        } else if (line.contains(",")) {
            return line.substring(0, line.indexOf(","));
        }
        return "";
    }

    @Test
    public void testGetAuthor() {
        String line = "[90] \"A Review of the Data Broker Industry: Collection, Use, and Sale of ConsumerData for Marketing Purposes,\" Staff Report, United States Senate Committee on Com‐merce, Science, and Transportation, commerce.senate.gov, December 2013.";
        line = "[30] AppJet, Inc.: \"Etherpad and EasySync Technical Manual,\" github.com, March 26,2011.";
        Ref ret = getRef(line);
        System.out.println(ret);
    }
}
