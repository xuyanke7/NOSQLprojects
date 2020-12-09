package com.bjtu.redis.spec;

import java.util.ArrayList;

public class ActionSpec {
    String name;
    ArrayList<String> saves = new ArrayList<>();
    ArrayList<String> retrieves = new ArrayList<>();
    public ActionSpec(String name,ArrayList<String> saves,ArrayList<String> retrieves){
        this.name = name;
        this.retrieves = retrieves;
        this.saves = saves;
    }

    public ArrayList<String> getRetrieves() {
        return retrieves;
    }

    public ArrayList<String> getSaves() {
        return saves;
    }
}
