package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;

public interface Marker {
    void marking(Data[] data, boolean[] mark, int start, int end);
}
