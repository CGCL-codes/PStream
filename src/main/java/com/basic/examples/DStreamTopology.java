package com.basic.examples;

import com.basic.core.Constraints;
import com.basic.core.SchedulingTopologyBuilder;
import com.basic.core.util.MyScheme;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

import java.util.Arrays;

import static com.basic.core.Constraints.SCHEDULER_BOLT_ID;

/**
 * locate com.basic.examples
 * Created by tj on 2017/5/8.
 * Submit stormtopology
 * storm jar DStream-1.0-SNAPSHOT.jar com.basic.examples.DStreamTopology DStreamTopology 8
 */
public class DStreamTopology {
    public static final String KAFKA_SPOUT_ID ="kafka-spout";
    public static final String AGGREGATOR_BOLT_ID= "aggregator-bolt";
    public static final String WORDCOUNTER_BOLT_ID ="wordcountter-bolt";
    public static final String TOPOLOGY_NAME= "keyGroupingBalancing-topology";

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        String zks = "root2:2181,root4:2181,root5:2181";// default zookeeper configuration
        String topic= "tweetswordtopic3";// default kafka topic configuration
        String zkRoot = "/stormkafka"; // default zookeeper root configuration for storm
        String id = "DStreamTopology";// default application ID

        BrokerHosts brokerHosts = new ZkHosts(zks,"/kafka/brokers");// default kafka BrokerHosts
        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
        spoutConf.scheme = new SchemeAsMultiScheme(new MyScheme());
        spoutConf.ignoreZkOffsets = true;
        spoutConf.zkServers = Arrays.asList(new String[] {"root2", "root4", "root5"});
        spoutConf.zkPort = 2181;
        //      spoutConf.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
        spoutConf.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        KafkaSpout kafkaSpout=new KafkaSpout(spoutConf);

        WordCounterBolt wordCounterBolt=new WordCounterBolt();
        MyAggregatorBolt aggregatorBolt=new MyAggregatorBolt();

        SchedulingTopologyBuilder builder=new SchedulingTopologyBuilder();
        Integer numworkers=Integer.valueOf(args[1]);

        builder.setSpout(KAFKA_SPOUT_ID, kafkaSpout, 9);
        builder.setDifferentiatedScheduling(KAFKA_SPOUT_ID,"word");
        builder.setBolt(WORDCOUNTER_BOLT_ID,wordCounterBolt, 36).fieldsGrouping(SCHEDULER_BOLT_ID+builder.getSchedulingNum(), Constraints.nohotFileds, new Fields(Constraints.wordFileds)).shuffleGrouping(SCHEDULER_BOLT_ID+builder.getSchedulingNum(), Constraints.hotFileds);
        builder.setBolt(AGGREGATOR_BOLT_ID, aggregatorBolt, 36).fieldsGrouping(WORDCOUNTER_BOLT_ID, new Fields(Constraints.wordFileds));
        //Topology config
        Config config=new Config();
        config.setNumWorkers(numworkers);//config numworkers
        if(args[0].equals("local")){
            LocalCluster localCluster=new LocalCluster();

            localCluster.submitTopology(TOPOLOGY_NAME,config,builder.createTopology());
            Utils.sleep(50*1000);//50s
            localCluster.killTopology(TOPOLOGY_NAME);
            localCluster.shutdown();
        }else {
            StormSubmitter.submitTopology(args[0],config,builder.createTopology());
        }

    }
}
