package com.basic.core.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * locate com.basic.core.bolt
 * Created by tj on 2017/7/14.
 */
public abstract class AggregatorBolt extends BaseRichBolt {

    private OutputCollector outputCollector;

    public abstract void AggregatorFunc(OutputCollector outputCollector,Tuple tuple);

    public abstract void declareFields(OutputFieldsDeclarer outputFieldsDeclarer);

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector=outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        AggregatorFunc(outputCollector,tuple);
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        declareFields(outputFieldsDeclarer);
    }
}
