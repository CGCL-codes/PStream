package com.basic.core.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;

/**
 * Created by tj on 2017/3/18.
 * HDFSOperatorUtil
 */
public class HdfsOperationUtil {

    private static Configuration conf = new Configuration();
    private static final String HADOOP_URL="hdfs://192.168.223.202:9000";

    private static FileSystem fs;

    private static DistributedFileSystem hdfs;

    static {
        try {
            FileSystem.setDefaultUri(conf, HADOOP_URL);
            fs = FileSystem.get(conf);
            hdfs = (DistributedFileSystem)fs;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileSystem getFs() {
        return fs;
    }

    public static Configuration getConf() {
        return conf;
    }

    public static void setConf(Configuration conf) {
        HdfsOperationUtil.conf = conf;
    }

}

