package com.gcvisualization.gc;

import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;

public abstract class GC {
    protected final Data[] heap;
    protected final int start; //
    protected final int end; //
    protected final TopManager topManager;

    public GC(Data[] heap, int start, int end, TopManager topManager){
        this.heap = heap;
        this.start = start;
        this.end = end;
        this.topManager = topManager;
    }


    public void execute(){
        System.out.println(this.getClass().getName()+" 발생");
        this.search();
        this.cleaning();
    }

    protected abstract void search();
    protected abstract void cleaning();
}
