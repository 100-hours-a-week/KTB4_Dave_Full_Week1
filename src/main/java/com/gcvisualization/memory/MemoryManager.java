package com.gcvisualization.memory;

import com.gcvisualization.ui.DataPrinter;
import com.gcvisualization.gc.FullGC;
import com.gcvisualization.gc.GC;
import com.gcvisualization.gc.MinorGC;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MemoryManager {
    private final Data[] heap;
    private final Data[] meta;
    private final MinorGC[] minorGCS;
    private final GC majorGC;
    private final FullGC fullGC; // metaTop 데이터 주고받기 위해 com.gcvisualization.gc.GC 대시 com.gcvisualization.gc.FullGC 사용
    private final TopManager topManager;
    private static final int THREAD_POOL_SIZE = 2;
    private final ExecutorService executorService;

    public MemoryManager(Data[] heap, Data[] meta, MinorGC[] minorGCS, GC majorGC, FullGC fullGC, TopManager topManager){
        this.heap = heap;
        this.meta = meta;
        this.minorGCS = minorGCS;
        this.majorGC = majorGC;
        this.fullGC = fullGC;
        this.topManager = topManager;
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }


    private void garbageCollect(GC gc){
        // 원래 GC의 경우 major com.gcvisualization.gc.GC 같은 게 실패하면 Full GC를 시행해야 하나 여기서는 soft reference 삭제 등의 작동을 구현하지 않았기 때문에
        // major GC만 실패해도 OOME 발생으로 대체
        gc.execute();
    }

    private void multiThreadingMinorGC(){
        int surv = 1;
        if(!topManager.getNextSurvivor()){
            surv++;
        }

        CompletableFuture<Void> edenFuture =
                CompletableFuture.runAsync(minorGCS[0], executorService);
        CompletableFuture<Void> survivorFuture =
                CompletableFuture.runAsync(minorGCS[surv], executorService);

        edenFuture.join();
        survivorFuture.join();
        topManager.initEdenTop();
        topManager.setNextYoungTop();
    }

    public void garbageCollect(){
        // com.gcvisualization.gc.FullGC 실행
        int youngBound = TopManager.SURVIVOR_2_END;
        int oldTop = topManager.getOldTop();
        garbageCollect(this.fullGC);
        if(oldTop < youngBound){ //? 왜 이런 코드가?
            topManager.initOldTop();
        }
    }

    public void insertHeapData(Data d){
        // 에덴에 값을 넣을 때 자리가 부족하면 minor com.gcvisualization.gc.GC 실행
        // minor com.gcvisualization.gc.GC 검사 전에 조건 만족 시 major GC를 먼저 수행.
        int eden = TopManager.EDEN_END;
        int oldTop = topManager.getOldTop();
        int edenTop = topManager.getEdenTop();
        int size = d.getSize();
        int now = topManager.allocateEden(size);

        if(now == TopManager.DATA_TOO_LARGE){
            now = topManager.allocateOld(size);
            if(now == TopManager.DATA_TOO_LARGE){
                throw new OutOfMemoryError();
            }
            if(now == TopManager.RETRYABLE_FAILURE){
                garbageCollect(majorGC);
                now = topManager.allocateOld(size);
                if(now == TopManager.RETRYABLE_FAILURE){
                    throw new OutOfMemoryError();
                }
            }
            for(int i = 0; i < size; i++){
                heap[now+i] = d;
            }
            return;
        }

        if(now == TopManager.RETRYABLE_FAILURE){
            if(!topManager.oldAreaCheck()){
                // Old Generation의 남은 공간이 eden보다 작은 경우 majorGC 실행
                garbageCollect(majorGC);
            }
            try {
                multiThreadingMinorGC();
                now = topManager.allocateEden(size);
            }
            catch(OutOfMemoryError t){
                throw new OutOfMemoryError();
            }
        }
        for(int i = 0; i < size; i++){
            heap[now+ i] = d;
        }
        topManager.setEdenTop(edenTop+d.getSize());
    }

    public void insertMetaData(Data d){
        // meta 데이터 넣을 자리 부족하면 full com.gcvisualization.gc.GC 실행
        // 여기선 콘솔 프로그램이니 하나씩만 넣으니까 Full com.gcvisualization.gc.GC 시 1 이상의 값만 확보되면 상관없고 0일 경우 에러 던지기
        int metaTop = topManager.getMetaTop();
        int size = d.getSize();
        int now = topManager.allocateMeta(size);

        if(now == TopManager.DATA_TOO_LARGE){
            throw new OutOfMemoryError();
        }

        if(now == TopManager.RETRYABLE_FAILURE){
            garbageCollect();
            now = topManager.allocateMeta(size);
            if(now == TopManager.RETRYABLE_FAILURE){
                throw new OutOfMemoryError();
            }
        }
        for(int i = 0; i < size; i++){
            meta[now+i] = d;
        }
    }



    public void initData(){
        // 모든 데이터 비우기
        Arrays.fill(heap, null);
        Arrays.fill(meta, null);
        topManager.topInit();
    }

    public void showData(){
        DataPrinter.printData(heap, "heap");
        DataPrinter.printData(meta, "meta");
    }

    public void executorServiceShutdown(){
        executorService.shutdown();
    }
}
