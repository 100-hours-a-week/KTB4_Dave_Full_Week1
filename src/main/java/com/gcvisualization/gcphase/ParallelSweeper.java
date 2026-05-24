package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;

public class ParallelSweeper implements Sweeper {

    @Override
    public int sweeping(Data[] data, boolean[] mark, int start, int end){
        int result = 0;
        for(int i = start; i < end; i++){
            if(data[i] != null){
                if(mark[i]) {
                    i += data[i].getSize() - 1;
                }
                if(!mark[i]) {
                    data[i] = null;
                    result++;
                }
            }
        }
        return result;
    }
}
