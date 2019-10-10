package redis.ch07.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Query {
    public final List<List<String>> all = new ArrayList<List<String>>();
    public final Set<String> unwanted = new HashSet<String>();
}
