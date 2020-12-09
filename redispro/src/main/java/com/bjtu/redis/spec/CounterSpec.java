package com.bjtu.redis.spec;



/**
 * @author xuyifan
 */
public class CounterSpec {
    private String counterName;
    private String counterIndex;
    private String type;
    private  String keyFields;
    private  String valueFields;
    private int expireTime;

    public String getCounterIndex() {
        return counterIndex;
    }

    public String getCounterName() {
        return counterName;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public String getType() {
        return type;
    }

    public  String getKeyFields() {
        return keyFields;
    }

    public  String getValueFields() {
        return valueFields;
    }


    public void setCounterIndex(String counterIndex) {
        this.counterIndex = counterIndex;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public void setKeyFields(String keyFields) {
        this.keyFields = keyFields;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValueFields(String valueFields) {
        this.valueFields = valueFields;
    }

}
