package other;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ParseRef {
    public static void main(String[] args) throws IOException {
        List<String> lines = getLines();
        System.out.println("line: " + lines.size());
        List<Ref> allRef = lines.stream().map(getRef()).collect(Collectors.toList());
        allRef.stream().forEach(System.out::println);
        Collection<Map<String, Integer>> temp;
        //temp = getAuthorsCounter(allRef);
        temp = getOrgCounter(allRef);
        Stream<Map.Entry<String, Integer>> sorted = getSortedStream(temp);
        sorted.forEach(author -> System.out.println(author.getKey() + ":" + author.getValue()));
    }

    private static Collection<Map<String, Integer>> getAuthorsCounter(List<Ref> allRef) {
        return allRef.stream().flatMap(r -> r.getAuthors().stream().map(a -> new HashMap<String, Integer>() {
            {
                put(a, 1);
            }
        })).collect(Collectors.toList());
    }

    private static Collection<Map<String, Integer>> getOrgCounter(List<Ref> allRef) {
        return allRef.stream().map(a -> new HashMap<String, Integer>() {
            {
                put(a.getOrg(), 1);
            }
        }).collect(Collectors.toList());
    }

    private static Stream<Map.Entry<String, Integer>> getSortedStream(Collection<Map<String, Integer>> temp) {
        Map<String, Integer> authorList = temp.stream().reduce((r, m) -> {
            String key = m.keySet().iterator().next();
            int count = r.containsKey(key) ? r.get(key) : 0;
            r.put(key, count + 1);
            return r;
        }).get();
        return authorList.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue));
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
        boolean colonBeforeQuote = indexOfColon >= 0 && (indexOfColon < indexOfQuote || indexOfQuote < 0);
        String processedLine = (colonBeforeQuote ? line.substring(0, indexOfColon) : "")
                .trim().replace(" and", ",").replace(", Inc.", " Inc.").replace("et al.", "")
                .replace(" J ", " J. ").replace(" M ", " M. ");
        List<String> names = asList(processedLine.split(","));
        names = names.stream().map(n -> n.trim()).collect(Collectors.toList());
        return names;
    }

    private static String getOrg(String line, String name, String year) {
        int beginIndex = line.indexOf(name) + name.length() + 1;
        line = line.substring(beginIndex);
        int endIndex = line.contains(",") ? line.indexOf(",")
                : isNotEmpty(year) ? line.lastIndexOf(year)
                : line.length();
        String ret = line.substring(0, endIndex).trim();
        ret = ret.startsWith("at ") ? ret.substring(3) : ret;
        List<String> keywords = asList("OSDI", "CIDR", "SIGMOD", "VLDB", "QCon", "HotOS", "PODC", "SoCC", "ICDE", "STOC", "EuroSys", "FAST", "SOSP", "TOCS",
                "NSDI", "ISCA", "MTAGS", "EDBT", "LADIS", "USITS", "INFOCOM", "HPBDC", "PDIS", "RSWeb", "TOPLAS", "SIGCOMM", "PLDI", "COMPSAC", "CSCW", "SIGCOMM", "HotCloud",
                "WSFM", "SE4ML", "WDAG", "FTCS", "JFLA", "TODS", "PLoP", "DAIS", "PODS", "ATC", "LISA", "ECOOP", "AofA", "PAM", "ICIS", "SPA", "PLOS", "NetDB", "NDSS", "DSN", "PGCon", "EMSOFT",
                "O’Reilly", "MIT Press", "arXiv", "Microsoft Research", "blogspot", "twitter.com", "github", "apache", "IBM", "Oracle", "ISBN", "John Wiley & Sons", "amazon");
        for (String keyword : keywords) {
            if (ret.contains(keyword)) {
                return keyword;
            }
        }
        return ret;
    }

    private static String getName(String line) {
        String ret = "";
        if (line.contains("\"")) {
            ret = line.substring(line.indexOf("\"") + 1, line.lastIndexOf('"'));
        } else if (line.contains(":")) {
            int beginIndex = line.indexOf(":") + 1;
            int endIndex = line.substring(beginIndex).indexOf(',');
            ret = line.substring(beginIndex, beginIndex + endIndex);
        } else if (line.contains(",")) {
            ret = line.substring(0, line.indexOf(","));
        }
//        if (ret.trim().endsWith(",")) {
//            ret = ret.substring(0, ret.length() - 1);
//        }
        return ret;
    }

    @Test
    public void testGetAuthor() {
        String line = "[90] \"A Review of the Data Broker Industry: Collection, Use, and Sale of ConsumerData for Marketing Purposes,\" Staff Report, United States Senate Committee on Com‐merce, Science, and Transportation, commerce.senate.gov, December 2013.";
        line = "[30] AppJet, Inc.: \"Etherpad and EasySync Technical Manual,\" github.com, March 26,2011.";
        line = "[17] Martin Fowler: Patterns of Enterprise Application Architecture. Addison Wesley,2002. ISBN: 978-0-321-12742-6";
        Ref ret = getRef(line);
        System.out.println(ret);
    }
}
