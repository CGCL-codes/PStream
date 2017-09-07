# DStream

## Overview

DStream is a popularity-aware differentiated distributed stream processing system, which identifies the popularity of keys in the stream data and uses a differentiated partitioning scheme. 

Traditional distributed stream processing systems usually leverage either a shuffle grouping or a key grouping strategy for partitioning the stream workloads among multiple processing units, leading to notable problems of unsatisfied system throughput and processing latency. We find the key to efficient stream scheduling is the popularity of the stream data. We design DStream, which assigns the workloads with popular keys using shuffle grouping while assigns unpopular ones using key grouping. We also design a novel efficient and light-weighted probabilistic counting scheme for identifying the current hot keys in dynamic real-time streams.


## Structure of DStream

![image](https://github.com/DStream-Storm/DStream/raw/master/image/DStreamStructure.png)

DStream consists of two components: 1) an independent predicting component for detecting potential hot keys, and 2) a scheduling component in each processing element instance. 

The predicting component leverages a novel probabilistic counting scheme to precisely identify the current hot keys in a stream, and achieves probabilistic counting of the tuples associated with a key. The keys likely to be hot ones are detected and recorded in a synopsis of potential hot keys. The synopsis keeps updating along with the stream processing, and identifies the hot keys. The synopsis also uses a popularity decline mechanism to identify the keys which are once hot but now unpopular.

The scheduling component stores the identified hot keys in a space efficient Counting Bloom filter. It quickly decides whether the key of the coming tuple is hot or not, and chooses the preferable scheduling schemes for the tuple with almost no latency.


## How to use?

### Environment

We implement DStream atop Apache Storm (version 1.0.2 or higher), and deploy the system on a cluster. Each machine is equipped with an octa-core 2.4GHz Xeon CPU, 64.0GB RAM, and a 1000Mbps Ethernet interface card. One machine in the cluster serves as the master node to host the Storm Nimbus. The other machines run Storm supervisors.

### Initial Setting

Install Apache Storm (Please refer to http://storm.apache.org/ to learn more).

Install Apace Maven (Please refer to http://maven.apache.org/ to learn more).

Build and package the example

```txt
mvn clean package -Dmaven.test.skip=true
```

Submit the example to the Storm cluster

```txt
storm jar DStream-1.0-SNAPSHOT.jar com.basic.examples.DStreamTopology DStreamTopology 8
```

### Configurations

Configurations including Threshold_r, Threshold_l and Threshold_p in ./src/main/resources/dstream.properties.

```txt
Threshold_r=6 (by default)
Threshold_l=16 (by default)
Threshold_p=0.01 (by default)
```

### Using DStream

Import SchedulingTopologyBuilder in the application source code

```txt
import com.basic.core.SchedulingTopologyBuilder;
```

Build SchedulingTopologyBuilder before the building of the topology

```txt
SchedulingTopologyBuilder builder=new SchedulingTopologyBuilder();
```

Generate SchedulingTopologyBuilder according to the Threshold_r, Threshold_l and Threshold_p (config in ./src/main/resources/dstream.properties).

```java
SchedulingTopologyBuilder builder=new SchedulingTopologyBuilder();
```

Set Differentiated Scheduling in Storm topology

```java
builder.setSpout(KAFKA_SPOUT_ID, kafkaSpout, PARALLISM);
builder.setBalancingScheduling(KAFKA_SPOUT_ID,"word");
builder.setBolt(WORDCOUNTER_BOLT_ID,wordCounterBolt, PARALLISM).fieldsGrouping(Constraints.SPLITTER_BOLT_ID+builder.getSchedulingNum(), Constraints.nohotFileds, new Fields(Constraints.wordFileds)).shuffleGrouping(Constraints.SPLITTER_BOLT_ID+builder.getSchedulingNum(), Constraints.hotFileds);
builder.setBolt(AGGREGATOR_BOLT_ID, aggregatorBolt, PARALLISM).fieldsGrouping(WORDCOUNTER_BOLT_ID, new Fields(Constraints.wordFileds));
```

## Publications

If you want know more detailed information, please refer to this paper:

Hanhua Chen, Fan Zhang, Hai Jin. "Popularity-aware Differentiated Distributed Stream Processing on Skewed Steams." in Proceedings of ICNP, 2017.


## Author and Copyright

DStream is developed in Cluster and Grid Computing Lab, Services Computing Technology and System Lab, Big Data Technology and System Lab, School of Computer Science and Technology, Huazhong University of Science and Technology, Wuhan, China by Hanhua Chen (chen@hust.edu.cn), Fan Zhang(zhangf@hust.edu.cn), Hai Jin (hjin@hust.edu.cn)

Copyright (C) 2017, [STCS & CGCL](http://grid.hust.edu.cn/) and [Huazhong University of Science and Technology](http://www.hust.edu.cn).


