package com.gcvisualization.memory;

import java.util.concurrent.atomic.AtomicInteger;

public class TopManager {
    public static final int EDEN_END = 3;
    public static final int SURVIVOR_1_END = 4;
    public static final int SURVIVOR_2_END = 5;
    public static final int OLD_END = 15;
    public static final int META_END = 5;
    private int edenTop;
    private final AtomicInteger survivorTop = new AtomicInteger(EDEN_END);
    private final AtomicInteger oldTop = new AtomicInteger(SURVIVOR_2_END);
    private int metaTop;
    private boolean nextSurvivor = false;
    public static final int RETRYABLE_FAILURE = -1;
    public static final int DATA_TOO_LARGE = -2;

    public TopManager(){
        topInit();
    }

    public int getEdenTop(){
        return edenTop;
    }

    public void setEdenTop(int edenTop){
        this.edenTop = edenTop;
    }

    public int getOldTop() {
        return oldTop.get();
    }

    public void setOldTop(int oldTop) {
        this.oldTop.set(oldTop);
    }

    public int getMetaTop() {
        return metaTop;
    }

    public void setMetaTop(int metaTop) {
        this.metaTop = metaTop;
    }

    public void setNextSurvivor(){
        nextSurvivor = !nextSurvivor;
    }

    public boolean getNextSurvivor(){
        return nextSurvivor;
    }


    public void setNextSurvivorTop(){
        setNextSurvivor();
        if(nextSurvivor){
            survivorTop.set(SURVIVOR_1_END);
        }
        else{
            survivorTop.set(EDEN_END);
        }
    }

    public int allocateEden(int size){
        int loc = edenTop;
        int next = loc + size;

        if(size > EDEN_END){
            return DATA_TOO_LARGE;
        }
        if(next > EDEN_END){
            return RETRYABLE_FAILURE;
        }
        edenTop = next;

        return loc;
    }

    public int allocateSurvivor(int size){
        int loc;
        int next;
        int limit = SURVIVOR_1_END;
        if(nextSurvivor){
            limit = SURVIVOR_2_END;
        }

        do{
            loc = survivorTop.get();
            next = loc+size;
            if(next > limit){
                return RETRYABLE_FAILURE;
            }
        }while(!survivorTop.compareAndSet(loc, next));

        return loc;
    }

    public int allocateOld(int size){
        int loc;
        int next;
        if(size > OLD_END-SURVIVOR_2_END){
            return DATA_TOO_LARGE;
        }
        do{
            loc = oldTop.get();
            next = loc + size;
            if(next > OLD_END){
                return RETRYABLE_FAILURE;
            }
        }while(!oldTop.compareAndSet(loc, next));
        return loc;
    }

    public int allocateMeta(int size){
        int loc = metaTop;
        int next = loc+size;

        if(size > META_END){
            return DATA_TOO_LARGE;
        }

        if(next > META_END){
            return RETRYABLE_FAILURE;
        }
        metaTop = next;
        return loc;
    }


    public void initEdenTop(){
        edenTop = 0;
    }

    public void initSurvivorTop(){
        survivorTop.set(EDEN_END);
        nextSurvivor = false;
    }

    public void initOldTop(){
        oldTop.set(SURVIVOR_2_END);
    }

    public void initMetaTop(){
        metaTop = 0;
    }

    public void topInit(){
        initEdenTop();
        initSurvivorTop();
        initOldTop();
        initMetaTop();
    }

    public boolean oldAreaCheck(){
        return (OLD_END-oldTop.get()) >= SURVIVOR_1_END;
    }
}
