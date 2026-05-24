package com.gcvisualization.memory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemoryTimePassTest {

    @Test
    void timePass_shouldDecreaseLiveTime() throws InterruptedException {
        Data d = new Data("A", 5, 1);
        Data[] heap = new Data[10];
        Data[] meta = new Data[10];

        heap[0] = d;

        MemoryTimePass timePass = new MemoryTimePass(heap, meta);

        timePass.timeStart();
        Thread.sleep(1000);
        // 강제로 시간 경과 시뮬레이션
        timePass.timePass();

        assertTrue(d.getLiveTime() < 5);
    }

    @Test
    void timePass_shouldSkipMultiSlotObjects() throws InterruptedException {
        Data d = new Data("A", 5, 2);
        Data b = new Data("B", 5, 1);

        Data[] heap = new Data[10];
        Data[] meta = new Data[10];

        heap[0] = d;
        heap[1] = d;
        heap[2] = b;


        MemoryTimePass tp = new MemoryTimePass(heap, meta);

        tp.timeStart();
        Thread.sleep(1000);
        tp.timePass();

        // size=2인데 두 번 감소하면 안됨 → 한 번만 감소
        assertEquals(4, d.getLiveTime());
    }

    @Test
    void timePass_shouldProcessHeapAndMeta() throws InterruptedException {

        Data h = new Data("h", 5, 1);
        Data m = new Data("m", 5, 1);

        Data[] heap = new Data[10];
        Data[] meta = new Data[10];

        heap[0] = h;
        meta[0] = m;

        MemoryTimePass tp = new MemoryTimePass(heap, meta);

        tp.timeStart();
        Thread.sleep(1000);
        tp.timePass();

        assertTrue(h.getLiveTime() < 5);
        assertTrue(m.getLiveTime() < 5);
    }
}
