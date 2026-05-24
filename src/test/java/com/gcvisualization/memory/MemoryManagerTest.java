package com.gcvisualization.memory;

import com.gcvisualization.gc.FullGC;
import com.gcvisualization.gc.GC;
import com.gcvisualization.gc.MajorGC;
import com.gcvisualization.gc.MinorGC;
import com.gcvisualization.gcphase.ParallelCompactor;
import com.gcvisualization.gcphase.ParallelMarker;
import com.gcvisualization.gcphase.ParallelSweeper;
import com.gcvisualization.gcphase.Sweeper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryManagerTest {
    private MemoryManager memoryManager;
    private TopManager topManager;

    private Data[] heap;
    private Data[] meta;

    @BeforeEach
    void setUp() {
        topManager = new TopManager();

        heap = new Data[TopManager.OLD_END];
        meta = new Data[TopManager.META_END];

        MinorGC[] minorGCS = new MinorGC[3];
        minorGCS[0] = new MinorGC(heap, 0, TopManager.EDEN_END, topManager);
        minorGCS[1] = new MinorGC(heap, TopManager.EDEN_END, TopManager.SURVIVOR_1_END, topManager);
        minorGCS[2] = new MinorGC(heap, TopManager.SURVIVOR_1_END, TopManager.SURVIVOR_2_END, topManager);

        GC majorGC = new MajorGC(heap, new boolean[TopManager.OLD_END], TopManager.SURVIVOR_2_END, TopManager.OLD_END, topManager, new ParallelMarker(), new ParallelSweeper(), new ParallelCompactor());
        GC fullGC = new FullGC(heap, new boolean[TopManager.OLD_END], meta, new boolean[TopManager.META_END], 0, TopManager.OLD_END, topManager, new ParallelMarker(), new ParallelSweeper(), new ParallelCompactor());

        memoryManager = new MemoryManager(heap, meta, minorGCS, majorGC, fullGC, topManager);
    }

    boolean existInArea(Data[] area, Data d){
        boolean result = false;

        for(Data data: area){
            if(data == d) return true;
        }

        return result;
    }

    @Test
    void insertHeapData_whenHeapSpaceEnough_thenSuccess() {
        Data d = new Data("test", 1, 1);

        memoryManager.insertHeapData(d);

        assertTrue(existInArea(heap, d));
    }

    @Test
    void insertMetaData_whenMetaSpaceEnough_thenSuccess() {
        Data d = new Data("test", 1, 1);

        memoryManager.insertMetaData(d);

        assertTrue(existInArea(meta, d));
    }

    @Test
    void triggerMinorGC_whenEdenSpaceFull() {
        Data full = new Data("full", 1, TopManager.EDEN_END);
        memoryManager.insertHeapData(full);
        Data d = new Data("test", 1, 1);
        memoryManager.insertHeapData(d);

        assertTrue(existInArea(heap, d));

    }

    @Test
    void triggerFullGC_whenMetaSpaceFull() {
        Data full = new Data("full", 1, TopManager.META_END);
        memoryManager.insertMetaData(full);
        while (full.isLive()) {
            full.decreaseLiveTime(1);
        }
        Data d = new Data("test", 1, 1);
        memoryManager.insertMetaData(d);

        assertTrue(existInArea(meta, d));

    }

    @Test
    void triggerMajorGC_whenOldSpaceFull(){
        Data large = new Data("large", 1, 8);
        memoryManager.insertHeapData(large);
        while(large.isLive()){
            large.decreaseLiveTime(1);
        }
        Data d = new Data("test", 1, 5);
        memoryManager.insertHeapData(d);

        assertTrue(existInArea(heap, d));
    }

    @Test
    void insertHeapData_shouldThrowOOME_whenObjectLargerThanOld() {
        Data large = new Data("large", 1, TopManager.OLD_END);

        assertThrows(OutOfMemoryError.class, () -> memoryManager.insertHeapData(large));
    }

    @Test
    void insertMetaData_shouldThrowOOME_whenObjectLargerThanMeta() {
        Data large = new Data("large", 1, TopManager.META_END+1);

        assertThrows(OutOfMemoryError.class, () -> memoryManager.insertMetaData(large));
    }

    @Test
    void insertHeapData_shouldThrowOOME_whenOldSpaceNotEnough(){
        Data large1 = new Data("large1", 1, 5);
        Data large2 = new Data("small1", 1, 6);
        memoryManager.insertHeapData(large1);
        assertThrows(OutOfMemoryError.class, () -> memoryManager.insertHeapData(large2));
    }

    @Test
    void insertMetaData_shouldThrowOOME_whenMetaSpaceNotEnough(){
        Data large1 = new Data("large1", 1, 3);
        Data large2 = new Data("small1", 1, 3);
        memoryManager.insertMetaData(large1);
        assertThrows(OutOfMemoryError.class, () -> memoryManager.insertMetaData(large2));
    }
}
