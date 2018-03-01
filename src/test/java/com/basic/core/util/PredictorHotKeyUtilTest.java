package com.basic.core.util;

import com.basic.core.Constraints;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.BitSet;

/**
 * locate com.basic.storm.util
 * Created by 79875 on 2017/5/6.
 */
public class PredictorHotKeyUtilTest {
    private PredictorHotKeyUtil predictorHotKeyUtil=new PredictorHotKeyUtil();
    private static final Logger LOG = LoggerFactory.getLogger(PredictorHotKeyUtilTest.class);
    @Test
    public void coinCountUtilUp() throws Exception {
        int i = predictorHotKeyUtil.countCointUtilUp();
        System.out.println(i);
    }

    @Test
    public void TestCoinRadnodm(){
        int coin_up=0,coin_down=0;
        for(int i=0;i<100000;i++){
            int rand = (int)(Math.random()*2);
            if(rand==0)
                coin_up++;
            else coin_down++;
        }
        System.out.println(coin_up);
        System.out.println(coin_down);
    }

    @Test
    public void changePositon(){
        BitSet bitm =new BitSet(Constraints.Threshold_l);
        bitm.set(6);
        bitm.set(5);
        System.out.println(bitm);
        byte[] bytes = bitm.toByteArray();
        long[] longs = bitm.toLongArray();
        System.out.println(bytes[0]);
        System.out.println(longs[0]);
        int i = bytes[0] >>> 1;
    }

    @Test
    public void changePositon2(){
        BitSet bitm =new BitSet(16);
        bitm.set(6);
        bitm.set(5);
        System.out.println(bitm);
        long[] lo = bitm.toLongArray();
        if(lo.length > 0){
            for(int j=0;j<lo.length - 1;j++){
                lo[j] = lo[j] >>> 1;
                lo[j] = lo[j] | (lo[j+1] << 63);
            }
            lo[lo.length-1] = lo[lo.length-1] >>> 1;
        }
        bitm = BitSet.valueOf(lo);
        System.out.println(bitm.size());
        System.out.println(bitm);
    }


    @Test
    public void testBitSet(){
        BitSet bitm =new BitSet();
        bitm.set(6);
        bitm.set(5);
        //bitm.set(65);
        System.out.println(bitm.size());
    }

    @Test
    public void Main() throws Exception {
        String inputFile="/user/root/flinkwordcount/input/resultTweets.txt";
        FileSystem fs = HdfsOperationUtil.getFs();
        FSDataInputStream dataInputStream = fs.open(new Path(inputFile));
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(dataInputStream));
        long startTimeSystemTime= System.currentTimeMillis();
        String text=null;
        while ((text=bufferedReader.readLine())!=null){
            predictorHotKeyUtil.simpleComputPredictorHotKey(text);
        }
        long endTimeSystemTime = System.currentTimeMillis();
        LOG.info("startTime:"+new Timestamp(startTimeSystemTime));
        LOG.info("endTime:"+new Timestamp(endTimeSystemTime));
        long timelong = (endTimeSystemTime-startTimeSystemTime) / 1000;
        LOG.info("totalTime:"+timelong+" s"+"------or------"+timelong/60+" min");
        System.exit(0);
    }

}
