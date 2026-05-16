import java.util.Arrays;

public class JVM {
    private final Data[] heap;
    private final Data[] meta;
    private final GC minorGC;
    private final GC majorGC;
    private final FullGC fullGC; // metaTop 데이터 주고받기 위해 GC 대시 FullGC 사용
    private int eden;
    private int edenTop;
    private int oldTop;
    private int metaTop;

    public JVM(Data[] heap, Data[] meta, GC minorGC, GC majorGC, FullGC fullGC, int eden, int oldTop){
        this.heap = heap;
        this.meta = meta;
        this.minorGC = minorGC;
        this.majorGC = majorGC;
        this.fullGC = fullGC;
        this.eden = eden;
        edenTop = 0;
        this.oldTop = oldTop;
        metaTop = 0;
    }

    public void nextTime(int time){
        for(int i = 0; i < oldTop; i++){
            if(heap[i] != null){
                heap[i].decreaseLiveTime(time);
            }
        }
        for(int i = 0; i < metaTop; i++){
            if(meta[i] != null){
                meta[i].decreaseLiveTime(time);
            }
        }
    }

    private int garbageCollect(GC gc){
        // 원래 GC의 경우 major GC 같은 게 실패하면 Full GC를 시행해야 하나 여기서는 soft reference 삭제 등의 작동을 구현하지 않았기 때문에
        // major GC만 실패해도 OOME 발생시킴으로 대체
        // 추가로 major와 full gc의 경우 정리한 메모리 수를 전달하지만 minor의 경우 성공 아니면 실패니 성공 시 1 반환
        // 반환한 메모리가 0인 경우 실패
        int[] result = gc.execute();
        if(result[1] == 0){
            System.out.println("메모리 확보 실패?");
        }
        return result[0];
    }

    public void garbageCollect(){
        this.garbageCollect(this.fullGC);
        this.metaTop = fullGC.getMetaTop();
    }

    public void insertHeapData(Data d){
        // 에덴에 값을 넣을 때 자리가 부족하면 minor GC 실행
        // minor GC하다가 old generation 영역 부족하면 major GC 실행
        // major GC 끝난 후에 다시 minor gc 실행해야 하는데 이부분을 잘 처리해야 할 듯
        // major GC로 해결 안되면 원래 Full GC 시행하면서 soft reference에 대한 삭제도 일어나야 하는데 단순화니까 full gc 생략하고 에러 던지기?
        // 정반대로 minor GC 검사 전에 조건 만족 시 major GC를 먼저 수행하도록 하는 게 좋을 것 같기도 하다.
        if(edenTop + d.getSize() >= eden){
            if(heap.length - oldTop < eden){
                garbageCollect(majorGC);
            }
            try {
                oldTop = garbageCollect(minorGC);
            }
            catch(Throwable t){
                if(t instanceof OutOfMemoryError) {
                    System.out.println("OOME 발생");
                    System.out.println("메모리 초기화를 진행합니다.");
                    initData();
                }
            }
            edenTop = 0;
        }
        for(int i = edenTop; i < edenTop+d.getSize(); i++){
            heap[i] = d;
        }
        minorGC.setTop(edenTop+d.getSize());
    }

    public void insertMetaData(Data d){
        // meta 데이터 넣을 자리 부족하면 full GC 실행
        // 여기서 고민 중인게 보통 클래스 메타 데이터를 넣을 때 클래스 로더 객체를 힙 영역에 동시에 넣을까라는 생각이 든다
        // 이건 너무 과도하게 하는 것 같고 단순화니까 그냥 메타데이터만 관리하는 게 맞는 것 같다.
        // 여기선 콘솔 프로그램이니 하나씩만 넣으니까 Full GC 시 1 이상의 값만 확보되면 상관없고 0일 경우 에러 던지기
        if(metaTop + d.getSize() >= meta.length){
            garbageCollect();
        }
        for(int i = metaTop; i < metaTop + d.getSize(); i++){
            meta[i] = d;
        }
        fullGC.setMetaTop(metaTop+d.getSize());
    }

    public void printHeap(){
        System.out.println("heap 영역 데이터");
        System.out.print("| ");
        for(int i = 0; i < heap.length; i++){
            if(heap[i] != null) {
                System.out.print(heap[i].getName()+" : " + heap[i].getLiveTime() +" | ");
            }
            else{
                System.out.print("X : X | ");
            }
        }
        System.out.println();
    }

    public void printMeta(){
        System.out.println("meta 영역 데이터");
        System.out.print("| ");
        for(int i = 0; i < meta.length; i++){
            if(meta[i] != null) {
                System.out.print(meta[i].getName()+" : " + meta[i].getLiveTime() +" | ");
            }
            else{
                System.out.print("X : X | ");
            }
        }
        System.out.println();
    }

    public void initData(){
        // 모든 데이터 비우기
        Arrays.fill(heap, null);
        Arrays.fill(meta, null);
    }

}
