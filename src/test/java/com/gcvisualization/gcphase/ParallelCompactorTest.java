package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParallelCompactorTest {
    @Test
    void compactor_shouldCompactLiveObjects(){
        ParallelCompactor compactor = new ParallelCompactor();
        Data[] data = new Data[10];
        Data first = new Data("first", 1, 1);
        Data second = new Data("second", 1, 1);
        data[0] = first;
        data[9] = second;

        compactor.compacting(data, 0, 10);
        assertEquals(second, data[1]);
    }

    @Test
    void compactor_shouldPreserveMultiSlotObjectLayout(){

        ParallelCompactor compactor = new ParallelCompactor();

        Data[] data = new Data[10];

        Data a = new Data("A", 1, 2);
        Data b = new Data("B", 1, 2);

        data[0] = a;
        data[1] = a;

        data[4] = b;
        data[5] = b;

        compactor.compacting(data, 0, 10);

        assertSame(a, data[0]);
        assertSame(a, data[1]);

        assertSame(b, data[2]);
        assertSame(b, data[3]);

        assertNull(data[4]);
        assertNull(data[5]);
    }
}
