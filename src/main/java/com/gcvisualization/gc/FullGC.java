package com.gcvisualization.gc;

import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;

import java.util.Arrays;

public class FullGC extends MajorGC{
    private final Data[] meta;
    private final boolean[] metaMarking;

    public FullGC(Data[] heap, int start, int end, TopManager topManager, Data[] meta) {
        super(heap, start, end, topManager);
        this.meta = meta;
        metaMarking = new boolean[meta.length];
    }

    @Override
    public void execute(){
        Arrays.fill(metaMarking, false);
        super.execute();
    }

    @Override
    protected void search(){
        int metaTop = topManager.getMetaTop();
        super.search();
        for(int i = 0; i < metaTop; i++){
            if(meta[i] != null){
                if(meta[i].isLive()){
                    metaMarking[i] = true;
                }
            }
        }
    }

    @Override
    protected int cleaning(){
        // 정리한 데이터 크기를 반환한다.
        int result = 0;
        int metaTop = topManager.getMetaTop();

        result += super.cleaning();

        for(int i = 0; i < metaTop; i++){
            if(meta[i] != null){
                if(!metaMarking[i]){
                    meta[i] = null;
                    result++;
                }
            }
            else{
                break;
            }
        }

        return result;
    }

    @Override
    protected void compacting(){
        int youngBound = TopManager.SURVIVOR_2_END;
        int metaTop = topManager.getMetaTop();
        topManager.setEdenTop(compacting(heap, start, youngBound));
        topManager.setOldTop(compacting(heap, youngBound, end));
        topManager.setMetaTop(compacting(meta, 0, metaTop));
    }
}
