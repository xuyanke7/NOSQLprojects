package com.bjtu.redis;

import com.bjtu.redis.spec.*;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;


/*基本操作封装
*
* 基本操作封装之NUM
* public void incrOp()
* public void decrOp()
*
* 基本操作封装之STR
* public void strOp()
*
* 基本操作封装之expire time
* public void expireT()
*
* 基本封装操作之LIST
* public void listOp()
*
* 基本封装操作之SET
* public void setOp()
* public void setOpRem()
*
*
* 基本操作封装之FREQ
*
* public void showFreqOp()
* public void incrFreqOp()
* public void userEnterTime()
* public void userIncrFreq()

 */

public class BasicOperation {
    private final Jedis jedis = JedisInstance.getInstance().getResource();
    private NumCounterSpec numCounterSpec;
    private StrCounterSpec strCounterSpec;
    private ListCounterSpec listCounterSpec;
    private SetCounterSpec setCounterSpec;
    private FreqCounterSpec freqCounterSpec;
    public BasicOperation(){}
    public BasicOperation(NumCounterSpec spec){
        this.numCounterSpec = spec;
    }
    public BasicOperation(StrCounterSpec spec){
        this.strCounterSpec = spec;
    }
    public BasicOperation(ListCounterSpec spec){
        this.listCounterSpec = spec;
    }
    public BasicOperation(SetCounterSpec spec){
        this.setCounterSpec = spec;
    }
    public BasicOperation(FreqCounterSpec spec){
        this.freqCounterSpec = spec;
    }



    public void operation(String CounterName) {
        switch(CounterName){
            case "increaseCounter":incrOp();break;
            case "decreaseCounter":decrOp();break;
            case "setStrCounter":strOp();break;
            case "addListCounter":listOp();break;
            case "addSetCounter":setOp();break;
            case "remSetCounter":setOpRem();break;
            case "showFreqCounter":showFreqOp();break;
            case "incrFreqCounter":incrFreqOp();break;
            case "userEnterTimeCounter":userEnterTime();break;
            case "userIncrFreqCounter":userIncrFreq();break;
            default:break;
        }
    }
    //基本操作封装之NUM
    public void incrOp(){
        String key = numCounterSpec.getKeyFields();
        String value = numCounterSpec.getValueFields();
        long valueLong = Long.parseLong(value);
        //有效的key且自增value有效
        if(jedis.exists(key)&&value!=null){
            //进行自增操作
            jedis.incrBy(key,valueLong);
            System.out.println("key:"+key+"自增了"+value+",现在值为"+jedis.get(key));
        }
        //有效key但自增value无效
        else if(jedis.exists(key)&&value==null){
            System.out.println("无效自增操作，key："+key+"当前值为："+jedis.get(key));
        }
        //key不存在但为有效自增值
        else if(!jedis.exists(key)&&value!=null){
            jedis.setnx(key,value);
            System.out.println("该key不存在,默认创建该key，key"+key+"当前值为"+value);
        }
        //key不存在且自增值无效
        else{
            System.out.println("无效自增操作");
        }
    }
    public void decrOp(){
        String key = numCounterSpec.getKeyFields();
        String value = numCounterSpec.getValueFields();
        long valueLong = Long.parseLong(value);
        //有效的key且自减value有效
        if(jedis.exists(key)&&value!=null){
            //进行自建操作
            jedis.incrBy(key,valueLong);
            System.out.println("key:"+key+"自减了"+value+",现在值为"+jedis.get(key));
        }
        //有效key但自减value无效
        else if(jedis.exists(key)&&value==null){
            System.out.println("无效自减操作，key："+key+"当前值为："+jedis.get(key));
        }
        //key不存在但为有效自减值
        else if(!jedis.exists(key)&&value!=null){
            jedis.setnx(key,value);
            System.out.println("该key不存在,默认创建该key，key"+key+"当前值为"+value);
        }
        //key不存在且自减值无效
        else{
            System.out.println("无效自减操作");
        }
    }

    //基本操作封装之STR
    public void strOp(){
        String key = strCounterSpec.getKeyFields();
        String value = strCounterSpec.getValueFields();
        //有效的key且value有效
        if(jedis.exists(key)&&value!=null){
            //将key赋值value
            jedis.set(key,value);
            System.out.println("key:"+key+"设置值为"+jedis.get(key));
        }
        //有效key但value无效
        else if(jedis.exists(key)&&value==null){
            System.out.println("无效赋值操作，key："+key+"当前值为："+jedis.get(key));
        }
        //key不存在但value有效
        else if(!jedis.exists(key)&&value!=null){
            jedis.setnx(key,value);
            System.out.println("该key不存在,默认创建该key，key"+key+"当前赋值为"+value);
        }
        //key不存在且value无效
        else{
            System.out.println("无效操作");
        }
    }

    //基本操作封装之expire time
    public void expireT(String key,int time){
        //如果key存在且过期时间有效
        if(jedis.exists(key)&&time>0){
            //存在过期时间
            if(jedis.ttl(key)>=0){
                jedis.expire(key, time);
                System.out.println("key:"+key+"修改过期时间为"+jedis.ttl(key));
            }
            //key为永久的
            else if(jedis.ttl(key)==-1){
                jedis.expire(key,time);
                System.out.println("key:"+key+"设置过期时间为"+jedis.ttl(key));
            }
            //key已经过期
            else if(jedis.ttl(key)==-2){
                System.out.println("key:"+key+"已过期");
            }
        }
        //key存在但过期时间无效
        else if(jedis.exists(key)&&time<=0){
            System.out.println("无效过期时间");
        }
        //key不存在
        else {
            System.out.println("key"+key+"不存在");
        }
    }

    //基本封装操作之LIST
    public void listOp(){
        String key = listCounterSpec.getKeyFields();
        String value = listCounterSpec.getValueFields();
        //有效的key且value有效
        if(jedis.exists(key)&&value!=null){
            //list添加新value
            jedis.lpush(key,value);
            System.out.println("key:"+key+"列表添加新元素:"+value);
        }
        //有效key但value无效
        else if(jedis.exists(key)&&value==null){
            System.out.println("添加值为空，操作无效");
        }
        //key不存在但value有效
        else if(!jedis.exists(key)&&value!=null){
            jedis.lpush(key,value);
            System.out.println("该key不存在,默认创建该key，key"+key+"添加第一个元素为"+value);
        }
        //key不存在且value无效
        else{
            System.out.println("无效操作");
        }
    }

    //基本封装操作之SET
    public void setOp(){
        String key = setCounterSpec.getKeyFields();
        String value = setCounterSpec.getValueFields();
        //有效的key且value有效
        if(jedis.exists(key)&&value!=null){
            //向名称为key的set中添加value
            jedis.sadd(key,value);
            System.out.println("key:"+key+"添加元素"+value);
        }
        //有效key但value无效
        else if(jedis.exists(key)&&value==null){
            System.out.println("添加的值无效，key："+key+"输出当前元素");
            for (String s:jedis.smembers(key)
                 ) {
                System.out.println(s);
            }
        }
        //key不存在但value有效
        else if(!jedis.exists(key)&&value!=null){
            jedis.sadd(key,value);
            System.out.println("该key不存在,创建该key，key"+key+"添加元素"+value);
        }
        //key不存在且value无效
        else{
            System.out.println("无效操作");
        }
    }
    public void setOpRem(){
        String key = setCounterSpec.getKeyFields();
        String value = setCounterSpec.getValueFields();
        //有效的key且value有效
        if(jedis.exists(key)&&value!=null){
            //向名称为key的set中删除value
            jedis.srem(key,value);
            System.out.println("key:"+key+"删除元素"+value);
        }
        //有效key但value无效
        else if(jedis.exists(key)&&value==null){
            System.out.println("删除的值无效，key："+key);
        }
        //key不存在
        else if(!jedis.exists(key)){
            System.out.println("该key不存在,删除操作无效");
        }
    }

    //基本封装操作之ZSET
    public void zsetOpAdd(String key,double score,String value){
        //有效的key且value有效
        if(jedis.exists(key)&&value!=null){
            //向名称为key的zset中添加value
            jedis.zadd(key,score,value);
            System.out.println("key:"+key+"添加元素"+value);
        }
        //有效key但value无效
        else if(jedis.exists(key)&&value==null){
            System.out.println("添加的值无效，key："+key);
        }
        //key不存在但value有效
        else if(!jedis.exists(key)&&value!=null){
            jedis.zadd(key,score,value);
            System.out.println("该key不存在,创建该key，key"+key+"添加元素"+value);
        }
        //key不存在且value无效
        else{
            System.out.println("无效操作");
        }
    }

    //FREQ封装
    public void showFreqOp(){
        String key = freqCounterSpec.getKeyFields();
        System.out.println("时间段 "+key+" ,统计个数 "+jedis.hget("freqtest",key));
    }
    public void incrFreqOp(){
        //key为新添加的时间
        String key = freqCounterSpec.getKeyFields();
        String value = freqCounterSpec.getValueFields();
        long valueLong = Long.parseLong(value);
        /*
        如果该key的时间在freqtest的时间段里，那么该时间段加value
         */
        for (String s :
                jedis.hgetAll("freqtest").keySet()) {
            long preValue = Long.parseLong(jedis.hget("freqtest",s));
            //对时间key进行处理，判断是否在keyset中，如果在，该keyset的值加valueLOng
            if(judgeTimeIsOnFreq(key,s)){
                jedis.hset("freqtest",s,String.valueOf(preValue+valueLong));
                System.out.println("在"+key+"时,counter增加了"+valueLong+"，时间段"+s+"计数原来为"+preValue+"，现在为"+jedis.hget("freqtest",s));
            }
        }

        ArrayList<CounterSpec> freqList = new ArrayList<CounterSpec>();
    }

    public void userEnterTime(){
        String key = setCounterSpec.getKeyFields();
        String value = setCounterSpec.getValueFields();
        //有效的key且value有效
        if(jedis.exists(key)&&value!=null){
            //向名称为key的set中添加value
            jedis.sadd(key,value);
            System.out.println("用户"+key+"新进入时间为"+value);
            System.out.println("用户进入时间列表：");
            for (String s:jedis.smembers(key)
            ) {
                System.out.println(s);
            }
        }
        //有效key但value无效
        else if(jedis.exists(key)&&value==null){
            System.out.println("进入时间无效，用户："+key+"进入时间列表");
            for (String s:jedis.smembers(key)
            ) {
                System.out.println(s);
            }
        }
        //key不存在但value有效
        else if(!jedis.exists(key)&&value!=null){
            jedis.sadd(key,value);
            System.out.println("该用户不存在,创建该用户，用户"+key+"新进入时间"+value);
        }
        //key不存在且value无效
        else{
            System.out.println("无效操作");
        }
    }

    public void userIncrFreq(){
        String key = freqCounterSpec.getKeyFields();
        String value = freqCounterSpec.getValueFields();
        /*
        如果该key的时间在freqtest的时间段里，那么该时间段加value
         */
        for (String s :
                jedis.hgetAll("userFreqList").keySet()) {
            long preValue = Long.parseLong(jedis.hget("userFreqList",s));
            //对时间key进行处理，判断是否在keyset中，如果在，该keyset的值加valueLOng
            if(judgeTimeIsOnFreq(key,s)){
                jedis.hset("userFreqList",s,String.valueOf(preValue+1));
                System.out.println("在时间段"+s+"内，用户"+value+"进入了一次，该时间段该用户进入"+Long.parseLong(jedis.hget("userFreqList",s))+"次");
            }
        }
    }


    public boolean judgeTimeIsOnFreq(String timeStr,String freqStr){

        long timeLong = Long.parseLong(timeStr);
        long s1=Long.parseLong(freqStr.substring(0,8));
        long s2=Long.parseLong(freqStr.substring(9,17));

        return (timeLong>=s1 && timeLong<=s2);
    }


}
