package com.gcvisualization.gc;

import com.gcvisualization.gcphase.Compactor;
import com.gcvisualization.gcphase.Marker;
import com.gcvisualization.gcphase.Sweeper;
import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;

public class FullGC extends GC{
    private final Data[] meta;
    private final boolean[] mark;
    private final boolean[] metaMark;
    private final Marker marker;
    private final Sweeper sweeper;
    private final Compactor compactor;

    public FullGC(Data[] heap, boolean[] mark, Data[] meta, boolean[] metaMark, int start, int end, TopManager topManager, Marker marker, Sweeper sweeper, Compactor compactor) {
        super(heap, start, end, topManager);
        this.mark = mark;
        this.meta = meta;
        this.metaMark = metaMark;
        this.marker = marker;
        this.sweeper = sweeper;
        this.compactor = compactor;
    }

    @Override
    public void execute(){
        super.execute();
        this.compacting();
    }

    @Override
    protected void search(){
        int oldTop = topManager.getOldTop();
        int metaTop = topManager.getMetaTop();

        marker.marking(heap, mark, start, oldTop);
        marker.marking(meta, metaMark, 0, metaTop);
    }

    @Override
    protected void cleaning(){
        // 정리한 데이터 크기를 반환한다.
        int result = 0;
        int oldTop = topManager.getOldTop();
        int metaTop = topManager.getMetaTop();

        result += sweeper.sweeping(heap, mark, start, oldTop);
        result += sweeper.sweeping(meta, metaMark, 0, metaTop);

        if(result == 0){
            // Full GC의 경우 공간 부족이 아닌 외부에서 강제로 호출할 수 있기에 정리한 메모리가 0이어도 공간이 충분한 경우 정상 종료
            if(oldTop < TopManager.SURVIVOR_2_END + (TopManager.OLD_END - TopManager.SURVIVOR_2_END) / 4 * 3){
                return;
            }
            if(metaTop < (TopManager.META_END)/4 * 3){
                return;
            }
            throw new OutOfMemoryError();
        }
    }

    protected void compacting(){
        int youngBound = TopManager.SURVIVOR_2_END;
        int metaTop = topManager.getMetaTop();
        topManager.setEdenTop(compactor.compacting(heap, start, youngBound));
        topManager.setOldTop(compactor.compacting(heap, youngBound, end));
        topManager.setMetaTop(compactor.compacting(meta, 0, metaTop));
    }
}
