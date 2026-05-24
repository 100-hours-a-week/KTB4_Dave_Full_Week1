package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;

public interface Compactor {
    int compacting(Data[] data, int start, int end);
}
