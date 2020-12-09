package com.bjtu.redis.spec;

public class FreqCounterSpec extends CounterSpec {
    private String timeType;

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getTimeType() {
        return timeType;
    }
}
