package com.gcvisualization.gc;

import com.gcvisualization.memory.Data;
import com.gcvisualization.memory.TopManager;

public class MinorGC extends GC implements Runnable{

    public MinorGC(Data[] heap, int start, int end, TopManager topManager) {
        super(heap, start, end, topManager);
    }


    @Override
    public void run() {
        execute();
    }


    @Override
    protected void search() {
        // 공간 부족 시 에러를 던져서 JVM에서 에러 처리 시키기
        boolean nextSurvivor = topManager.getNextSurvivor();
        int copyStart, copyEnd;
        // 멀티 스레드로 처리하지 않을 때 start부터 end까지 검사하면 복사한 객체에 대해 중복 탐색할 수 있음
        // 이름 방지하기 위해 copyStart, copyEnd로 복사하는 영역을 건너뛸 수 있도록 관리
        if(nextSurvivor){
            copyStart = TopManager.SURVIVOR_1_END;
            copyEnd = TopManager.SURVIVOR_2_END;
        }
        else{
            copyStart = TopManager.EDEN_END;
            copyEnd = TopManager.SURVIVOR_1_END;
        }
        for(int i = start; i < end; i++){
            if(i >= copyStart && i < copyEnd){
                i += copyEnd - copyStart - 1;
                continue;
            }

            if(heap[i] != null && heap[i].isLive()){
                Data d = heap[i];
                if(!copy(d)){
                    System.out.println("OOME");
                    throw new OutOfMemoryError("promotionFailed");
                }
                i += d.getSize()-1;
            }
        }
    }


    private boolean copy(Data d){
        int size = d.getSize();
        int now;
        if(d.isPromotion()){
            return promotion(d);
        }
        else{
            now = topManager.allocateSurvivor(size);
            if(now == TopManager.RETRYABLE_FAILURE){
                return promotion(d);
            }
        }

        for(int i = 0; i < size; i++){
            heap[now+i] = d;
        }
        d.surviveGC();

        return true;
    }

    private boolean promotion(Data d){
        int size = d.getSize();
        int now = topManager.allocateOld(size);
        if(now == TopManager.RETRYABLE_FAILURE){
            // 보통 Major GC를 실행한 후 minor GC를 실행하기에 공간이 부족한 경우 전체 공간의 부족을 의미한다.
            return false;
        }
        for(int i = 0; i < size; i++){
            heap[now+i] = d;
        }
        d.surviveGC();
        return true;
    }


    @Override
    protected void cleaning() {
        // 정리의 경우 major와 full의 경우 정리한 데이터 수를 반환해줄 건데 minor는 정리한 개수를 안 세니까 애매함.
        // 멤버변수로 몇개의 데이터가 있는지 알아야 하나 싶음
        // 어차피 여기서 에러 체킹이 되는게 아니라 search에서 되니까 적당히 1 반환하면 될 듯
        int surv1Bound = TopManager.SURVIVOR_1_END;
        boolean nextSurv = topManager.getNextSurvivor();
        int edenBound = TopManager.EDEN_END;
        int survBase = surv1Bound;
        if(nextSurv) survBase = edenBound;

        for(int i = start; i < edenBound; i++){
            heap[i] = null;
        }
        for(int i = survBase; i < survBase + (surv1Bound - edenBound); i++){
            heap[i] = null;
        }

    }

}
