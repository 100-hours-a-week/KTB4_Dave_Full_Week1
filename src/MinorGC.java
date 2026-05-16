public class MinorGC extends GC{
    private int edenBound; // surv1 시작점
    private int surv1Bound; // surv2 시작점
    private boolean nextSurv; // false면 surv1, true면 surv2로 복제
    private int youngTop; // young에서 다음으로 복사할 주소
    private int oldTop; // old에서 다음으로 복사할 주소

    // 현재 어느 부분에 객체를 삽입할 수 있는지 저장해야 하는데 이걸 young과 old에 대해서 저장해야 하는데 각 GC에서 가지고 있어야 하나?
    // JVM에서 관리하는 게 맞기는 한데 이런 단순화 구현에서는 GC가 하는게 맞나 싶기도 한데
    // JVM

    public MinorGC( Data[] heap, int start, int end, int top, int edenBound, int surv1Bound) {
        super(heap, start, end, top);
        this.edenBound = edenBound;
        this. surv1Bound = surv1Bound;
        this.nextSurv = false;
        this.youngTop = edenBound;
        this.oldTop = end;
    }

    @Override
    protected void search() {
        // 여기서 옮기다가 공간 부족하면 어떻게 처리할지 정해야 함
        // 1. Young Generation이 부족한 경우
        // 2. Old Generation이 부족한 경우
        // 에러를 던져서 JVM에서 분기 처리 시키기가 가장 유력할 듯
        // 결국 young에 크기가 커서 넣지 못해도 다음 객체는 넣을 수 있으니
        for(int i = start; i < top; i++){
            if(heap[i] != null){
                if(heap[i].isLive()){
                    Data d = heap[i];
                    copy(d);
                }
            }
        }
        int survBase = nextSurv? edenBound: surv1Bound;

        for(int i = survBase; i < survBase+(surv1Bound-edenBound); i++){
            if(heap[i] != null){
                if(heap[i].isLive()){
                    Data d = heap[i];
                    if(!copy(d)){
                        System.out.println("OOME 발생");
                        throw new OutOfMemoryError();
                    }
                }
            }
            else{
                break;
            }
        }

        if(nextSurv){
            nextSurv = false;
            youngTop = edenBound;
        }
        else{
            nextSurv = true;
            youngTop = surv1Bound;
        }
    }

    private int checkSize(Data d){
        // gc 생존 횟수를 넘은 경우나 young generation 공간이 부족한 경우 promotion
        int size = d.getSize();
        int now = youngTop;
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
            if(now + size >= heap.length) {
                return -1;
            }
        }
        return now;
    }

    private boolean copy(Data d){
        int size = d.getSize();
        int now = checkSize(d);
        if(now == youngTop) youngTop += size;
        else oldTop += size;
        if(now == -1){
            // old에도 공간이 부족한거니 OOME상태라고 판단하면 됨
            return false;
        }

        for(int i = 0; i < size; i++){
            heap[now+i] = d;
            now++;
        }
        d.surviveGC();

        return true;
    }

    @Override
    protected int cleaning() {
        // 정리의 경우 major와 full의 경우 정리한 데이터 수를 반환해줄 건데 minor는 정리한 개수를 안 세니까 애매함.
        // 멤버변수로 몇개의 데이터가 있는지 알아야 하나 싶음
        // 어차피 여기서 에러 체킹이 되는게 아니라 search에서 되니까 적당히 1 반환하면 될 듯
        int survBase = surv1Bound;
        if(nextSurv) survBase = edenBound;

        for(int i = start; i < edenBound; i++){
            heap[i] = null;
        }
        for(int i = survBase; i < survBase + (surv1Bound - edenBound); i++){
            heap[i] = null;
        }

        top = start;
        return 1;
    }
}
