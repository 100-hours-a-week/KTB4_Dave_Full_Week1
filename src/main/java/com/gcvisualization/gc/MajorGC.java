package com.gcvisualization.gc;

import com.gcvisualization.gcphase.Compactor;
import com.gcvisualization.gcphase.Marker;
import com.gcvisualization.gcphase.Sweeper;
import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;

public class MajorGC extends GC{
    protected final boolean[] mark;
    protected final Marker marker;
    protected final Sweeper sweeper;
    protected final Compactor compactor;
    public MajorGC(Data[] heap, boolean[] mark, int start, int end, TopManager topManager, Marker marker, Sweeper sweeper, Compactor compactor) {
        super(heap, start, end, topManager);
        this.mark = mark;
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
    protected void search() {
        int oldTop = topManager.getOldTop();
        marker.marking(heap, mark, start, oldTop);
    }

    @Override
    protected void cleaning() {
        // 실제로는 GC Root로 탐색해서 도달하지 못한 객체를 지워야 하지만 단순화하는 과정에서 실제 참조가 아닌 생존기간을 이용해서 cleaning으로 나눈 의미가 없어보임
        // 이런 식으로 단순화했을 때는 오히려 생존이 아닌 죽은 객체만 표시해서 해당 객체들에 대해 정리하는게 더 비슷한 작동방식으로 보일 수도 있을 것 같음
        int top = topManager.getOldTop();
        int result = sweeper.sweeping(heap,mark,start,top);

        // Major GC가 발생했다는 건 이미 Old 영역의 공간이 많이 남지 않은 상태이기 때문에 정리한 메모리 공간이 0일 경우 OOME 발생
        // 혹은 크기가 큰 객체가 삽입되는 시점의 공간 부족으로 호출된 것으로 공간확보가 되지 않은 경우 OOME
        if (result == 0){
            throw new OutOfMemoryError();
        }
    }

    protected void compacting() {
        // 죽은 객체 삭제 후 빈 자리를 당겨서 연속된 빈 공간을 크게 만들어 준다.
        int top = topManager.getOldTop();
        topManager.setOldTop(compactor.compacting(heap,start,top));
    }

}
