package com.gcvisualization;

import com.gcvisualization.exception.OOMEHandler;
import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.MemoryManager;
import com.gcvisualization.memory.MemoryTimePass;

public class JVM {
    private final MemoryTimePass memoryTimePass;
    private final MemoryManager memoryManager;
    private boolean isTimerOn = false;

    public JVM(MemoryTimePass memoryTimePass, MemoryManager memoryManager) {
        this.memoryTimePass = memoryTimePass;
        this.memoryManager = memoryManager;
    }

    public void passTimeIfNeeded(){
        if(isTimerOn){
            memoryTimePass.timePass();
        }
    }

    public void setTimerState(){
        passTimeIfNeeded();
        isTimerOn = !isTimerOn;
        memoryTimePass.timeStart();
    }
    public boolean getTimerState(){
        return isTimerOn;
    }

    public void garbageCollect(){
        passTimeIfNeeded();
        OOMEHandler.execute(memoryManager::garbageCollect, this::initData);
    }

    public void insertHeapData(Data d){
        passTimeIfNeeded();
        OOMEHandler.execute(memoryManager::insertHeapData, d, this::initData);
    }

    public void insertMetaData(Data d){
        passTimeIfNeeded();
        OOMEHandler.execute(memoryManager::insertMetaData, d, this::initData);
    }

    public void showData(){
        memoryManager.showData();
    }

    public void nowData(){
        passTimeIfNeeded();
        memoryManager.showData();
    }

    public void shutDown(){
        memoryManager.executorServiceShutdown();
    }

    public void initData(){
        memoryManager.initData();
    }
}
