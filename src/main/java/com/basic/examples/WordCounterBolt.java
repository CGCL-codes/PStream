package com.basic.examples;

import com.basic.core.Constraints;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * locate com.basic.storm.bolt
 * Created by tj on 2017/5/8.
 */
public class WordCounterBolt extends BaseRichBolt {
    private Map<String, Long> counts = new HashMap<String, Long>();
    private OutputCollector outputCollector;
    private Jedis jedis=null;
    private Timer timer;
    private long boltstatus=0L;

    private String hset;

    public WordCounterBolt(String hset) {
        this.hset = hset;
    }

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        final int thisTaskId = context.getThisTaskIndex();
        this.outputCollector = collector;
        jedis=new Jedis("root2",6379);
        timer=new Timer();

              timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                jedis.hset(hset,String.valueOf(thisTaskId),String.valueOf(boltstatus));
            }
        }, 1,1000);
    }

    @Override
    public void execute(Tuple tuple) {
        boltstatus++;

        String word = tuple.getStringByField(Constraints.wordFileds);
        if (!word.isEmpty()) {
            Long count = counts.get(word);
            if (count == null) {
                count = 0L;
            }
            count++;
            counts.put(word, count);
            outputCollector.emit(tuple,new Values(word,count));
        }
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }


    @Override
    public void cleanup() {
    }

}
