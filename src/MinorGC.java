public class MinorGC extends GC implements Runnable{

    public MinorGC( Data[] heap, int start, int end, TopManager topManager) {
        super(heap, start, end, topManager);
    }


    @Override
    public void run() {
        execute();
    }


    @Override
    protected void search() {
        // 공간 부족 시 에러를 던져서 JVM에서 에러 처리 시키기
        for(int i = start; i < end; i++){
            if(heap[i] != null && heap[i].isLive()){
                Data d = heap[i];
                if(!copy(d)){
                    System.out.println("OOME");
                    throw new OutOfMemoryError();
                }
                i += d.getSize()-1;
            }
        }
    }


    private boolean copy(Data d){
        int size = d.getSize();
        int now;
        if(d.isPromotion()){
            now = topManager.getOldCopyLoc(size);
        }
        else{
            now = topManager.getYoungCopyLoc(size);
        }
        if(now == -1){
            return false;
        }

        for(int i = 0; i < size; i++){
            heap[now+i] = d;
        }
        d.surviveGC();

        return true;
    }

    @Override
    protected int cleaning() {
        // 정리의 경우 major와 full의 경우 정리한 데이터 수를 반환해줄 건데 minor는 정리한 개수를 안 세니까 애매함.
        // 멤버변수로 몇개의 데이터가 있는지 알아야 하나 싶음
        // 어차피 여기서 에러 체킹이 되는게 아니라 search에서 되니까 적당히 1 반환하면 될 듯
        int surv1Bound = TopManager.getSurvivor1();
        boolean nextSurv = topManager.getNextSurvivor();
        int edenBound = TopManager.getEden();
        int survBase = surv1Bound;
        if(nextSurv) survBase = edenBound;

        for(int i = start; i < edenBound; i++){
            heap[i] = null;
        }
        for(int i = survBase; i < survBase + (surv1Bound - edenBound); i++){
            heap[i] = null;
        }

        return 1;
    }

}
