package com.pineapple.java.ds;

import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class CollectionsDemo {
    @Test
    public void testCollectionsEmptyList() {
        List ret = Collections.EMPTY_LIST;
//        ret.add("");
//        ret.get(0);
        ret.toArray();
    }
}
