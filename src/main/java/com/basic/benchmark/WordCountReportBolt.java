package com.basic.benchmark;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by 79875 on 2017/3/7.
 * 
 */
public class WordCountReportBolt extends BaseRichBolt {
    private Logger logger= LoggerFactory.getLogger(WordCountReportBolt.class);
    private OutputCollector outputCollector;

    private boolean type;
    public WordCountReportBolt(boolean type) {
        this.type = type;
    }

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector=outputCollector;
        logger.info("------------WordCountReportBolt prepare------------");
    }

    public void execute(Tuple tuple) {
        Long currentTimeMills=tuple.getLongByField("timeinfo");
        Long tupplecount=tuple.getLongByField("tuplecount");
        int taskid=tuple.getIntegerByField("taskid");

    
        Timestamp timestamp=new Timestamp(currentTimeMills);
        if(type)
            DataBaseUtil.insertBalancingTupleCount(timestamp,tupplecount,taskid);
        else
            DataBaseUtil.insertPKGTupleCount(timestamp,tupplecount,taskid);
             }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}

