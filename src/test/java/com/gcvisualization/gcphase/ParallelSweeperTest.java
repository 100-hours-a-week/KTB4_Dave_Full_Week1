package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParallelSweeperTest {
    @Test
    void sweeper_shouldSweepUnMarkedObjects(){
        ParallelSweeper sweeper = new ParallelSweeper();
        Data[] data = new Data[10];
        boolean[] mark = new boolean[10];

        Data dead = new Data("dead", 1, 1);

        data[0] = dead;
        mark[0] = false;

        sweeper.sweeping(data, mark, 0, 10);
        assertNull(data[0]);
    }

    @Test
    void sweeper_shouldNotSweepMarkedObjects(){
        ParallelSweeper sweeper = new ParallelSweeper();
        Data[] data = new Data[10];
        boolean[] mark = new boolean[10];

        Data live = new Data("live", 1, 1);

        data[0] = live;
        mark[0] = true;

        sweeper.sweeping(data, mark, 0, 10);
        assertSame(data[0], live);
    }

    @Test
    void sweeper_shouldReturnSweepNum(){
        ParallelSweeper sweeper = new ParallelSweeper();
        Data[] data = new Data[10];
        boolean[] mark = new boolean[10];

        Data live = new Data("live", 1, 1);

        data[0] = live;
        mark[0] = true;

        int num = sweeper.sweeping(data, mark, 0, 10);
        assertSame(0, num);
    }

}
