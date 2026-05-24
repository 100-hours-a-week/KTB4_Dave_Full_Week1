package com.gcvisualization.memory;

public class Data {
    private final String name;
    private int liveTime;
    private final int size;
    private int gcTime;

    public Data(String name, int liveTime, int size){
        if(liveTime <= 0){
            throw new IllegalArgumentException("유효기간은 0보다 큰 정수를 입력해야 합니다.");
        }
        if(size <= 0){
            throw new IllegalArgumentException("크기는 0보다 큰 정수를 입력해야 합니다.");
        }

        this.name = name;
        this.liveTime = liveTime;
        this.size = size;
        this.gcTime = 0;
    }

    public void decreaseLiveTime(int time){
        liveTime -= time;
    }

    public void surviveGC(){
        gcTime++;
    }

    public String getName(){
        return name;
    }

    public int getLiveTime(){
        return liveTime;
    }

    public boolean isLive(){
        return liveTime > 0;
    }

    public int getSize(){
        return size;
    }

    public int getGcTime(){
        return gcTime;
    }

    public boolean isPromotion(){
        return gcTime > 2;
    }
}
