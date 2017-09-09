package com.basic.core.util;

import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

/**
 * locate com.basic.storm.util
 * Created by 79875 on 2017/5/13.
 */
public class SynopsisHashMapTest {
    private SynopsisHashMap<String,Integer> synopsisHashMap=new SynopsisHashMap();

    @Test
    public void TestIterator(){
        synopsisHashMap.put("a",1);
        synopsisHashMap.put("a",5);
        synopsisHashMap.put("b",5);
        synopsisHashMap.put("c",3);
        synopsisHashMap.put("d",4);
        Iterator<Map.Entry<String, Integer>> entryIterator = synopsisHashMap.newEntryIterator();
        Map.Entry<String, Integer> next1 = entryIterator.next();
        entryIterator.remove();
        entryIterator=entryIterator = synopsisHashMap.newEntryIterator();
        while (entryIterator.hasNext()){
            Map.Entry<String, Integer> next = entryIterator.next();
            System.out.println(next.getKey()+" "+next.getValue());
        }
    }
}
