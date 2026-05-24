package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;

public class ParallelCompactor implements Compactor {

    @Override
    public int compacting(Data[] data, int start, int end){
        int compTop = start;
        int size;
        for(int i = start; i < end; i++){
            if(data[i] != null) {
                size = data[i].getSize();
                if(compTop < i) {
                    data[compTop++] = data[i];
                    data[i] = null;
                }else if (compTop == i){
                    i += size -1;
                    compTop += size;
                }
            }
        }

        return compTop;
    }
}
