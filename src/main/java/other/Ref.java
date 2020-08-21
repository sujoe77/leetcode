package other;

import java.util.ArrayList;
import java.util.List;

public class Ref {
    private final List<String> authors = new ArrayList();
    private final String name;
    private final String org;
    private final int year;
    private final String doi;

    public Ref(String name, String org, int year, String doi) {
        this.name = name;
        this.org = org;
        this.year = year;
        this.doi = doi;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getName() {
        return name;
    }

    public String getOrg() {
        return org;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Ref{" +
                "authors=" + authors +
                ", name='" + name + '\'' +
                ", org='" + org + '\'' +
                ", year=" + year +
                '}';
    }
}
