package com.basic.core.util;

import com.basic.core.inter.DumpRemoveHandler;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.basic.core.Constraints.*;

/**
 * locate com.basic.storm.util
 * Created by tj on 2017/5/6.
 */
public class PredictorHotKeyUtil implements Serializable{

    private static volatile PredictorHotKeyUtil predictorHotKeyUtil = null;

    private SynopsisHashMap<String, BitSet> predictHotKeyMap = new SynopsisHashMap<String, BitSet>();

    private long dumpKeyCount=0L;
(PredictorHotKeyUtil.class);

    private long totalDelayTime=0L;// The total actual delay of the winning key
    private long totalKeyCount=0L;// Statistics the total number of winning keys

    private Random random=new Random();

    public PredictorHotKeyUtil() {
    }

    public static PredictorHotKeyUtil getPredictorHotKeyUtilInstance(){
        if(null == predictorHotKeyUtil)
        {
            synchronized (PredictorHotKeyUtil.class)
            {
                predictorHotKeyUtil=new PredictorHotKeyUtil();
            }
        }
        return predictorHotKeyUtil;
    }

    /**
     *  Throw coins until the coin looks up before the number of coins cast
     *  @return
     */
    public int countCointUtilUp(){
        int rand = (int)(Math.random()*Math.pow(2,Threshold_r));
        int count=Threshold_r-1;
        while(rand == 0 && count < Threshold_l)     //Max length set equal to max length+r;
        {
            rand = (int)(Math.random()*2);
            count++;
        }
        return count;
    }

    /**
     * Hot word attenuation in hot words
     */
    public void SynopsisHashMapAllDump(DumpRemoveHandler dumpRemoveHandler) {
        int dumpsize = (int) (1 / Threshold_p);
        dumpKeyCount++;
        if (dumpKeyCount == dumpsize) {
            //dump all key
            Iterator<Map.Entry<String, BitSet>> iterator = predictHotKeyMap.newEntryIterator();
            while (iterator.hasNext()){
                Map.Entry<String, BitSet> next = iterator.next();
                BitSet bitm = next.getValue();
                String key = next.getKey();
                if(key!=null){
                    long[] lo = bitm.toLongArray();
                    if(lo.length > 0){
                        for(int j=0;j<lo.length - 1;j++){
                            lo[j] = lo[j] >>> 1;
                            lo[j] = lo[j] | (lo[j+1] << 63);
                        }
                        lo[lo.length-1] = lo[lo.length-1] >>> 1;
                    }
                    bitm = BitSet.valueOf(lo);
                    if (bitm.isEmpty()) {
                        iterator.remove();
                        dumpRemoveHandler.dumpRemove(key);
                    }else
                        next.setValue(bitm);
                }
            }
            dumpKeyCount = 0;
        }
    }

    /**
     * New Words in Hot Words Random Dump
     */
    public void SynopsisHashMapRandomDump(DumpRemoveHandler dumpRemoveHandler) {
        int size=predictHotKeyMap.size;
        long startTimeSystemTime=System.currentTimeMillis();
        Iterator<Map.Entry<String, BitSet>> iterator = predictHotKeyMap.newEntryIterator();
        while (iterator.hasNext()){
            Map.Entry<String, BitSet> next = iterator.next();
            if (random.nextDouble()> Threshold_p){
                continue;
            }
            BitSet bitm = next.getValue();
            String key = next.getKey();
            if(key!=null){
                long[] lo = bitm.toLongArray();
                if(lo.length > 0){
                    for(int j=0;j<lo.length - 1;j++){
                        lo[j] = lo[j] >>> 1;
                        lo[j] = lo[j] | (lo[j+1] << 63);
                    }
                    lo[lo.length-1] = lo[lo.length-1] >>> 1;
                }
                bitm = BitSet.valueOf(lo);
                if (bitm.isEmpty()) {
                    iterator.remove();
                    dumpRemoveHandler.dumpRemove(key);
                }else
                    next.setValue(bitm);
            }
        }
    }

    /**
     * PredictorHotKey
     * @param key
     * @param coninCount
     */
    public void PredictorHotKey(String key,int coninCount){
        int count=coninCount-Threshold_r;
        BitSet bitmap=null;
        if(predictHotKeyMap.get(key)!=null)
            bitmap = (BitSet) predictHotKeyMap.get(key);
        else
            bitmap=new BitSet(Threshold_l);

        bitmap.set(coninCount);
        predictHotKeyMap.put(key,bitmap);

        if(bitmap.cardinality() >= 2)
        {
                  }
    }

    /**
     * Simple calculation of predicting hot words
     * @param key
     */
    public void simpleComputPredictorHotKey(String key) {
        int count = countCointUtilUp();
        int dumpsize = (int) (1 / Threshold_p);

        if (count >= Threshold_r) {
            PredictorHotKey(key, count - Threshold_r);
            SynopsisHashMapAllDump(new DumpRemoveHandler() {
                @Override
                public void dumpRemove(String key) {

                }
            });
        }
    }


    public boolean isHotKey(String key){
       if(!predictHotKeyMap.containsKey(key))
           return false;
        if(predictHotKeyMap.get(key).cardinality() >= 2)
            return true;
        else
            return false;
    }

    public long getTotalDelayTime() {
        return totalDelayTime;
    }

    public void setTotalDelayTime(long totalDelayTime) {
        this.totalDelayTime = totalDelayTime;
    }

    public long getTotalKeyCount() {
        return totalKeyCount;
    }

    public void setTotalKeyCount(long totalKeyCount) {
        this.totalKeyCount = totalKeyCount;
    }

    public SynopsisHashMap<String, BitSet> getPredictHotKeyMap() {
        return predictHotKeyMap;
    }

    public void setPredictHotKeyMap(SynopsisHashMap<String, BitSet> predictHotKeyMap) {
        this.predictHotKeyMap = predictHotKeyMap;
    }
}
