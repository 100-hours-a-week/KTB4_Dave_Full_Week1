package com.gcvisualization.gc;

import com.gcvisualization.gcphase.ParallelCompactor;
import com.gcvisualization.gcphase.ParallelMarker;
import com.gcvisualization.gcphase.ParallelSweeper;
import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FullGCTest {

    private TopManager topManager;
    private FullGC fullGC;

    private Data[] heap;
    private Data[] meta;

    private boolean[] mark;
    private boolean[] metaMark;

    @BeforeEach
    void setUp() {
        topManager = new TopManager();

        heap = new Data[TopManager.OLD_END];
        meta = new Data[TopManager.META_END];

        mark = new boolean[TopManager.OLD_END];
        metaMark = new boolean[TopManager.META_END];

        fullGC = new FullGC(heap, mark, meta, metaMark, 0, TopManager.OLD_END, topManager, new ParallelMarker(), new ParallelSweeper(), new ParallelCompactor());
    }

    private boolean existsInHeap(Data target) {
        for (Data d : heap) {
            if (d == target) {
                return true;
            }
        }
        return false;
    }

    private boolean existsInMeta(Data target) {
        for (Data d : meta) {
            if (d == target) {
                return true;
            }
        }
        return false;
    }

    @Test
    void fullGC_shouldRemoveDeadObjectsFromHeap() {
        Data dead = new Data("dead", 1, 1);
        Data live = new Data("live", 1, 1);

        while (dead.isLive()) {
            dead.decreaseLiveTime(1);
        }

        int deadIndex = topManager.allocateOld(1);
        heap[deadIndex] = dead;

        int liveIndex = topManager.allocateOld(1);
        heap[liveIndex] = live;

        fullGC.execute();

        assertFalse(existsInHeap(dead));
        assertTrue(existsInHeap(live));
    }

    @Test
    void fullGC_shouldRemoveDeadObjectsFromMeta() {
        Data deadMeta = new Data("deadMeta", 1, 1);
        Data liveMeta = new Data("liveMeta", 1, 1);

        while (deadMeta.isLive()) {
            deadMeta.decreaseLiveTime(1);
        }

        int deadIndex = topManager.allocateMeta(1);
        meta[deadIndex] = deadMeta;

        int liveIndex = topManager.allocateMeta(1);
        meta[liveIndex] = liveMeta;

        fullGC.execute();

        assertFalse(existsInMeta(deadMeta));
        assertTrue(existsInMeta(liveMeta));
    }

    @Test
    void fullGC_shouldCompactOldGeneration() {
        Data live = new Data("live", 1, 1);
        Data dead = new Data("dead", 1, 1);
        Data live2 = new Data("live2", 1, 1);

        while (dead.isLive()) {
            dead.decreaseLiveTime(1);
        }

        int liveIndex = topManager.allocateOld(1);
        heap[liveIndex] = live;

        int deadIndex = topManager.allocateOld(1);
        heap[deadIndex] = dead;

        int live2Index = topManager.allocateOld(1);
        heap[live2Index] = live2;

        fullGC.execute();

        assertSame(live, heap[TopManager.SURVIVOR_2_END]);
        assertSame(live2, heap[TopManager.SURVIVOR_2_END + 1]);
    }

    @Test
    void fullGC_shouldCompactMetaArea() {

        Data live = new Data("metaLive", 1, 1);
        Data dead = new Data("metaDead", 1, 1);
        Data live2 = new Data("metaLive2", 1, 1);

        while (dead.isLive()) {
            dead.decreaseLiveTime(1);
        }

        int liveIndex = topManager.allocateMeta(1);
        meta[liveIndex] = live;

        int deadIndex = topManager.allocateMeta(1);
        meta[deadIndex] = dead;

        int live2Index = topManager.allocateMeta(1);
        meta[live2Index] = live2;

        fullGC.execute();

        assertSame(live, meta[0]);
        assertSame(live2, meta[1]);
    }

    @Test
    void fullGC_whenNoMemoryCanBeFreed_thenThrowOOME() {

        Data heapData = new Data("heapFull", 1, 10);
        Data metaData = new Data("metaFull", 1, 5);

        int heapIndex = topManager.allocateOld(10);
        for(int i = 0; i < 10; i++){
            heap[heapIndex+i] = heapData;
        }

        int metaIndex = topManager.allocateMeta(5);
        for(int i = 0; i < 5; i++){
            meta[metaIndex+i] = metaData;
        }

        assertThrows(OutOfMemoryError.class, fullGC::execute);
    }
}