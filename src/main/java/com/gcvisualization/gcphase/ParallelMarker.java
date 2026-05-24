package com.gcvisualization.gcphase;

import com.gcvisualization.memory.Data;

import java.util.Arrays;

public class ParallelMarker implements Marker{

    @Override
    public void marking(Data[] data, boolean[] mark, int start, int end){
        Arrays.fill(mark, false);
        for(int i = start; i < end; i++){
            if(data[i] != null && data[i].isLive()){
                mark[i] = true;
            }
        }
    }

}
