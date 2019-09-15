package sort;

import java.util.LinkedList;
import java.util.List;

public class MergeSort {
    public static void main(String[] args) {
        List<Integer> source = getSourceList();
        List<List<Integer>> listOfList = split(source);
        List<Integer> result = sort(listOfList);
        for (int item : result) {
            System.out.println(item);
        }
    }

    private static List<Integer> getSourceList() {
        List<Integer> source = new LinkedList<>(); //Arrays.asList(new Integer[]{12, 11, 3, 5, 19, 1, 18, 0, 9, 8, 20, 2, 4, 6,15});
        for (int i = 0; i < 1000; i++) {
            source.add(1000 - i);
        }
        return source;
    }

    private static List<List<Integer>> split(List<Integer> source) {
        List<List<Integer>> ret = new LinkedList<>();
        for (final int item : source) {
            ret.add(new LinkedList() {{
                add(item);
            }});
        }
        return ret;
    }

    private static List<Integer> sort(List<List<Integer>> listOfList) {
        if (listOfList.size() == 1) {
            return listOfList.get(0);
        }
        List<List<Integer>> result = new LinkedList<>();
        while (!listOfList.isEmpty()) {
            if (listOfList.size() > 1) {
                List<Integer> merged = merge(listOfList.get(0), listOfList.get(1));
                result.add(merged);
                listOfList.remove(0);
            } else {
                result.add(listOfList.get(0));
            }
            listOfList.remove(0);
        }
        return sort(result);
    }

    private static List<Integer> merge(List<Integer> list1, List<Integer> list2) {
        List<Integer> ret = new LinkedList<>();
        if (list1 == null && list2 == null) {
            return ret;
        } else if (list1 == null || list1.isEmpty()) {
            return list2;
        } else if (list2 == null || list2.isEmpty()) {
            return list1;
        }
        while (0 < list1.size() + list2.size()) {
            int item = list1.isEmpty() ? list2.get(0)
                    : list2.isEmpty() ? list1.get(0)
                    : Math.min(list1.get(0), list2.get(0));
            ret.add(item);
            if (!list1.isEmpty() && item == list1.get(0)) {
                list1.remove(0);
            } else {
                list2.remove(0);
            }
        }
        return ret;
    }
}
