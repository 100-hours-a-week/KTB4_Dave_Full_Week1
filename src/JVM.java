public class JVM {
    private final Data[] heap;
    private final Data[] meta;
    private final MinorGC minorGC;
    private final GC majorGC;
    private final FullGC fullGC; // metaTop 데이터 주고받기 위해 GC 대시 FullGC 사용
    private int eden;
    private int edenTop;
    private int youngBound;
    private int oldTop;
    private int metaTop;

    public JVM(Data[] heap, Data[] meta, MinorGC minorGC, GC majorGC, FullGC fullGC, int eden, int youngBound){
        this.heap = heap;
        this.meta = meta;
        this.minorGC = minorGC;
        this.majorGC = majorGC;
        this.fullGC = fullGC;
        this.eden = eden;
        edenTop = 0;
        this.youngBound = youngBound;
        this.oldTop = youngBound;
        metaTop = 0;
    }

    public void nextTime(int time){
        // 데이터들의 유효기간 감소
        for(int i = 0; i < oldTop; i++){
            if(heap[i] != null){
                heap[i].decreaseLiveTime(time);
                i += heap[i].getSize() - 1;
            }
        }
        for(int i = 0; i < metaTop; i++){
            if(meta[i] != null){
                meta[i].decreaseLiveTime(time);
                i += meta[i].getSize() - 1;
            }
        }
    }

    private void garbageCollect(GC gc){
        // 원래 GC의 경우 major GC 같은 게 실패하면 Full GC를 시행해야 하나 여기서는 soft reference 삭제 등의 작동을 구현하지 않았기 때문에
        // major GC만 실패해도 OOME 발생으로 대체
        // 추가로 major와 full gc의 경우 정리한 메모리 수를 전달하지만 minor의 경우 성공 아니면 실패니 성공 시 1 반환
        // 반환한 메모리가 0인 경우 실패
        GCResult result = gc.execute();
        oldTopSync(result.getOldTop());
        if(result.getCleanDataSize() == 0){
            if(oldTop <= youngBound + ((heap.length-youngBound)/4) * 3){
                // 메모리에 충분한 공간이 있을 때 FullGC를 실행했을 때는 에러를 일으키지 않아야 한다.
                // 비율 기준으로 메모리 저장공간이 75%를 초과한 경우에 에러를 일으키도록 설정
                // 물론 정확한 기준으로 하려면 double을 사용해야 하지만 간단한 과제이기 때문에 정수 계산 채ㄷ
                return;
            }
            System.out.println("메모리 확보 실패");
            throw new OutOfMemoryError();
        }
    }

    public void garbageCollect(){
        // FullGC 실행
        garbageCollect(this.fullGC);
        if(oldTop < youngBound){
            oldTop = youngBound;
        }
        this.metaTop = fullGC.getMetaTop();
    }

    public void insertHeapData(Data d){
        // 에덴에 값을 넣을 때 자리가 부족하면 minor GC 실행
        // minor GC 검사 전에 조건 만족 시 major GC를 먼저 수행.
        if(d.getSize() > eden){
            if(d.getSize() > heap.length - youngBound){
                handleError();
                return;
            }
            if(oldTop+d.getSize() > heap.length){
                garbageCollect(majorGC);
            }
            if(oldTop+d.getSize() > heap.length){
                handleError();
                return;
            }
            else{
                for(int i = oldTop; i < oldTop+d.getSize(); i++){
                    heap[i] = d;
                }
                oldTop += d.getSize();
                oldTopSync(oldTop);
                return;
            }
        }

        if(edenTop + d.getSize() > eden){
            if(heap.length - oldTop < eden){
                // Old Generation의 남은 공간이 eden보다 작은 경우 majorGC 실행
                garbageCollect(majorGC);
            }
            try {
                garbageCollect(minorGC);
            }
            catch(Throwable t){
                if(t instanceof OutOfMemoryError) {
                    handleError();
                    return;
                }
            }
            edenTop = 0;
        }
        for(int i = edenTop; i < edenTop+d.getSize(); i++){
            heap[i] = d;
        }
        edenTop+=d.getSize();
        minorGC.setTop(edenTop);
    }

    public void insertMetaData(Data d){
        // meta 데이터 넣을 자리 부족하면 full GC 실행
        // 여기선 콘솔 프로그램이니 하나씩만 넣으니까 Full GC 시 1 이상의 값만 확보되면 상관없고 0일 경우 에러 던지기
        if(d.getSize() > meta.length){
            handleError();
            return;
        }

        if(metaTop + d.getSize() > meta.length){
            garbageCollect();
        }
        if(metaTop + d.getSize() > meta.length){
            handleError();
            return;
        }
        for(int i = metaTop; i < metaTop + d.getSize(); i++){
            meta[i] = d;
        }
        metaTop += d.getSize();
        fullGC.setMetaTop(metaTop);
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

    public void showData(){
        printHeap();
        printMeta();
    }

    public void initData(){
        // 모든 데이터 비우기
        for(int i = 0; i < heap.length; i++){
            heap[i] = null;
        }
        for(int i = 0; i < meta.length; i++){
            meta[i] = null;
        }
        oldTop = youngBound;
        edenTop = 0;
        metaTop = 0;
    }

    public void oldTopSync(int oldTop){
        minorGC.setOldTop(oldTop);
        majorGC.setTop(oldTop);
        fullGC.setTop(oldTop);
    }


    public void handleError(){
        System.out.println("OOME 발생");
        System.out.println("프로세스 종료. 메모리 초기화를 진행합니다.");
        initData();
    }


}
