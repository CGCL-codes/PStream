package com.basic.examples;

import com.basic.core.Constraints;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

/**
 * locate com.basic.storm.bolt
 * Created by tj on 2017/5/8.
 */
public class WordCounterBolt extends BaseRichBolt {
    private Map<String, Long> counts = new HashMap<String, Long>();
    private OutputCollector outputCollector;
    private long boltstatus=0L;

    public WordCounterBolt() {
    }

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        final int thisTaskId = context.getThisTaskIndex();
        this.outputCollector = collector;
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
