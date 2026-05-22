public class MinorGC extends GC{


    public MinorGC( Data[] heap, int start, int end, TopManager topManager) {
        super(heap, start, end, topManager);
    }

    @Override
    public void execute(){
        // result[0] 에는 oldTop, [1]에는 정리한 메모리 수를 전송한다.
        System.out.println("Minor GC 발생");
        this.search();
        this.cleaning();
    }

    @Override
    protected void search() {
        int top = topManager.getEdenTop();
        boolean nextSurv = topManager.getNextSurvivor();
        int edenBound = TopManager.getEden();
        int surv1Bound = TopManager.getSurvivor1();
        // 공간 부족 시 에러를 던져서 JVM에서 에러 처리 시키기
        for(int i = start; i < top; i++){
            if(heap[i] != null){
                if(heap[i].isLive()){
                    Data d = heap[i];
                    if(!copy(d)){
                        throw new OutOfMemoryError();
                    }
                    i += d.getSize()-1;
                }
            }
        }
        int survBase = nextSurv? edenBound: surv1Bound;

        for(int i = survBase; i < survBase+(surv1Bound-edenBound); i++){
            if(heap[i] != null){
                if(heap[i].isLive()){
                    Data d = heap[i];
                    if(!copy(d)){
                        throw new OutOfMemoryError();
                    }
                }
            }
            else{
                break;
            }
        }


    }

    private int checkSize(Data d){
        // gc 생존 횟수를 넘은 경우나 young generation 공간이 부족한 경우 promotion
        int youngTop = topManager.getYoungTop();
        int oldTop = topManager.getOldTop();
        boolean nextSurv = topManager.getNextSurvivor();
        int surv1Bound = TopManager.getSurvivor1();
        int size = d.getSize();
        int now = youngTop;
        System.out.println("now size end surv1 " + now + " " + size + " " + end + " " + surv1Bound + " " + nextSurv + " " + d.isPromotion());
        if(d.isPromotion()) {
            now = oldTop;
        }
        else {
            if (nextSurv) {
                if (now + size > end) now = oldTop;
            } else {
                if (now + size > surv1Bound) now = oldTop;
            }
        }

        if(now == oldTop){
            if(now + size > heap.length) {
                return -1;
            }
        }
        return now;
    }

    private boolean copy(Data d){
        int size = d.getSize();
        int now = checkSize(d);
        int youngTop = topManager.getYoungTop();
        int oldTop = topManager.getOldTop();
        if(now == youngTop) topManager.setYoungTop(youngTop + size);
        else topManager.setOldTop(oldTop+size);
        if(now == -1){
            // old에도 공간이 부족한거니 OOME상태라고 판단하면 됨
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

        topManager.initEdenTop();
        System.out.println("edenTop " + topManager.getEdenTop());
        topManager.setNextYoungTop();
        return 1;
    }
}
