package com.basic.core.bolt;

import com.basic.core.Constraints;
import org.apache.hadoop.util.bloom.CountingBloomFilter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * locate com.basic.storm.bolt
 * Created by tj on 2017/5/8.
 */
public class SplitterBolt extends BaseRichBolt {
    private String UPSTREAM_COMPONENT_ID;
    private String UPSTREAM_FIEDLS;
    private OutputCollector collector;
    private CountingBloomFilter bf;

    public SplitterBolt(String UPSTREAM_COMPONENT_ID,String UPSTREAM_FIEDLS) {
        this.UPSTREAM_COMPONENT_ID = UPSTREAM_COMPONENT_ID;
        this.UPSTREAM_FIEDLS=UPSTREAM_FIEDLS;
    }

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.bf = new CountingBloomFilter(16,4,1);
    }

    public void execute(Tuple tuple) {
        if(tuple.getSourceComponent().equals(UPSTREAM_COMPONENT_ID)){
            String word = tuple.getStringByField(UPSTREAM_FIEDLS);
            if(word.length() <= 0) {
                collector.ack(tuple);
                return;
            }
            collector.emit(Constraints.coinFileds, new Values(word));
            Key ky = new Key(word.getBytes());
            if(bf.membershipTest(ky))
                collector.emit(Constraints.hotFileds, tuple, new Values(word));
            else
                collector.emit(Constraints.nohotFileds, tuple, new Values(word));

        }else {
            String key = tuple.getStringByField(Constraints.wordFileds);
            Integer type = tuple.getIntegerByField(Constraints.typeFileds);
            Key hk = new Key(key.getBytes());
            if(!bf.membershipTest(hk) && type.equals(1))
                bf.add(hk);
            if(bf.membershipTest(hk) && type.equals(0))
                bf.delete(hk);
        }
        collector.ack(tuple);
    }

    public void cleanup(){
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(Constraints.coinFileds, new Fields(Constraints.wordFileds));
        declarer.declareStream(Constraints.hotFileds, new Fields(Constraints.wordFileds));
        declarer.declareStream(Constraints.nohotFileds, new Fields(Constraints.wordFileds));
    }

}
