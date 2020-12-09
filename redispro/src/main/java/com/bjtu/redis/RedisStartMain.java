package com.bjtu.redis;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bjtu.redis.spec.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author xuyifan
 */
public class RedisStartMain {
    enum actionType{
        INVALID_ACTION,INCREASE_ACTION,DECREASE_ACTION,SETSTR_ACTION,ADDLIST_ACTION,ADDSET_ACTION,REMSET_ACTION
    }
    enum type{
        increaseCounter,decreaseCounter,setStrCounter,addListCounter,addSetCounter,remSetCounter
    }

    private static final HashMap<String, CounterSpec> counterMap = new HashMap<>();
    private static final HashMap<String, NumCounterSpec> NumcounterMap = new HashMap<>();
    private static final HashMap<String, StrCounterSpec> StrcounterMap = new HashMap<>();
    private static final HashMap<String, SetCounterSpec> SetcounterMap = new HashMap<>();
    private static final HashMap<String, ListCounterSpec> ListcounterMap = new HashMap<>();
    private static final HashMap<String, FreqCounterSpec> FreqcounterMap = new HashMap<>();
    private static final HashMap<String, ActionSpec> actionMap = new HashMap<>();
    private static final ArrayList<String> counternameArray = new ArrayList<String>();
    private static final ArrayList<String> actionnameArray = new ArrayList<String>();
    private static  FileAlterationMonitor monitor;
    private static  String RESOURCES_PATH ;


    public static void readJson() throws IOException {
        System.out.println("开始读取json文件");
        //读取counters
        InputStream counterInputStream = new FileInputStream(RESOURCES_PATH+"/counters.json");
        String counterStr = IOUtils.toString(counterInputStream, StandardCharsets.UTF_8);
        JSONObject counterObj = JSON.parseObject(counterStr);
        JSONArray counters = counterObj.getJSONArray("counters");
        for(int i=0;i<counters.size();i++) {
            //根据读取到的JSON的type，将json解析成不同的类
            String type = (String)counters.getJSONObject(i).get("type");
            switch (type){
                case "Num":
                    NumCounterSpec numCounterSpec = counters.getJSONObject(i).toJavaObject(NumCounterSpec.class);
                    NumcounterMap.put(numCounterSpec.getCounterName(),numCounterSpec);
                    counterMap.put(numCounterSpec.getCounterName(),numCounterSpec);
                    counternameArray.add(numCounterSpec.getCounterName());break;
                case "String":
                    StrCounterSpec strCounterSpec = counters.getJSONObject(i).toJavaObject(StrCounterSpec.class);
                    StrcounterMap.put(strCounterSpec.getCounterName(),strCounterSpec);
                    counterMap.put(strCounterSpec.getCounterName(),strCounterSpec);
                    counternameArray.add(strCounterSpec.getCounterName());break;
                case "List":
                    ListCounterSpec listCounterSpec = counters.getJSONObject(i).toJavaObject(ListCounterSpec.class);
                    ListcounterMap.put(listCounterSpec.getCounterName(),listCounterSpec);
                    counterMap.put(listCounterSpec.getCounterName(),listCounterSpec);
                    counternameArray.add(listCounterSpec.getCounterName());break;
                case "Set":
                    SetCounterSpec setCounterSpec = counters.getJSONObject(i).toJavaObject(SetCounterSpec.class);
                    SetcounterMap.put(setCounterSpec.getCounterName(),setCounterSpec);
                    counterMap.put(setCounterSpec.getCounterName(),setCounterSpec);
                    counternameArray.add(setCounterSpec.getCounterName());break;
                case "Freq":
                    FreqCounterSpec freqCounterSpec = counters.getJSONObject(i).toJavaObject(FreqCounterSpec.class);
                    FreqcounterMap.put(freqCounterSpec.getCounterName(),freqCounterSpec);
                    counterMap.put(freqCounterSpec.getCounterName(),freqCounterSpec);
                    counternameArray.add(freqCounterSpec.getCounterName());break;
                default:break;
            }
        }

        //读取actions
        InputStream actionInputStream = new FileInputStream(RESOURCES_PATH +"/actions.json");
        String actionStr = IOUtils.toString(actionInputStream, StandardCharsets.UTF_8);
        JSONObject actionsObj = JSON.parseObject(actionStr);
        JSONArray actions = actionsObj.getJSONArray("actions");
        //对每个actions
        for(int i=0;i<actions.size();i++) {
            String actionName = (String) actions.getJSONObject(i).get("name");
            ArrayList<String> saves = new ArrayList<>();
            ArrayList<String> retrieves = new ArrayList<>();
            JSONArray saveJsonArray = actions.getJSONObject(i).getJSONArray("save");
            JSONArray retrieveJsonArray = actions.getJSONObject(i).getJSONArray("retrieve");
            //System.out.println(actionName);
            for(int j=0;j<saveJsonArray.size();j++) {
                String saveJsonCounterName = (String) saveJsonArray.getJSONObject(j).get("counterName");
                saves.add(saveJsonCounterName);
                //System.out.println(saveJsonCounterName);
            }
            for(int k=0;k<retrieveJsonArray.size();k++) {
                String retrieveJsonCounterName = (String) retrieveJsonArray.getJSONObject(k).get("counterName");
                retrieves.add(retrieveJsonCounterName);
                //System.out.println(retrieveJsonCounterName);
            }
            ActionSpec actionSpec = new ActionSpec(actionName,saves,retrieves);
            actionMap.put(actionName,actionSpec);
            actionnameArray.add(actionName);

        }

        System.out.println("json文件读取完毕");
    }

    //监听json的变化
    private static void listenJson() throws Exception {
        File directory = new File(RESOURCES_PATH);
        FileAlterationObserver observer = new FileAlterationObserver(directory);
        observer.addListener(new FileAlterationListenerAdaptor(){
            @Override
            public void onFileChange(File file) {
                System.out.println("\njson文件更改，重新读取!\n");
                try {
                    RedisStartMain.readJson();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        long interval = 1000;
        monitor = new FileAlterationMonitor(interval);
        monitor.addObserver(observer);
        monitor.start();
        System.out.println("监听json文件");
    }

    public static  void actionOperation(int type){
        ActionSpec actionSpec = actionMap.get(actionnameArray.get(type-1));
        ArrayList<String> saveCounters = actionSpec.getSaves();
        ArrayList<String> retrievesCounters = actionSpec.getRetrieves();
        BasicOperation basicOperation;
        String countertype;
        for (String retrieveCounter :
                retrievesCounters) {
            if(counterMap.containsKey(retrieveCounter)){
                countertype = counterMap.get(retrieveCounter).getType();
                switch(countertype){
                    case "Num":basicOperation= new BasicOperation(NumcounterMap.get(retrieveCounter));break;
                    case "String":basicOperation= new BasicOperation(StrcounterMap.get(retrieveCounter));break;
                    case "List":basicOperation= new BasicOperation(ListcounterMap.get(retrieveCounter));break;
                    case "Set":basicOperation= new BasicOperation(SetcounterMap.get(retrieveCounter));break;
                    case "Freq":basicOperation= new BasicOperation(FreqcounterMap.get(retrieveCounter));break;
                    default:basicOperation= new BasicOperation();break;
                }
                basicOperation.operation(retrieveCounter);
            }
        }
        for (String saveCounter :
                saveCounters) {
            if(counterMap.containsKey(saveCounter)){
                countertype = counterMap.get(saveCounter).getType();
                switch(countertype){
                    case "Num":basicOperation= new BasicOperation(NumcounterMap.get(saveCounter));break;
                    case "String":basicOperation= new BasicOperation(StrcounterMap.get(saveCounter));break;
                    case "List":basicOperation= new BasicOperation(ListcounterMap.get(saveCounter));break;
                    case "Set":basicOperation= new BasicOperation(SetcounterMap.get(saveCounter));break;
                    case "Freq":basicOperation= new BasicOperation(FreqcounterMap.get(saveCounter));break;
                    default:basicOperation= new BasicOperation();break;
                }
                basicOperation.operation(saveCounter);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        RESOURCES_PATH = pathCal();
        //监听json文件的变化
        listenJson();

        //读取json文件
        readJson();

        //测试json文件是否读取成功
        for(HashMap.Entry<String, CounterSpec> entry : counterMap.entrySet()){
            //System.out.println(entry.getValue().toString());
        }
        int choice;
        do{
            System.out.println("\nCounter系统:\n可以进行的actions列表：\n" +
                    "1.increase\n" +
                    "2.decrease\n" +
                    "3.setStr\n" +
                    "4.addList\n" +
                    "5.addSet\n" +
                    "6.remSet\n" +
                    "7.showFreq\n" +
                    "8.incrFreq\n" +
                    "9.UserIncrFreq\n" +
                    "-1：退出");
            choice = new Scanner(System.in).nextInt();
            if(choice>=1 && choice<=9){
                actionOperation(choice);
            }
            else if(choice!=-1){
                System.out.println("无效选择，请重新输入");
            }
        }while(choice!=-1);
        monitor.stop();
    }

    private static String pathCal() {
        String temppathstr =  RedisStartMain.class.getClassLoader().getResource("").toString();
        File file = new File(temppathstr);
        file = new File(file.getParent());
        return file.getParent().substring(6)+"/src/main/resources";
    }
}
