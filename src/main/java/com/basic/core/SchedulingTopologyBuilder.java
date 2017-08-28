package com.basic.core;

import com.basic.core.bolt.CoinBolt;
import com.basic.core.bolt.PredictorBolt;
import com.basic.core.bolt.SplitterBolt;
import org.apache.storm.topology.TopologyBuilder;

/**
 * locate com.basic.core
 * Created by 79875 on 2017/7/18.
 */
public class SchedulingTopologyBuilder extends TopologyBuilder {

    private int schedulingNum=0;

    public void setBalancingScheduling(String UPStreamCompoentID,String UPStreamCompoentIDFields){
        SplitterBolt splitterBolt=new SplitterBolt(UPStreamCompoentID,UPStreamCompoentIDFields);
        CoinBolt coinBolt=new CoinBolt();
        PredictorBolt predictorBolt=new PredictorBolt();
        this.setBolt(Constraints.SPLITTER_BOLT_ID+schedulingNum, splitterBolt, 36).shuffleGrouping(UPStreamCompoentID).allGrouping(Constraints.PREDICTOR_BOLT_ID+schedulingNum);
        this.setBolt(Constraints.COIN_BOLT_ID+schedulingNum, coinBolt, 36).shuffleGrouping(Constraints.SPLITTER_BOLT_ID+schedulingNum, Constraints.coinFileds);
        this.setBolt(Constraints.PREDICTOR_BOLT_ID+schedulingNum, predictorBolt,1).globalGrouping(Constraints.COIN_BOLT_ID+schedulingNum);
        schedulingNum++;
    }

    public int getSchedulingNum() {
        return schedulingNum-1;
    }

}
