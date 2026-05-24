package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;

public interface Sweeper {
    int sweeping(Data[] data, boolean[] mark, int start, int end);
}
