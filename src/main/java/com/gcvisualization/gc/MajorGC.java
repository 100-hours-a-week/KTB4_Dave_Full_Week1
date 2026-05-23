package com.gcvisualization.gc;

import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;

import java.util.Arrays;

public class MajorGC extends GC{
    protected final boolean[] marking;
    public MajorGC(Data[] heap, int start, int end, TopManager topManager) {
        super(heap, start, end, topManager);
        this.marking = new boolean[end - start];
    }

    @Override
    public void execute(){
        System.out.println(this.getClass().getName()+" 발생");
        Arrays.fill(marking, false);
        int oldTop = topManager.getOldTop();
        int youngBound = TopManager.SURVIVOR_2_END;
        this.search();
        if(this.cleaning() == 0){
            if(oldTop <= youngBound + ((heap.length-youngBound)/4) * 3){
                // 메모리에 충분한 공간이 있을 때 FullGC를 실행했을 때는 에러를 일으키지 않아야 한다.
                // 비율 기준으로 메모리 저장공간이 75%를 초과한 경우에 에러를 일으키도록 설정
                // 물론 정확한 기준으로 하려면 double을 사용해야 하지만 간단한 과제이기 때문에 정수 계산
                return;
            }
            System.out.println("메모리 확보 실패");
            throw new OutOfMemoryError();
        }

        this.compacting();
    }

    @Override
    protected void search() {
        int top = topManager.getOldTop();
        for(int i = start; i < top; i++){
            if(heap[i] != null) {
                if (heap[i].isLive()) {
                    marking[i - start] = true;
                }
            }

        }
    }

    @Override
    protected int cleaning() {
        // 실제로는 com.gcvisualization.gc.GC Root로 탐색해서 도달하지 못한 객체를 지워야 하지만 단순화하는 과정에서 실제 참조가 아닌 생존기간을 이용해서 cleaning으로 나눈 의미가 없어보임
        // 이런 식으로 단순화했을 때는 오히려 생존이 아닌 죽은 객체만 표시해서 해당 객체들에 대해 정리하는게 더 비슷한 작동방식으로 보일 수도 있을 것 같음
        int result = 0;
        int top = topManager.getOldTop();
        for(int i = start; i < top; i++){
            if(heap[i] != null){
                if(!marking[i - start]){
                    heap[i] = null;
                    result++;
                }
            }
        }

        return result;
    }

    protected void compacting() {
        // 죽은 객체 삭제 후 빈 자리를 당겨서 연속된 빈 공간을 크게 만들어 준다.
        int top = topManager.getOldTop();
        topManager.setOldTop(compacting(heap, start, top));
    }

    protected int compacting(Data[] data, int start, int end){
        // 죽은 객체 삭제 후 빈 자리를 당겨서 연속된 빈 공간을 크게 만들어 준다.
        int compTop = start;

        for(int i = start; i < end; i++){
            if(data[i] != null){
                int size = data[i].getSize();
                Data d = data[i];
//                System.out.println("compTop, i, d Name: " + compTop + " " + "i " + d.getName());
                if(i > compTop) {
                    for (int j = i; j < i + size; j++) {
                        data[j] = null;
                    }
                    for (int j = 0; j < size; j++) {
//                        System.out.println("compTop: " + compTop);
                        data[compTop + j] = d;
                    }
//                    System.out.println("d name: "+d.getName());
                }
                else{
                    i += size-1;
                }
                compTop += size;
            }
        }
        return compTop;
    }

}
