package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParallelMarkerTest {
    @Test
    void marker_shouldMarkLiveObjects() {
        ParallelMarker marker = new ParallelMarker();

        Data[] data = new Data[10];
        boolean[] mark = new boolean[10];

        Data live = new Data("live", 1, 1);

        data[0] = live;

        marker.marking(data, mark, 0, 10);

        assertTrue(mark[0]);
    }

    @Test
    void marker_shouldNotMarkDeadObjects(){
        ParallelMarker marker = new ParallelMarker();

        Data[] data = new Data[10];
        boolean[] mark = new boolean[10];

        Data dead = new Data("dead", 1, 1);
        while(dead.isLive()) {
            dead.decreaseLiveTime(1);
        }

        data[0] = dead;

        marker.marking(data, mark, 0, 10);

        assertFalse(mark[0]);
    }
}
