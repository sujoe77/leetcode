package other.search;

import java.util.HashSet;
import java.util.Set;

public class Words {
    public static Set<String> getWords(String article){
        Set<String> ret = new HashSet<>();
        StringBuffer sb = new StringBuffer();

        for(char c : article.toCharArray()){
            if(isLetter(c)){
                sb.append(c);
            } else {
                if(sb.toString().length() > 1 && !ret.contains(sb.toString())){
                    ret.add(sb.toString());
                }
                sb.delete(0, sb.length());
            }
        }
        return ret;
    }

    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
