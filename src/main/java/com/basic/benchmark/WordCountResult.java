package com.basic.benchmark;

/**
 * Created by 79875 on 2017/3/7.
 */
public class WordCountResult {

    private Long systemMills;
    private Long tupplecount;

    public WordCountResult() {
    }

    public WordCountResult(Long systemMills, Long tupplecount) {
        this.systemMills = systemMills;
        this.tupplecount = tupplecount;
    }

    public Long getSystemMills() {
        return systemMills;
    }

    public void setSystemMills(Long systemMills) {
        this.systemMills = systemMills;
    }

    public Long getTupplecount() {
        return tupplecount;
    }

    public void setTupplecount(Long tupplecount) {
        this.tupplecount = tupplecount;
    }
}
