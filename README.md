# DStream

DStream is a popularity-aware differentiated distributed stream processing system, which identifies the popularity of keys in dynamic real-time streams and uses a differentiated scheduling scheme for stream data partitioning. DStream greatly outperforms Storm on skew distributed data in terms of system throughput and the average processing latency.

## Introduction

Traditional distributed stream processing systems, such as S4, Samza, Storm, *etc.* ususally leverage high efficient data parallism for high performance stream processing. Specifically, they create multiple instances for an operator which work in parallel for achieving high system throughput and low processing latency. In these systems, shuffle grouping and key grouping are two basic schemes for partitioning the stream workloads. With shuffle grouping scheme, an instance of an upstream processing element partitions the workloads of the stream among all the downstream instances in a round-robin style. It thus generates an even distribution of the workloads. However, such a scheme suffers the problem of scalability in terms of memory. Each instance potentially needs to keep the states for all the keys in the stream. It is clear that the consumed memory for the application grows linearly with the parallelism level, i.e., the number of instances of the processing element. Given a number of *N* unique keys and a number of *M* word-count instances in the system, the amount of required memory space is *O*(*MN*). It is clear that such a shuffle grouping partitioning scheme is hard to scale for large-scale workloads.

Key grouping uses the hashing based scheme partition the key space nearly evenly among all the downstream instances. It thus guarantees that the tuples with the same key are assigned to the same downstream instance. It is clear, such a partition scheme needs an amount of *O*(*N*) memory space, where *N* is the number of unique keys in the data stream. However, such a scheme leads to heavily load imbalance due to the skewed distribution in various real-world datasets. This results in poor computation resource efficiency and thus greatly degrades the system performance, especailly the system throughput.

We examine the performance of the shuffle grouping scheme in greater detail with the experiment shown in the following figure. In the experiment, the system varies the parallelism level by creating different numbers of downstream word-count instances. With each parallelism level, the system adjusts the source's emitting speed of tuples to put the examination of the system to its performance limit. The result shows that when the parallelism level is low, the system throughput increases with the degree of parallelism level. However, when the number of instances continues to increase, the system throughput stops increasing. The experiment reveals that the memory wall restricts the scalability of the system with shuffle grouping strategy.

![image](https://github.com/DStream-Storm/DStream/raw/master/image/Shufflegrouping.png)

We also carefully examine the performance of the key grouping strategy in the presence of data skewness, we conduct extended experiments for key grouping using seven synthetic datasets (the datasets follow zipf distributions with coefficients varying from 0.5 to 2.0). The results in the following figure show that the throughput decreases greatly with the increase of the level of skewness of the stream data. This reveals that the system load imbalance due to data skewness leads to significant performance degradation. In contrast, the performance of shuffle grouping remains much more stable in the presence of skewness.

![image](https://github.com/DStream-Storm/DStream/raw/master/image/Keygrouping.png)

Based on the above analysis, we find that the key for efficient distributed stream processing is to differentiate the popularity of keys. The key grouping scheme is memory efficient for a large number of rare keys. It however suffers from serious problem of load imbalance caused by the hot keys. On the contrary, the shuffle grouping scheme is able to balance the heavy workloads caused by hot keys. It however does not scale in terms of memory because of the large number of rare keys. Based on this insight, we design DStream and implement DStream on top of Apache Storm. DStream identifies the popularity of keys in the stream data and uses a differentiated partitioning scheme. For hot keys, DStream chooses the shuffle grouping strategy, while for unpopular keys, it selects key grouping. 

A most important part of DStream is the light-weighted hot key predictor. Identifying the popularity of the keys in a distributed stream processing system is challenging. It is also very difficult to meet the rigorous requirements of both computation and memory efficiency and cope with the highly dynamic real-time streams. To address this issue, we design a novel light-weighted predictor for identifying the current hot keys in the real-time data streams. Two factors contribute to the efficiency of this design: 1) we propose a novel probabilistic counting scheme, which is computation and memory efficient so that the predictor based on it can be well integrated in an instance of processing element in a distributed stream processing system; 2) the predictor based on the proposed probabilistic counting scheme can adapt to the popularity changes in the dynamic stream processing environment.

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
storm jar DStream-1.0-SNAPSHOT.jar com.basic.examples.DStreamTopology DStreamTopology *PARALLISM*
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

Set Differentiated Scheduling and hot key predictor in the application topology

```java
builder.setSpout(KAFKA_SPOUT_ID, kafkaSpout, PARALLISM);
builder.setDifferentiatedScheduling(KAFKA_SPOUT_ID,"word");
builder.setBolt(WORDCOUNTER_BOLT_ID,wordCounterBolt, PARALLISM).fieldsGrouping(Constraints.SCHEDULER_BOLT_ID+builder.getSchedulingNum(), Constraints.nohotFileds, new Fields(Constraints.wordFileds)).shuffleGrouping(Constraints.SCHEDULER_BOLT_ID+builder.getSchedulingNum(), Constraints.hotFileds);
builder.setBolt(AGGREGATOR_BOLT_ID, aggregatorBolt, PARALLISM).fieldsGrouping(WORDCOUNTER_BOLT_ID, new Fields(Constraints.wordFileds));
```

## Publications

If you want to know more detailed information, please refer to this paper:

Hanhua Chen, Fan Zhang, Hai Jin. "Popularity-aware Differentiated Distributed Stream Processing on Skewed Steams." in Proceedings of ICNP, 2017.


## Author and Copyright

DStream is developed in Cluster and Grid Computing Lab, Services Computing Technology and System Lab, Big Data Technology and System Lab, School of Computer Science and Technology, Huazhong University of Science and Technology, Wuhan, China by Hanhua Chen (chen@hust.edu.cn), Fan Zhang(zhangf@hust.edu.cn), Hai Jin (hjin@hust.edu.cn), Jie Tan(tjmaster@hust.edu.cn)

Copyright (C) 2017, [STCS & CGCL](http://grid.hust.edu.cn/) and [Huazhong University of Science and Technology](http://www.hust.edu.cn).


