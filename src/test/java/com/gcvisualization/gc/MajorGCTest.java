package com.gcvisualization.gc;

import com.gcvisualization.gcphase.ParallelCompactor;
import com.gcvisualization.gcphase.ParallelMarker;
import com.gcvisualization.gcphase.ParallelSweeper;
import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MajorGCTest {
    TopManager topManager;
    MajorGC majorGC;
    Data[] data;
    boolean[] mark;

    @BeforeEach
    void setUp(){
        topManager = new TopManager();
        data = new Data[TopManager.OLD_END];
        mark = new boolean[TopManager.OLD_END];
        majorGC = new MajorGC(
                data, mark, TopManager.SURVIVOR_2_END, TopManager.OLD_END, topManager, new ParallelMarker(), new ParallelSweeper(), new ParallelCompactor());
    }

    private boolean exists(Data target){
        for(Data d : data){
            if(d == target){
                return true;
            }
        }
        return false;
    }

    @Test
    void majorGC_shouldRemoveDeadObjects(){
        Data dead = new Data("dead", 1, 1);
        Data live = new Data("live", 1, 1);
        while(dead.isLive()){
            dead.decreaseLiveTime(1);
        }
        int deadIndex = topManager.allocateOld(1);
        data[deadIndex] = dead;
        int liveIndex = topManager.allocateOld(1);
        data[liveIndex] = live;

        majorGC.execute();

        assertFalse(exists(dead));
        assertTrue(exists(live));
    }

    @Test
    void majorGC_shouldCompactEmptySpace(){
        Data live = new Data("live", 1, 1);
        Data dead = new Data("dead", 1, 1);
        Data live2 = new Data("live2", 1, 1);
        while(dead.isLive()){
            dead.decreaseLiveTime(1);
        }
        int liveIndex = topManager.allocateOld(1);
        data[liveIndex] = live;
        int deadIndex = topManager.allocateOld(1);
        data[deadIndex] = dead;
        int live2Index = topManager.allocateOld(1);
        data[live2Index] = live2;

        majorGC.execute();

        int i = 0;
        for(Data d : data){
            if(d != null){
                System.out.println(i + " " + d.getName());
            }
            i++;
        }
        assertSame(live2, data[liveIndex+1]);
    }

    @Test
    void majorGC_whenNoMemoryCanBeFreed_thenThrowOOME(){
        Data d = new Data("large", 10, 1);
        int now = topManager.allocateOld(10);
        data[now] = d;

       assertThrows (OutOfMemoryError.class, majorGC::execute);
    }
}
